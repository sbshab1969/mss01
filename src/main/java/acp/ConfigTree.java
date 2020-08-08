package acp;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import acp.utils.*;

public class ConfigTree extends MyInternalFrame {
  private static final long serialVersionUID = 1L;
 
  private int act = ACT_NONE;
  private int recId = -1;
  private int resultForm = RES_NONE;

  private static final int FIELDS_COUNT = 10;
  private static final String FIELD_NAME = "field";
  
  private String cfgName;
  private Clob cfgClob;
  private Document doc;

  private JTree tree = new JTree();
  private DefaultTreeModel treeModel;

  private JTable attrTable = new JTable();
  private DataModelAttr dmAttr;

  private JTable fieldTable = new JTable();
  private DataModelField dmField;

  Container cp = getContentPane();
  JSplitPane  splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
  JTabbedPane tabPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

  JPanel pnlTree = new JPanel();
  JPanel pnlAttr = new JPanel();
  JPanel pnlField = new JPanel();
  
  JScrollPane treeView = new JScrollPane();
  JScrollPane attrView = new JScrollPane();
  JScrollPane fieldView = new JScrollPane();
  
  JPanel pnlTreeBut = new JPanel();
  JPanel pnlTreeButRec = new JPanel();
  JButton btnTreeAdd = new JButton(Messages.getString("Button.Add"));
  JButton btnTreeDel = new JButton(Messages.getString("Button.Delete"));

  JPanel pnlAttrBut = new JPanel();
  JPanel pnlAttrButRec = new JPanel();
  JButton btnAttrAdd = new JButton(Messages.getString("Button.Add"));
  JButton btnAttrUpd = new JButton(Messages.getString("Button.Edit"));
  JButton btnAttrDel = new JButton(Messages.getString("Button.Delete"));

  JPanel pnlFieldBut = new JPanel();
  JPanel pnlFieldButRec = new JPanel();
  JPanel pnlFieldButImp = new JPanel();
  JButton btnFieldAdd = new JButton(Messages.getString("Button.Add"));
  JButton btnFieldUpd = new JButton(Messages.getString("Button.Edit"));
  JButton btnFieldDel = new JButton(Messages.getString("Button.Delete"));
  JButton btnFieldImp = new JButton(Messages.getString("Button.Import"));
  JButton btnFieldExp = new JButton(Messages.getString("Button.Export"));

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JButton btnSave = new JButton(Messages.getString("Button.Save"));
  JButton btnCancel = new JButton(Messages.getString("Button.Cancel"));

