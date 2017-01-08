package org.jsqltool.gui.tableplugins;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.event.*;
import org.jsqltool.conn.DbConnectionUtil;
import org.jsqltool.conn.DbConnection;
import org.jsqltool.model.CustomTableModel;
import java.sql.*;
import java.util.*;
import org.jsqltool.gui.*;
import org.jsqltool.gui.tablepanel.*;
import org.jsqltool.*;
import org.jsqltool.gui.tableplugins.constraints.*;
import org.jsqltool.gui.panel.*;
import org.jsqltool.utils.ImageLoader;
import org.jsqltool.utils.Options;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Panel used to show the table constraints.
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
public class ConstraintsTablePanel extends TablePluginAdapter {

  private Hashtable pk = null;
  JPanel buttonsPanel = new JPanel();
  JButton enableAllButton = new JButton();
  JButton disableAllButton = new JButton();
  JButton enableButton = new JButton();
  JButton disableButton = new JButton();
  JButton dropButton = new JButton();
  ImageIcon enableAllImage;
  ImageIcon disableAllImage;
  ImageIcon enableImage;
  ImageIcon disableImage;
  ImageIcon dropImage;
  FlowLayout flowLayout1 = new FlowLayout();
  BorderLayout borderLayout1 = new BorderLayout();
  private JFrame parent = null;
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  BorderLayout borderLayout2 = new BorderLayout();

  /** implementation class related to database type */
  private Constraints cons = null;


  public ConstraintsTablePanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * @return position of this panel inside the JTabbedPane of table detail
   */
  public int getTabbedPosition() {
    return 2;
  }


  /**
   * @return folder name
   */
  public String getTabbedName() {
    return Options.getInstance().getResource("constraints");
  }


  public void init(DbConnectionUtil dbConnUtil) {
    if (dbConnUtil.getDbConnection().getDbType()==DbConnection.ORACLE_TYPE)
      cons = new OracleConstraints(dbConnUtil);
    else if (dbConnUtil.getDbConnection().getDbType()==DbConnection.SQLSERVER_TYPE)
      cons = new SqlServerConstraints(dbConnUtil);
    else
      cons = new VoidConstraints(dbConnUtil);
  }


  public void jbInit() {
    flowLayout1.setVgap(0);
    this.setLayout(borderLayout2);
    borderLayout2.setHgap(5);
    borderLayout2.setVgap(5);
    this.add(buttonsPanel, BorderLayout.NORTH);
    buttonsPanel.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    flowLayout1.setHgap(0);
    enableAllButton.addActionListener(new ConstraintsTablePanel_enableAllButton_actionAdapter(this));
    disableAllButton.addActionListener(new ConstraintsTablePanel_disableAllButton_actionAdapter(this));
    enableButton.addActionListener(new ConstraintsTablePanel_enableButton_actionAdapter(this));
    disableButton.addActionListener(new ConstraintsTablePanel_disableButton_actionAdapter(this));
    dropButton.addActionListener(new ConstraintsTablePanel_dropButton_actionAdapter(this));
    enableAllButton.setBorder(null);
    enableAllButton.setPreferredSize(new Dimension(24, 24));
    disableAllButton.setBorder(null);
    disableAllButton.setPreferredSize(new Dimension(24, 24));
    enableButton.setBorder(null);
    enableButton.setPreferredSize(new Dimension(24, 24));
    disableButton.setBorder(null);
    disableButton.setPreferredSize(new Dimension(24, 24));
    dropButton.setBorder(null);
    dropButton.setPreferredSize(new Dimension(24, 24));
    enableAllImage = ImageLoader.getInstance().getIcon("enableall.gif");
    disableAllImage = ImageLoader.getInstance().getIcon("disableall.gif");
    enableImage = ImageLoader.getInstance().getIcon("enable.gif");
    disableImage = ImageLoader.getInstance().getIcon("disable.gif");
    dropImage = ImageLoader.getInstance().getIcon("drop.gif");

    enableAllButton.setIcon(enableAllImage);
    enableAllButton.setToolTipText(Options.getInstance().getResource("enable all constraints"));
    enableAllButton.setMaximumSize(new Dimension(24,24));
    disableAllButton.setIcon(disableAllImage);
    disableAllButton.setToolTipText(Options.getInstance().getResource("disable all constraints"));
    disableAllButton.setMaximumSize(new Dimension(24,24));
    enableButton.setIcon(enableImage);
    enableButton.setToolTipText(Options.getInstance().getResource("enable the current constraint"));
    enableButton.setMaximumSize(new Dimension(24,24));
    disableButton.setIcon(disableImage);
    disableButton.setToolTipText(Options.getInstance().getResource("disable the current constraint"));
    disableButton.setMaximumSize(new Dimension(24,24));
    dropButton.setIcon(dropImage);
    dropButton.setToolTipText(Options.getInstance().getResource("drop constraint"));
    dropButton.setMaximumSize(new Dimension(24,24));

    buttonsPanel.add(enableAllButton, null);
    buttonsPanel.add(disableAllButton, null);
    buttonsPanel.add(enableButton, null);
    buttonsPanel.add(disableButton, null);
    buttonsPanel.add(dropButton, null);

  }


