package org.jsqltool.gui.tablepanel;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import org.jsqltool.conn.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;
import java.util.ArrayList;
import org.jsqltool.model.*;
import org.jsqltool.gui.*;
import org.jsqltool.*;
import org.jsqltool.gui.tableplugins.*;
import org.jsqltool.utils.ImageLoader;
import org.jsqltool.utils.Options;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Panel used to show a table detail.
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
public class TableDetailPanel extends JPanel implements ChangeListener {
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel mainPanel = new JPanel();
  JTabbedPane tabbedPane = new JTabbedPane();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel buttonsPanel = new JPanel();
  JButton refreshTablesButton = new JButton();
  JButton refreshDetailButton = new JButton();
  ImageIcon refreshTablesImage;
  ImageIcon refreshDetailImage;
  FlowLayout flowLayout1 = new FlowLayout();
  private TableListFrame parentFrame = null;
  private String tableName = null;
  private DbConnectionUtil dbConnUtil = null;
  private MainFrame parent = null;


  public TableDetailPanel() {
    this(null,null,null);
  }

  public TableDetailPanel(MainFrame parent,DbConnectionUtil dbConnUtil,TableListFrame parentFrame) {
    this.dbConnUtil = dbConnUtil;
    this.parentFrame = parentFrame;
    this.parent = parent;
    try {
      init();
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }


  void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    mainPanel.setLayout(borderLayout2);
    buttonsPanel.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    flowLayout1.setHgap(0);
    refreshTablesButton.addActionListener(new TableDetailPanel_refreshTablesButton_actionAdapter(this));
    refreshDetailButton.addActionListener(new TableDetailPanel_refreshDetailButton_actionAdapter(this));
    refreshTablesButton.setBorder(null);
    refreshTablesButton.setMinimumSize(new Dimension(24, 24));
    refreshTablesButton.setPreferredSize(new Dimension(24, 24));
    refreshDetailButton.setBorder(null);
    refreshDetailButton.setPreferredSize(new Dimension(24, 24));
    this.add(mainPanel, BorderLayout.CENTER);
    mainPanel.add(tabbedPane,  BorderLayout.CENTER);
    mainPanel.add(buttonsPanel, BorderLayout.NORTH);

    refreshTablesImage = ImageLoader.getInstance().getIcon("refreshtables.gif");
    refreshDetailImage = ImageLoader.getInstance().getIcon("refresh.gif");
    refreshTablesButton.setIcon(refreshTablesImage);
    refreshTablesButton.setToolTipText(Options.getInstance().getResource("refresh all"));
    refreshTablesButton.setMaximumSize(new Dimension(24,24));
    refreshDetailButton.setIcon(refreshDetailImage);
    refreshDetailButton.setToolTipText(Options.getInstance().getResource("refresh detail"));
    refreshDetailButton.setMaximumSize(new Dimension(24,24));
    buttonsPanel.add(refreshTablesButton, null);
    buttonsPanel.add(refreshDetailButton, null);
  }


  public void updateContent(final String tableName) {
    this.tableName = tableName;
    ProgressDialog.getInstance().startProgressNoClose();
    Thread t = new Thread() {
      public void run() {
        try {
          if (tableName!=null &&
             !tableName.equals( ((TablePlugin) tabbedPane.getSelectedComponent()).getTableName() ))
           ( (TablePlugin) tabbedPane.getSelectedComponent()).setTableName(tableName);
          ( (TablePlugin) tabbedPane.getSelectedComponent()).updateContent();
//          for (int i = 0; i < tabbedPane.getComponentCount(); i++) {
//            if (i != tabbedPane.getSelectedIndex()) {
//              ( (TablePlugin) tabbedPane.getComponent(i)).updateContent(tableName);
//            }
//          }
        }
        catch (Throwable ex) {
          ex.printStackTrace();
        }
        ProgressDialog.getInstance().forceStopProgress();
      }
    };
    t.start();
  }


