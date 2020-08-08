package acp;

import java.awt.event.*;
import java.util.*;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import acp.utils.*;

public class MainMenu implements ActionListener {

  protected JMenuBar createMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = null;
    JMenuItem menuItem = null;
    
    // -----------------------------------------
    // 1. Пункт меню Протоколы
    // -----------------------------------------
    menu = new JMenu(Messages.getString("Menu.File"));
    menuBar.add(menu);
    
    // Пункт меню Протоколы -> Загруженные файлы
    menuItem = new JMenuItem(Messages.getString("Menu.File.LogUpload"));
    menuItem.setActionCommand("upload");
    menuItem.addActionListener(this);
    menu.add(menuItem);

    // Пункт меню Протоколы -> Другие протоколы
    menuItem = new JMenuItem(Messages.getString("Menu.File.LogOther"));
    menuItem.setActionCommand("otherlogs");
    menuItem.addActionListener(this);
    menu.add(menuItem);

    menu.add(new JSeparator());

    menuItem = new JMenuItem(Messages.getString("Menu.File.Exit"));
    menuItem.setActionCommand("quit");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
      KeyEvent.VK_F4, InputEvent.CTRL_MASK));
    menuItem.addActionListener(this);
    menu.add(menuItem);

    // -----------------------------------------
    // 2. Пункт меню Справочники
    // -----------------------------------------
    menu = new JMenu(Messages.getString("Menu.Refs"));
    menuBar.add(menu);

    // Пункт меню Справочники -> Константы
    menuItem = new JMenuItem(Messages.getString("Menu.Refs.Consts"));
    menuItem.setActionCommand("const");
    menuItem.addActionListener(this);
    menu.add(menuItem);
    
    // Пункт меню Справочники -> Переменные
    menuItem = new JMenuItem(Messages.getString("Menu.Refs.Variables"));
    menuItem.setActionCommand("vars");
    menuItem.addActionListener(this);
    menu.add(menuItem);

    menu.add(new JSeparator());

    // Пункт меню Справочники -> Источники
    menuItem = new JMenuItem(Messages.getString("Menu.Refs.Sources"));
    menuItem.setActionCommand("src");
    menuItem.addActionListener(this);
    menu.add(menuItem);
    
    // Пункт меню Справочники -> Конфигурации источников
    menuItem = new JMenuItem(Messages.getString("Menu.Refs.Configs"));
    menuItem.setActionCommand("cfg");
    menuItem.addActionListener(this);
    menu.add(menuItem);
    
    menu.add(new JSeparator());
    
    // Пункт меню Справочники -> Редактирование таблиц
    JMenu submenu = new JMenu(Messages.getString("Menu.Refs.Tables"));

    // Пункт меню Справочники -> Редактирование таблиц -> Местные номера
    menuItem = new JMenuItem(Messages.getString("Menu.Refs.Tables.LocalNmbs"));
    menuItem.setActionCommand("table_lclnmbs");
    menuItem.addActionListener(this);
    submenu.add(menuItem);

    // Пункт меню Справочники -> Редактирование таблиц -> SID-pref
    menuItem = new JMenuItem(Messages.getString("Menu.Refs.Tables.SIDPref"));
    menuItem.setActionCommand("table_sidpref");
    menuItem.addActionListener(this);
    submenu.add(menuItem);

    // Пункт меню Справочники -> Редактирование таблиц -> trace-SOP
    menuItem = new JMenuItem(Messages.getString("Menu.Refs.Tables.TraceSOP"));
    menuItem.setActionCommand("table_tracesop");
    menuItem.addActionListener(this);
    submenu.add(menuItem);
    menu.add(submenu);

    // Пункт меню Справочники -> Редактирование таблиц -> Input
    menuItem = new JMenuItem(Messages.getString("Menu.Refs.Tables.Input"));
    menuItem.setActionCommand("table_input");
    menuItem.addActionListener(this);
    submenu.add(menuItem);
    menu.add(submenu);

    // Пункт меню Справочники -> Редактирование таблиц -> Output
    menuItem = new JMenuItem(Messages.getString("Menu.Refs.Tables.Output"));
    menuItem.setActionCommand("table_output");
    menuItem.addActionListener(this);
    submenu.add(menuItem);
    menu.add(submenu);

    // -----------------------------------------
    // 3. Пункт меню Помощь
    // -----------------------------------------
    // menuBar.add(Box.createHorizontalGlue());  // Замена setHelpMenu(menu)
    menu = new JMenu(Messages.getString("Menu.Help"));
    // menuBar.setHelpMenu(menu);  // Does not work!!!!
    menuBar.add(menu);  // Обычный способ добавления item
    
    // Пункт меню Помощь -> О программе
    menuItem = new JMenuItem(Messages.getString("Menu.Help.About"));
    menuItem.setActionCommand("about");
    menuItem.addActionListener(this);
    menu.add(menuItem);
  
    return menuBar;
  }
  
  public void actionPerformed(ActionEvent e) {
    String actionComm = e.getActionCommand();
    
    if (actionComm.equals("upload")) {
      if (DbConnect.testConnection()) {
        FileList fileList = new FileList();
        boolean resInit = fileList.initTable();
        if (resInit) {
          // fileList.setVisible(true);
          fileList.showModal(true);
        }
        fileList = null;
      } else {
        DialogUtils.errorPrint("Connection is null");
      }
    } else if (actionComm.equals("otherlogs")) {
      if (DbConnect.testConnection()) {
        FileLogs otherLogs = new FileLogs(0);
        boolean resInit = otherLogs.initTable();
        if (resInit) {
          // otherLogs.setVisible(true);
          otherLogs.showModal(true);
        }
        otherLogs = null;
      } else {
        DialogUtils.errorPrint("Connection is null");
      }
    } else if (actionComm.equals("const")) {
      if (DbConnect.testConnection()) {
        ConstList constList = new ConstList();
        boolean resInit = constList.initTable();
        if (resInit) {
          // constList.setVisible(true);
          constList.showModal(true);
        }
        constList = null;
      } else {
        DialogUtils.errorPrint("Connection is null");
      }
    } else if (actionComm.equals("vars")) {
      if (DbConnect.testConnection()) {
        VarList varList = new VarList();
        boolean resInit = varList.initTable();
        if (resInit) {
          varList.showModal(true);
        }
        varList = null;
      } else {
        DialogUtils.errorPrint("Connection is null");
      }
    } else if (actionComm.equals("src")) {
      if (DbConnect.testConnection()) {
        SourceList srcList = new SourceList();
        boolean resInit = srcList.initTable();
        if (resInit) {
          srcList.showModal(true);
        }
        srcList = null;
      } else {
        DialogUtils.errorPrint("Connection is null");
      }
    } else if (e.getActionCommand().equals("cfg")) {
      if (DbConnect.testConnection()) {
        ConfigList cfgList = new ConfigList();
        boolean resInit = cfgList.initTable();
        if (resInit) {
          cfgList.showModal(true);
        }
        cfgList = null;
      } else {
        DialogUtils.errorPrint("Connection is null");
      }
    } else if (e.getActionCommand().equals("table_lclnmbs")) {
      if (DbConnect.testConnection()) {
        ArrayList<String> params = new ArrayList<String>();
        params.add("/config/ats/fmt/local_nmbs/field");
        params.add("min");
        params.add("max");
        XmlTableList tblList = new XmlTableList(params, "local_nmbs");
        boolean resInit = tblList.initTable();
        if (resInit) {
          tblList.showModal(true);
        }
        tblList = null;
      } else {
        DialogUtils.errorPrint("Connection is null");
      }
    } else if (e.getActionCommand().equals("table_sidpref")) {
      if (DbConnect.testConnection()) {
        ArrayList<String> params = new ArrayList<String>();
        params.add("/config/ats/fmt/sid_pref/field");
        params.add("key");
        params.add("value");
        XmlTableList tblList = new XmlTableList(params, "sid_pref");
        boolean resInit = tblList.initTable();
        if (resInit) {
          tblList.showModal(true);
        }
        tblList = null;
      } else {
        DialogUtils.errorPrint("Connection is null");
      }
    } else if (e.getActionCommand().equals("table_tracesop")) {
      if (DbConnect.testConnection()) {
        ArrayList<String> params = new ArrayList<String>();
        params.add("/config/ats/fmt/trace_sop/field");
        params.add("key");
        params.add("value");
        XmlTableList tblList = new XmlTableList(params, "trace_sop");
        boolean resInit = tblList.initTable();
        if (resInit) {
          tblList.showModal(true);
        }
        tblList = null;
      } else {
        DialogUtils.errorPrint("Connection is null");
      }
    } else if (e.getActionCommand().equals("table_input")) {
      if (DbConnect.testConnection()) {
        ArrayList<String> params = new ArrayList<String>();
        params.add("/config/ats/input");
        params.add("dir");
        XmlTableList tblList = new XmlTableList(params, "input");
        boolean resInit = tblList.initTable();
        if (resInit) {
          tblList.showModal(true);
        }
        tblList = null;
      } else {
        DialogUtils.errorPrint("Connection is null");
      }
    } else if (e.getActionCommand().equals("table_output")) {
      if (DbConnect.testConnection()) {
        ArrayList<String> params = new ArrayList<String>();
        params.add("/config/ats/output");
        params.add("dir");
        params.add("file");
        XmlTableList tblList = new XmlTableList(params, "output");
        boolean resInit = tblList.initTable();
        if (resInit) {
          tblList.showModal(true);
        }
        tblList = null;
      } else {
        DialogUtils.errorPrint("Connection is null");
      }
    } else if (e.getActionCommand().equals("about")) {
      if (DbConnect.testConnection()) {
        About myAbout = new About();
        myAbout.createText();
        myAbout.showModal(true);
        myAbout = null;
      } else {
        DialogUtils.errorPrint("Connection is null");
      }
    } else if (actionComm.equals("quit")) {
      DbConnect.disconnect();
      System.exit(0);
    }
  }
}
