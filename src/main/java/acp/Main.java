package acp;

import javax.swing.*;

import java.awt.event.*;

import acp.utils.*;

public class Main extends JFrame {
  private static final long serialVersionUID = 1L;
  private static final JDesktopPane desktop = new JDesktopPane();

  public Main() {
    super(Messages.getString("Title.Main") + " (1)");

    // Установка Look and Feel
    try {
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel"); // default
    //   UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    //   UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
    //   UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
    //   UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }
    // System.out.println(UIManager.getSystemLookAndFeelClassName());

    setContentPane(desktop);
    setSize(1200, 700);
    setLocationRelativeTo(null);  // размещение по центру экрана
    setExtendedState(MAXIMIZED_BOTH);
//    desktop.setDragMode(JDesktopPane.LIVE_DRAG_MODE);
//    desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

    MainMenu mainMenu = new MainMenu();
    setJMenuBar(mainMenu.createMenuBar());

    setDefaultCloseOperation(EXIT_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        formWindowClosing(evt);
      }
    });

  }

  private void formWindowClosing(WindowEvent evt) {
    DbConnect.disconnect();
  }

  private static void createAndShowGUI() {
    // ---------------------------
    // Установка L&F перед созданием формы
    // JFrame.setDefaultLookAndFeelDecorated(true);
    // JDialog.setDefaultLookAndFeelDecorated(true);
    Main frame = new Main();
    // Установка L&F после создания формы
    // frame.setUndecorated(true);
    // frame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
    frame.setVisible(true);
    // ---------------------------
    if (DbConnect.testConnection()) {
//      System.out.println(Messages.getString("Message.ConnectOK"));
    }
    // ---------------------------
  }
  
  public static void main(String[] args) {
//    Locale.setDefault(Locale.US);
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowGUI();
      }  
    });
  }

  public static JDesktopPane getDesktop() {
    return desktop;
  }

}
