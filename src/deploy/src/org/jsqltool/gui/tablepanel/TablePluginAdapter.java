package org.jsqltool.gui.tablepanel;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.event.*;
import org.jsqltool.conn.DbConnectionUtil;
import org.jsqltool.model.CustomTableModel;
import java.sql.*;
import java.util.*;
import org.jsqltool.gui.*;
import org.jsqltool.*;
import org.jsqltool.gui.panel.*;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Generic plugin panel: each plugin panel has to extend this.
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
public class TablePluginAdapter extends JPanel implements TablePlugin {

  protected String tableName = null;
  private Hashtable pk = null;
  DataPanel dataPanel = null;
  BorderLayout borderLayout1 = new BorderLayout();
  private DbConnectionUtil dbConnUtil = null;
  private JFrame parent = null;
  BorderLayout borderLayout2 = new BorderLayout();


  public TablePluginAdapter() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  public String getQuery(String tableName,DbConnectionUtil dbConnUtil) {
    return null;
  }



  /**
   * This method is called from the table detail before the inizialization of the plugin panel
   */
  public void init(DbConnectionUtil dbConnUtil) {}


  /**
   * @return panel position inside the JTabbedPane related to the table detail
   */
  public int getTabbedPosition() {
    return -1;
  }


  /**
   * @return folder name of the plugin panel, inside the JTabbedPane related to the table detail
   */
  public String getTabbedName() {
    return "";
  }


  /**
   * @return entity name
   */
  public String getTableName() {
    return tableName;
  }


  public void setQuery(String tableName) {
    this.tableName = tableName;
    String query = getQuery(tableName,dbConnUtil);
    if (query!=null) {
      this.dataPanel.setQuery(query,new Vector());
    }
    try {
      ((CustomTableModel)dataPanel.getTableModel()).setEditMode(CustomTableModel.DETAIL_REC);
    }
    catch (Exception ex) {
    }
  }


  private void jbInit() throws Exception {
    this.setLayout(borderLayout2);
  }


   public final void resetPanel() {
     dataPanel.resetPanel();
   }


  /**
   * This method is called from the table detail to inizialize the plugin panel
   */
  public final void initPanel(MainFrame parent,DbConnectionUtil dbConnUtil) {
    this.parent = parent;
    this.dbConnUtil = dbConnUtil;
    this.dataPanel = new DataPanel(dbConnUtil,new TableModelListener() {
      public void tableChanged(TableModelEvent e){}
    });
    this.add(dataPanel,BorderLayout.CENTER);
    init(dbConnUtil);
  }


  /**
   * This method is called from the table detail to set entity name.
   * @param tableName table name (edventualy including catalog name) that table plugin have to show
   */
  public final void setTableName(String tableName) {
    this.tableName = tableName;
  }


  /**
   * This method is called from the table detail to update the plugin panel content.
   */
  public final void updateContent() {
    setQuery(tableName);
  }


  public final DbConnectionUtil getDbConnUtil() {
    return dbConnUtil;
  }


  public DataPanel getDataPanel() {
    return dataPanel;
  }


  /**
   * @return infos about the author of the plugin panel; "" or null does not report any info about the plugin panel
   */
  public String getAuthor(){
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
  public String getName(){
    return this.getClass().getName();
  }


  public boolean equals(Object o) {
    return (o instanceof TablePlugin &&
            ((TablePlugin)o).getName().equals(getName()));
  }



}
