package acp;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.LineBorder;

import acp.utils.*;

public class FileInfo extends MyInternalFrame {
  private static final long serialVersionUID = 1L;
 
  private int recId = -1;
  private int resultForm = RES_NONE;

  final String[] fields = {
        "mssf_id"
      , "mssf_name"
      , "mssf_md5"
      , "to_char(mssf_dt_create, 'dd.mm.yyyy hh24:mi:ss') dt_create"
      , "to_char(mssf_dt_work, 'dd.mm.yyyy hh24:mi:ss') dt_work"
      , "mssf_owner"
      , "extract(mssf_statistic,'statistic/records/all/text()').getStringVal() records_all"
      , "extract(mssf_statistic,'statistic/records/error/text()').getStringVal() records_err"
      , "msso_name"};

  final String[] fieldnames = {
        Messages.getString("Column.fi_id")
      , Messages.getString("Column.fi_name")
      , Messages.getString("Column.fi_md5")
      , Messages.getString("Column.fi_dt_create")
      , Messages.getString("Column.fi_dt_work")
      , Messages.getString("Column.fi_owner")
      , Messages.getString("Column.fi_records_all")
      , Messages.getString("Column.fi_records_error")
      , Messages.getString("Column.fi_config") };

  private int fieldCount = fields.length;
  ArrayList<String> recValue = new ArrayList<String>();
  
  String strFrom = "mss_files, mss_options";
  String strSelectFrom = null;
  String strAwhere = "mssf_msso_id=msso_id";
  String strWhere = null;

  JPanel pnlData = new JPanel();
  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JButton btnClose = new JButton(Messages.getString("Button.Close"));

  public FileInfo() {
    setResizable(true);
    Container cp = getContentPane();
    pnlData.setLayout(new SpringLayout());
    pnlData.setBorder(new LineBorder(Color.BLACK));
    pnlButtons.setLayout(new BorderLayout());
    pnlButtons.add(pnlBtnRecord, BorderLayout.EAST);
    pnlBtnRecord.add(btnClose);

    btnClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });

    cp.add(pnlData, BorderLayout.CENTER);
    cp.add(pnlButtons, BorderLayout.SOUTH);
    initForm(ACT_NONE,recId);
  }

  public boolean initForm(int act, int recId) {
    boolean res = true;
    this.resultForm = RES_NONE;
    this.recId = recId;
    // ------------------------
    // Заголовок
    // ------------------------
    if (act == ACT_GET) {
      setTitle(Messages.getString("Title.FileInfo"));
    } else {
      setTitle(Messages.getString("Title.RecordNone"));
    }
    // ------------------------
    // Значения полей
    // ------------------------
    if (act == ACT_GET) {
      res = queryRecord(recId);
      if (res==true) {
        fillForm();
        pack();
      }
    }
    setToCenter();
    // ------------------------
    return res;
  }

  private boolean queryRecord(int recId) {
    boolean res = false;
    recValue.clear();
    strSelectFrom = DbUtils.buildSelectFrom(fields, null, strFrom);
    strWhere = strAwhere + " and mssf_id=" + recId;
    // --------------------------------------------------
    String query = DbUtils.testQuery(dbConnection, strSelectFrom, strWhere, null);
    try {
      Statement stmt = dbConnection.createStatement();
      ResultSet rsq = stmt.executeQuery(query);
      if (rsq.next()) {
        for (int i = 0; i < fieldCount; i++) {
          String val = rsq.getString(i+1);
          recValue.add(val);
        }
        res = true;
      } else {
        DialogUtils.errorPrint(Messages.getString("Message.EmptySelect"));
      }
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    // -----------------------
    return res;
  }

  private void fillForm() {
    for (int i = 0; i < fieldCount; i++) {
      JLabel lblName = new JLabel(fieldnames[i], JLabel.TRAILING);
      pnlData.add(lblName);
      // ---
      JLabel lblInfo = new JLabel(recValue.get(i));
      lblInfo.setForeground(new Color(0, 0, 128));
      pnlData.add(lblInfo);
    }
    SpringUtilities.makeCompactGrid(pnlData, fieldCount, 2, 10, 10, 10, 10);
  }

  public int getResultForm() {
    return resultForm;
  }

}