  void refreshTablesButton_actionPerformed(ActionEvent e) {
    new Thread() {
      public void run() {
        ProgressDialog.getInstance().startProgress();
        try {
          parentFrame.updateLists();
          refreshDetailButton_actionPerformed(null);
        }
        catch (Throwable ex) {
        }
        finally {
          ProgressDialog.getInstance().stopProgress();
        }
      }
    }.start();

  }

  void refreshDetailButton_actionPerformed(ActionEvent e) {
    updateContent(tableName);
  }


  private void init() {
    ArrayList sortPanels = getSortPanels();
    TablePlugin panel = null;
    for(int i=0;i<sortPanels.size();i++)
      try {
        panel = (TablePlugin)sortPanels.get(i);
        tabbedPane.add((JPanel)panel,panel.getTabbedName());
        tabbedPane.addChangeListener(this);
        panel.initPanel(parent, dbConnUtil);
      }
      catch (Exception ex1) {
        ex1.printStackTrace();
      }

  }


  /**
   * Invoked when the target of the listener has changed its state.
   * @param e  a ChangeEvent object
   */
  public void stateChanged(ChangeEvent e) {
    if (tableName!=null && !tableName.equals( ((TablePlugin) tabbedPane.getSelectedComponent()).getTableName() )) {
      System.out.println( ((TablePlugin) tabbedPane.getSelectedComponent()).getName() );
      ( (TablePlugin) tabbedPane.getSelectedComponent()).setTableName(tableName);
      Thread t = new Thread() {
        public void run() {
          try {
             ( (TablePlugin) tabbedPane.getSelectedComponent()).updateContent();
          }
          catch (Throwable ex) {
            ex.printStackTrace();
          }
          ProgressDialog.getInstance().forceStopProgress();
        }
      };
      t.start();
    }
  }



  public static ArrayList getSortPanels() {
    String path = MainApp.class.getClassLoader().getResource(".").getPath().substring(1);
    File dir = new File(path+"org/jsqltool/gui/tableplugins/");
    String[] files = dir.list();
    File file = null;
    TablePlugin panel = null;
    ArrayList panels = new ArrayList();
    panels.add(new ColumnsTablePanel());
    panels.add(new ConstraintsTablePanel());
    panels.add(new DataTablePanel());
    panels.add(new IndexesTablePanel());
    panels.add(new ScriptTablePanel());
    panels.add(new TriggersTablePanel());
    if (files!=null)
      for(int i=0;i<files.length;i++) {
        file = new File(files[i]);
        try {
          if (file.getName().endsWith(".class")) {
            panel = (TablePlugin)Class.forName("org.jsqltool.gui.tableplugins." + file.getName().substring(0, file.getName().indexOf("."))).newInstance();
            if (!(panel instanceof JPanel))
              continue;
            if (!panels.contains(panel))
              panels.add(panel);
          }
        }
        catch (Exception ex) {
        }
        catch (Error er) {
        }
      }
    ArrayList sortPanels = new ArrayList();
    for(int i=0;i<panels.size();i++)
      sortPanels.add(null);
    for(int i=0;i<panels.size();i++) {
      panel = (TablePlugin)panels.get(i);
      sortPanels.set(panel.getTabbedPosition(),panel);
    }
    return sortPanels;
  }



}

class TableDetailPanel_refreshTablesButton_actionAdapter implements java.awt.event.ActionListener {
  TableDetailPanel adaptee;

  TableDetailPanel_refreshTablesButton_actionAdapter(TableDetailPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.refreshTablesButton_actionPerformed(e);
  }
}

class TableDetailPanel_refreshDetailButton_actionAdapter implements java.awt.event.ActionListener {
  TableDetailPanel adaptee;

  TableDetailPanel_refreshDetailButton_actionAdapter(TableDetailPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.refreshDetailButton_actionPerformed(e);
  }
}

