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
import org.jsqltool.utils.Options;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Panel that shows table columns.
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
public class ColumnsTablePanel extends JPanel implements TablePlugin {

  private String tableName = null;
  private Hashtable pk = null;
  JPanel buttonsPanel = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  DataPanel dataPanel = null;
  BorderLayout borderLayout1 = new BorderLayout();
  private DbConnectionUtil dbConnUtil = null;
  private JFrame parent = null;
  GridBagLayout gridBagLayout1 = new GridBagLayout();


  public ColumnsTablePanel() {
    try {
      jbInit();
   }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  private void jbInit() throws Exception {
    this.setLayout(gridBagLayout1);
    flowLayout1.setVgap(0);
    this.add(buttonsPanel,      new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 0, 0), 0, 0));
    buttonsPanel.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    flowLayout1.setHgap(0);
  }


   public final void resetPanel() {
     dataPanel.resetPanel();
   }


 /**
  * @return panel position inside the JTabbedPane related to the table detail
  */
  public int getTabbedPosition() {
    return 0;
  }


  /**
   * @return folder name of the plugin panel, inside the JTabbedPane related to the table detail
   */
  public String getTabbedName() {
    return Options.getInstance().getResource("columns");
  }


  /**
   * This method is called from the table detail to inizialize the plugin panel
   */
  public void initPanel(MainFrame parent,DbConnectionUtil dbConnUtil) {
    this.parent = parent;
    this.dbConnUtil = dbConnUtil;
    this.dataPanel = new DataPanel(dbConnUtil,new TableModelListener() {
      public void tableChanged(TableModelEvent e){}
    });
    this.dataPanel.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    this.add(dataPanel,   new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    init();
  }


  private void init() {
    // pop-up menu creation...
    final JPopupMenu tableMenu = new JPopupMenu();
    JMenuItem addColMenu = new JMenuItem(Options.getInstance().getResource("add column"));
    JMenuItem dropColMenu = new JMenuItem(Options.getInstance().getResource("drop column"));
    JMenuItem dropTableMenu = new JMenuItem(Options.getInstance().getResource("drop table"));
    addColMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new AddColumnDialog(parent,tableName,dbConnUtil);
        updateContent();
      }
    });

    dropColMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (dataPanel.getTable().getSelectedRow()!=-1)
          try {
            PreparedStatement pstmt = dbConnUtil.getConn().prepareStatement(
                "ALTER TABLE "+tableName+" DROP COLUMN " + dataPanel.getTable().getModel().getValueAt(dataPanel.getTable().getSelectedRow(),0)
            );
            pstmt.execute();
            pstmt.close();
            updateContent();
          }
          catch (Exception ex) {
            JOptionPane.showMessageDialog(
                parent,
                Options.getInstance().getResource("error while dropping table:")+"\n"+ex.getMessage(),
                Options.getInstance().getResource("error"),
                JOptionPane.ERROR_MESSAGE);
          }

      }
    });

    dropTableMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (JOptionPane.showConfirmDialog(
            parent,
            Options.getInstance().getResource("confirm drop table?"),
            Options.getInstance().getResource("attention"),
            JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {

          try {
            PreparedStatement pstmt = dbConnUtil.getConn().prepareStatement(
                "DROP TABLE " + tableName
                );
            pstmt.execute();
            pstmt.close();
            tableName = null;
            updateContent();
          }
          catch (Exception ex) {
            JOptionPane.showMessageDialog(
                parent,
                Options.getInstance().getResource("error while dropping table:")+"\n"+ex.getMessage(),
                Options.getInstance().getResource("error"),
                JOptionPane.ERROR_MESSAGE);
          }

        }
      }
    });

    tableMenu.add(addColMenu);
    tableMenu.add(dropColMenu);
    tableMenu.add(dropTableMenu);

    dataPanel.getTable().addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) { // right mouse click
          // show pop-up menu...
          tableMenu.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    });
  }



  /**
   * This method is called from the table detail to update the plugin panel content.
   * @param tableName table name (edventualy including catalog name) that table plugin have to show
   */
  public final void updateContent() {
    if (tableName==null || tableName.equals("")) {
      dataPanel.getTable().setModel(new DefaultTableModel(new String[]{
        Options.getInstance().getResource("column"),
        Options.getInstance().getResource("data type"),
        Options.getInstance().getResource("pK"),
        Options.getInstance().getResource("null?")
      },0));
    } else {
      dataPanel.getTable().setModel(dbConnUtil.getTableColumns(tableName));
    }
    dataPanel.getTable().getColumnModel().getColumn(0).setPreferredWidth(200);
    dataPanel.getTable().getColumnModel().getColumn(1).setPreferredWidth(140);
    dataPanel.getTable().getColumnModel().getColumn(2).setPreferredWidth(50);
    dataPanel.getTable().getColumnModel().getColumn(3).setPreferredWidth(50);
    dataPanel.getTable().getColumnModel().getColumn(4).setPreferredWidth(50);
//    dataPanel.getTable().setColumnSelectionAllowed(false);
//    dataPanel.getTable().setRowSelectionAllowed(false);
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
    return Options.getInstance().getResource("table columns plugin");
  }




  public boolean equals(Object o) {
    return (o instanceof TablePlugin &&
            ((TablePlugin)o).getName().equals(getName()));
  }




}
