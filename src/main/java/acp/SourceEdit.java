package acp;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import javax.swing.*;
import javax.swing.border.LineBorder;

import acp.utils.*;

public class SourceEdit extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  private String tableName;
  private int act = ACT_NONE;
  private int recId = -1;
  private int resultForm = RES_NONE;

  JPanel pnlData = new JPanel();
  JLabel lblName = new JLabel(Messages.getString("Column.Name"), JLabel.TRAILING);
  JTextField txtName = new JTextField(20);

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JButton btnSave = new JButton(Messages.getString("Button.Save"));
  JButton btnCancel = new JButton(Messages.getString("Button.Cancel"));

  public SourceEdit(String tableName) {
    this.tableName = tableName;
    Container cp = getContentPane();

    pnlData.setLayout(new SpringLayout());
    pnlData.setBorder(new LineBorder(Color.BLACK));
    pnlData.add(lblName);
    pnlData.add(txtName);
    lblName.setLabelFor(txtName);
    SpringUtilities.makeCompactGrid(pnlData,1,2,10,10,10,10);

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

  private void setEditableRecord(int act) {
    if (act == ACT_NEW || act == ACT_EDIT) {
      txtName.setEditable(true);
      btnSave.setEnabled(true);
    } else if (act == ACT_DELETE) {
      txtName.setEditable(false);
      btnSave.setEnabled(true);
    } else {
      txtName.setEditable(false);
      btnSave.setEnabled(false);
    }
  }

  private void clearRecord() {
    this.recId = -1;
    txtName.setText("");
  }

  private boolean queryRecord(int recId) {
    boolean res = false;
    String recName = "";
    try {
      String query = createQuery(ACT_GET, recId); 
      Statement stmt = dbConnection.createStatement();
      ResultSet rsq = stmt.executeQuery(query);
      if (rsq.next()) {
        this.recId = recId;
        recName = rsq.getString("MSSS_NAME");
        res = true;
      } else {
        DialogUtils.errorPrint(Messages.getString("Message.EmptySelect"));
      }
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    // -----------------------
    if (res) {
      txtName.setText(recName);
    }
    // -----------------------
    return res;
  }

  private boolean validateRecord() {
    if (txtName.getText().equals("")) {
      DialogUtils.errorMsg(Messages.getString("Message.IsEmpty")+": " + Messages.getString("Column.Name"));
      return false;
    }
    return true;
  }

  private String createQuery(int act, int recId) {
    StringBuilder query = null;
    if (act == ACT_GET) {
      query = new StringBuilder();
      query.append("select msss_name from " + tableName); 
      query.append(" where msss_id=" + recId);
    
    } else if (act == ACT_NEW) {
      query = new StringBuilder();
      query.append("insert into " + tableName + " "); 
      query.append("(msss_id, msss_name, msss_dt_create, msss_dt_modify, msss_owner) "); 
      query.append("values (mssc_seq.nextval, ");
      query.append("'" + txtName.getText() + "', ");
      query.append("sysdate, sysdate, user)");
      
    } else if (act == ACT_EDIT) {
        query = new StringBuilder();
        query.append("update " + tableName); 
        query.append(" set msss_name='" + txtName.getText() + "' ");
        query.append(",msss_dt_modify=sysdate, msss_owner=user");
        query.append(" where msss_id=" + recId);
        
//    } else if (act == ACT_DELETE) {
//      query = new StringBuilder();
//      query.append("delete from " + tableName + " where msss_id=" + recId); 
    }
    System.out.println(query);
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
