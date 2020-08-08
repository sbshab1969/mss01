package acp;

import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;
import javax.swing.border.*;

import acp.utils.*;

public class ConfigXml extends MyInternalFrame {
  private static final long serialVersionUID = 1L;
 
  private int act = ACT_NONE;
  private int recId = -1;
  private int resultForm = RES_NONE;

  JPanel pnlData = new JPanel();
  JTextArea txtConf = new JTextArea();

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JButton btnSave = new JButton(Messages.getString("Button.Save"));
  JButton btnCancel = new JButton(Messages.getString("Button.Cancel"));

  public ConfigXml() {
    setSize(700, 500);
    setResizable(true);
    Container cp = getContentPane();

    pnlData.setLayout(new BorderLayout());
    pnlData.setBorder(new LineBorder(Color.BLACK));
//    pnlData.setBorder(new EmptyBorder(2, 2, 2, 2));
    
    JScrollPane txtView = new JScrollPane(txtConf);
    pnlData.add(txtView, BorderLayout.CENTER);
    
    pnlButtons.add(pnlBtnRecord);
    pnlBtnRecord.setLayout(new GridLayout(1, 2, 30, 0));
    pnlBtnRecord.add(btnSave);
    pnlBtnRecord.add(btnCancel);

    cp.add(pnlData, BorderLayout.CENTER);
    cp.add(pnlButtons, BorderLayout.SOUTH);
    setToCenter();

    MyActionListener myActionListener = new MyActionListener();
    btnSave.addActionListener(myActionListener);
    btnCancel.addActionListener(myActionListener);
    
    initForm(ACT_NONE,recId);
  }

  public boolean initForm(int act, int recId) {
    boolean res = true;
    this.act = act;
    this.resultForm = RES_NONE;
    this.recId = recId;
    // ------------------------
    // Заголовок
    // ------------------------
    if (act == ACT_EDIT) {
      setTitle(Messages.getString("Title.DirectEdit"));
    } else {
      setTitle(Messages.getString("Title.RecordNone"));
    }
    // ------------------------
    // Значения полей
    // ------------------------
    if (act == ACT_EDIT) {
      res = queryRecord(recId);
    }
    return res;
  }

  private boolean queryRecord(int recId) {
    boolean res = false;
    String recConf = "";
    try {
      String query = createQuery(ACT_GET, recId); 
      Statement stmt = dbConnection.createStatement();
      ResultSet rsq = stmt.executeQuery(query);
      if (rsq.next()) {
        this.recId = recId;
        recConf = rsq.getString("MSSO_CONF");
        res = true;
      } else {
        DialogUtils.errorPrint(Messages.getString("Message.EmptySelect"));
      }
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    // -----------------------
    if (res) {
      txtConf.setText(recConf);
    }
    // -----------------------
    return res;
  }

  private String createQuery(int act, int recId) {
    StringBuilder query = null;
    if (act == ACT_GET) {
      query = new StringBuilder();
      query.append("select t.msso_config.getstringval() msso_conf from mss_options t"); 
      query.append(" where msso_id=" + recId);
      
    } else if (act == ACT_EDIT) {
      query = new StringBuilder();
      query.append("update mss_options"); 
      query.append(" set msso_config=XMLType('" + txtConf.getText() + "')");
      query.append(", msso_dt_modify=SYSDATE");
      query.append(", msso_owner=USER");
      query.append(" where msso_id=" + recId);
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
        if (act == ACT_EDIT) {
          String query = createQuery(act, recId);
          if (query != null) {
            int res = -1;
            res = DbUtils.executeUpdate(dbConnection, query);
            if (res>=0) {
              dispose();
              resultForm = RES_OK;
            }  
          } else {
            DialogUtils.errorMsg(Messages.getString("Message.EmptySelect"));
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
