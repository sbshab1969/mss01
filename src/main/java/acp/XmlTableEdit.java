package acp;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.LineBorder;

import acp.utils.*;

public class XmlTableEdit extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  private ArrayList<String> params;
  private int paramSize;
  
  private String strFrom;

  private int act = ACT_NONE;
  private int recId = -1;
  private int resultForm = RES_NONE;

  JPanel pnlData = new JPanel();
  ArrayList<JLabel> lblList = new ArrayList<JLabel>();  
  ArrayList<JTextField> textList = new ArrayList<JTextField>();  
  ArrayList<String> recOldValue = new ArrayList<String>();  
  ArrayList<String> recValue = new ArrayList<String>();  

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JButton btnSave = new JButton(Messages.getString("Button.Save"));
  JButton btnCancel = new JButton(Messages.getString("Button.Cancel"));

  public XmlTableEdit(ArrayList<String> pars) {
    this.params = pars;
    paramSize = pars.size();

    Container cp = getContentPane();

    pnlData.setLayout(new SpringLayout());
    pnlData.setBorder(new LineBorder(Color.BLACK));
    
    String[] path = params.get(0).split("/");
    for (int i = 1; i < paramSize; i++) {
      // ------------------
      String lblName = FieldConfig.getString(path[path.length-1] + "." + params.get(i));
      JLabel lbl = new JLabel(lblName, JLabel.TRAILING);
      lblList.add(lbl);
      pnlData.add(lbl);
      // ------------------
      JTextField edt = new JTextField(30);
      textList.add(edt);
      pnlData.add(edt);
      // ------------------
      lbl.setLabelFor(edt);
    }
    SpringUtilities.makeCompactGrid(pnlData,paramSize-1,2,10,10,10,10);

    pnlButtons.add(pnlBtnRecord);
    pnlBtnRecord.setLayout(new GridLayout(1, 2, 20, 0));
    pnlBtnRecord.add(btnSave);
    pnlBtnRecord.add(btnCancel);

    cp.add(pnlData, BorderLayout.CENTER);
    cp.add(pnlButtons, BorderLayout.SOUTH);

    pack();
    setToCenter();

    MyActionListener myActionListener = new MyActionListener();
    btnSave.addActionListener(myActionListener);
    btnCancel.addActionListener(myActionListener);

    // Обязательно после listners
    initForm(ACT_NONE,recId);
  }

  public boolean initForm(int act, int recId) {
    boolean res = true;
    this.act = act;
    this.resultForm = RES_NONE;
    // ------------------------
    // Заголовок
    // ------------------------
    if (act == ACT_NEW) {
      setTitle(Messages.getString("Title.RecordAdd"));
    } else if (act == ACT_EDIT) {
      setTitle(Messages.getString("Title.RecordEdit"));
    } else if (act == ACT_DELETE) {
      setTitle(Messages.getString("Title.RecordDelete"));
    } else {
      setTitle(Messages.getString("Title.RecordNone"));
    }
    // ------------------------
    // Значения полей
    // ------------------------
    clearRecord();
    if (act == ACT_EDIT || act == ACT_DELETE) {
      res = queryRecord(recId);
    }
    // ------------------------
    // Доступность полей
    // ------------------------
    setEditableRecord(act);
    // ------------------------
    return res;
  }

  private String createTable(int recId) {
    String res = "table(mss.spr_option_id(" + recId + ",'" + params.get(0) + "'";
    for (int i = 1; i < paramSize; i++) {
      res += ",'" + params.get(i) + "'";
    }
    for (int i = paramSize; i <= 5; i++) {
      res += ",null";
    }
    res += "))";
    return res;
  }

  private void setEditableRecord(int act) {
    if (act == ACT_NEW || act == ACT_EDIT) {
      setEditableFields(true);
      btnSave.setEnabled(true);
    } else if (act == ACT_DELETE) {
      setEditableFields(false);
      btnSave.setEnabled(true);
    } else {
      setEditableFields(false);
      btnSave.setEnabled(false);
    }
  }
  
  private void setEditableFields(boolean flag) {
    for (int i = 1; i < paramSize; i++) {
      textList.get(i-1).setEditable(flag);
    }
  }

  private void clearRecord() {
    this.recId = -1;
    for (int i = 1; i < paramSize; i++) {
      textList.get(i-1).setText("");
    }
  }

  private boolean queryRecord(int recId) {
    boolean res = false;
    recValue.clear();
    recOldValue.clear();
    try {
      String query = createQuery(ACT_GET, recId); 
      Statement stmt = dbConnection.createStatement();
      ResultSet rsq = stmt.executeQuery(query);
      if (rsq.next()) {
        this.recId = recId;
        for (int i = 1; i < paramSize; i++) {
          String val = rsq.getString("P" + i);
          recValue.add(val);
          recOldValue.add(val);
        }
        res = true;
      } else {
        DialogUtils.errorPrint(Messages.getString("Message.EmptySelect"));
      }
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    // -----------------------
    if (res) {
      for (int i = 1; i < paramSize; i++) {
        textList.get(i-1).setText(recValue.get(i-1));
      }
    }
    // -----------------------
    return res;
  }

  private boolean validateRecord() {
    for (int i = 1; i < paramSize; i++) {
      if (textList.get(i-1).getText().equals("")) {
        DialogUtils.errorMsg(Messages.getString("Message.IsEmpty")+": " + lblList.get(i-1).getText());
        return false;
      }
    }
    return true;
  }

  private String createQuery(int act, int recId) {
    StringBuilder query = null;
    if (act == ACT_GET) {
      strFrom = createTable(recId);
      query = new StringBuilder();
      query.append("select t.* from " + strFrom + " t");
    
    } else if (act == ACT_EDIT) {
      String where = "";
      for (int i = 1; i < paramSize; i++) {
        where += "[@" + params.get(i) + "=\"" + recOldValue.get(i-1) + "\"]";
      }  
      // ------
      recValue.clear();
      for (int i = 1; i < paramSize; i++) {
        String val = textList.get(i-1).getText();
        recValue.add(val);
      }
      // ------
      query = new StringBuilder();
      query.append("update mss_options set ");
      query.append("msso_config = updatexml(msso_config");
      for (int i = 1; i < paramSize; i++) {
        query.append(",'" + params.get(0) + where + "/@" + params.get(i) + "'");
        query.append(",'" + recValue.get(i-1) + "'");
      }
      query.append(") where msso_id = " + recId);
    }

//    System.out.println(query);
    if (query == null) {
      return null;
    } else {
      return query.toString();
    }  
  }

  private class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object objSource = ae.getSource();
      if (objSource.equals(btnSave)) {
        if (act == ACT_NEW || act == ACT_EDIT) {
          boolean resValidate = validateRecord();  
          if (resValidate) {
            String query = createQuery(act, recId);
            if (query != null) {
              DbUtils.executeUpdate(dbConnection, query);
              dispose();
              resultForm = RES_OK;
            } else {
              DialogUtils.errorMsg(Messages.getString("Message.EmptySelect"));
            }
          }
        }  

      } else if (objSource.equals(btnCancel)) {
        dispose();
        resultForm = RES_CANCEL;
      }
    }
  }

  public int getResultForm() {
    return resultForm;
  }

}
