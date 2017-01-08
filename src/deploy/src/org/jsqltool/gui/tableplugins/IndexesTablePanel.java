package org.jsqltool.gui.tableplugins;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.event.*;
import org.jsqltool.conn.DbConnectionUtil;
import org.jsqltool.conn.DbConnection;
import org.jsqltool.model.CustomTableModel;
import java.sql.*;
import java.util.*;
import org.jsqltool.gui.*;
import org.jsqltool.gui.tablepanel.*;
import org.jsqltool.*;
import org.jsqltool.gui.tableplugins.indexes.*;
import org.jsqltool.gui.panel.*;
import org.jsqltool.model.CustomTableModel;
import org.jsqltool.utils.Options;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: This panel contains indexes properties.
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
public class IndexesTablePanel extends TablePluginAdapter {

  private Hashtable pk = null;
  JPanel buttonsPanel = new JPanel();
  JButton enableAllButton = new JButton();
  JButton disableAllButton = new JButton();
  JButton enableButton = new JButton();
  JButton disableButton = new JButton();
  JButton dropButton = new JButton();
  ImageIcon enableAllImage;
  ImageIcon disableAllImage;
  ImageIcon enableImage;
  ImageIcon disableImage;
  ImageIcon dropImage;
  FlowLayout flowLayout1 = new FlowLayout();
  BorderLayout borderLayout1 = new BorderLayout();
  private JFrame parent = null;
  GridBagLayout gridBagLayout1 = new GridBagLayout();

  /** class related to the dabase type currently in use */
  private Indexes indexes = null;


  public IndexesTablePanel() {
/*
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
*/
  }


  /**
   * @return panel position inside the JTabbedPane related to the table detail
   */
  public int getTabbedPosition() {
    return 3;
  }


  /**
   * @return folder name of the plugin panel, inside the JTabbedPane related to the table detail
   */
  public String getTabbedName() {
    return Options.getInstance().getResource("indexes");
  }


  /**
   * This method is called from the table detail before the inizialization of the plugin panel
   */
  public void init(DbConnectionUtil dbConnUtil) {
    if (dbConnUtil.getDbConnection().getDbType()==DbConnection.ORACLE_TYPE)
      indexes = new OracleIndexes(dbConnUtil);
    else
      indexes = new VoidIndexes(dbConnUtil);
  }


  public String getQuery(String tableName,DbConnectionUtil dbConnUtil) {
    return indexes.getQueryIndexes(tableName);
  }


  /**
   * @return infos about the author of the plugin panel; "" or null does not report any info about the plugin panel
   */
  public String getAuthor() {
    return "";
  }


  /**
   * @return plugin panel version
   */
  public String getVersion() {
    return "1.0";
  }


  /**
   * @return plugin panel name, reported into the about window
   */
  public String getName() {
    return Options.getInstance().getResource("table indexes plugin");
  }


  public void setQuery(String tableName) {
    if (indexes.getQueryIndexes(tableName)!=null) {
      super.setQuery(tableName);
      return;
    }
    this.tableName = tableName;
    try {
      ResultSet rset = this.getDbConnUtil().getConn().getMetaData().getIndexInfo(null,null,tableName,false,true);
      int num = rset.getMetaData().getColumnCount();
      String[] colNames = new String[num];
      Class[] classNames = new Class[num];
      int[] typeNames = new int[num];
      for(int i=0;i<num;i++) {
        try {
          colNames[i] = rset.getMetaData().getColumnName(i+1);
          classNames[i] = Class.forName(rset.getMetaData().getColumnClassName(i+1));
          typeNames[i] = rset.getMetaData().getColumnType(i+1);
        }
        catch (Exception ex) {
        }catch (Error er) {
        }
      }
      CustomTableModel model = new CustomTableModel(colNames,classNames,typeNames);
      Object[] row = null;
      while(rset.next()) {
        row = new Object[num];
        for(int i=0;i<num;i++)
          row[i] = rset.getObject(i+1);
        model.addRow(row);
      }
      rset.close();
      this.getDataPanel().getTable().setModel(model);
      ((CustomTableModel)this.getDataPanel().getTableModel()).setEditMode(CustomTableModel.DETAIL_REC);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }


}

