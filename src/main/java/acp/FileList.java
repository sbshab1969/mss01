package acp;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.*;

import com.nqadmin.swingSet.*;
import com.nqadmin.swingSet.datasources.*;

import acp.utils.*;

public class FileList extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  final String tableName = "mss_files";

  final String[] fields = { "mssf_id", "mssf_name", "mssf_md5", "mssf_owner", "mssf_dt_work",
                "extract(mssf_statistic,'statistic/records/all/text()').getStringval() rec_count" };

  final String[] fieldnames = { 
        "ID"
      , Messages.getString("Column.FileName")
      , "MD5"
      , Messages.getString("Column.Owner")
      , Messages.getString("Column.DateWork")
      , Messages.getString("Column.RecordCount")};

  final String pkColumn = "mssf_id";

  String strFrom = tableName;
  String strSelectFrom = null;
  String strAwhere = "rownum<=20";
  String strWhere = null;
  String strOrder = null;
  int seqId = 1000;

  SSDataGrid table = new SSDataGrid();
  SSJdbcRowSetImpl rs = null;

  JPanel pnlFilter = new JPanel();
  JPanel pnlFilter_1 = new JPanel();
  JPanel pnlFilter_2 = new JPanel();
  JPanel pnlBtnFilter = new JPanel();

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JPanel pnlBtnAct = new JPanel();
  JPanel pnlBtnExit = new JPanel();

  SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
  NumberFormat formatNumb = NumberFormat.getInstance();

  JLabel lblFileName = new JLabel(Messages.getString("Column.FileName"));
  JTextField txtFileName = new JTextField(20);
  JLabel lblOwner = new JLabel(Messages.getString("Column.Owner"));
  JTextField txtOwner = new JTextField(20);

  JLabel lblDtBegin = new JLabel(Messages.getString("Column.DateWork") + 
      Messages.getString("Column.Begin")); // , JLabel.TRAILING
  JLabel lblDtEnd = new JLabel(Messages.getString("Column.End"), JLabel.CENTER);
  JFormattedTextField dtBegin = new JFormattedTextField(formatDate);
  JFormattedTextField dtEnd = new JFormattedTextField(formatDate);

  JLabel lblRecBegin = new JLabel(Messages.getString("Column.RecordCount") + 
      Messages.getString("Column.Begin"));
  JLabel lblRecEnd = new JLabel(Messages.getString("Column.End"), JLabel.CENTER);
  JFormattedTextField recBegin = new JFormattedTextField(formatNumb);
  JFormattedTextField recEnd = new JFormattedTextField(formatNumb);
  
  JButton btnFilter = new JButton(Messages.getString("Button.Filter"));
  JButton btnFltClear = new JButton(Messages.getString("Button.Clear"));
  
  JButton btnInfo = new JButton(Messages.getString("Button.Info"));
  JButton btnLogs = new JButton(Messages.getString("Button.Logs"));
  JButton btnRefresh = new JButton(Messages.getString("Button.Refresh"));
  JButton btnClose = new JButton(Messages.getString("Button.Close"));
  
  public FileList() {
    desktop.add(this);
    setTitle(Messages.getString("Title.FileList"));
    setSize(800, 600);
    setToCenter(); // метод из MyInternalFrame
    setMaximizable(true);
    setResizable(true);

    // Filter ---
    pnlFilter.setLayout(new BorderLayout());
//    pnlFilter.setLayout(new GridBagLayout());
//    pnlFilter.setBorder(new TitledBorder(new LineBorder(Color.BLACK),Messages.getString("Title.Filter")));

    lblFileName.setLabelFor(txtFileName);
    lblOwner.setLabelFor(txtOwner);

    Calendar gcBefore = new GregorianCalendar();
//    gcBefore.setTime(new Date());
    gcBefore.add(Calendar.DAY_OF_YEAR, -7);
//    gcBefore.add(Calendar.MONTH, -1);
    gcBefore.add(Calendar.YEAR, -3);
    Date dtBefore = gcBefore.getTime();
    Date dtNow = new Date();
    dtBegin.setValue(dtBefore);
    dtEnd.setValue(dtNow);
    
//  recBegin.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT); // default
//  recBegin.setFocusLostBehavior(JFormattedTextField.COMMIT);
//  recBegin.setFocusLostBehavior(JFormattedTextField.REVERT);
//  recBegin.setFocusLostBehavior(JFormattedTextField.PERSIST);
    
    pnlFilter_1.add(lblFileName);
    pnlFilter_1.add(txtFileName);
    pnlFilter_1.add(lblOwner);
    pnlFilter_1.add(txtOwner);

    pnlFilter_1.setLayout(new SpringLayout());
//    pnlFilter_1.setBorder(new LineBorder(Color.BLACK));
    pnlFilter_1.add(lblDtBegin);
    pnlFilter_1.add(dtBegin);
    pnlFilter_1.add(lblDtEnd);
    pnlFilter_1.add(dtEnd);
    
    pnlFilter_1.add(lblRecBegin);
    pnlFilter_1.add(recBegin);
    pnlFilter_1.add(lblRecEnd);
    pnlFilter_1.add(recEnd);
    
    SpringUtilities.makeCompactGrid(pnlFilter_1,3,4,8,8,8,8);

    pnlFilter_2.setLayout(new FlowLayout());
//  pnlFilter_2.setLayout(new FlowLayout(FlowLayout.CENTER,6,6));
    pnlFilter_2.add(pnlBtnFilter);

    pnlBtnFilter.setLayout(new GridLayout(2,1,5,5));
    pnlBtnFilter.add(btnFilter);
    pnlBtnFilter.add(btnFltClear);

    pnlFilter.setLayout(new BorderLayout());
    pnlFilter.add(pnlFilter_1,BorderLayout.CENTER);
    pnlFilter.add(pnlFilter_2,BorderLayout.EAST);
    
    // Buttons ---
    pnlButtons.setLayout(new BorderLayout());
    pnlButtons.add(pnlBtnRecord, BorderLayout.WEST);
    pnlButtons.add(pnlBtnAct, BorderLayout.CENTER);
    pnlButtons.add(pnlBtnExit, BorderLayout.EAST);

    pnlBtnRecord.add(btnInfo);
    pnlBtnRecord.add(btnLogs);
    pnlBtnAct.add(btnRefresh);
    pnlBtnExit.add(btnClose);

    // --- Layout ---
    Container cp = getContentPane();
    cp.setLayout(new BorderLayout()); // default layout for JFrame
    cp.add(pnlFilter, BorderLayout.NORTH);
    cp.add(table.getComponent(), BorderLayout.CENTER);
    cp.add(pnlButtons, BorderLayout.SOUTH);

    // Listeners ---
    MyActionListener myActionListener = new MyActionListener();
    btnFilter.addActionListener(myActionListener);
    btnFltClear.addActionListener(myActionListener);
    btnInfo.addActionListener(myActionListener);
    btnLogs.addActionListener(myActionListener);
    btnRefresh.addActionListener(myActionListener);
    btnClose.addActionListener(myActionListener);
  }

  public boolean initTable() {
    boolean res = false;
    // --------------------------------------------------
    strSelectFrom = DbUtils.buildSelectFrom(fields, null, strFrom);
//    strWhere = strAwhere;
    strWhere = getWherePhrase();
    strOrder = pkColumn;
    // --------------------------------------------------
    String query = DbUtils.testQuery(dbConnection, strSelectFrom, strWhere, strOrder);
//    System.out.println(query);
    // --------------------------------------------------
    if (query != null) {
      rs = new SSJdbcRowSetImpl(ssConnection);
      rs.setCommand(query);
      table.setHeaders(fieldnames);
      table.setSSRowSet(rs);
      res = true;
    }  
    if (res) {
      // ------------------------------------
      table.setInsertion(false);
      table.setUneditableColumns(DbUtils.getFieldNums(fields));
      // ------------------------------------
      table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      // table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      // table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      if (table.getRowCount() > 0) {
        table.setRowSelectionInterval(0, 0);
      }
      // ------------------------------------
      // table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      // table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
      // table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
      // table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
      table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
      // ------------------------------------
      table.setMessageWindow(this);
      table.addMouseListener(new MyMouseListener());
    }
    return res;
  }

  private void refreshTable(int navMode) {
    refreshTable(navMode,0);
  }

  private void refreshTable(int navMode, int recNum) {
    int currRecord = table.getSelectedRow();
    // -------------------
    table.setSSRowSet(rs);
    // -------------------
    int selRow = -1;
    int rows = table.getRowCount();
    if (rows > 0) {
      switch (navMode) {
      case NAV_FIRST:
        selRow = 0;
        break;
      case NAV_LAST:
        selRow = rows-1;
        break;
      case NAV_CURRENT:
        selRow = currRecord;
        break;
      case NAV_ABSOLUTE:
        selRow = recNum;
        break;
      default:
        selRow = 0;
      }  
      if (selRow < 0) {
        selRow = 0;
      }
      if (selRow >= rows) {
        selRow = rows-1;
      }
    }
    // -------------------
    if (selRow >= 0) {
      table.setRowSelectionInterval(selRow, selRow);
      try {
        rs.absolute(selRow+1);
      } catch (SQLException e) {
        DialogUtils.errorPrint(e);
      }
    }
  }

  private Integer getRecordId(int selectRow) {
    Integer result = null;
    if (selectRow>=0) {
      try {
        rs.absolute(selectRow + 1);
        int recId = rs.getInt(pkColumn);
        result = new Integer(recId);
      } catch (SQLException e) {
        DialogUtils.errorPrint(e);
      }
    } else {
      DialogUtils.errorPrint(Messages.getString("Message.NoSelectRecord"));
    }  
    return result;
  }
 
  private void showInfo(int recId) {
    FileInfo fileInfo = new FileInfo();
    boolean resInit = true;
    resInit = fileInfo.initForm(ACT_GET,recId);
    if (resInit) {
      desktop.add(fileInfo);
      try {
        fileInfo.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      fileInfo.showModal(true);
      // -----------------------
    }
    fileInfo = null;
  }
  
  private void showLogs(int recId) {
    FileLogs fileLog = new FileLogs(recId);
    boolean resInit = true;
    resInit = fileLog.initTable();
    if (resInit) {
//      desktop.add(fileLog);
      try {
        fileLog.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      fileLog.showModal(true);
      // -----------------------
    }
    fileLog = null;
  }

  private void clearFilter() {
    txtFileName.setText("");
    txtOwner.setText("");
    dtBegin.setValue(null);
    dtEnd.setValue(null);
    recBegin.setValue(null);
    recEnd.setValue(null);
  }

  private String getWherePhrase() {
    String phWhere = strAwhere;
    String vField = "";
    String vBeg = "";
    String vEnd = "";
    String valueBeg = "";
    String valueEnd = "";
    String str = null;
    // ----------------------
    if (!(txtFileName.getText()).equals("")) {
      vField = "upper(mssf_name)";
      str = vField + " like upper('" + txtFileName.getText() + "%')";
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }
    // ----------------------
    if (!(txtOwner.getText()).equals("")) {
      vField = "upper(mssf_owner)";
      str = vField + " like upper('" + txtOwner.getText() + "%')";
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }
    // ----------------------
    vField = "trunc(mssf_dt_work)";
    vBeg = dtBegin.getText();
    vEnd = dtEnd.getText();
    valueBeg = "to_date('" +  vBeg +"','dd.mm.yyyy')";
    valueEnd = "to_date('" +  vEnd +"','dd.mm.yyyy')";
    if (!vBeg.equals("") || !vEnd.equals("")) {
      if (!vBeg.equals("") && !vEnd.equals("")) {
        str = vField + " between " + valueBeg + " and " + valueEnd;
      } else if (!vBeg.equals("") && vEnd.equals("")) {
        str = vField + " >= " + valueBeg;
      } else if (vBeg.equals("") && !vEnd.equals("")) {
        str = vField + " <= " + valueEnd;
      }
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }  
    // ----------------------
    vField = "to_number(extract(mssf_statistic,'statistic/records/all/text()').getstringval())";
    vBeg = recBegin.getText();
    vEnd = recEnd.getText();
    valueBeg = vBeg;
    valueEnd = vEnd;
    if (!vBeg.equals("") || !vEnd.equals("")) {
      if (!vBeg.equals("") && !vEnd.equals("")) {
        str = vField + " between " + valueBeg + " and " + valueEnd;
      } else if (!vBeg.equals("") && vEnd.equals("")) {
        str = vField + " >= " + valueBeg;
      } else if (vBeg.equals("") && !vEnd.equals("")) {
        str = vField + " <= " + valueEnd;
      }
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }  
    // ----------------------
//    System.out.println(phWhere);
    return phWhere;
  }

  private class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object objSource = ae.getSource();
      if (objSource.equals(btnFilter)) {
        String phWhere = getWherePhrase();
        String query = DbUtils.testQuery(dbConnection, strSelectFrom, phWhere, strOrder);
        if (query != null) {
          strWhere = phWhere;
          rs.setCommand(query);
          refreshTable(NAV_FIRST);
        }  

      } else if (objSource.equals(btnFltClear)) {
        clearFilter();
        strWhere = strAwhere;
        String query = DbUtils.buildQuery(strSelectFrom, strWhere, strOrder);
        rs.setCommand(query);
        refreshTable(NAV_FIRST);

      } else if (objSource.equals(btnInfo)) {
        Integer recordId = getRecordId(table.getSelectedRow());
        if (recordId != null) {
          int recId = recordId.intValue();
          showInfo(recId);
        }

      } else if (objSource.equals(btnLogs)) {
        Integer recordId = getRecordId(table.getSelectedRow());
        if (recordId != null) {
          int recId = recordId.intValue();
          showLogs(recId);
        }

      } else if (objSource.equals(btnRefresh)) {
        refreshTable(NAV_CURRENT);

      } else if (objSource.equals(btnClose)) {
        dispose();
      }
    }
  }

  private class MyMouseListener extends MouseAdapter {
    public void mouseClicked(MouseEvent e) {
      if (e.getClickCount() == 2) {
        Integer recordId = getRecordId(table.getSelectedRow());
        if (recordId != null) {
          int recId = recordId.intValue();
          showInfo(recId);
        }
      }
    }
  }

}
