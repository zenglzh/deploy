package org.jsqltool.gui.tableplugins;

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
import org.jsqltool.gui.tablepanel.*;
import org.jsqltool.gui.panel.*;
import org.jsqltool.gui.graphics.SQLTextArea;
import org.jsqltool.utils.Options;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Panel that shows table SQL scripts.
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
public class ScriptTablePanel extends JPanel implements TablePlugin {

  private String tableName = null;
  private SQLTextArea sql = new SQLTextArea();
  BorderLayout borderLayout1 = new BorderLayout();
  private DbConnectionUtil dbConnUtil = null;
  private JFrame parent = null;
  GridBagLayout gridBagLayout1 = new GridBagLayout();


  public ScriptTablePanel() {
    try {
      jbInit();
   }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  private void jbInit() throws Exception {
    this.setLayout(gridBagLayout1);
    this.add(sql,      new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 0, 0), 0, 0));
    sql.setEditable(false);
  }


   public final void resetPanel() {
     sql.setText("");
   }


 /**
  * @return panel position inside the JTabbedPane related to the table detail
  */
  public int getTabbedPosition() {
    return 4;
  }


  /**
   * @return folder name of the plugin panel, inside the JTabbedPane related to the table detail
   */
  public String getTabbedName() {
    return Options.getInstance().getResource("script");
  }


  /**
   * This method is called from the table detail to inizialize the plugin panel
   */
  public void initPanel(MainFrame parent,DbConnectionUtil dbConnUtil) {
    this.parent = parent;
    this.dbConnUtil = dbConnUtil;
  }


  /**
   * This method is called from the table detail to update the plugin panel content.
   * @param tableName table name (edventualy including catalog name) that table plugin have to show
   */
  public final void updateContent() {
    this.tableName = tableName;
    if (tableName==null || tableName.equals("")) {
      sql.setText("");
    } else {
      try {
        TableModel cols = dbConnUtil.getTableColumns(tableName);
        String t = tableName;
        if (t.indexOf(".")!=-1)
          t = t.substring(t.indexOf(".")+1);
        String text = "CREATE TABLE " + t + "\n(\n";
        int namelen = 0;
        int len = 0;
        Hashtable pk = new Hashtable();
        for (int i = 0; i < cols.getRowCount(); i++) {
          len = cols.getValueAt(i, 0).toString().length();
          if (len > namelen) {
            namelen = len;
          }
          if (cols.getValueAt(i, 2)!=null) {
            pk.put(cols.getValueAt(i, 2),cols.getValueAt(i, 0));
          }
        }
        for (int i = 0; i < cols.getRowCount(); i++) {
          text += "  " + rpad(cols.getValueAt(i, 0).toString(), namelen + 1) + cols.getValueAt(i, 1) + " ";
          text += (cols.getValueAt(i,4)!=null ? "DEFAULT "+cols.getValueAt(i,4)+" " : "");
          text += ( ( (Boolean) cols.getValueAt(i, 3)).booleanValue() ? "" : "NOT NULL");
          text += ",\n";
        }
        if (pk.size()>0) {
          text += "  PRIMARY KEY(";
          for(int i =0;i<pk.size();i++)
            text += pk.get(new Integer(i+1))+",";
          text = text.substring(0,text.length()-1);
          text += ")\n";
        }
        else
          text = text.substring(0,text.length()-2)+"\n";
        text += ");\n\n";

        // indexes...
        TableModel model = dbConnUtil.getTableIndexes(tableName);
        String indexName = null;
        boolean unique = false;
        for(int i=0;i<model.getRowCount();i++) {
          if (model.getValueAt(i,5)==null)
            continue;
          unique = false;
          if (model.getValueAt(i,3).getClass().equals(String.class))
            unique = model.getValueAt(i,3).toString().equals("0");
          else
          if (model.getValueAt(i,3) instanceof Number)
            unique = ((Number)model.getValueAt(i,3)).intValue()==0;

          if (model.getValueAt(i,5).equals(indexName)) {
            text += model.getValueAt(i,8)+",";
            continue;
          }
          else {
            if (indexName!=null) {
              text = text.substring(0,text.length()-1);
              text += ");\n\n";
            }
            indexName = model.getValueAt(i,5).toString();
          }
          text += "CREATE ";
          if (unique)
            text += "UNIQUE ";
          text += "INDEX "+model.getValueAt(i,5)+" ON "+t+" (";
          text += model.getValueAt(i,8)+",";
        }
        if (indexName!=null) {
          text = text.substring(0,text.length()-1);
          text += ");\n\n";
        }

        // fk...
        model = dbConnUtil.getCrossReference(tableName);
        String fkName = null;
        String pkList = "";
        String pkTable = null;
        for(int i=0;i<model.getRowCount();i++) {
          if (model.getValueAt(i,11)==null)
            continue;

          if (model.getValueAt(i,11).equals(fkName)) {
            text += model.getValueAt(i,7)+",";
            pkList += model.getValueAt(i,3)+",";
            continue;
          }
          else {
            if (fkName!=null) {
              text = text.substring(0,text.length()-1);
              text += ")\n";
              text += "  REFERENCES "+pkTable+"\n  (";
              pkList = pkList.substring(0,pkList.length()-1);
              text += pkList+")\n);\n\n";
            }
            fkName = model.getValueAt(i,11).toString();
          }
          text += "ALTER TABLE "+t+" ADD CONSTRAINT "+fkName+" FOREIGN KEY \n  (";
          text += model.getValueAt(i,7)+",";
          pkList = model.getValueAt(i,3)+",";
          pkTable = model.getValueAt(i,2).toString();
        }
        if (fkName!=null) {
          text = text.substring(0,text.length()-1);
          text += ")\n";
          text += "  REFERENCES "+pkTable+"\n  (";
          pkList = pkList.substring(0,pkList.length()-1);
          text += pkList+")\n);\n\n";
        }

        sql.setText(text);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }

    }
  }


  /**
   * This method is called from the table detail to set entity name.
   * @param tableName table name (edventualy including catalog name) that table plugin have to show
   */
  public final void setTableName(String tableName) {
    this.tableName = tableName;
  }


  /**
   * @return entity name
   */
  public String getTableName() {
    return tableName;
  }


  private String rpad(String text,int len) {
    for(int i=text.length();i<len;i++)
      text += " ";
    return text;
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
    return Options.getInstance().getResource("script plugin");
  }




  public boolean equals(Object o) {
    return (o instanceof TablePlugin &&
            ((TablePlugin)o).getName().equals(getName()));
  }


}
