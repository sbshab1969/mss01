package acp;

import java.awt.*;
import java.sql.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.html.HTMLEditorKit;

import acp.utils.*;

public class About extends MyInternalFrame {
  private static final long serialVersionUID = 1L;
  static final Connection dbConnection = DbConnect.getDbConnection();

  JEditorPane txt = new JEditorPane();

  private String versionMss;
  private String versionMssDate;
  private String certSystem;
  private String certProduct;
  private String certTu;
  private String certPartNumb;
  private String certAddress;
  private String certPhone;
  private String certFax;
  private String certEmail;
  private String certEmailSup;
  private String certWww;

  public About() {
    desktop.add(this);
    setTitle(Messages.getString("Title.About"));
    setSize(600, 400);
    setToCenter(); // метод из MyInternalFrame
//    setMaximizable(true);
//    setResizable(true);

    Container cp = getContentPane();
    cp.setLayout(new SpringLayout());
//    cp.setLayout(new BorderLayout());
    txt.setEditorKit(new HTMLEditorKit());
    txt.setEditable(false);
    txt.setBorder(new LineBorder(Color.BLACK));
//    txt.setBorder(new EmptyBorder(2, 2, 2, 2));
    txt.setText("");
    cp.add(txt);
    SpringUtilities.makeGrid(cp, 1, 1, 5, 5, 5, 5);
  }
  
  public void createText() {
    fillFields();
    String text = fillText();
    txt.setText(text);
  }
    
  private void fillFields() {
    CallableStatement cs = null;
    String sql = null;
    // ---------------------------
    sql = "{? = call getvarv(?)}";
    try {
      cs = dbConnection.prepareCall(sql);
      cs.registerOutParameter(1, java.sql.Types.VARCHAR);
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    versionMss   = getVarV(cs, "version_mss");
    certSystem   = getVarV(cs, "cert_system");
    certProduct  = getVarV(cs, "cert_product");
    certTu       = getVarV(cs, "cert_tu");
    certPartNumb = getVarV(cs, "cert_partnumber");
    certAddress  = getVarV(cs, "cert_address");
    certPhone    = getVarV(cs, "cert_phone");
    certFax      = getVarV(cs, "cert_fax");
    certEmail    = getVarV(cs, "cert_email");
    certEmailSup = getVarV(cs, "cert_email_support");
    certWww      = getVarV(cs, "cert_www");
    // ---------------------------
    sql = "{? = call getvard(?,?)}";
    try {
      cs = dbConnection.prepareCall(sql);
      cs.registerOutParameter(1, java.sql.Types.VARCHAR);
      cs.setString(3, "dd.mm.yyyy");
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    versionMssDate = getVarV(cs, "version_mss");
    // ---------------------------
  }

  private String getVarV(CallableStatement cst, String varname) {
    String res = null;
    try {
      cst.setString(2, varname);
      cst.execute();
      res = cst.getString(1);
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    if (res == null) {
      res = varname;
    }
    return res;
  }
  
  private String fillText() {
    StringBuilder sb = new StringBuilder();
    sb.append("<html><head></head><body style=\"font: Sans 10pt\">");
    sb.append("<table width=\"100%\">");
    sb.append("<tr><td colspan=\"3\" align=\"center\"><h3>" + certSystem + "</h3></td></tr>");
    sb.append("<tr><td colspan=\"3\" align=\"center\"><h4>Комплекс \"" + certProduct + "\"</h4></td></tr>");
    sb.append("<tr><td colspan=\"2\">Релиз ПО:</td>");
    sb.append("<td>" + versionMss + " от " + versionMssDate + "</td></tr>");
    sb.append("<tr><td colspan=\"2\">Технические условия:</td>");
    sb.append("<td>" + certTu + "</td></tr>");
    sb.append("<tr><td colspan=\"2\">Заводской номер:</td>");
    sb.append("<td>" + certPartNumb + "</td></tr>");
    sb.append("<tr><td colspan=\"3\">Контактная информация:</td></tr>");
    sb.append("<tr><td colspan=\"3\">");
    sb.append("Межрегиональный филиал информационно-сетевых технологий ОАО \"Уралсвязьинформ\"</td></tr>");
    sb.append("<tr><td colspan=\"3\">" + certAddress + "</td></tr>");
    sb.append("<tr><td width=\"30\">&nbsp;</td><td colspan=\"2\" width=\"90%\">тел.: " + certPhone + "</td></tr>");
    sb.append("<tr><td>&nbsp;</td><td colspan=\"2\">факс: " + certFax + "</td></tr>");
    sb.append("<tr><td>&nbsp;</td><td colspan=\"2\">e-mail: <a href=\"" + certEmail + "\">");
    sb.append(certEmail + "</a></td></tr>");
    sb.append("<tr><td>&nbsp;</td><td colspan=\"2\">support e-mail: <a href=\"" + certEmailSup + "\">");
    sb.append(certEmailSup + "</a></td></tr>");
    sb.append("<tr><td>&nbsp;</td><td colspan=\"2\"><a href=\"" + certWww + "\">" + certWww + "</a></td></tr>");
    sb.append("</table></body></html>");
    return sb.toString();
  }
  
}
