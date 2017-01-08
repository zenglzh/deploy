package org.jsqltool.replication.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;

import org.jsqltool.replication.*;
import org.jsqltool.gui.*;
import org.jsqltool.utils.ImageLoader;
import org.jsqltool.utils.Options;
import org.jsqltool.conn.gui.ConnectionFrame;
import org.jsqltool.conn.DbConnection;
import org.jsqltool.conn.DbConnectionUtil;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Window used to define a data replication profile.
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
public class ReplicationDialog extends JDialog {
  JPanel mainPanel = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel nameLabel = new JLabel();
  JTextField nameTF = new JTextField();
  JLabel srcLabel = new JLabel();
  JComboBox srcComboBox = new JComboBox();
  JLabel destLabel = new JLabel();
  JComboBox destComboBox = new JComboBox();
  JPanel buttonsPanel = new JPanel();
  JButton okButton = new JButton();
  JButton cancelButton = new JButton();
  JLabel tablesLabel = new JLabel();
  JScrollPane tablesScrollPane = new JScrollPane();
  JList tablesList = new JList();
  JCheckBox recreateCheckBox = new JCheckBox();

  public static final int INSERT = 0;
  public static final int EDIT = 1;

  /** current window mode (edit/insert)*/
  private int mode;

  /** current replication profile */
  private ReplicationProfile profile = null;

  /** parent frame */
  private ReplicationFrame parentFrame =  null;

  /** main frame */
  private MainFrame frame = null;

  /** connections defined */
  private ArrayList conns = null;


