package acp.utils;

import java.awt.Component;

import javax.swing.JOptionPane;

public class DialogUtils {

  public static void errorMsg(Component parentComp, String msg) {
    JOptionPane.showMessageDialog(parentComp, msg,
        Messages.getString("Title.Error"), JOptionPane.ERROR_MESSAGE);
  }

  public static void errorMsg(String msg) {
    errorMsg(null, msg);
  }

  public static void errorPrint(String msg) {
    // System.err.println(msg);
    errorMsg(msg);
  }

  public static void errorPrint(Exception e) {
//    errorMsg(null, e.toString());
//    errorMsg(null, e.getClass().getName());
    errorMsg(null, e.getMessage());
//  e.printStackTrace();
    System.err.println(e.toString());
  }

  public static void infoDialog(String str) {
    JOptionPane.showMessageDialog(null, str, Messages.getString("Title.Info"),
        JOptionPane.INFORMATION_MESSAGE);
  }

  public static int confirmDialog(String message, String title, int initialValue) {
    int res = initialValue;
    Object[] options = { Messages.getString("Button.Yes"), Messages.getString("Button.No") };
    res = JOptionPane.showOptionDialog(null, message, title,
        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
        options[initialValue]);
    return res;
  }

}
