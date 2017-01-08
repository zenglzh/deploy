package org.jsqltool.gui.tableplugins.constraints;

import javax.swing.*;
import javax.swing.table.*;
import org.jsqltool.conn.DbConnectionUtil;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Constraints panel customized for MS SQL Server database.
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
public class SqlServerConstraints implements Constraints {

  private DbConnectionUtil dbConnUtil = null;


  public SqlServerConstraints(DbConnectionUtil dbConnUtil) {
    this.dbConnUtil = dbConnUtil;
  }

  public boolean enableConstraint(String tableName,Object constraintId) {
    return false;
  }


  public boolean disableConstraint(String tableName,Object constraintId) {
    return false;
  }


  public boolean dropConstraint(String tableName,Object constraintId) {
    return false;
  }


  public String getQueryConstraints(String tableName) {
    if (tableName.indexOf(".")>-1)
      tableName = tableName.substring(tableName.indexOf(".")+1);
//    return "select c.name as 'CONSTRAINT NAME',a.name as 'COLUMN NAME',c.xtype as 'CONTRAINT TYPE' from syscolumns a,sysconstraints b,sysobjects c,sysobjects d "+
//           "where a.id=b.id and b.constid=c.id and d.id=b.id and d.name='"+tableName+"'";

    return "SELECT c.name as 'CONSTRAINT NAME',a.name as 'COLUMN NAME',C.XTYPE AS 'CONTRAINT TYPE',COM.TEXT AS 'DESCRIPTION' FROM SYSCOLUMNS A,SYSCONSTRAINTS B,SYSOBJECTS C,SYSOBJECTS D,SYSCOMMENTS COM "+
           "WHERE A.ID=B.ID AND B.CONSTID=C.ID AND D.ID=B.ID AND D.NAME='"+tableName+"' AND B.CONSTID=COM.ID ";


//SELECT c.name as 'CONSTRAINT NAME',a.name as 'COLUMN NAME',C.XTYPE AS 'CONTRAINT TYPE',COM.TEXT FROM SYSCOLUMNS A,SYSCONSTRAINTS B,SYSOBJECTS C,SYSOBJECTS D,SYSCOMMENTS COM
//WHERE A.ID=B.ID AND B.CONSTID=C.ID AND D.ID=B.ID AND D.NAME='PRM06_PRM01_PRM02' AND B.CONSTID=COM.ID
  }




}