  public String getQuery(String tableName,DbConnectionUtil dbConnUtil) {
    return cons.getQueryConstraints(tableName);
  }


  void enableAllButton_actionPerformed(ActionEvent e) {
    Object[] row = new Object[getDataPanel().getTableModel().getColumnCount()];
    for(int j=0;j<getDataPanel().getTableModel().getRowCount();j++) {
      for(int i=0;i<getDataPanel().getTableModel().getColumnCount();i++)
        row[i] = getDataPanel().getTableModel().getValueAt(j,i);
      cons.enableConstraint(tableName,row);
    }
    this.setQuery(tableName);
  }

  void disableAllButton_actionPerformed(ActionEvent e) {
    Object[] row = new Object[getDataPanel().getTableModel().getColumnCount()];
    for(int j=0;j<getDataPanel().getTableModel().getRowCount();j++) {
      for(int i=0;i<getDataPanel().getTableModel().getColumnCount();i++)
        row[i] = getDataPanel().getTableModel().getValueAt(j,i);
      cons.disableConstraint(tableName,row);
    }
    this.setQuery(tableName);
  }

  void enableButton_actionPerformed(ActionEvent e) {
    if (getDataPanel().getTable().getSelectedRow()!=-1) {
      Object[] row = new Object[getDataPanel().getTableModel().getColumnCount()];
      for(int i=0;i<getDataPanel().getTableModel().getColumnCount();i++)
        row[i] = getDataPanel().getTableModel().getValueAt(getDataPanel().getTable().getSelectedRow(),i);
      cons.enableConstraint(tableName,row);
      this.setQuery(tableName);
    }
  }

  void disableButton_actionPerformed(ActionEvent e) {
    if (getDataPanel().getTable().getSelectedRow()!=-1) {
      Object[] row = new Object[getDataPanel().getTableModel().getColumnCount()];
      for(int i=0;i<getDataPanel().getTableModel().getColumnCount();i++)
        row[i] = getDataPanel().getTableModel().getValueAt(getDataPanel().getTable().getSelectedRow(),i);
      cons.disableConstraint(tableName,row);
      this.setQuery(tableName);
    }
  }

  void dropButton_actionPerformed(ActionEvent e) {
    if (getDataPanel().getTable().getSelectedRow()!=-1) {
      if (JOptionPane.showConfirmDialog(
          parent,
          Options.getInstance().getResource("are you sure to drop constraint?"),
          Options.getInstance().getResource("drop contraint"),
          JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE)==1
      )
        return;
      Object[] row = new Object[getDataPanel().getTableModel().getColumnCount()];
      for(int i=0;i<getDataPanel().getTableModel().getColumnCount();i++)
        row[i] = getDataPanel().getTableModel().getValueAt(getDataPanel().getTable().getSelectedRow(),i);
      cons.dropConstraint(tableName,row);
      this.setQuery(tableName);
    }
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
    return Options.getInstance().getResource("table constraints plugin");
  }



}

class ConstraintsTablePanel_enableAllButton_actionAdapter implements java.awt.event.ActionListener {
  ConstraintsTablePanel adaptee;

  ConstraintsTablePanel_enableAllButton_actionAdapter(ConstraintsTablePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.enableAllButton_actionPerformed(e);
  }
}

class ConstraintsTablePanel_disableAllButton_actionAdapter implements java.awt.event.ActionListener {
  ConstraintsTablePanel adaptee;

  ConstraintsTablePanel_disableAllButton_actionAdapter(ConstraintsTablePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.disableAllButton_actionPerformed(e);
  }
}

class ConstraintsTablePanel_enableButton_actionAdapter implements java.awt.event.ActionListener {
  ConstraintsTablePanel adaptee;

  ConstraintsTablePanel_enableButton_actionAdapter(ConstraintsTablePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.enableButton_actionPerformed(e);
  }
}

class ConstraintsTablePanel_disableButton_actionAdapter implements java.awt.event.ActionListener {
  ConstraintsTablePanel adaptee;

  ConstraintsTablePanel_disableButton_actionAdapter(ConstraintsTablePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.disableButton_actionPerformed(e);
  }
}

class ConstraintsTablePanel_dropButton_actionAdapter implements java.awt.event.ActionListener {
  ConstraintsTablePanel adaptee;

  ConstraintsTablePanel_dropButton_actionAdapter(ConstraintsTablePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.dropButton_actionPerformed(e);
  }
}
