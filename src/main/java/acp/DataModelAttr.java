package acp;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import acp.utils.FieldConfig;
import acp.utils.Messages;

class DataModelAttr extends AbstractTableModel {
  private static final long serialVersionUID = 1L;

  private Node node;

  private final String[] HEADERS = { 
      Messages.getString("Column.Name")
     ,Messages.getString("Column.Value")
     };

  private final int      COLS    = 2;
  private int            rows    = 0;
  ArrayList<String[]>    list = new ArrayList<>();
//  ArrayList<String[]>    list = new ArrayList<String[]>();


  DataModelAttr(Node vNode) {
    node = vNode;
    refreshTable();
  }
  
  public void refreshTable() {
    rows = 0;
    list.clear();
    // ----------
    NamedNodeMap attrs = node.getAttributes();
    for (int i = 0; i < attrs.getLength(); i++) {
      Node attr = attrs.item(i);
      String key = node.getNodeName().trim() + "." + attr.getNodeName().trim();
      String attrName = FieldConfig.getString(key);
      String attrValue = attr.getNodeValue();
      addRow(attrName,attrValue);
    }
  }

  public Node getNode() {
    return node;
  }

  public Node getAttr(int n) {
    NamedNodeMap attrs = node.getAttributes();
    Node attr = attrs.item(n);
    return attr;
  }

  public String getColumnName(int col) {
    return HEADERS[col];
  }

  public int getRowCount() {
    return rows;
  }

  public int getColumnCount() {
    return COLS;
  }

  public Object getValueAt(int row, int col) {
    return list.get(row)[col];
  }

  public void setValueAt(Object val, int row, int col) {
    String strVal = val.toString();
    list.get(row)[col] = strVal;
    fireTableDataChanged();
  }

  public boolean isCellEditable(int row, int col) {
//    return (col != 0);
    return false;
  }

  public Class<?> getColumnClass(int col) {
    return String.class;
  }

  public void addRow(String name, String value) {
    String[] newRow = new String[COLS];
    newRow[0] = name;
    newRow[1] = value;
    // ----------
    list.add(newRow);
    rows++;
  }

  public void deleteRow(int row) {
    list.remove(row);
    rows--;
    fireTableDataChanged();
  }

}
