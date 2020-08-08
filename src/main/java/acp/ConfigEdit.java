package acp;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.border.LineBorder;

import com.nqadmin.swingSet.*;

import acp.utils.*;

public class ConfigEdit extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  private String tableName;
  private int act = ACT_NONE;
  private int recId = -1;
  private int resultForm = RES_NONE;

  SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");

  JPanel pnlData = new JPanel();
  JLabel lblName = new JLabel(Messages.getString("Column.Name"), JLabel.TRAILING);
  JTextField txtName = new JTextField(20);
  JLabel lblSource = new JLabel(Messages.getString("Column.SourceName"), JLabel.TRAILING);
  SSDBComboBox cbdbSource = null;
//  SSDBComboBox cbdbSource = new SSDBComboBox();
//  JTextField cbdbSource = new JTextField(20);
  JPanel pnlDt = new JPanel();
  JLabel lblDtBegin = new JLabel(Messages.getString("Column.Date") + 
      Messages.getString("Column.Begin"), JLabel.TRAILING);
  JLabel lblDtEnd = new JLabel("  " + Messages.getString("Column.End") + "  ");
  JFormattedTextField dtBegin = new JFormattedTextField();
  JFormattedTextField dtEnd = new JFormattedTextField();
  JLabel lblComment = new JLabel(Messages.getString("Column.Comment"), JLabel.TRAILING);
  JTextArea taComment = new JTextArea(5,20);
  JScrollPane spComment = new JScrollPane(taComment);
  
  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JButton btnSave = new JButton(Messages.getString("Button.Save"));
  JButton btnCancel = new JButton(Messages.getString("Button.Cancel"));

  public ConfigEdit(String tableName) {
    this.tableName = tableName;
    Container cp = getContentPane();

    pnlData.setLayout(new SpringLayout());
    pnlData.setBorder(new LineBorder(Color.BLACK));
    
    pnlData.add(lblName);
    pnlData.add(txtName);
    lblName.setLabelFor(txtName);
//    lblName.setBorder(new LineBorder(Color.BLACK));
    
    String query = "select msss_id, msss_name from mss_source order by msss_name";
    cbdbSource = new SSDBComboBox(ssConnection,query,"msss_id","msss_name");

    pnlData.add(lblSource);
    pnlData.add(cbdbSource);
    lblSource.setLabelFor(cbdbSource);
    
    pnlDt.setLayout(new SpringLayout());
    dtBegin.setColumns(10);
    dtEnd.setColumns(10);
    pnlData.add(lblDtBegin);
    pnlData.add(pnlDt);
    pnlDt.add(dtBegin);
    pnlDt.add(lblDtEnd);
    pnlDt.add(dtEnd);
    SpringUtilities.makeCompactGrid(pnlDt,1,3,0,0,0,0);

    lblComment.setVerticalAlignment(SwingConstants.TOP);
//    lblComment.setBorder(new LineBorder(Color.BLACK));
    pnlData.add(lblComment);
    pnlData.add(spComment);

    SpringUtilities.makeCompactGrid(pnlData,4,2,10,10,10,10);

    pnlButtons.add(pnlBtnRecord);
    pnlBtnRecord.setLayout(new GridLayout(1, 2, 30, 0));
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
    // Список
    // ------------------------
    if (act == ACT_NEW || act == ACT_EDIT || act == ACT_DELETE) {
      try {
        cbdbSource.execute();
      } catch (SQLException e1) {
        DialogUtils.errorPrint(e1);
      } catch (Exception e1) {
        DialogUtils.errorPrint(e1);
      }
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
      cbdbSource.setEnabled(true);
      dtBegin.setEditable(true);
      dtEnd.setEditable(true);
      taComment.setEnabled(true);
      btnSave.setEnabled(true);
    } else if (act == ACT_DELETE) {
      txtName.setEditable(false);
      cbdbSource.setEnabled(false);
      dtBegin.setEditable(false);
      dtEnd.setEditable(false);
      taComment.setEnabled(false);
      btnSave.setEnabled(true);
    } else {
      txtName.setEditable(false);
      cbdbSource.setEnabled(false);
      dtBegin.setEditable(false);
      dtEnd.setEditable(false);
      taComment.setEnabled(false);
      btnSave.setEnabled(false);
    }
  }

  private void clearRecord() {
    this.recId = -1;
    txtName.setText("");
    cbdbSource.setSelectedIndex(-1);
    dtBegin.setValue(null);
    dtEnd.setValue(null);
    taComment.setText(null);
  }

  private boolean queryRecord(int recId) {
    boolean res = false;
    String recName = "";
    int recSource = 0;
    java.util.Date recDtBegin = null;
    java.util.Date recDtEnd = null;
    String recComment = "";
    try {
      String query = createQuery(ACT_GET, recId); 
      Statement stmt = dbConnection.createStatement();
      ResultSet rsq = stmt.executeQuery(query);
      if (rsq.next()) {
        this.recId = recId;
        recName = rsq.getString("MSSO_NAME");
        recSource = rsq.getInt("MSSO_MSSS_ID");
        recDtBegin = rsq.getDate("MSSO_DT_BEGIN");
        recDtEnd = rsq.getDate("MSSO_DT_END");
        recComment = rsq.getString("MSSO_COMMENT");
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
      cbdbSource.setSelectedValue(recSource);
      dtBegin.setValue(recDtBegin);
      dtEnd.setValue(recDtEnd);
      taComment.setText(recComment);
    }
    // -----------------------
    return res;
  }

  private boolean validateRecord() {
    if (txtName.getText().equals("")) {
      DialogUtils.errorMsg(Messages.getString("Message.IsEmpty")+": " + Messages.getString("Column.Name"));
      return false;
    }
    if (cbdbSource.getSelectedIndex() == -1) {
      DialogUtils.errorMsg(Messages.getString("Message.IsEmpty")+": " + Messages.getString("Column.SourceName"));
      return false;
    }
    if (dtBegin.getText().equals("")) {
      DialogUtils.errorMsg(Messages.getString("Message.IsEmpty")+": " + Messages.getString("Column.DateBegin"));
      return false;
    }
    if (dtEnd.getText().equals("")) {
      DialogUtils.errorMsg(Messages.getString("Message.IsEmpty")+": " + Messages.getString("Column.DateEnd"));
      return false;
    }
    if (taComment.getText().equals("")) {
      DialogUtils.errorMsg(Messages.getString("Message.IsEmpty")+": " + Messages.getString("Column.Comment"));
      return false;
    }
    return true;
  }

  private String createQuery(int act, int recId) {
    StringBuilder query = null;
    String valueD = "";
    String emptyXml = "<?xml version=\"1.0\"?><config><sverka.ats/></config>";
    if (act == ACT_GET) {
      query = new StringBuilder();
      query.append("select msso_name,msso_msss_id,msso_dt_begin,msso_dt_end,msso_comment" + 
      " from " + tableName); 
      query.append(" where msso_id=" + recId);
    
    } else if (act == ACT_NEW) {
      query = new StringBuilder();
      query.append("insert into " + tableName + " "); 
      query.append("(msso_id, msso_name, msso_config, msso_dt_begin, msso_dt_end, msso_comment"); 
      query.append(", msso_dt_create, msso_dt_modify, msso_owner, msso_msss_id)"); 
      query.append(" values (msso_seq.nextval");
      query.append(", '" + txtName.getText() + "'");
      query.append(", '" + emptyXml + "'");
      valueD = "to_date('" +  dtBegin.getText() +"','dd.mm.yyyy')";
      query.append(", " + valueD);
      valueD = "to_date('" +  dtEnd.getText() +"','dd.mm.yyyy')";
      query.append(", " + valueD);
      query.append(", '" + taComment.getText() + "'");
      query.append(", sysdate, sysdate, user");
      query.append(", " + cbdbSource.getSelectedValue() + ")");
    } else if (act == ACT_EDIT) {
        query = new StringBuilder();
        query.append("update " + tableName); 
        query.append(" set msso_name='" + txtName.getText() + "'");
        valueD = "to_date('" +  dtBegin.getText() +"','dd.mm.yyyy')";
        query.append(", msso_dt_begin=" + valueD);
        valueD = "to_date('" +  dtEnd.getText() +"','dd.mm.yyyy')";
        query.append(", msso_dt_end=" + valueD);
        query.append(", msso_comment='" + taComment.getText() + "'");
        query.append(", msso_dt_modify=SYSDATE");
        query.append(", msso_owner=USER");
        query.append(", msso_msss_id=" + cbdbSource.getSelectedValue());
        query.append(" where msso_id=" + recId);
        
//    } else if (act == ACT_DELETE) {
//      query = new StringBuilder();
//      query.append("delete from " + tableName + " where msso_id=" + recId); 
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
