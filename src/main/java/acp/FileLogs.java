package acp;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import com.nqadmin.swingSet.*;
import com.nqadmin.swingSet.datasources.*;

import acp.utils.*;

public class FileLogs extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  int fileId;
  
  final String tableName = "mss_logs";
  final String[] fields = {"to_char(mssl_dt_event,'dd.mm.yyyy hh24:mi:ss') mssl_dt_event", "mssl_desc"};
  final String[] fieldnames = {Messages.getString("Column.Time"), Messages.getString("Column.Desc")};
  final String pkColumn = "mssl_id";

  String strFrom = tableName;
  String strSelectFrom = null;
  String strAwhere = null;
  String strWhere = null;
  String strOrder = null;

  SSDataGrid table = new SSDataGrid();
  SSJdbcRowSetImpl rs = null;

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnExit = new JPanel();
  JButton btnClose = new JButton(Messages.getString("Button.Close"));
  
  public FileLogs(int file_id) {
    fileId = file_id;

    desktop.add(this);
    if (fileId > 0) {
      setTitle(Messages.getString("Title.AdvFileInfo"));
    } else {  
      setTitle(Messages.getString("Title.OtherLogs"));
    }  
    strAwhere = "mssl_ref_id=" + fileId + " and rownum<=10";
    
    setSize(700, 500);
    setToCenter();
    setMaximizable(true);
    setResizable(true);

    // Buttons ---
    pnlButtons.setLayout(new BorderLayout());
    pnlButtons.add(pnlBtnExit, BorderLayout.EAST);
    pnlBtnExit.add(btnClose);

    btnClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });

    // --- Layout ---
    Container cp = getContentPane();
    cp.setLayout(new BorderLayout());
    cp.add(table.getComponent(), BorderLayout.CENTER);
    cp.add(pnlButtons, BorderLayout.SOUTH);
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
      res = true;
    }  
    if (res) {
      // ------------------------------------
      table.setInsertion(false);
      table.setUneditableColumns(DbUtils.getFieldNums(fields));
      // ------------------------------------
      table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//      if (table.getRowCount() > 0) {
//        table.setRowSelectionInterval(0, 0);
//      }
      table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
      table.setMessageWindow(this);
    }
    return res;
  }

}