  public ConfigTree() {
    setMaximizable(true);
    setResizable(true);
    setClosable(true);
    setSize(700, 600);

    splitter.setTopComponent(pnlTree);
    splitter.setBottomComponent(tabPane);
//  splitter.setBottomComponent(pnlAttr);
    tabPane.add(Messages.getString("Title.Attrs"),pnlAttr);
    tabPane.add(Messages.getString("Title.Fields"),pnlField);
//    splitter.setDividerLocation(getHeight() / 3);
    splitter.setOneTouchExpandable(true);
    splitter.setResizeWeight(0.7);
    splitter.setContinuousLayout(true);

    treeView.setViewportView(tree);
    attrView.setViewportView(attrTable);
    attrTable.setFillsViewportHeight(true);
    attrTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    fieldView.setViewportView(fieldTable);
    fieldTable.setFillsViewportHeight(true);
    fieldTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    fieldTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    pnlTree.setLayout(new BorderLayout());
    pnlTreeBut.setLayout(new BorderLayout());
    pnlTreeBut.setBorder(new LineBorder(Color.BLACK));
    pnlTreeBut.add(pnlTreeButRec,BorderLayout.WEST);
    pnlTreeButRec.add(btnTreeAdd);
    pnlTreeButRec.add(btnTreeDel);

    pnlAttr.setLayout(new BorderLayout());
    pnlAttrBut.setLayout(new BorderLayout());
    pnlAttrBut.setBorder(new LineBorder(Color.BLACK));
    pnlAttrBut.add(pnlAttrButRec,BorderLayout.WEST);
    pnlAttrButRec.add(btnAttrAdd);
    pnlAttrButRec.add(btnAttrUpd);
    pnlAttrButRec.add(btnAttrDel);

    pnlField.setLayout(new BorderLayout());
    
    pnlFieldBut.setLayout(new BorderLayout());
    pnlFieldBut.setBorder(new LineBorder(Color.BLACK));
    pnlFieldBut.add(pnlFieldButRec,BorderLayout.WEST);
    pnlFieldBut.add(pnlFieldButImp,BorderLayout.EAST);

    pnlFieldButRec.add(btnFieldAdd);
    pnlFieldButRec.add(btnFieldUpd);
    pnlFieldButRec.add(btnFieldDel);

    pnlFieldButImp.add(btnFieldExp);
    pnlFieldButImp.add(btnFieldImp);

    pnlTree.add(treeView,BorderLayout.CENTER);
    pnlTree.add(pnlTreeBut,BorderLayout.SOUTH);

    pnlAttr.add(attrView,BorderLayout.CENTER);
    pnlAttr.add(pnlAttrBut,BorderLayout.SOUTH);
    
    pnlField.add(fieldView,BorderLayout.CENTER);
    pnlField.add(pnlFieldBut,BorderLayout.SOUTH);

    pnlButtons.add(pnlBtnRecord);
    pnlBtnRecord.setLayout(new GridLayout(1, 2, 50, 0));
    pnlBtnRecord.add(btnSave);
    pnlBtnRecord.add(btnCancel);
    
    cp.add(splitter, BorderLayout.CENTER);
    cp.add(pnlButtons, BorderLayout.SOUTH);
    setToCenter();

    tree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode treeNode = 
            (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (treeNode != null) {
          createTableAttr(treeNode);
          createTableField(treeNode);
        }
      }
    });

    TreeActionListener treeActionListener = new TreeActionListener();
    btnTreeAdd.addActionListener(treeActionListener);
    btnTreeDel.addActionListener(treeActionListener);

    AttrActionListener attrActionListener = new AttrActionListener();
    btnAttrAdd.addActionListener(attrActionListener);
    btnAttrUpd.addActionListener(attrActionListener);
    btnAttrDel.addActionListener(attrActionListener);

    FieldActionListener fieldActionListener = new FieldActionListener();
    btnFieldAdd.addActionListener(fieldActionListener);
    btnFieldUpd.addActionListener(fieldActionListener);
    btnFieldDel.addActionListener(fieldActionListener);
    btnFieldImp.addActionListener(fieldActionListener);
    btnFieldExp.addActionListener(fieldActionListener);

    MyActionListener myActionListener = new MyActionListener();
    btnSave.addActionListener(myActionListener);
    btnCancel.addActionListener(myActionListener);
    
    attrTable.addMouseListener(new MyAttrMouseListener());
    fieldTable.addMouseListener(new MyFieldMouseListener());

    initForm(ACT_NONE,recId);
  }

  public boolean initForm(int act, int recId) {
    boolean res = true;
    this.act = act;
    this.resultForm = RES_NONE;
    this.recId = recId;
    setTitle(Messages.getString("Title.RecordNone"));
    // ------------------------
    if (act == ACT_EDIT) {
      res = queryRecord(recId);
      if (res) {
        res = createDoc();
      }
      if (res) {
        createTree();
      }
    }
    // ------------------------
    if (act == ACT_EDIT) {
      setTitle(Messages.getString("Title.ConfigEdit") + " " + cfgName);
    }
    // ------------------------
    return res;
  }

  private boolean queryRecord(int recId) {
    boolean res = false;
    try {
      String query = createQuery(ACT_GET, recId); 
      Statement stmt = dbConnection.createStatement();
      ResultSet rsq = stmt.executeQuery(query);
      if (rsq.next()) {
        this.recId = recId;
        cfgName = rsq.getString("MSSO_NAME");
        cfgClob = rsq.getClob("MSSO_CONF");
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

  private String createQuery(int act, int recId) {
    StringBuilder query = null;
    if (act == ACT_GET) {
      query = new StringBuilder();
      query.append("select t.msso_name, t.msso_config.getClobval() msso_conf from mss_options t"); 
      query.append(" where msso_id=" + recId);
      
    } else if (act == ACT_EDIT) {
      String strCfg = XmlUtils.xml2string(doc);
      query = new StringBuilder();
      query.append("update mss_options"); 
      query.append(" set msso_config=XMLType('" + strCfg + "')");
      query.append(", msso_dt_modify=SYSDATE");
      query.append(", msso_owner=USER");
      query.append(" where msso_id=" + recId);
    }
//    System.out.println(query);
    if (query == null) {
      return null;
    } else {
      return query.toString();
    }  
  }

  private boolean createDoc() {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = null;
    try {
      builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      DialogUtils.errorPrint(e);
      return false;
    }
    InputSource is = new InputSource();
    try {
      Reader charStream = cfgClob.getCharacterStream();
      is.setCharacterStream(charStream);
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
      return false;
    }
    try {
      doc = builder.parse(is);
    } catch (SAXException | IOException e) {
      DialogUtils.errorPrint(e);
      return false;
    }
    doc.setXmlStandalone(true);
    doc.normalizeDocument();
//  doc.getDocumentElement().normalize();
    return true;
  }

  private void createTree() {
    Element root = doc.getDocumentElement();
    NodeList childs = root.getChildNodes();
    for (int i = 0; i < childs.getLength(); i++) {
      Node cfg = childs.item(i);
      if (XmlUtils.isValidNode(cfg)) {
        String nodeName = cfg.getNodeName().trim();
        if (nodeName.equalsIgnoreCase("ats") || 
            nodeName.equalsIgnoreCase("eml.mailer") || 
            nodeName.equalsIgnoreCase("sverka.ats")) {

          DefaultMutableTreeNode top = new DefaultMutableTreeNode(new NodeInfo(cfg));
          treeModel = new DefaultTreeModel(top);
          tree.setModel(treeModel);

          tree.removeAll();
          NodeList cfgChilds = cfg.getChildNodes();
          for (int j = 0; j < cfgChilds.getLength(); j++) {
            Node childNode = cfgChilds.item(j);
            putNode(top,childNode);
          }
          tree.setSelectionRow(0);
          tree.expandRow(0);
//          tree.setSelectionPath(new TreePath(top.getPath()));
//          tree.expandPath(new TreePath(top.getPath()));
        }
      }
    }
  }

  private void putNode(DefaultMutableTreeNode treeNode, Node newNode) {
    if (XmlUtils.isValidNode(newNode)) {
      DefaultMutableTreeNode item = new DefaultMutableTreeNode(new NodeInfo(newNode));
      treeNode.add(item);
      NodeList subNodes = newNode.getChildNodes();
      for (int i = 0; i < subNodes.getLength(); i++) {
        Node subnode = subNodes.item(i);
//        if (!subnode.getNodeName().equals("field")) {
          putNode(item, subnode);
//        }  
      }
    }
  }
  
  private Node getNode(DefaultMutableTreeNode treeNode) {
    NodeInfo nodeInfo = (NodeInfo) treeNode.getUserObject();
    Node node = nodeInfo.getNode();
    return node;
  }

  private Node getCurrentNode() {
    DefaultMutableTreeNode currentTreeNode = 
        (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
    Node node = getNode(currentTreeNode);
    return node;
  }

  private void createTableAttr(DefaultMutableTreeNode treeNode) {
    Node node = getNode(treeNode);
    dmAttr = new DataModelAttr(node);
    attrTable.setModel(dmAttr);
    if (dmAttr.getRowCount()>0) {
      attrTable.setRowSelectionInterval(0, 0);
    }
  }
  
  private void createTableField(DefaultMutableTreeNode treeNode) {
    Node node = getNode(treeNode);
    dmField = new DataModelField(node);
    fieldTable.setModel(dmField);
    if (dmField.getColumnCount()<FIELDS_COUNT) {
      fieldTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    } else {
      fieldTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }
    if (dmField.getRowCount()>0) {
      fieldTable.setRowSelectionInterval(0, 0);
    }
  }

  private void addTreeNode(int act) {
    DefaultMutableTreeNode currentTreeNode = 
        (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
    Node node = getNode(currentTreeNode);
    // ----------------------------------------------
    ConfigChild cfgChild = new ConfigChild(node);
    // ----------------------------------------------
    boolean resInit = true;
    resInit = cfgChild.initForm(act);
    if (resInit) {
      desktop.add(cfgChild);
      try {
        cfgChild.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      cfgChild.showModal(true);
      // -----------------------
      int resForm = cfgChild.getResultForm();
      if (resForm == RES_OK) {
        Node newNode = cfgChild.getNewNode();
        DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(new NodeInfo(newNode));
        currentTreeNode.add(newTreeNode);
        treeModel.reload(currentTreeNode);
//        tree.setSelectionPath(new TreePath(newTreeNode.getPath()));
      }  
    }
    cfgChild = null;
    //---------------------------
    int currentRow = tree.getSelectionModel().getMinSelectionRow();
    if (currentRow < 0 ) {
      tree.setSelectionRow(0);
    }  
    dmField.fillTable();
    fieldTable.updateUI();
  }

  private void deleteTreeNode() {
    DefaultMutableTreeNode currentTreeNode = 
        (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
    Node node = getNode(currentTreeNode);
    
    Node parentNode = node.getParentNode();
    if (parentNode == null) {
      DialogUtils.errorMsg(Messages.getString("Message.RootNode"));
      return;
    }
    
    DefaultMutableTreeNode parentTreeNode = (DefaultMutableTreeNode) (currentTreeNode.getParent());
    if (parentTreeNode == null) {
      DialogUtils.errorMsg(Messages.getString("Message.RootNode"));
      return;
    }
    
    if (DialogUtils.confirmDialog(
        Messages.getString("Message.DeleteNode"),
        Messages.getString("Title.RecordDelete"), 1) == 0) {
      //---------------------------
      parentNode.removeChild(node);  // XML
      treeModel.removeNodeFromParent(currentTreeNode);  // Tree
      //---------------------------
      treeModel.reload(parentTreeNode);
//      tree.setSelectionRow(0);
      tree.setSelectionPath(new TreePath(parentTreeNode.getPath()));
    }
  }

  private void refreshTreeNode() {
    DefaultMutableTreeNode currentTreeNode = 
        (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
    NodeInfo nodeInfo = (NodeInfo) currentTreeNode.getUserObject();
    nodeInfo.fillTitle();
    treeModel.reload(currentTreeNode);
    int currentRow = tree.getSelectionModel().getMinSelectionRow();
    if (currentRow<0) {
      currentRow = 0;
    }
    tree.setSelectionRow(currentRow);
  }

  private class TreeActionListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object objSource = ae.getSource();
      if (objSource.equals(btnTreeAdd)) {
        Node node = getCurrentNode();
        if (XmlUtils.getCountNewNodes(node) > 0) {
          addTreeNode(ACT_NEW);
        } else {
          DialogUtils.errorMsg(Messages.getString("Message.NoNewChild"));
        }

      } else if (objSource.equals(btnTreeDel)) {
        deleteTreeNode();
      }
    }
  }

  private boolean testAttr() {
    int rows = dmAttr.getRowCount();
    if (rows == 0) {
      DialogUtils.errorMsg(Messages.getString("Message.NoAttr"));
      return false;
    }
    int row = attrTable.getSelectedRow();
    if (row < 0) {
      DialogUtils.errorMsg(Messages.getString("Message.NoSelectAttr"));
      return false;
    }
    return true;
  }
  
  private void editAttr(int act, Node vNode) {
    int row = -1;
    Node vAttr = null;
    if (act == ACT_EDIT) {
      if (testAttr() == false) {
        return;
      }
      row = attrTable.getSelectedRow();
      vAttr = dmAttr.getAttr(row);
    }
    // ----------------------------------------------
    ConfigAttr cfgAttr = new ConfigAttr(vNode,vAttr);
    // ----------------------------------------------
    boolean resInit = true;
    resInit = cfgAttr.initForm(act);
    if (resInit) {
      desktop.add(cfgAttr);
      try {
        cfgAttr.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      cfgAttr.showModal(true);
      // -----------------------
      int resForm = cfgAttr.getResultForm();
      if (resForm == RES_OK) {
        dmAttr.refreshTable();    // Table
        refreshTreeNode(); // Tree
        if (row >= 0) {
          attrTable.setRowSelectionInterval(row, row);
        } else {
          attrTable.setRowSelectionInterval(0, 0);
        }
      }  
    }
    cfgAttr = null;
  }

  private void deleteAttr() {
    if (testAttr() == false) {
      return;
    }
    int row = attrTable.getSelectedRow();
    if (DialogUtils.confirmDialog(
        Messages.getString("Message.DeleteRecord"),
        Messages.getString("Title.RecordDelete"), 1) == 0) {
      //--------------------
      dmAttr.deleteRow(row);  // Attr
      XmlUtils.deleteAttr(dmAttr.getNode(), row);  // XML
      refreshTreeNode(); // Tree
      //--------------------
      if (dmAttr.getRowCount()>0) {
        attrTable.setRowSelectionInterval(0, 0);
      }
    }
  }

  private boolean testField() {
    int rows = dmField.getRowCount();
    if (rows == 0) {
      DialogUtils.errorMsg(Messages.getString("Message.NoField"));
      return false;
    }
    int row = fieldTable.getSelectedRow();
    if (row < 0) {
      DialogUtils.errorMsg(Messages.getString("Message.NoSelectField"));
      return false;
    }
    return true;
  }

  private void editFields(int act) {
    int row = -1;
    if (act == ACT_NEW) {
      String nodeName = getCurrentNode().getNodeName();
      if (XmlUtils.existValidNode(nodeName, FIELD_NAME) == false) {
        DialogUtils.errorMsg(Messages.getString("Message.NoNewField"));
        return;
      }
    } else if (act == ACT_EDIT) {
      if (testField() == false) {
        return;
      }
      row = fieldTable.getSelectedRow();
    }
    // ----------------------------------------------
    ConfigFields cfgFields = new ConfigFields(dmField);
    // ----------------------------------------------
    boolean resInit = true;
    resInit = cfgFields.initForm(act,row);
    if (resInit) {
      desktop.add(cfgFields);
      try {
        cfgFields.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      cfgFields.showModal(true);
      // -----------------------
      int resForm = cfgFields.getResultForm();
      if (resForm == RES_OK) {
        dmField.fillTable();    // Table
        DefaultMutableTreeNode currentTreeNode = 
            (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (act == ACT_NEW) {
          Node newNode = cfgFields.getCurrentNode();
          DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(new NodeInfo(newNode));
          currentTreeNode.add(newTreeNode);
          treeModel.reload(currentTreeNode);
          tree.setSelectionPath(new TreePath(currentTreeNode.getPath()));
          row = dmField.getRowCount() - 1;
          fieldTable.setRowSelectionInterval(row, row);
//          fieldTable.updateUI();
          
        } else if (act == ACT_EDIT) {
          DefaultMutableTreeNode childTreeNode = (DefaultMutableTreeNode) 
              currentTreeNode.getChildAt(row);
          NodeInfo nodeInfo = (NodeInfo) childTreeNode.getUserObject();
          nodeInfo.fillTitle();
          refreshTreeNode(); // Tree
          if (row >= 0) {
            fieldTable.setRowSelectionInterval(row, row);
          } else {
            fieldTable.setRowSelectionInterval(0, 0);
          }
        }
      }  
    }
    cfgFields = null;
  }

  private void deleteField() {
    if (testField() == false) {
      return;
    }
    int row = fieldTable.getSelectedRow();
    if (DialogUtils.confirmDialog(
        Messages.getString("Message.DeleteRecord"),
        Messages.getString("Title.RecordDelete"), 1) == 0) {
      //--------------------
      dmField.deleteRow(row);  // Fields
      XmlUtils.deleteChild(dmField.getNode(), row);  // XML
      //--------------------
      DefaultMutableTreeNode currentTreeNode = 
          (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
      currentTreeNode.remove(row); // Tree      
      refreshTreeNode(); // Tree
      //--------------------
      if (dmField.getRowCount()>0) {
        fieldTable.setRowSelectionInterval(0, 0);
      }
    }
  }

  private void exportFields() {
    if (testField() == false) {
      return;
    }
    JFileChooser fch = new JFileChooser();
//    if (fch.showSaveDialog(btnFieldExp) == JFileChooser.APPROVE_OPTION) {
    if (fch.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
      File vDir = fch.getSelectedFile();
      try {
        FileWriter fw = new FileWriter(vDir.getAbsolutePath());
        int rows = dmField.getRowCount();
        int cols = dmField.getColumnCount();
        for (int i = 0; i < rows; i++) {
          String[] newRow = dmField.getRowData(i); 
          StringBuilder sbLine = new StringBuilder();
          for (int j = 0; j < cols; j++) {
            if (newRow[j] != null) {
              sbLine.append(newRow[j]);
            }  
            if (j < cols-1) {
              sbLine.append(";");
            }  
          }
          sbLine.append("\n");
          fw.write(sbLine.toString());
        }  
        fw.close();
        DialogUtils.infoDialog(Messages.getString("Message.ExportOK"));
      } catch (IOException e) {
        DialogUtils.errorPrint(e);
      }
    }
  }

  private String[] mySplit(String line) {
    line += "&";
    String[] arrLine = line.split(";");
    int cnt = arrLine.length;
    String strLast = arrLine[cnt-1];
    if (strLast.length() > 1) {
      arrLine[cnt-1] = strLast.substring(0, strLast.length()-1);
    } else {
      arrLine[cnt-1] = "";
    }
//    System.out.println(Arrays.deepToString(arrLine));
    return arrLine;
  }
  
  private boolean testImportFile(File impFile, int cntFields) {
    try {
      FileReader fr = new FileReader(impFile.getAbsolutePath());
      BufferedReader br = new BufferedReader(fr);
      String line;
      String[] arrFields;
      boolean isErr = false;
      while ((line = br.readLine()) != null) {
        arrFields = mySplit(line);
        if (arrFields.length != cntFields) {
          DialogUtils.errorMsg(Messages.getString("Message.CountFieldsError"));
          isErr = true;
          break;
        }
      }
      fr.close();
      if (isErr) {
        return false;
      }
    } catch (IOException e) {
      DialogUtils.errorPrint(e);
      return false;
    }
    return true;
  }

  private void fillAttrs(Node node, String[] attrValues) {
    Element nodeElem = (Element) node;
    int cols = dmField.getColumnCount();
    ArrayList<String> validAttrs = dmField.getValidAttrs();
    for (int i = 0; i < cols; i++) {
      String attrName = validAttrs.get(i);
      String attrValue = attrValues[i]; 
      if (!attrValue.equals("")) {
        nodeElem.setAttribute(attrName, attrValue);
      } else {
        nodeElem.removeAttribute(attrName);
      }
    }
  }

  private void loadFile(File impFile, int cntFields) {
    DefaultMutableTreeNode currentTreeNode = 
        (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
    Node currentNode = getNode(currentTreeNode);
    Document docum = currentNode.getOwnerDocument(); 
    try {
      FileReader fr = new FileReader(impFile.getAbsolutePath());
      BufferedReader br = new BufferedReader(fr);
      String line;
      String[] arrFields;
      while ((line = br.readLine()) != null) {
        // XML --------------------
        Element item = docum.createElement(FIELD_NAME);
        Node newNode = currentNode.appendChild(item);
        arrFields = mySplit(line);
        fillAttrs(newNode, arrFields);
        // Tree -----------------------
        DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(new NodeInfo(newNode));
        currentTreeNode.add(newTreeNode);
      }
      fr.close();
    } catch (IOException e) {
      DialogUtils.errorPrint(e);
      return;
    }
    treeModel.reload(currentTreeNode);
    TreePath treePath = new TreePath(currentTreeNode.getPath());
    tree.setSelectionPath(treePath);
    tree.expandPath(treePath);
    
    dmField.fillTable();
    fieldTable.updateUI();
  }

  private void importFields() {
    String nodeName = getCurrentNode().getNodeName();
    if (XmlUtils.existValidNode(nodeName, FIELD_NAME) == false) {
      DialogUtils.errorMsg(Messages.getString("Message.NoNewField"));
      return;
    }
    int cntFields = dmField.getColumnCount();
    JFileChooser fch = new JFileChooser();
    fch.setFileFilter(new FileNameExtensionFilter("Text files", "csv", "txt"));
    fch.setFileSelectionMode(JFileChooser.FILES_ONLY);
    if (fch.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
      File fileImport = fch.getSelectedFile();
      if (testImportFile(fileImport,cntFields)) {
        loadFile(fileImport,cntFields);
      }
    }  
  }

  private class AttrActionListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object objSource = ae.getSource();
      if (objSource.equals(btnAttrAdd)) {
        Node node = dmAttr.getNode();
        if (XmlUtils.getCountNewAttrs(node) > 0) {
          editAttr(ACT_NEW, node);
        } else {
          DialogUtils.errorMsg(Messages.getString("Message.NoNewAttr"));
        }
        
      } else if (objSource.equals(btnAttrUpd)) {
        editAttr(ACT_EDIT, dmAttr.getNode());
        
      } else if (objSource.equals(btnAttrDel)) {
        deleteAttr();
      }
    }
  }

  private class FieldActionListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object objSource = ae.getSource();
      if (objSource.equals(btnFieldAdd)) {
        editFields(ACT_NEW);
        
      } else if (objSource.equals(btnFieldUpd)) {
        editFields(ACT_EDIT);
        
      } else if (objSource.equals(btnFieldDel)) {
        deleteField();

      } else if (objSource.equals(btnFieldExp)) {
        exportFields();

      } else if (objSource.equals(btnFieldImp)) {
        importFields();
      }
    }
  }

  private class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object objSource = ae.getSource();
      if (objSource.equals(btnSave)) {
        if (act == ACT_EDIT) {
          String query = createQuery(act, recId);
          if (query != null) {
            int res = -1;
            res = DbUtils.executeUpdate(dbConnection, query);
            if (res>=0) {
              dispose();
              resultForm = RES_OK;
            }  
          } else {
            DialogUtils.errorMsg(Messages.getString("Message.EmptySelect"));
          }
        }  

      } else if (objSource.equals(btnCancel)) {
        dispose();
        resultForm = RES_CANCEL;
      }
    }
  }

  private class MyAttrMouseListener extends MouseAdapter {
    public void mouseClicked(MouseEvent e) {
      if (e.getClickCount() == 2) {
        editAttr(ACT_EDIT, dmAttr.getNode());
      }
    }
  }

  private class MyFieldMouseListener extends MouseAdapter {
    public void mouseClicked(MouseEvent e) {
      if (e.getClickCount() == 2) {
        editFields(ACT_EDIT);
      }
    }
  }

  public int getResultForm() {
    return resultForm;
  }

}
