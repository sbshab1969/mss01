package acp;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.border.*;

import com.nqadmin.swingSet.*;
import com.nqadmin.swingSet.datasources.*;

import acp.utils.*;

public class ConstList extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  final String tableName = "mss_const";

  final String[] fields = { "mssc_id", "mssc_name", "mssc_value" };
  final String[] fieldnames = { "ID"
      , Messages.getString("Column.Name")
      , Messages.getString("Column.Value") };
  final String pkColumn = "mssc_id";
  
  String strFrom = tableName;
  String strSelectFrom = null;
  String strAwhere = null;
  String strWhere = null;
  String strOrder = null;
  int seqId = 1000;

  SSDataGrid table = new SSDataGrid();
  SSJdbcRowSetImpl rs = null;

  JPanel pnlFilter = new JPanel();
  JPanel pnlBtnFilter = new JPanel();
  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JPanel pnlBtnAct = new JPanel();
  JPanel pnlBtnExit = new JPanel();

  JLabel lblName = new JLabel(Messages.getString("Column.Name"));
  JTextField txtName = new JTextField(20);

  JButton btnFilter = new JButton(Messages.getString("Button.Filter"));
  JButton btnFltClear = new JButton(Messages.getString("Button.Clear"));
  JButton btnAdd = new JButton(Messages.getString("Button.Add"));
  JButton btnEdit = new JButton(Messages.getString("Button.Edit"));
  JButton btnDelete = new JButton(Messages.getString("Button.Delete"));
  JButton btnRefresh = new JButton(Messages.getString("Button.Refresh"));
  JButton btnClose = new JButton(Messages.getString("Button.Close"));
  
  public ConstList() {
    desktop.add(this);
    setTitle(Messages.getString("Title.ConstList"));
    setSize(640, 480);
    setToCenter(); // метод из MyInternalFrame
    setMaximizable(true);
    setResizable(true);
    // setIconifiable(true);
    // setClosable(true);

    // Filter ---
    // pnlFilter.setBorder(new TitledBorder(new LineBorder(Color.BLACK),""));
    pnlFilter.setBorder(new TitledBorder(new LineBorder(Color.BLACK),Messages.getString("Title.Filter")));
    pnlFilter.setLayout(new GridBagLayout());
    GridBagConstraints cons = new GridBagConstraints();
    cons.insets = new Insets(0, 2, 2, 2);
//    cons.gridwidth = GridBagConstraints.HORIZONTAL;
    cons.weightx = 1.0;

    lblName.setLabelFor(txtName);
    lblName.setHorizontalAlignment(SwingConstants.RIGHT);

    cons.anchor = GridBagConstraints.WEST;
    pnlFilter.add(lblName, cons);
    
    cons.anchor = GridBagConstraints.WEST;
//    cons.gridwidth = GridBagConstraints.HORIZONTAL;
    pnlFilter.add(txtName, cons);

    cons.gridwidth = GridBagConstraints.REMAINDER;
    cons.anchor = GridBagConstraints.EAST;
    // cons.anchor = GridBagConstraints.LINE_START;
    // cons.anchor = GridBagConstraints.LINE_END;
    // cons.fill = GridBagConstraints.HORIZONTAL;
    cons.weightx = 1.0;
    pnlFilter.add(pnlBtnFilter, cons);

//    pnlBtnFilter.setLayout(new FlowLayout(FlowLayout.CENTER,5,0));
    pnlBtnFilter.setLayout(new GridLayout(1, 2, 2, 2));
    pnlBtnFilter.add(btnFilter);
    pnlBtnFilter.add(btnFltClear);
//    pnlBtnFilter.setBorder(new LineBorder(Color.BLACK));

    // Buttons ---
    pnlBtnRecord.add(btnAdd);
    pnlBtnRecord.add(btnEdit);
    pnlBtnRecord.add(btnDelete);
    pnlBtnAct.add(btnRefresh);
    pnlBtnExit.add(btnClose);

    pnlButtons.setLayout(new BorderLayout());
    pnlButtons.add(pnlBtnRecord, BorderLayout.WEST);
    pnlButtons.add(pnlBtnAct, BorderLayout.CENTER);
    pnlButtons.add(pnlBtnExit, BorderLayout.EAST);

    // --- Layout ---
    Container cp = getContentPane();
    cp.setLayout(new BorderLayout()); // default layout for JFrame
    cp.add(pnlFilter, BorderLayout.NORTH);
    cp.add(table.getComponent(), BorderLayout.CENTER);
    // JScrollPane scrollPane = new JScrollPane(table);
    // cp.add(scrollPane, BorderLayout.CENTER);
    cp.add(pnlButtons, BorderLayout.SOUTH);

    // Listeners ---
    MyActionListener myActionListener = new MyActionListener();
    btnFilter.addActionListener(myActionListener);
    btnFltClear.addActionListener(myActionListener);
    btnAdd.addActionListener(myActionListener);
    btnEdit.addActionListener(myActionListener);
    btnDelete.addActionListener(myActionListener);
    btnRefresh.addActionListener(myActionListener);
    btnClose.addActionListener(myActionListener);
  }

  public boolean initTable() {
    boolean res = false;
    // --------------------------------------------------
    strSelectFrom = DbUtils.buildSelectFrom(fields, null, strFrom);
    strWhere = strAwhere;
    strOrder = pkColumn;
    // --------------------------------------------------
    String query = DbUtils.testQuery(dbConnection, strSelectFrom, strWhere, strOrder);
    // --------------------------------------------------
    if (query != null) {
      rs = new SSJdbcRowSetImpl(ssConnection);
      rs.setCommand(query);
      table.setHeaders(fieldnames);
      table.setSSRowSet(rs);
//      table.setFillsViewportHeight(true);
      res = true;
    }  
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
    ConstEdit constEdit = new ConstEdit(tableName);
    boolean resInit = true;
    resInit = constEdit.initForm(act, recId);
    if (resInit) {
      desktop.add(constEdit);
      try {
        constEdit.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      constEdit.showModal(true);
      // -----------------------
      int resForm = constEdit.getResultForm();
      if (resForm == RES_OK) {
        if (act == ACT_NEW) { 
          refreshTable(NAV_LAST);
        } else {
          refreshTable(NAV_CURRENT);
        }
      }
    }
    constEdit = null;
  }

  private void clearFilter() {
    txtName.setText("");
  }

  private String getWherePhrase() {
    String phWhere = strAwhere;
    String str = null;
    if (!(txtName.getText()).equals("")) {
      str = "upper(mssc_name) like upper('" + txtName.getText() + "%')";
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }
    return phWhere;
  }

  private boolean validateRecord(int recId) {
    if (recId < seqId) {
      DialogUtils.errorMsg(Messages.getString("Message.DeleteSystemRecord"));
      return false;
    }
    return true;
  }

  private String createQuery(int act, int recId) {
    StringBuilder query = null;
    if (act == ACT_DELETE) {
      query = new StringBuilder();
      query.append("delete from " + tableName + " where " + pkColumn + "=" + recId); 
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

      } else if (objSource.equals(btnAdd)) {
        editRecord(ACT_NEW,-1);

      } else if (objSource.equals(btnEdit)) {
        Integer recordId = getRecordId(table.getSelectedRow());
        if (recordId != null) {
          int recId = recordId.intValue();
          editRecord(ACT_EDIT,recId);
        }

      } else if (objSource.equals(btnDelete)) {
        Integer recordId = getRecordId(table.getSelectedRow());
        if (recordId != null) {
          int recId = recordId.intValue();
          // editRecord(ACT_DELETE,recId);
          boolean resValidate = validateRecord(recId);  
          if (resValidate) {
            if (DialogUtils.confirmDialog(Messages.getString("Message.DeleteRecord") + " /id=" + recId + "/",
                       Messages.getString("Title.RecordDelete"), 1) == 0) {
              String query = createQuery(ACT_DELETE, recId);
              if (query != null) {
                DbUtils.executeUpdate(dbConnection, query);
                refreshTable(NAV_CURRENT);
              } else {
                DialogUtils.errorMsg(Messages.getString("Message.EmptySelect"));
              }
            }  
          }
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
