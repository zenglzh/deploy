package org.jsqltool.gui.tableplugins.constraints;

import javax.swing.*;
import javax.swing.table.*;
import org.jsqltool.conn.DbConnectionUtil;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Constraints panel that does not support any contraints operations.
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
public class VoidConstraints implements Constraints {

  private DbConnectionUtil dbConnUtil = null;


  public VoidConstraints(DbConnectionUtil dbConnUtil) {
    this.dbConnUtil = dbConnUtil;
  }


  public boolean enableAllConstraints(String tableName) {
    return false;
  }

  public boolean disableAllConstraints(String tableName) {
    return false;
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
    return null;
  }




}
