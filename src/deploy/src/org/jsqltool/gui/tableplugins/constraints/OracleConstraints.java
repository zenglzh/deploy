package org.jsqltool.gui.tableplugins.constraints;

import javax.swing.*;
import javax.swing.table.*;
import java.util.Vector;
import org.jsqltool.conn.DbConnectionUtil;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Constraints panel customized for Oracle database.
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
public class OracleConstraints implements Constraints {

  private DbConnectionUtil dbConnUtil = null;


  public OracleConstraints(DbConnectionUtil dbConnUtil) {
    this.dbConnUtil = dbConnUtil;
  }

  public boolean enableConstraint(String tableName,Object constraintId) {
    return dbConnUtil.executeStmt(
        "ALTER TABLE "+tableName+" ENABLE CONSTRAINT "+
        ((Object[])constraintId)[0],
        new Vector()
    )>0;
  }


  public boolean disableConstraint(String tableName,Object constraintId) {
    return dbConnUtil.executeStmt(
        "ALTER TABLE "+tableName+" DISABLE CONSTRAINT "+
        ((Object[])constraintId)[0],
        new Vector()
    )>0;
  }


  public boolean dropConstraint(String tableName,Object constraintId) {
    return dbConnUtil.executeStmt(
        "ALTER TABLE "+tableName+" DROP CONSTRAINT "+
        ((Object[])constraintId)[0],
        new Vector()
    )>0;
  }


  public String getQueryConstraints(String tableName) {
    if (tableName.indexOf(".")>-1)
      tableName = tableName.substring(tableName.indexOf(".")+1);
//    return "select * from all_constraints where table_name='"+tableName+"'";
    return "SELECT   a1.constraint_name NAME, "+
      "DECODE (a1.constraint_type, "+
      "       'C', 'Check', "+
      "       'P', 'Primary Key', "+
      "       'R', 'Referential Integrity', "+
      "       'U', 'Unique Key', "+
      "       'V', 'Check Option on a view' "+
      "      ) TYPE, "+
      "a1.r_constraint_name rname, INITCAP (a1.status) status, "+
      "INITCAP (a1.delete_rule) delete_rule, c1.column_name, c1.POSITION, "+
      "r_constraint_name, r_owner, a1.search_condition constext, "+
      "INITCAP (a1.DEFERRABLE) DEFERRABLE, INITCAP (a1.DEFERRED) DEFERRED "+
      "FROM user_cons_columns c1, user_constraints a1 "+
      "WHERE c1.table_name = a1.table_name "+
      "AND c1.constraint_name = a1.constraint_name "+
      "AND c1.owner = a1.owner "+
      "AND a1.table_name = '"+tableName+"'";
  }




}