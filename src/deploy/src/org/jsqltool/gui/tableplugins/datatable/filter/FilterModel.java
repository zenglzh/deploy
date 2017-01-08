package org.jsqltool.gui.tableplugins.datatable.filter;

import java.util.Vector;
import java.util.StringTokenizer;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Filter/Order model, which is applied to a table.
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
public class FilterModel {

  private String orderClause = "";
  private String whereClause = "";

  public FilterModel() { }


  public String getOrderClause() {
    return this.orderClause;
  }


  public String getWhereClause() {
    return this.whereClause;
  }

  public void setOrderClauseFromColums(Object[] columns) {
    if (columns.length==0) {
      this.orderClause = "";
    } else {
      this.orderClause = " ORDER BY ";
      for(int i=0;i<columns.length;i++)
        this.orderClause += columns[i].toString()+",";
      this.orderClause = this.orderClause.substring(0,this.orderClause.length()-1);
    }
  }

  public void setWhereClause(String whereClause) {
    if (whereClause.length()>0)
      this.whereClause =
          (whereClause.indexOf("WHERE")==-1 ? " WHERE ":"")+
          whereClause;
    else
      this.whereClause = "";
  }

  public void setOrderClause(String orderClause) {
    this.orderClause = orderClause;
  }

}