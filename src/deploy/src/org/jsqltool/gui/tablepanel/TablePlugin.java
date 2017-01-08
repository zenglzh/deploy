package org.jsqltool.gui.tablepanel;

import org.jsqltool.conn.DbConnectionUtil;
import javax.swing.JFrame;
import org.jsqltool.gui.MainFrame;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: This interface must be implemented by each table plugin.
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
public interface TablePlugin {

  /**
   * @return panel position inside the JTabbedPane related to the table detail
   */
  public int getTabbedPosition();


  /**
   * @return folder name of the plugin panel, inside the JTabbedPane related to the table detail
   */
  public String getTabbedName();


  /**
   * @return infos about the author of the plugin panel; "" or null does not report any info about the plugin panel
   */
  public String getAuthor();


  /**
   * @return plugin panel version
   */
  public String getVersion();


  /**
   * @return plugin panel name, reported into the about window
   */
  public String getName();


  /**
   * This method is called from the table detail to inizialize the plugin panel
   */
  public void initPanel(MainFrame parent,DbConnectionUtil dbConnUtil);


  /**
   * This method is called from the table detail to update the plugin panel content.
   */
  public void updateContent();


  /**
   * This method is called from the table detail to set entity name.
   * @param tableName table name (edventualy including catalog name) that table plugin have to show
   */
  public void setTableName(String tableName);


  /**
   * @return entity name
   */
  public String getTableName();

}