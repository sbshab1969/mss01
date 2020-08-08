package acp;

import java.awt.*;
import java.sql.*;

import javax.swing.*;

import com.nqadmin.swingSet.datasources.SSConnection;

public class MyInternalFrame extends ModalInternalFrame {
  private static final long serialVersionUID = 1L;

  final static int NAV_NONE = -1;
  final static int NAV_FIRST = 0;
  final static int NAV_LAST = 1;
  final static int NAV_CURRENT = 2;
  final static int NAV_ABSOLUTE = 3;

  final static int ACT_NONE = -1;
  final static int ACT_GET = 0;
  final static int ACT_NEW = 1;
  final static int ACT_EDIT = 2;
  final static int ACT_DELETE = 3;
  final static int ACT_COPY = 4;

  final static int RES_NONE = -1;
  final static int RES_OK = 1;
  final static int RES_CANCEL = 0;

  protected static final JDesktopPane desktop = Main.getDesktop();
  protected static final SSConnection ssConnection = DbConnect.getSsConnection();
  protected static final Connection dbConnection = DbConnect.getDbConnection();

  public MyInternalFrame() {
    setSize(400, 300);
    setToCenter();
    setClosable(true);
//   setResizable(true);
//   setIconifiable(true);
  }

  public void setToCenter() {
    int desktopWidth = desktop.getWidth();
    int desktopHeight = desktop.getHeight();
    int frameWidth = getWidth();
    int frameHeight = getHeight();
    
    Point newLocation = new Point((desktopWidth-frameWidth)/2, (desktopHeight-frameHeight)/2);
    setLocation(newLocation);
    // doLayout();
    validate();
  }
}
