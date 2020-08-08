package acp;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.LineBorder;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import acp.utils.*;

public class ConfigChild extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  private int act = ACT_NONE;
  private int resultForm = RES_NONE;

  private Node parentNode;
  private Node newNode;

  JPanel pnlData = new JPanel();
  JLabel lblName = new JLabel(Messages.getString("Column.Name"), JLabel.TRAILING);
//  JComboBox<String> cmbName = new JComboBox<String>();
  DefaultListModel<String> listModel = new DefaultListModel<String>();
  JList<String> lstName;
  JScrollPane listScrollPane;
  
  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JButton btnSave = new JButton(Messages.getString("Button.Save"));
  JButton btnCancel = new JButton(Messages.getString("Button.Cancel"));

  public ConfigChild(Node vParent) {
//    setResizable(true);
    parentNode = vParent;
    Container cp = getContentPane();
    
    if (parentNode != null) {
      ArrayList<String> newNodes = XmlUtils.getNewNodes(parentNode);
      for (String key : newNodes) {
        String itemName = FieldConfig.getString(key);
//        cmbName.addItem(itemName);
        listModel.addElement(itemName);
      }
    }  
//  cmbName.setMaximumRowCount(3);
    lstName = new JList<String>(listModel);
    lstName.setLayoutOrientation(JList.VERTICAL);
    lstName.setVisibleRowCount(5);
    listScrollPane = new JScrollPane(lstName);
    if (listModel.getSize()>0) {
      lstName.setSelectedIndex(0);
    }

    pnlData.setLayout(new SpringLayout());
    pnlData.setBorder(new LineBorder(Color.BLACK));
    pnlData.add(lblName);
//    pnlData.add(cmbName);
    pnlData.add(listScrollPane);

    SpringUtilities.makeCompactGrid(pnlData,1,2,10,10,10,10);

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
    } else {
      setTitle(Messages.getString("Title.RecordNone"));
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
//      cmbName.setEnabled(true);
      lstName.setEnabled(true);
      btnSave.setEnabled(true);
    } else {
//      cmbName.setEnabled(false);
      lstName.setEnabled(false);
      btnSave.setEnabled(false);
    }
  }

//  private void clearRecord() {
//    cmbName.setSelectedIndex(-1);
//    lstName.setSelectedIndex(-1);
//  }

  private boolean validateRecord() {
//    if (cmbName.getSelectedIndex() == -1) {
    if (lstName.getSelectedIndex() == -1) {
      DialogUtils.errorMsg(Messages.getString("Message.IsEmpty")+": " + Messages.getString("Column.Name"));
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
            ArrayList<String> newNodes = XmlUtils.getNewNodes(parentNode);
//            String newNodeName = newNodes.get(cmbName.getSelectedIndex());
            String newNodeName = newNodes.get(lstName.getSelectedIndex());
            //---------------------------
            Document docum = parentNode.getOwnerDocument(); 
            Element item = docum.createElement(newNodeName);
            newNode = parentNode.appendChild(item);
            //---------------------------
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

  public Node getNewNode() {
    return newNode;
  }

}