  public ReplicationDialog(MainFrame frame,ReplicationFrame parentFrame,ReplicationProfile profile,int mode) {
    super(frame, Options.getInstance().getResource("data replication profile"), true);
    this.frame = frame;
    this.parentFrame = parentFrame;
    this.profile = profile;
    this.mode = mode;
    init();
    try {
      jbInit();
      setSize(500,400);
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension frameSize = this.getSize();
      if (frameSize.height > screenSize.height) {
        frameSize.height = screenSize.height;
      }
      if (frameSize.width > screenSize.width) {
        frameSize.width = screenSize.width;
      }
      this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
      setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
      setVisible(true);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }


  public ReplicationDialog() {
    try {
      jbInit();
      setSize(500,400);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }


  /**
   * Load combo-box connections and set window content according to the window mode.
   */
  private void init() {
    try {
      // load combo-box connections...
      ConnectionFrame f = new ConnectionFrame();
      conns = f.getConnections();
      DbConnection dbConn = null;
      DbConnection srcDbConn = null;
      for (int i = 0; i < conns.size(); i++) {
        dbConn = (DbConnection) conns.get(i);
        srcComboBox.addItem(dbConn.getName());
        destComboBox.addItem(dbConn.getName());
        if (mode == EDIT && profile.getSourceDatabase().equals(dbConn.getName())) {
          srcDbConn = dbConn;
        }
      }
      srcComboBox.setSelectedIndex(-1);
      destComboBox.setSelectedIndex(-1);

      // set window content according to the window mode...
      if (mode == EDIT) {
        nameTF.setText(profile.getName());
        srcComboBox.setSelectedItem(profile.getSourceDatabase());
        destComboBox.setSelectedItem(profile.getDestDatabase());
        recreateCheckBox.setSelected(profile.isRecreateTablesContent());
        DefaultListModel model = initTablesList(srcDbConn);
        int[] indexes = new int[profile.getTablesList().size()];
        for (int i = 0; i < profile.getTablesList().size(); i++) {
          indexes[i] = model.indexOf(profile.getTablesList().get(i));
        }
        tablesList.setSelectedIndices(indexes);
        nameTF.setEditable(false);
        srcComboBox.setEnabled(false);
        destComboBox.setEnabled(false);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          frame,
          Options.getInstance().getResource("error when initializing window")+":\n"+ex.getMessage(),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE
      );
    }
  }


  /**
   * Fill in the tables list, according to the selected source database.
   * @param dbConn source database descriptor
   * @return tables list model
   */
  private DefaultListModel initTablesList(DbConnection dbConn) {
    DefaultListModel model = new DefaultListModel();
    if (dbConn==null) {
      tablesList.setModel(model);
      tablesList.revalidate();
      tablesList.repaint();
      return model;
    }

    DbConnectionUtil dbUtil = new DbConnectionUtil(frame,dbConn);
    java.util.List tables = dbUtil.getTables(dbConn.getUsername(),"TABLE");
    for(int i=0;i<tables.size();i++)
      model.addElement(tables.get(i));
    tablesList.setModel(model);
    tablesList.revalidate();
    tablesList.repaint();
    return model;
  }


  private void jbInit() throws Exception {
    mainPanel.setLayout(gridBagLayout1);
    nameLabel.setText(Options.getInstance().getResource("profile name"));
    nameTF.setColumns(20);
    srcLabel.setText(Options.getInstance().getResource("source database"));
    destLabel.setText(Options.getInstance().getResource("target database"));
    buttonsPanel.setBorder(BorderFactory.createEtchedBorder());
    mainPanel.setBorder(BorderFactory.createEtchedBorder());
    okButton.setMnemonic(Options.getInstance().getResource("okbutton.mnemonic").charAt(0));
    okButton.setText(Options.getInstance().getResource("okbutton.text"));
    okButton.addActionListener(new ReplicationDialog_okButton_actionAdapter(this));
    cancelButton.setMnemonic(Options.getInstance().getResource("cancelbutton.mnemonic").charAt(0));
    cancelButton.setText(Options.getInstance().getResource("cancelbutton.text"));
    cancelButton.addActionListener(new ReplicationDialog_cancelButton_actionAdapter(this));
    destComboBox.addItemListener(new ReplicationDialog_destComboBox_itemAdapter(this));
    srcComboBox.addItemListener(new ReplicationDialog_srcComboBox_itemAdapter(this));
    tablesLabel.setText(Options.getInstance().getResource("source tables"));
    recreateCheckBox.setText(Options.getInstance().getResource("re-create tables content"));
    getContentPane().add(mainPanel);
    mainPanel.add(nameLabel,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    mainPanel.add(nameTF,    new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    mainPanel.add(srcLabel,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    mainPanel.add(srcComboBox,    new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    mainPanel.add(destLabel,    new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    mainPanel.add(destComboBox,   new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    this.getContentPane().add(buttonsPanel,  BorderLayout.SOUTH);
    buttonsPanel.add(okButton, null);
    buttonsPanel.add(cancelButton, null);
    mainPanel.add(tablesLabel,   new GridBagConstraints(0, 2, 4, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 5, 5, 5), 0, 0));
    mainPanel.add(tablesScrollPane,   new GridBagConstraints(0, 3, 4, 1, 1.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
    mainPanel.add(recreateCheckBox,  new GridBagConstraints(0, 4, 4, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    tablesScrollPane.getViewport().add(tablesList, null);
  }


  void okButton_actionPerformed(ActionEvent e) {
    if (nameTF.getText().trim().length()==0) {
      JOptionPane.showMessageDialog(
          frame,
          Options.getInstance().getResource("you must set a profile name"),
          Options.getInstance().getResource("attention"),
          JOptionPane.ERROR_MESSAGE
      );
      return;
    }
    if (srcComboBox.getSelectedIndex()==-1 ||
        destComboBox.getSelectedIndex()==-1) {
      JOptionPane.showMessageDialog(
          frame,
          Options.getInstance().getResource("you must set a source and target database"),
          Options.getInstance().getResource("attention"),
          JOptionPane.ERROR_MESSAGE
      );
      return;
    }
    if (tablesList.getSelectedIndices().length==0) {
      JOptionPane.showMessageDialog(
          frame,
          Options.getInstance().getResource("you must select at least one table"),
          Options.getInstance().getResource("attention"),
          JOptionPane.ERROR_MESSAGE
      );
      return;
    }

    // save the current profile...
    profile.setName(nameTF.getText().trim());
    profile.setSourceDatabase(srcComboBox.getSelectedItem().toString());
    profile.setDestDatabase(destComboBox.getSelectedItem().toString());
    profile.setRecreateTablesContent(recreateCheckBox.isSelected());
    ArrayList tables = new ArrayList();
    for(int i=0;i<tablesList.getSelectedValues().length;i++)
      tables.add( tablesList.getSelectedValues()[i].toString() );
    profile.setTablesList(tables);
    parentFrame.updateList(profile, mode == EDIT);
    setVisible(false);
  }


  void cancelButton_actionPerformed(ActionEvent e) {
    setVisible(false);
  }


  void destComboBox_itemStateChanged(ItemEvent e) {
    if (e.getStateChange()==e.SELECTED &&
        destComboBox.getSelectedIndex()!=-1) {
      if (srcComboBox.getSelectedIndex() != -1 &&
          srcComboBox.getSelectedItem().equals(destComboBox.getSelectedItem())) {
        JOptionPane.showMessageDialog(
            frame,
            Options.getInstance().getResource("you cannot set the same connection for source and target database"),
            Options.getInstance().getResource("attention"),
            JOptionPane.ERROR_MESSAGE
        );
        destComboBox.setSelectedIndex(-1);
      }
    }

  }


  void srcComboBox_itemStateChanged(ItemEvent e) {
    if (e.getStateChange()==e.SELECTED &&
        srcComboBox.getSelectedIndex()!=-1) {
      if (destComboBox.getSelectedIndex()!=-1 &&
          destComboBox.getSelectedItem().equals(srcComboBox.getSelectedItem())) {
        JOptionPane.showMessageDialog(
            frame,
            Options.getInstance().getResource("you cannot set the same connection for source and target database"),
            Options.getInstance().getResource("attention"),
            JOptionPane.ERROR_MESSAGE
        );
        srcComboBox.setSelectedIndex(-1);
        return;
      }
      initTablesList(
        srcComboBox.getSelectedIndex()==-1 ?
        null :
        (DbConnection)conns.get(srcComboBox.getSelectedIndex())
      );
    }

  }


}

class ReplicationDialog_okButton_actionAdapter implements java.awt.event.ActionListener {
  ReplicationDialog adaptee;

  ReplicationDialog_okButton_actionAdapter(ReplicationDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.okButton_actionPerformed(e);
  }
}

class ReplicationDialog_cancelButton_actionAdapter implements java.awt.event.ActionListener {
  ReplicationDialog adaptee;

  ReplicationDialog_cancelButton_actionAdapter(ReplicationDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cancelButton_actionPerformed(e);
  }
}

class ReplicationDialog_destComboBox_itemAdapter implements java.awt.event.ItemListener {
  ReplicationDialog adaptee;

  ReplicationDialog_destComboBox_itemAdapter(ReplicationDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void itemStateChanged(ItemEvent e) {
    adaptee.destComboBox_itemStateChanged(e);
  }
}

class ReplicationDialog_srcComboBox_itemAdapter implements java.awt.event.ItemListener {
  ReplicationDialog adaptee;

  ReplicationDialog_srcComboBox_itemAdapter(ReplicationDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void itemStateChanged(ItemEvent e) {
    adaptee.srcComboBox_itemStateChanged(e);
  }
}