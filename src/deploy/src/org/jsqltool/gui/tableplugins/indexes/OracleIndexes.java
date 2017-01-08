package org.jsqltool.gui.tableplugins.indexes;

import javax.swing.*;
import javax.swing.table.*;
import org.jsqltool.conn.DbConnectionUtil;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: This implementation defines indexes panel for Oracle databases.
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
public class OracleIndexes implements Indexes {

  private DbConnectionUtil dbConnUtil = null;


  public OracleIndexes(DbConnectionUtil dbConnUtil) {
    this.dbConnUtil = dbConnUtil;
  }

  public String getQueryIndexes(String tableName) {
    if (tableName.indexOf(".")>-1)
      tableName = tableName.substring(tableName.indexOf(".")+1);
    return "SELECT   i.index_name, i.uniqueness, c.column_name, i.status, "+
     "    c.column_position, i.owner, c.table_name, c.descend, i.partitioned "+
     "FROM all_ind_columns c, all_indexes i "+
     "WHERE c.index_owner = i.owner "+
//     "AND i.table_owner = :ownname "+
     "AND c.index_name = i.index_name "+
     "AND i.table_name = '"+tableName+"' "+
     "ORDER BY 1, 4 ";
  }



}