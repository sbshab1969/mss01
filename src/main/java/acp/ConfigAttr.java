package acp;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.LineBorder;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

import acp.utils.*;

public class ConfigAttr extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  private int act = ACT_NONE;
  private int resultForm = RES_NONE;

  private Node node;
  private Node attr;

  private String nodeName;
  private String attrName;

  JPanel pnlData = new JPanel();
  JLabel lblName = new JLabel(Messages.getString("Column.Name"), JLabel.TRAILING);
  JComboBox<String> cmbName = new JComboBox<String>();
  JLabel lblValue = new JLabel(Messages.getString("Column.Value"), JLabel.TRAILING);
  JTextField txtValue = new JTextField(30);

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JButton btnSave = new JButton(Messages.getString("Button.Save"));
  JButton btnCancel = new JButton(Messages.getString("Button.Cancel"));

  public ConfigAttr(Node vNode, Node vAttr) {
    node = vNode;
    attr = vAttr;
    if (node != null) {
      nodeName = node.getNodeName();
    }
    if (attr != null) {
      attrName = attr.getNodeName();
    }
    Container cp = getContentPane();
    cmbName.setMaximumRowCount(4);

    pnlData.setLayout(new SpringLayout());
    pnlData.setBorder(new LineBorder(Color.BLACK));
    pnlData.add(lblName);
    pnlData.add(cmbName);
    lblName.setLabelFor(cmbName);
    
    pnlData.add(lblValue);
    pnlData.add(txtValue);
    lblValue.setLabelFor(txtValue);
    SpringUtilities.makeCompactGrid(pnlData,2,2,10,10,10,10);

    pnlButtons.add(pnlBtnRecord);
    pnlBtnRecord.setLayout(new GridLayout(1, 2, 20, 0));
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
    initForm(ACT_NONE);
  }

  public boolean initForm(int act) {
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
    if (act == ACT_NEW) {
      if (node != null) {
        ArrayList<String> newAttrs = XmlUtils.getNewAttrs(node);
        for (String newAttr : newAttrs) {
          String key = nodeName + "." + newAttr;
          String itemName = FieldConfig.getString(key);
          cmbName.addItem(itemName);
        }
      }
    } else if (act == ACT_EDIT) {
      if (attr != null) {
        ArrayList<String> oldAttrs = XmlUtils.getOldAttrs(node);
        for (String oldAttr : oldAttrs) {
          String key = nodeName + "." + oldAttr;
          String itemName = FieldConfig.getString(key);
          cmbName.addItem(itemName);
        }
        String key = nodeName + "." + attrName;
        String itemName = FieldConfig.getString(key);
        cmbName.setSelectedItem(itemName);
        txtValue.setText(attr.getNodeValue());
      }
    }
    // ------------------------
    // Доступность полей
    // ------------------------
    setEditableRecord(act);
    // ------------------------
    return res;
  }

  private void setEditableRecord(int act) {
    if (act == ACT_NEW) {
      cmbName.setEnabled(true);
      txtValue.setEditable(true);
      btnSave.setEnabled(true);
    } else if (act == ACT_EDIT) {
      cmbName.setEnabled(false);
      txtValue.setEditable(true);
      btnSave.setEnabled(true);
    } else {
      cmbName.setEnabled(false);
      txtValue.setEditable(false);
      btnSave.setEnabled(false);
    }
  }

  private void clearRecord() {
    cmbName.setSelectedIndex(-1);
    txtValue.setText("");
  }

  private boolean validateRecord() {
    if (cmbName.getSelectedIndex() == -1) {
      DialogUtils.errorMsg(Messages.getString("Message.IsEmpty")+": " + Messages.getString("Column.Name"));
      return false;
    }
    if (txtValue.getText().equals("")) {
      DialogUtils.errorMsg(Messages.getString("Message.IsEmpty")+": " + Messages.getString("Column.Value"));
      return false;
    }
    return true;
  }

  private class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object objSource = ae.getSource();
      if (objSource.equals(btnSave)) {
        if (act == ACT_NEW) {
          boolean resValidate = validateRecord();  
          if (resValidate) {
            ArrayList<String> newAttrs = XmlUtils.getNewAttrs(node);
            String newAttrName = newAttrs.get(cmbName.getSelectedIndex());
            String newAttrValue = txtValue.getText();
            //---------------------------
            // Правильны оба варианта
            // Сейчас работает первый (Element)
            //---------------------------
            if (node instanceof Element) {
              Element nodeElem = (Element) node;
              nodeElem.setAttribute(newAttrName, newAttrValue);
           } else {
              Document docum = node.getOwnerDocument(); 
              Attr newAttr = docum.createAttribute(newAttrName);
//              newAttr.setNodeValue(newAttrValue);
              newAttr.setValue(newAttrValue);
              NamedNodeMap nodeMap = node.getAttributes(); 
              nodeMap.setNamedItem(newAttr);
            }
            //---------------------------
            dispose();
            resultForm = RES_OK;
          }
          
        } else if (act == ACT_EDIT) {  
          boolean resValidate = validateRecord();  
          if (resValidate) {
            String textVal = txtValue.getText();
            // ------------------------
            attr.setNodeValue(textVal);  // !!!!!!!
            // ------------------------
            dispose();
            resultForm = RES_OK;
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

  public Node getAttr() {
    return attr;
  }

}
