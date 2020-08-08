package acp.utils;

import java.sql.*;

public class DbUtils {

  public static boolean emptyString(String str) {
    if (str == null || str.equals("")) {
      return true;
    }
    return false;
  }

  public static String strAddAnd(String str1, String str2) {
    String str = "";
    if (emptyString(str1) && emptyString(str2)) {
      str = "";
    } else if (!emptyString(str1) && emptyString(str2)) {
      str = str1;
    } else if (emptyString(str1) && !emptyString(str2)) {
      str = str2;
    } else {
      str = str1 + " and " + str2;
    }
    return str;
  }

  public static int[] getFieldNums(String[] fields) {
    int res[] = new int[fields.length];
    for (int i = 0; i < fields.length; i++) {
      res[i] = i;
    }
    return res;
  }

  public static String buildSelectFrom(String[] fields, String[] fieldnames, String tblFrom) {
    StringBuilder query = new StringBuilder("select ");
    if (fields != null) {
      for (int i = 0; i < fields.length; i++) {
        query.append(fields[i]);
        if (fieldnames != null) 
          query.append(" " + "\"" + fieldnames[i] + "\"");
        if (i != fields.length - 1)
          query.append(", ");
      }  
    } else {
      query.append("*");
    }
    query.append(" from " + tblFrom);
    return query.toString();
  }

  public static String buildQuery(String selFrom, String where, String order) {
    StringBuilder query = new StringBuilder(selFrom);
    if (!emptyString(where)) {
      query.append(" where " + where);
    }
    if (!emptyString(order)) {
      query.append(" order by " + order);
    }
//    System.out.println(query);
    return query.toString();
  }

  public static String testQuery(Connection conn, String selFrom, 
         String selWhere, String selOrder) {
    String query;
    String where = "1=2";
    if (!emptyString(selWhere)) {
      where += " and " + selWhere;
    }
    // ------------------------------------
    query = buildQuery(selFrom, where, selOrder);
    boolean res = executeQuery(conn, query);
    if (res) {
      query = buildQuery(selFrom, selWhere, selOrder);
    } else {
      query = null;
    }
    // ------------------------------------
    return query;
  }

  public static boolean executeQuery(Connection conn, String query) {
    boolean res = false;
    try {
      Statement stmt = conn.createStatement();
      stmt.executeQuery(query);
      res = true;
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    } catch (Exception e) {
      DialogUtils.errorPrint(e);
    }
    return res;
  }

  public static int executeUpdate(Connection conn, String query) {
    int res = -1;
    try {
      Statement stmt = conn.createStatement();
      res = stmt.executeUpdate(query);
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    return res;
  }

}
