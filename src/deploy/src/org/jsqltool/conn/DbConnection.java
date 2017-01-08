package org.jsqltool.conn;

import java.util.*;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: database connection descriptor:
 * contains connection properties, old queries and tables filters.
 * This window allows to create/edit/delete connections.
 * </p>
 * <p>Copyright: Copyright (C) 2006 Mauro Carniel</p>
 *
 * <p> This file is part of JSqlTool project.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the (LGPL) Lesser General Public
 * License as published by the Free Software Foundation;
 *
 *                GNU LESSER GENERAL PUBLIC LICENSE
 *                 Version 2.1, February 1999
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free
 * Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *       The author may be contacted at:
 *           maurocarniel@tin.it</p>
 *
 * @author Mauro Carniel
 * @version 1.0
 */
public class DbConnection {

  public static final int ORACLE_TYPE = 0;
  public static final int SQLSERVER_TYPE = 1;
  public static final int ODBC_TYPE = 2;
  public static final int OTHER_TYPE = 3;


  public String getClassName(int dbType) {
    if (dbType==ORACLE_TYPE)
      return "oracle.jdbc.driver.OracleDriver";
    else if (dbType==SQLSERVER_TYPE)
      return "com.microsoft.jdbc.sqlserver.SQLServerDriver";
    else if (dbType==ODBC_TYPE)
      return "sun.jdbc.odbc.JdbcOdbcDriver";
    else // OTHER_TYPE
      return "";
  }

  public String getUrl(int dbType,String host,String port,String sid) {
    if (dbType==ORACLE_TYPE)
      return "jdbc:oracle:thin:@"+host+":"+port+":"+sid;
    else if (dbType==SQLSERVER_TYPE)
      return "jdbc:microsoft:sqlserver://"+host+":"+port+";DatabaseName="+sid+";SelectMethod=cursor";
    else if (dbType==ODBC_TYPE)
      return "jdbc:odbc:"+sid;
    else // OTHER_TYPE
      return "";
  }


  public String getHost() {
    if (dbType==ORACLE_TYPE)
      return url.substring(18,url.indexOf(":",18));
    else if (dbType==SQLSERVER_TYPE)
      return url.substring(27,url.indexOf(":",27));
    else if (dbType==ODBC_TYPE)
      return "";
    else // OTHER_TYPE
      return "";
  }


  public String getPort() {
    if (dbType==ORACLE_TYPE) {
      int index = url.indexOf(":",18);
      return url.substring(index+1,url.indexOf(":",index+1));
    } else if (dbType==SQLSERVER_TYPE) {
      int index = url.indexOf(":",27);
      return url.substring(index+1,url.indexOf(";",index+1));
    } else if (dbType==ODBC_TYPE)
      return "";
    else // OTHER_TYPE
      return "";
  }


  public String getSID() {
    if (dbType==ORACLE_TYPE) {
      int index = url.indexOf(":",18);
      index = url.indexOf(":",index+1);
      return url.substring(index+1);
    } else if (dbType==SQLSERVER_TYPE) {
      int index = url.indexOf("=");
      return url.substring(index+1,url.indexOf(";",index+1));
    } else if (dbType==ODBC_TYPE) {
      return url.substring(url.lastIndexOf(":")+1);
    } else // OTHER_TYPE
      return "";
  }


  private int dbType;
  private String name;
  private String className;
  private String url;
  private String username;
  private String password;
  private boolean autoCommit;
  private int isolationLevel;
  private boolean readOnly;
  private String catalog;
  private boolean quotes;

  /** collection of filters/orderers, one for each table */
  private Hashtable filters = new Hashtable();

  /** old queriesm, stored in the connection profile file */
  private ArrayList oldQueries = new ArrayList();



  public DbConnection(
      int dbType,
      String name,
      String className,
      String url,
      String username,
      String password,
      boolean autoCommit,
      int isolationLevel,
      boolean readOnly,
      String catalog,
      Hashtable filters,
      ArrayList oldQueries,
      boolean quotes) {
    this.dbType = dbType;
    this.name = name;
    this.className = className;
    this.url = url;
    this.username = username;
    this.password = password;
    this.autoCommit = autoCommit;
    this.isolationLevel = isolationLevel;
    this.readOnly = readOnly;
    this.catalog = catalog;
    this.filters = filters;
    this.oldQueries = oldQueries;
    this.quotes = quotes;
  }


  public String getClassName() {
    return className;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public int getIsolationLevel() {
    return isolationLevel;
  }

  public boolean isAutoCommit() {
    return autoCommit;
  }

  public int getDbType() {
    return dbType;
  }

  public void setAutoCommit(boolean autoCommit) {
    this.autoCommit = autoCommit;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public void setDbType(int dbType) {
    this.dbType = dbType;
  }

  public void setIsolationLevel(int isolationLevel) {
    this.isolationLevel = isolationLevel;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }


  public String getCatalog() {
    return catalog;
  }


  public void setCatalog(String catalog) {
    this.catalog = catalog;
  }


  public Hashtable getFilters() {
    return filters;
  }


  public ArrayList getOldQueries() {
    return oldQueries;
  }


  public void setOldQueries(ArrayList oldQueries) {
    this.oldQueries = oldQueries;
  }


  public void setFilters(Hashtable filters) {
    this.filters = filters;
  }
  public boolean isQuotes() {
    return quotes;
  }
  public void setQuotes(boolean quotes) {
    this.quotes = quotes;
  }

}