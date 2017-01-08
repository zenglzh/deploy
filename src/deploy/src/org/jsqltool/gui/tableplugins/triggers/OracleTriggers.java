package org.jsqltool.gui.tableplugins.triggers;

import javax.swing.*;
import javax.swing.table.*;
import org.jsqltool.conn.DbConnectionUtil;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: This implementation defines triggers panel for Oracle databases.
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
public class OracleTriggers implements Triggers {

  private DbConnectionUtil dbConnUtil = null;


  public OracleTriggers(DbConnectionUtil dbConnUtil) {
    this.dbConnUtil = dbConnUtil;
  }

  public String getQueryTriggers(String tableName) {
    String schema = null;
    if (tableName.indexOf(".")>-1) {
      schema = tableName.substring(0,tableName.indexOf("."));
      tableName = tableName.substring(tableName.indexOf(".") + 1);
    }
    return "SELECT T.TRIGGER_NAME as \"Name\",T.TRIGGER_TYPE as \"Type\",T.TRIGGERING_EVENT  as \"Trigger Event\","+
           "SUBSTR (T.WHEN_CLAUSE, 1, 254) as \"When Clause\",T.STATUS as \"Status\","+
           "SUBSTR (T.DESCRIPTION, 1, 254) as \"Description\",O.STATUS as \"Enabled\",T.OWNER as \"Owner\",O.OBJECT_ID as \"Object Id\""+
//           "  FROM SYS.DBA_OBJECTS O, SYS.DBA_TRIGGERS T WHERE "+
           "  FROM SYS.ALL_OBJECTS O, SYS.ALL_TRIGGERS T WHERE "+
           (schema==null?"":"  T.TABLE_OWNER = '"+schema+"' AND ")+
           "O.object_type = 'TRIGGER' AND O.object_name = T.trigger_name AND T.table_name = '"+tableName+"' AND O.owner = T.owner ";
  }



}
