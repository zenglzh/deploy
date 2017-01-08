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
import org.jsqltool.gui.tableplugins.triggers.*;
import org.jsqltool.gui.panel.*;
import org.jsqltool.model.CustomTableModel;
import org.jsqltool.utils.Options;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: This panel contains triggers defined on table.
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
public class TriggersTablePanel extends TablePluginAdapter {

  private Hashtable pk = null;
  BorderLayout borderLayout1 = new BorderLayout();
  private JFrame parent = null;
  GridBagLayout gridBagLayout1 = new GridBagLayout();

  /** class related to the dabase type currently in use */
  private Triggers triggers = null;


  public TriggersTablePanel() {
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
    return 5;
  }


  /**
   * @return folder name of the plugin panel, inside the JTabbedPane related to the table detail
   */
  public String getTabbedName() {
    return Options.getInstance().getResource("triggers");
  }


  /**
   * This method is called from the table detail before the inizialization of the plugin panel
   */
  public void init(DbConnectionUtil dbConnUtil) {
    if (dbConnUtil.getDbConnection().getDbType()==DbConnection.ORACLE_TYPE)
      triggers = new OracleTriggers(dbConnUtil);
    else
      triggers = new VoidTriggers(dbConnUtil);
  }


  public String getQuery(String tableName,DbConnectionUtil dbConnUtil) {
    return triggers.getQueryTriggers(tableName);
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
    return Options.getInstance().getResource("table triggers plugin");
  }


  public void setQuery(String tableName) {
    if (triggers.getQueryTriggers(tableName)!=null) {
      try {
        super.setQuery(tableName);
      }
      catch (Exception ex) {
      }
      return;
    }
    this.tableName = tableName;
  }


}

