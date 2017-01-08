package org.jsqltool.replication;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;

import org.jsqltool.replication.*;
import org.jsqltool.gui.*;
import org.jsqltool.utils.ImageLoader;
import org.jsqltool.utils.Options;
import org.jsqltool.conn.*;
import org.jsqltool.conn.gui.ConnectionFrame;
import java.sql.*;
import javax.swing.table.TableModel;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Class used to manage a replication process.
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
public class Replication {

  /** data replication profile */
  private ReplicationProfile profile = null;

  /** main frame */
  private MainFrame parent = null;


  public Replication(MainFrame parent,ReplicationProfile profile) {
    this.parent = parent;
    this.profile = profile;
    DbConnectionUtil srcDbUtil = null;
    DbConnectionUtil destDbUtil = null;
    try {
      // retrieve source and destination db connections settings...
      ConnectionFrame f = new ConnectionFrame();
      ArrayList conns = f.getConnections();
      DbConnection dbConn = null;
      DbConnection srcDbConn = null;
      DbConnection destDbConn = null;
      for (int i = 0; i < conns.size(); i++) {
        dbConn = (DbConnection) conns.get(i);
        if (dbConn.getName().equals(profile.getSourceDatabase()))
          srcDbConn = dbConn;
        else if (dbConn.getName().equals(profile.getDestDatabase()))
          destDbConn = dbConn;
      }
      if (srcDbConn==null || destDbConn==null) {
        JOptionPane.showMessageDialog(
            parent,
            Options.getInstance().getResource("database connections don't exist"),
            Options.getInstance().getResource("error"),
            JOptionPane.ERROR_MESSAGE
        );
        return;
      }

      // start progress bar...
      try {
        ProgressDialog.getInstance().startProgress();
      }
      catch (Throwable ex5) {
      }

      // create source and target database connections...
      srcDbUtil = new DbConnectionUtil(parent,srcDbConn);
      destDbUtil = new DbConnectionUtil(parent,destDbConn);
      try {
        ProgressDialog.getInstance().startProgress();
      }
      catch (Throwable ex6) {
      }

      // fetch all target db table names...
      java.util.List destTables = destDbUtil.getTables(destDbConn.getUsername(),"TABLE");
      for(int i=0;i<destTables.size();i++)
        destTables.set(i,destTables.get(i).toString().toUpperCase());
      try {
        ProgressDialog.getInstance().startProgress();
      }
      catch (Throwable ex3) {
      }

      ArrayList tables = profile.getTablesList();
      String tableName = null;
      TableModel srcModel = null;
      TableModel destModel = null;
      ArrayList colsToAdd = new ArrayList();
      for(int i=0;i<tables.size();i++) {
        // for each table:
        tableName = tables.get(i).toString();

        // fetch source table structure...
        // the method "getTableColumns" returns: column, data type, pk, null?, default
        srcModel = srcDbUtil.getTableColumns(tableName);
        try {
          ProgressDialog.getInstance().startProgress();
        }
        catch (Throwable ex4) {
        }

        // 1. TABLE STRUCTURE ANALYSYS
        // check if the table exixts in the target db...
        if (destTables.indexOf(tableName.toUpperCase())==-1) {
          // the table doesn't exist: it must be created...
          createTable(destDbUtil,destDbConn.getDbType(),tableName,srcModel,false);
          destModel = srcModel;
        }
        else {
          // the table already exists:
          // if "re-create table content" is true then (i) drop table and (ii) re-create structure
          // else
          // check if the structure is the same of the src table:
          if (profile.isRecreateTablesContent()) {
            createTable(destDbUtil,destDbConn.getDbType(),tableName,srcModel,true);
            destModel = srcModel;
          }
          else {
            // fetch target table structure...
            // the method "getTableColumns" returns: column, data type, pk, null?, default
            destModel = srcDbUtil.getTableColumns(tableName);
            colsToAdd.clear();
            for(int j=0;j<srcModel.getRowCount();j++) {
              if (!tableContainsCol(srcModel.getValueAt(j,0).toString(),destModel))
                colsToAdd.add(srcModel.getValueAt(j,0));
            }
            if (colsToAdd.size()>0)
              updateTableStructure(destDbUtil.getConn(),destDbConn.getDbType(),tableName,srcModel,colsToAdd);
          }
        }


        // 2. DATA ANALYSYS

        // transfer data from source to destination database:
        // if "re-create table content" is true then all records read are inserted into the dest db
        // otherwise check if there exist some records in dest db: if yes, then
        // all records read are updated into the dest db and if update return 0 updates then the record is inserted
        // otherwise all records read are inserted into the dest db
        if (profile.isRecreateTablesContent()) {
          insertRecords(srcDbUtil,destDbUtil,tableName,srcModel);
        }
        else {
          if (canUpdate(destDbUtil,tableName,destModel)) {
            updateRecords(srcDbUtil,destDbUtil,tableName,srcModel);
          }
          else {
            insertRecords(srcDbUtil,destDbUtil,tableName,srcModel);
          }
        }


      }

      // stop progress bar...
      ProgressDialog.getInstance().stopProgress();

      destDbUtil.getConn().commit();

      JOptionPane.showMessageDialog(
          parent,
          Options.getInstance().getResource("data replication is completed."),
          Options.getInstance().getResource("replication completed"),
          JOptionPane.INFORMATION_MESSAGE
      );

    }
    catch (Exception ex) {
      ProgressDialog.getInstance().stopProgress();
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          parent,
          Options.getInstance().getResource("error while replicating data")+":\n"+ex.getMessage(),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE
      );
    }
    finally {
      try {
        if (srcDbUtil != null) {
          srcDbUtil.getConn().close();
        }
      }
      catch (Exception ex1) {
      }
      try {
        if (destDbUtil != null) {
          destDbUtil.getConn().rollback();
          destDbUtil.getConn().close();
        }
      }
      catch (Exception ex2) {
      }
    }
  }


  /**
   * Check if the destination table contains records and has a primary key defined.
   */
  private boolean canUpdate(DbConnectionUtil destDbUtil,String tableName,TableModel destModel) throws Exception {
    boolean hasPK = false;
    for(int i=0;i<destModel.getRowCount();i++)
      // destModel columns: column, data type, pk, null?, default
      if (destModel.getValueAt(i,2)!=null) {
        hasPK = true;
      }
    if (!hasPK)
      return false;

    Statement stmt = destDbUtil.getConn().createStatement();
    ResultSet rset = stmt.executeQuery("select * from "+tableName);
    boolean ok = false;
    if (rset.next())
      ok = true;

    try {
      rset.close();
    }
    catch (Exception ex3) {
    }
    try {
      stmt.close();
    }
    catch (Exception ex4) {
    }

    return ok;
  }


  /**
   * Insert Records and if a record insert fails, then update it.
   */
  private void insertRecords(DbConnectionUtil srcDbUtil,DbConnectionUtil destDbUtil,String tableName,TableModel srcModel) throws Exception {
    Statement stmt = srcDbUtil.getConn().createStatement();
    ResultSet rset = stmt.executeQuery("select * from "+tableName);

    // retrieve columns types...
    int[] colTypes = new int[srcModel.getRowCount()];
    for(int i=0;i<srcModel.getRowCount();i++) {
      colTypes[i] = rset.getMetaData().getColumnType(i+1);
    }

    // create SQL statement for an insert...
    String insSQL = "insert into "+tableName+"(";
    for(int i=0;i<srcModel.getRowCount();i++) {
      insSQL += (srcModel.getValueAt(i,0).toString().toUpperCase().startsWith("QUANTIT")?"QUANTITA":srcModel.getValueAt(i,0))+",";
    }
    insSQL = insSQL.substring(0,insSQL.length()-1);
    insSQL += ") values(";
    for(int i=0;i<srcModel.getRowCount();i++) {
      insSQL += "?,";
    }
    insSQL = insSQL.substring(0,insSQL.length()-1);
    insSQL += ")";

    // create SQL statement for an update...
    String updSQL = "update "+tableName+" set ";
    for(int i=0;i<srcModel.getRowCount();i++) {
      updSQL += (srcModel.getValueAt(i,0).toString().toUpperCase().startsWith("QUANTIT")?"QUANTITA":srcModel.getValueAt(i,0))+"=?,";
    }
    updSQL = updSQL.substring(0,updSQL.length()-1);
    updSQL += " where ";
    ArrayList pks = new ArrayList();
    ArrayList pksIndexes = new ArrayList();
    for(int i=0;i<srcModel.getRowCount();i++)
      if (srcModel.getValueAt(i,2)!=null) {
        updSQL += srcModel.getValueAt(i,0)+"=? and ";
        pks.add(srcModel.getValueAt(i,0));
        pksIndexes.add(new Integer(i));
      }
    if (updSQL.endsWith(" and "))
      updSQL = updSQL.substring(0,updSQL.length()-4);

    PreparedStatement destInsStmt = destDbUtil.getConn().prepareStatement(insSQL);
    PreparedStatement destUpdStmt = destDbUtil.getConn().prepareStatement(updSQL);
    Object[] row = new Object[srcModel.getRowCount()];
    while(rset.next()) {
      for(int i=0;i<srcModel.getRowCount();i++) {
        row[i] = rset.getObject(i+1);
        if (row[i]!=null)
          destInsStmt.setObject(i + 1, row[i]);
        else
          destInsStmt.setNull(i + 1, colTypes[i]);
      }
      try {
        destInsStmt.execute();
      }
      catch (SQLException ex) {
        if (pks.size()==0)
          throw ex;
        // the record already exists:
        // create an update statement...
        for(int i=0;i<srcModel.getRowCount();i++) {
          if (row[i]!=null)
            destUpdStmt.setObject(i + 1, row[i]);
          else
            destUpdStmt.setNull(i + 1, colTypes[i]);
        }
        for(int i=0;i<pks.size();i++)
          destUpdStmt.setObject(srcModel.getRowCount() + i + 1, row[((Integer)pksIndexes.get(i)).intValue()]);
        destUpdStmt.execute();
      }
    }

    try {
      rset.close();
    }
    catch (Exception ex3) {
    }
    try {
      stmt.close();
    }
    catch (Exception ex4) {
    }
    try {
      destInsStmt.close();
    }
    catch (Exception ex5) {
    }
    try {
      destUpdStmt.close();
    }
    catch (Exception ex5) {
    }

  }


  /**
   * Update Records and if a record update fails, then insert it.
   */
  private void updateRecords(DbConnectionUtil srcDbUtil,DbConnectionUtil destDbUtil,String tableName,TableModel srcModel) throws Exception {
    Statement stmt = srcDbUtil.getConn().createStatement();
    ResultSet rset = stmt.executeQuery("select * from "+tableName);

    // retrieve columns types...
    int[] colTypes = new int[srcModel.getRowCount()];
    for(int i=0;i<srcModel.getRowCount();i++) {
      colTypes[i] = rset.getMetaData().getColumnType(i+1);
    }

    // create SQL statement for an insert...
    String insSQL = "insert into "+tableName+"(";
    for(int i=0;i<srcModel.getRowCount();i++) {
      insSQL += srcModel.getValueAt(i,0)+",";
    }
    insSQL = insSQL.substring(0,insSQL.length()-1);
    insSQL += ") values(";
    for(int i=0;i<srcModel.getRowCount();i++) {
      insSQL += "?,";
    }
    insSQL = insSQL.substring(0,insSQL.length()-1);
    insSQL += ")";

    // create SQL statement for an update...
    String updSQL = "update "+tableName+" set ";
    for(int i=0;i<srcModel.getRowCount();i++) {
      updSQL += srcModel.getValueAt(i,0)+"=?,";
    }
    updSQL = updSQL.substring(0,updSQL.length()-1);
    updSQL += " where ";
    ArrayList pks = new ArrayList();
    ArrayList pksIndexes = new ArrayList();
    for(int i=0;i<srcModel.getRowCount();i++)
      if (srcModel.getValueAt(i,2)!=null) {
        updSQL += srcModel.getValueAt(i,0)+"=? and ";
        pks.add(srcModel.getValueAt(i,0));
        pksIndexes.add(new Integer(i));
      }
    if (updSQL.endsWith(" and "))
      updSQL = updSQL.substring(0,updSQL.length()-4);

    PreparedStatement destInsStmt = destDbUtil.getConn().prepareStatement(insSQL);
    PreparedStatement destUpdStmt = destDbUtil.getConn().prepareStatement(updSQL);
    Object[] row = new Object[srcModel.getRowCount()];
    while(rset.next()) {
      for(int i=0;i<srcModel.getRowCount();i++) {
        row[i] = rset.getObject(i+1);
        if (row[i]!=null)
          destUpdStmt.setObject(i + 1, row[i]);
        else
          destUpdStmt.setNull(i + 1, colTypes[i]);
      }
      for(int j=0;j<pks.size();j++)
        destUpdStmt.setObject(srcModel.getRowCount() + j + 1, row[((Integer)pksIndexes.get(j)).intValue()]);

      if (destUpdStmt.executeUpdate()==0) {
        // no record found: it will be inserted...
        for(int i=0;i<srcModel.getRowCount();i++) {
          if (row[i]!=null)
            destInsStmt.setObject(i + 1, row[i]);
          else
            destInsStmt.setNull(i + 1, colTypes[i]);
        }
        destInsStmt.execute();
      }

    }

    try {
      rset.close();
    }
    catch (Exception ex3) {
    }
    try {
      stmt.close();
    }
    catch (Exception ex4) {
    }
    try {
      destInsStmt.close();
    }
    catch (Exception ex5) {
    }
    try {
      destUpdStmt.close();
    }
    catch (Exception ex5) {
    }

  }


  /**
   * Check if there exist the specified column in the dest table.
   */
  private boolean tableContainsCol(String colToCheck,TableModel destModel) {
    for(int i=0;i<destModel.getRowCount();i++)
      if (destModel.getValueAt(i,0).equals(colToCheck))
        return true;
    return false;
  }


  /**
   * Update a table structure.
   */
  private void updateTableStructure(Connection destConn,int dbType,String tableName,TableModel srcModel,ArrayList colsToAdd) throws Exception {
    Statement stmt = destConn.createStatement();
    String sql = null;
    String colToAdd = null;
    for(int i=0;i<colsToAdd.size();i++) {
      colToAdd = colsToAdd.get(i).toString();
      sql = "alter table "+tableName+" add column "+colToAdd+" ";
      for(int j=0;j<srcModel.getRowCount();j++) {
        // srcModel columns: column, data type, pk, null?, default
        if (srcModel.getValueAt(j,0).equals(colToAdd)) {
          sql += getColType(srcModel.getValueAt(j,1).toString(),dbType)+" ";
          if (!((Boolean)srcModel.getValueAt(j,3)).booleanValue())
            sql += " NOT NULL ";
          break;
        }
      }
      stmt.execute(sql);
    }
    stmt.close();
  }


  /**
   * Create a table.
   */
  private void createTable(DbConnectionUtil destDbConnUtil,int dbType,String tableName,TableModel srcModel,boolean dropTable) throws Exception {
    Connection destConn = destDbConnUtil.getConn();
    Statement stmt = destConn.createStatement();
    if (dropTable) {
      stmt.execute("drop table "+tableName);
    }

    String sql = "create table "+tableName+"(";
    Hashtable pks = new Hashtable();
    for(int i=0;i<srcModel.getRowCount();i++) {
      // srcModel columns: column, data type, pk, null?, default
      sql += srcModel.getValueAt(i,0)+" ";
      sql += getColType(srcModel.getValueAt(i,1).toString(),dbType)+" ";
      if (!((Boolean)srcModel.getValueAt(i,3)).booleanValue())
        sql += " NOT NULL ";
      sql += ",";

      if (srcModel.getValueAt(i,2)!=null)
        pks.put(srcModel.getValueAt(i,2),srcModel.getValueAt(i,0));
    }
    if (pks.size()>0) {
      sql += "PRIMARY KEY(";
      for(int i=0;i<pks.size();i++)
        sql += pks.get(new Integer(i+1))+",";
      sql = sql.substring(0,sql.length()-1);
      sql += "),";
    }

    sql = sql.substring(0,sql.length()-1);
    sql += ")";
    stmt.execute(sql);

    // create indexes...
    String text = "";
    try {
      TableModel model = destDbConnUtil.getTableIndexes(tableName);
      String indexName = null;
      boolean unique = false;
      for (int i = 0; i < model.getRowCount(); i++) {
        if (model.getValueAt(i, 5) == null) {
          continue;
        }
        unique = false;
        if (model.getValueAt(i, 3).getClass().equals(String.class)) {
          unique = model.getValueAt(i, 3).toString().equals("0");
        }
        else
        if (model.getValueAt(i, 3)instanceof Number) {
          unique = ( (Number) model.getValueAt(i, 3)).intValue() == 0;

        }
        if (model.getValueAt(i, 5).equals(indexName)) {
          text += model.getValueAt(i, 8) + ",";
          continue;
        }
        else {
          if (indexName != null) {
            text = text.substring(0, text.length() - 1);
            text += ")";
            stmt.execute(text);
            text = "";
          }
          indexName = model.getValueAt(i, 5).toString();
        }
        text += "CREATE ";
        if (unique) {
          text += "UNIQUE ";
        }
//        text += "INDEX " + model.getValueAt(i, 5)+"_"+tableName + " ON " + tableName + " (";
        text += "INDEX " + model.getValueAt(i, 5)+" ON " + tableName + " (";
        text += model.getValueAt(i, 8) + ",";
      }
      if (indexName != null) {
        text = text.substring(0, text.length() - 1);
        text += ")";
        stmt.execute(text);
      }
    }
    catch (SQLException ex) {
      ex.printStackTrace();
    }


    stmt.close();
  }


  /**
   * @param type source table column type
   * @return destination table column type, according to the db type
   */
  private String getColType(String type,int dbType) {
    // case MySQL...
    if (type.equals("VARCHAR(0)"))
      type = "VARCHAR(255)";
    else if (type.equals("VARCHAR2(0)"))
      type = "VARCHAR2(255)";
    else if (type.equals("NUMERIC(0)") || type.equals("NUMERIC(0,0)"))
      type = "NUMERIC(20)";
    else if (type.equals("DECIMAL(0)") || type.equals("DECIMAL(0,0)"))
      type = "DECIMAL(20,5)";


    if (dbType==DbConnection.ORACLE_TYPE) {
      if (type.startsWith("VARCHAR2"))
        return "VARCHAR2"+type.substring(8);
      else if (type.startsWith("VARCHAR"))
        return "VARCHAR2"+type.substring(7);
      else if (type.startsWith("NUMERIC"))
        return "NUMBER"+type.substring(7);
    }
    else if (dbType==DbConnection.SQLSERVER_TYPE) {
      if (type.startsWith("VARCHAR2"))
        return "VARCHAR"+type.substring(7);
      else if (type.startsWith("NUMBER"))
        return "NUMERIC"+type.substring(6);
    }
    else {
      if (type.startsWith("VARCHAR2"))
        return "VARCHAR"+type.substring(8);
      else if (type.startsWith("NUMBER"))
        return "NUMERIC"+type.substring(6);
      else if (type.startsWith("INTEGER"))
        return "NUMERIC"+type.substring(7);
      else if (type.startsWith("DECIMAL"))
        return "NUMERIC"+type.substring(7);
      else if (type.startsWith("DATE"))
        return "TIMESTAMP";
    }
    return type;
  }



}