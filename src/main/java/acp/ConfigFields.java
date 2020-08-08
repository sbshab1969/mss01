package acp;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.LineBorder;

import org.w3c.dom.*;

import acp.utils.*;

public class ConfigFields extends MyInternalFrame {
  private static final long serialVersionUID = 1L;
  
  private static final int FIELDS_COUNT = 17;
  private static final String FIELD_NAME = "field";

  private DataModelField dmField;
  private int row = -1;
  private int cols = 0;
  
  ArrayList<String> validAttrs;
  String[] headers;
  String[] rowData;

  private Node parentNode;
  private Node currentNode;

  private int act = ACT_NONE;
  private int resultForm = RES_NONE;

  JScrollPane spFields;
  JPanel pnlData = new JPanel();
  ArrayList<JLabel> lblList = new ArrayList<JLabel>();  
  ArrayList<JTextField> textList = new ArrayList<JTextField>();  
  ArrayList<String> recOldValue = new ArrayList<String>();  
  ArrayList<String> recValue = new ArrayList<String>();  

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JButton btnSave = new JButton(Messages.getString("Button.Save"));
  JButton btnCancel = new JButton(Messages.getString("Button.Cancel"));

  public ConfigFields(DataModelField dmFld) {
//    setResizable(true);
    setSize(650, 600);

    this.dmField = dmFld;
    parentNode = dmField.getNode();
    
    Container cp = getContentPane();
    pnlData.setLayout(new SpringLayout());
    pnlData.setBorder(new LineBorder(Color.BLACK));
    spFields = new JScrollPane(pnlData);

    validAttrs = dmField.getValidAttrs();
    headers = dmField.getHeader();
    cols = headers.length;
    for (int i = 0; i < cols; i++) {
      // ------------------
      String lblName = headers[i];
      JLabel lbl = new JLabel(lblName, JLabel.TRAILING);
      lblList.add(lbl);
      pnlData.add(lbl);
      // ------------------
      JTextField edt = new JTextField(30);
      textList.add(edt);
      pnlData.add(edt);
      // ------------------
      lbl.setLabelFor(edt);
    }
    SpringUtilities.makeCompactGrid(pnlData,cols,2,10,10,10,10);

    pnlButtons.add(pnlBtnRecord);
    pnlBtnRecord.setLayout(new GridLayout(1, 2, 20, 0));
    pnlBtnRecord.add(btnSave);
    pnlBtnRecord.add(btnCancel);

//    cp.add(pnlData, BorderLayout.CENTER);
    cp.add(spFields, BorderLayout.CENTER);
    cp.add(pnlButtons, BorderLayout.SOUTH);

    if (cols <= FIELDS_COUNT) {
      pack();
    }  
    setToCenter();

    MyActionListener myActionListener = new MyActionListener();
    btnSave.addActionListener(myActionListener);
    btnCancel.addActionListener(myActionListener);

    // Обязательно после listners
    initForm(ACT_NONE,row);
  }

  public boolean initForm(int act, int rowN) {
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
    } else {
      setTitle(Messages.getString("Title.RecordNone"));
    }
    // ------------------------
    // Значения полей
    // ------------------------
    clearRecord();
    if (act == ACT_EDIT || act == ACT_DELETE) {
      this.row = rowN;
      if (row>=0) {
        rowData = dmField.getRowData(row);
        for (int i = 0; i < cols; i++) {
          textList.get(i).setText(rowData[i]);
        }
      }  
    }
    // ------------------------
    setEditableRecord(act);
    // ------------------------
    return res;
  }

  private void setEditableRecord(int act) {
    if (act == ACT_NEW || act == ACT_EDIT) {
      setEditableFields(true);
      btnSave.setEnabled(true);
    } else if (act == ACT_DELETE) {
      setEditableFields(false);
      btnSave.setEnabled(true);
    } else {
      setEditableFields(false);
      btnSave.setEnabled(false);
    }
  }
  
  private void setEditableFields(boolean flag) {
    for (int i = 0; i < cols; i++) {
      textList.get(i).setEditable(flag);
    }
  }

  private void clearRecord() {
    this.row = -1;
    for (int i = 0; i < cols; i++) {
      textList.get(i).setText("");
    }
  }

  private void fillAttrs() {
    for (int i = 0; i < cols; i++) {
      Element currentNodeElem = (Element) currentNode;
      String attrName = validAttrs.get(i);
      String attrValue = textList.get(i).getText(); 
      if (!attrValue.equals("")) {
        currentNodeElem.setAttribute(attrName, attrValue);
      } else {
        currentNodeElem.removeAttribute(attrName);
      }
    }
  }

  private class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object objSource = ae.getSource();
      if (objSource.equals(btnSave)) {
        if (act == ACT_NEW) {
          Document docum = parentNode.getOwnerDocument(); 
          Element item = docum.createElement(FIELD_NAME);
          currentNode = parentNode.appendChild(item);
        } else if (act == ACT_EDIT) {
          currentNode = XmlUtils.getChild(parentNode, row);
        }  
        fillAttrs();
        dispose();
        resultForm = RES_OK;

      } else if (objSource.equals(btnCancel)) {
        dispose();
        resultForm = RES_CANCEL;
      }
    }
  }

  public int getResultForm() {
    return resultForm;
  }

  public Node getCurrentNode() {
    return currentNode;
  }

}
