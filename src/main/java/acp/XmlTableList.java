package acp;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.*;

import com.nqadmin.swingSet.*;
import com.nqadmin.swingSet.datasources.*;

import acp.utils.*;

public class XmlTableList extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  private ArrayList<String> params;

  String   tableName = "mss_options";

  String[] fields;
  String[] fieldnames; 
  String   pkColumn = null;

  String   strFrom = null;
  String   strSelectFrom = null;
  String   strAwhere = null;
  String   strWhere = null;
  String   strOrder = null;
  int      seqId = 1000;

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

  JLabel lblSource = new JLabel(Messages.getString("Column.SourceName"), JLabel.TRAILING);
  SSDBComboBox cbdbSource = null;

  JButton btnFilter = new JButton(Messages.getString("Button.Filter"));
  JButton btnFltClear = new JButton(Messages.getString("Button.Clear"));
  JButton btnEdit = new JButton(Messages.getString("Button.Edit"));
  JButton btnRefresh = new JButton(Messages.getString("Button.Refresh"));
  JButton btnClose = new JButton(Messages.getString("Button.Close"));
  
  public XmlTableList(ArrayList<String> pars, String keyTitle) {
    this.params = pars;
    desktop.add(this);
    setTitle(FieldConfig.getString(keyTitle));
    setSize(640, 480);
    setToCenter(); // метод из MyInternalFrame
    setMaximizable(true);
    setResizable(true);

    // Filter ---
    String query = "select msss_id, msss_name from mss_source order by msss_name";
    cbdbSource = new SSDBComboBox(ssConnection,query,"msss_id","msss_name");
    
    pnlFilter.setLayout(new BorderLayout());
    lblSource.setLabelFor(cbdbSource);

    pnlFilter_1.setLayout(new SpringLayout());
//    pnlFilter_1.setBorder(new LineBorder(Color.BLACK));
    pnlFilter_1.add(lblSource);
    pnlFilter_1.add(cbdbSource);
    SpringUtilities.makeCompactGrid(pnlFilter_1,1,2,8,8,8,8);

    pnlFilter_2.setLayout(new FlowLayout());
    pnlFilter_2.add(pnlBtnFilter);
    pnlBtnFilter.setLayout(new GridLayout(1,2,5,5));
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

    pnlBtnRecord.add(btnEdit);
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
    btnEdit.addActionListener(myActionListener);
    btnRefresh.addActionListener(myActionListener);
    btnClose.addActionListener(myActionListener);
  }

  private void createFields() {
    String[] path = params.get(0).split("/");
    fields = new String[params.size()+2];
    fieldnames = new String[params.size()+2];
    //---
    fields[0] = "CONFIG_ID";
    fieldnames[0] = "ID";
    pkColumn = fields[0];
    //---
    for (int i = 1; i < params.size(); i++) {
      fields[i] = "P" + i;
      fieldnames[i] = FieldConfig.getString(path[path.length - 1] + "." + params.get(i));
    }
    //---
    fields[params.size()] = "to_char(DATE_BEGIN,'dd.mm.yyyy') DATE_BEGIN";
    fieldnames[params.size()] = Messages.getString("Column.DateBegin");
    //---
    fields[params.size() + 1] = "to_char(DATE_END,'dd.mm.yyyy') DATE_END";
    fieldnames[params.size() + 1] = Messages.getString("Column.DateEnd");
    //---
  }

  private String createTable(long src) {
    String res = "table(mss.spr_options(" + src + ",'" + params.get(0) + "'";
    for (int i = 1; i < params.size(); i++) {
      res += ",'" + params.get(i) + "'";
    }
    for (int i = params.size(); i <= 5; i++) {
      res += ",null";
    }
    res += "))";
    return res;
  }

  public boolean initTable() {
    boolean res = false;
    // --------------------------------------------------
    try {
      cbdbSource.execute();
    } catch (SQLException e1) {
      DialogUtils.errorPrint(e1);
    } catch (Exception e1) {
      DialogUtils.errorPrint(e1);
    }
    // --------------------------------------------------
    createFields();
    strFrom = createTable(-1);
//    fieldnames не передавать в запрос, только заголовки !!!!
//    strSelectFrom = DbUtils.buildSelectFrom(fields, fieldnames, strFrom);
    strSelectFrom = DbUtils.buildSelectFrom(fields, null, strFrom);
    strWhere = strAwhere;
    strOrder = pkColumn;
    // --------------------------------------------------
    String query = DbUtils.testQuery(dbConnection, strSelectFrom, strWhere, strOrder);
    // --------------------------------------------------
//    System.out.println(query);
    if (query != null) {
      rs = new SSJdbcRowSetImpl(ssConnection);
      rs.setCommand(query);
      table.setHeaders(fieldnames);
      table.setSSRowSet(rs);
      res = true;
    }  
/*    
    ResultSetMetaData rsMetaData;
    try {
      rsMetaData = rs.getMetaData();
      int columnCount = rsMetaData.getColumnCount();
      System.out.println("getColumnCount(): " + columnCount);
      for (int i = 1; i <= columnCount; i++) {
        System.out.println("getColumnName(" + i + "): " + rsMetaData.getColumnName(i));
      }
//      for (int i = 1; i <= columnCount; i++) {
//        System.out.println("getColumnType(" + i + "): " + rsMetaData.getColumnType(i));
//      }
    } catch (SQLException e) {
      e.printStackTrace();
    }    
*/    
    if (res) {
      // ------------------------------------
      table.setInsertion(false);
      // table.setPrimaryColumn(0);
      // table.setHiddenColumns(new int[] {0}); ;
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
//  !!!! ID будет работать только при использовании fieldnames в запросе
//        int recId = rs.getInt("ID");  
        result = new Integer(recId);
      } catch (SQLException e) {
        DialogUtils.errorPrint(e);
      }
    } else {
      DialogUtils.errorPrint(Messages.getString("Message.NoSelectRecord"));
    }  
    return result;
  }

  private void editRecord(int act, int recId) {
    XmlTableEdit xmlEdit = new XmlTableEdit(params);
    boolean resInit = false;
    resInit = xmlEdit.initForm(act, recId);
    if (resInit) {
      desktop.add(xmlEdit);
      try {
        xmlEdit.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      xmlEdit.showModal(true);
      // -----------------------
      int resForm = xmlEdit.getResultForm();
      if (resForm == RES_OK) {
        if (act == ACT_NEW) { 
          refreshTable(NAV_LAST);
        } else {
          refreshTable(NAV_CURRENT);
        }
      }
    }
    xmlEdit = null;
  }

  private void clearFilter() {
    cbdbSource.setSelectedIndex(-1);
  }

//  private String getWherePhrase() {
//    String phWhere = strAwhere;
//    return phWhere;
//  }

//  private boolean validateRecord(int recId) {
//    return true;
//  }

//  private String createQuery(int act, int recId) {
//    StringBuilder query = null;
////    System.out.println(query);
//    if (query == null) {
//      return null;
//    } else {
//      return query.toString();
//    }  
//  }

  private class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object objSource = ae.getSource();
      if (objSource.equals(btnFilter)) {
        // --------------------------------------------------
        strFrom = createTable(cbdbSource.getSelectedValue());
//        fieldnames не передавать в запрос, только заголовки !!!!
//        strSelectFrom = DbUtils.buildSelectFrom(fields, fieldnames, strFrom);
        strSelectFrom = DbUtils.buildSelectFrom(fields, null, strFrom);
        strWhere = strAwhere;
        strOrder = pkColumn;
        // --------------------------------------------------
        String query = DbUtils.testQuery(dbConnection, strSelectFrom, strWhere, strOrder);
        // --------------------------------------------------
        rs.setCommand(query);
        refreshTable(NAV_FIRST);

      } else if (objSource.equals(btnFltClear)) {
        clearFilter();
        strFrom = createTable(-1);
//        fieldnames не передавать в запрос, только заголовки !!!!
//        strSelectFrom = DbUtils.buildSelectFrom(fields, fieldnames, strFrom);
        strSelectFrom = DbUtils.buildSelectFrom(fields, null, strFrom);
        strWhere = strAwhere;
        String query = DbUtils.buildQuery(strSelectFrom, strWhere, strOrder);
        rs.setCommand(query);
        refreshTable(NAV_FIRST);

      } else if (objSource.equals(btnEdit)) {
        Integer recordId = getRecordId(table.getSelectedRow());
        if (recordId != null) {
          int recId = recordId.intValue();
          editRecord(ACT_EDIT,recId);
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
          editRecord(ACT_EDIT,recId);
        }
      }
    }
  }

}
