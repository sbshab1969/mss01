package acp;

import java.io.*;
import java.sql.*;
import java.util.*;
import com.nqadmin.swingSet.datasources.SSConnection;

import acp.utils.*;

public class DbConnect {
  private static SSConnection ssConnection = buildSsConnection();
  private static Connection dbConnection = ssConnection.getConnection();

  private static SSConnection buildSsConnection() {
    // 1. Открытие файла
    FileInputStream fis = null;
    try {
//      fis = new FileInputStream("c:\\home\\oracle.conf");
      fis = new FileInputStream("oracle.conf");
    } catch (FileNotFoundException e) {
      DialogUtils.errorPrint(e);  
      return null;
    }
    // 2. Загрузка properties
    Properties props = new Properties();
    try {
      props.loadFromXML(fis);
    } catch (NullPointerException e) {
      DialogUtils.errorPrint(e);  
      return null;
    } catch (InvalidPropertiesFormatException e) {
      DialogUtils.errorPrint(e);  
      return null;
    } catch (IOException e) {
      DialogUtils.errorPrint(e);  
      return null;
    } finally {
      try {
        fis.close();
      } catch (IOException e) {}  
    }
    String serv = props.getProperty("ConnectionString");
    String login = props.getProperty("User");
    String pass = props.getProperty("Password");
    String driver = props.getProperty("Driver");

    // 3. Connect to Db
    SSConnection myConn = null;
    try {
      myConn = new SSConnection(serv, login, pass, driver);
      myConn.createConnection();
   } catch (SQLException | ClassNotFoundException e) {
      disconnect();
      DialogUtils.errorPrint(e); 
      return null;
    } finally {
      try {
        if (fis != null)
          fis.close();
      } catch (IOException e) {}  
    }
    return myConn;
  }

  public static void disconnect() {
//    System.out.println("disconnect");
    ssConnection = null;
    dbConnection = null;
  }

  public static SSConnection getSsConnection() {
    return ssConnection;
  }

  public static Connection getDbConnection() {
    return dbConnection;
  }

  public static boolean testConnection() {
    if (ssConnection == null) {
      return false;
    }  
    return true;
  }

}
