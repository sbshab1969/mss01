package acp;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.LineBorder;

import acp.utils.*;

public class VarEdit extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  private String tableName;
  private int act = ACT_NONE;
  private int recId = -1;
  private int resultForm = RES_NONE;

  SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
  NumberFormat formatNumb = NumberFormat.getInstance();
  JLabel lblFormatD = new JLabel("/ " + Messages.getString("Column.DateFormat") + " /");
//  JLabel lblFormatD = new JLabel("/ " + formatDate.toPattern() + " /");

  Vector<String> keys = new Vector<String>();
  Vector<String> opts = new Vector<String>();
  
  JPanel pnlData = new JPanel();
  JLabel lblName = new JLabel(Messages.getString("Column.Name"));
  JLabel lblType = new JLabel(Messages.getString("Column.Type"));
  JLabel lblValueN = new JLabel(Messages.getString("Column.Number"));
  JLabel lblValueV = new JLabel(Messages.getString("Column.Varchar"));
  JLabel lblValueD = new JLabel(Messages.getString("Column.Date"));

  JTextField txtName = new JTextField(30);
  JComboBox<String> cmbType = null;
  JFormattedTextField txtValueN = new JFormattedTextField(formatNumb);
  JTextField txtValueV = new JTextField(30);
  JFormattedTextField txtValueD = new JFormattedTextField(formatDate);
  JPanel pnlDate = new JPanel();

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JButton btnSave = new JButton(Messages.getString("Button.Save"));
  JButton btnCancel = new JButton(Messages.getString("Button.Cancel"));

  public VarEdit(String tableName) {
    this.tableName = tableName;
    // --------------
    keys.add("N");
    keys.add("V");
    keys.add("D");
    keys.add("U");
    // --------------
    opts.add(Messages.getString("Column.Number"));  
    opts.add(Messages.getString("Column.Varchar")); 
    opts.add(Messages.getString("Column.Date"));   
    opts.add(Messages.getString("Column.Universal"));    
    // --------------
    cmbType = new JComboBox<String>(opts);
    cmbType.setSelectedIndex(-1);
    txtValueN.setColumns(14);
    txtValueD.setColumns(14);
    cmbType.setPreferredSize(txtValueN.getPreferredSize());
    // --------------
    Container cp = getContentPane();
 
    lblName.setLabelFor(txtName);
    lblType.setLabelFor(cmbType);
    lblValueN.setLabelFor(txtValueN);
    lblValueV.setLabelFor(txtValueV);
    lblValueD.setLabelFor(txtValueD);

    pnlData.setLayout(new GridBagLayout());
    pnlData.setBorder(new LineBorder(Color.BLACK));

//    pnlDate.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
    pnlDate.setLayout(new GridLayout(1, 2, 5, 0));
//    pnlDate.setBorder(new LineBorder(Color.BLACK));
    pnlDate.add(txtValueD);
    pnlDate.add(lblFormatD);

    GridBagConstraints cons = new GridBagConstraints();

    cons.insets = new Insets(10, 10, 2, 10);
    cons.gridx = 0;
    cons.gridy = 0;
    cons.anchor = GridBagConstraints.EAST;
    pnlData.add(lblName, cons);
    cons.gridx = 1;
    cons.gridy = 0;
    cons.anchor = GridBagConstraints.WEST;
    pnlData.add(txtName, cons);

    cons.insets = new Insets(2, 10, 2, 10);
    cons.gridx = 0;
    cons.gridy = 1;
    cons.anchor = GridBagConstraints.EAST;
    pnlData.add(lblType, cons);
    cons.gridx = 1;
    cons.gridy = 1;
    cons.anchor = GridBagConstraints.WEST;
    pnlData.add(cmbType, cons);

    cons.insets = new Insets(2, 10, 2, 10);
    cons.gridx = 0;
    cons.gridy = 2;
    cons.anchor = GridBagConstraints.EAST;
    pnlData.add(lblValueN, cons);
    cons.gridx = 1;
    cons.gridy = 2;
    cons.anchor = GridBagConstraints.WEST;
    pnlData.add(txtValueN, cons);

    cons.insets = new Insets(2, 10, 2, 10);
    cons.gridx = 0;
    cons.gridy = 3;
    cons.anchor = GridBagConstraints.EAST;
    pnlData.add(lblValueV, cons);
    cons.gridx = 1;
    cons.gridy = 3;
    cons.anchor = GridBagConstraints.WEST;
    pnlData.add(txtValueV, cons);

    cons.insets = new Insets(2, 10, 10, 10);
    cons.gridx = 0;
    cons.gridy = 4;
    cons.anchor = GridBagConstraints.EAST;
    pnlData.add(lblValueD, cons);
    cons.gridx = 1;
    cons.gridy = 4;
    cons.anchor = GridBagConstraints.WEST;
//    pnlData.add(txtValueD, cons);
    pnlData.add(pnlDate, cons);

    pnlButtons.add(pnlBtnRecord);
    pnlBtnRecord.setLayout(new GridLayout(1, 2, 30, 0));
    pnlBtnRecord.add(btnSave);
    pnlBtnRecord.add(btnCancel);

    cp.add(pnlData, BorderLayout.CENTER);
    cp.add(pnlButtons, BorderLayout.SOUTH);

    pack();
    setToCenter();

    MyActionListener myActionListener = new MyActionListener();
    cmbType.addActionListener(myActionListener);
    btnSave.addActionListener(myActionListener);
    btnCancel.addActionListener(myActionListener);

    // Обязательно после listeners
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
      cmbType.setEnabled(true);
      cmbType.setEditable(false);
      // setEditableVars();
      btnSave.setEnabled(true);
    } else if (act == ACT_DELETE) {
      txtName.setEditable(false);
      cmbType.setEnabled(true);
      cmbType.setEditable(false);
      // setEditableVars();
      txtValueV.setEditable(false);
      txtValueN.setEditable(false);
      txtValueD.setEditable(false);
      btnSave.setEnabled(true);
    } else {
      txtName.setEditable(false);
      cmbType.setEnabled(false);
      cmbType.setEditable(false);
      // setEditableVars();
      txtValueV.setEditable(false);
      txtValueN.setEditable(false);
      txtValueD.setEditable(false);
      btnSave.setEnabled(false);
    }
  }

  private void setEditableVars() {
    int keyIndex = cmbType.getSelectedIndex();
    String keyValue = null; 
    if (keyIndex >= 0) {
      keyValue = keys.get(keyIndex); 
    } else {
      keyValue = "0";
    }
    if (keyValue == "V") {
      txtValueV.setEditable(true);
      txtValueN.setEditable(false);
      txtValueD.setEditable(false);
    } else if (keyValue == "N") {
      txtValueV.setEditable(false);
      txtValueN.setEditable(true);
      txtValueD.setEditable(false);
    } else if (keyValue == "D") {
      txtValueV.setEditable(false);
      txtValueN.setEditable(false);
      txtValueD.setEditable(true);
    } else if (keyValue == "U") {
      txtValueV.setEditable(true);
      txtValueN.setEditable(true);
      txtValueD.setEditable(true);
    } else {
      txtValueV.setEditable(false);
      txtValueN.setEditable(false);
      txtValueD.setEditable(false);
    }
  }

  private void clearRecord() {
    this.recId = -1;
    txtName.setText("");
    cmbType.setSelectedIndex(-1);
    txtValueN.setValue(null);
    txtValueV.setText("");
    txtValueD.setValue(null);
  }

  private boolean queryRecord(int recId) {
    boolean res = false;
    String recName = "";
    String recType = "";
    String recValueN = null;
    Double valueN = null;
    String recValueV = "";
    java.util.Date recValueD = null;
    try {
      String query = createQuery(ACT_GET, recId); 
      Statement stmt = dbConnection.createStatement();
      ResultSet rsq = stmt.executeQuery(query);
      if (rsq.next()) {
        this.recId = recId;
        recName = rsq.getString("MSSV_NAME");
        recType = rsq.getString("MSSV_TYPE");
        recValueN = rsq.getString("MSSV_VALUEN");
        recValueV = rsq.getString("MSSV_VALUEV");
        recValueD = rsq.getDate("MSSV_VALUED");
        res = true;
      } else {
        DialogUtils.errorPrint(Messages.getString("Message.EmptySelect"));
      }
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    // -----------------------
    // Установка значений
    // -----------------------
    if (res) {
      txtName.setText(recName);
      cmbType.setSelectedIndex(keys.indexOf(recType));
      if (recValueN != null) {
        valueN = Double.valueOf(recValueN);
      } else {  
        valueN = null;
      }
      txtValueN.setValue(valueN);
      txtValueV.setText(recValueV);
      txtValueD.setValue(recValueD);
    }
    // -----------------------
    return res;
  }

  private boolean validateRecord() {
    if (txtName.getText().equals("")) {
      DialogUtils.errorMsg(Messages.getString("Message.IsEmpty")+": " + Messages.getString("Column.Name"));
      return false;
    }
    if (cmbType.getSelectedIndex() == -1) {
      DialogUtils.errorMsg(Messages.getString("Message.IsEmpty")+": " + Messages.getString("Column.Type"));
      return false;
    }

    String vType = keys.get(cmbType.getSelectedIndex());
    String valueN = txtValueN.getText();
    String valueV = txtValueV.getText();
    String valueD = txtValueD.getText();

    if (vType == "N") {
      if (valueN.equals("")) {
        DialogUtils.errorMsg(Messages.getString("Message.IsEmpty")+": " + Messages.getString("Column.Number"));
        return false;
      }
    } else if (vType == "V") {
      if (valueV.equals("")) {
        DialogUtils.errorMsg(Messages.getString("Message.IsEmpty")+": " + Messages.getString("Column.Varchar"));
        return false;
      }
    } else if (vType == "D") {
      if (valueD.equals("")) {
        DialogUtils.errorMsg(Messages.getString("Message.IsEmpty")+": " + Messages.getString("Column.Date"));
        return false;
      }
//    } else {
//      if (valueN.equals("")) {
//        DialogUtils.errorMsg(Messages.getString("Message.IsEmpty")+": " + Messages.getString("Column.Number"));
//        return false;
//      }
//      if (valueV.equals("")) {
//        DialogUtils.errorMsg(Messages.getString("Message.IsEmpty")+": " + Messages.getString("Column.Varchar"));
//        return false;
//      }
//      if (valueD.equals("")) {
//        DialogUtils.errorMsg(Messages.getString("Message.IsEmpty")+": " + Messages.getString("Column.Date"));
//        return false;
//      }
    }
    return true;
  }

  private String createQuery(int act, int recId) {
    StringBuilder query = null;
    String vNull = "null";
    String vName = null;
    String vType = null;
    String valueN = null;
    String valueV = null;
    String valueD = null;
    
    if (act == ACT_NEW || act == ACT_EDIT) {
      vName = txtName.getText();
      vType = keys.get(cmbType.getSelectedIndex());
      valueN = String.valueOf(txtValueN.getValue());
      valueV = txtValueV.getText();
      valueD = txtValueD.getText();
      if (vType == "N") {
        valueV = vNull;
        valueD = vNull;
      } else if (vType == "V") {
          valueN = vNull;
          valueV = "'" + valueV + "'";
          valueD = vNull;
      } else if (vType == "D") {
        valueN = vNull;
        valueV = vNull;
        valueD = "to_date('" + valueD +"','dd.mm.yyyy')";
      } else {
        valueV = "'" + valueV + "'";
        valueD = "to_date('" + valueD +"','dd.mm.yyyy')";
      }
    }

    if (act == ACT_GET) {
      query = new StringBuilder();
      query.append("select mssv_name,mssv_type,mssv_valuen,mssv_valuev,mssv_valued"); 
      query.append(" from " + tableName + " where mssv_id=" + recId);
    
    } else if (act == ACT_NEW) {
      query = new StringBuilder();
      query.append("insert into " + tableName + " (mssv_id, mssv_name, mssv_type, mssv_len, ");
      query.append("mssv_valuen, mssv_valuev, mssv_valued, mssv_last_modify, mssv_owner)");
      query.append(" values (mssv_seq.nextval");
      query.append(", upper('" + vName + "')");
      query.append(", '" + vType + "', 120");
      query.append(", " + valueN);
      query.append(", " + valueV);
      query.append(", " + valueD);
      query.append(", sysdate, user)");
      
    } else if (act == ACT_EDIT) {
        query = new StringBuilder();
        query.append("update " + tableName); 
        query.append(" set mssv_name=upper('" + vName + "')");
        query.append(", mssv_type='" + vType + "'");
        query.append(", mssv_valueN=" + valueN);
        query.append(", mssv_valueV=" + valueV);
        query.append(", mssv_valueD=" + valueD);
        query.append(", mssv_last_modify=sysdate");
        query.append(", mssv_owner=user");
        query.append(" where mssv_id=" + recId);
        
//    } else if (act == ACT_DELETE) {
//      query = new StringBuilder();
//      query.append("delete from " + tableName + " where mssv_id=" + recId); 
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
      if (objSource.equals(cmbType)) {
        if (act == ACT_NEW || act == ACT_EDIT) {
          setEditableVars();
        }  

      } else if (objSource.equals(btnSave)) {
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
