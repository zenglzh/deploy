package org.jsqltool.conn.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;

import org.jsqltool.conn.*;
import org.jsqltool.gui.*;
import org.jsqltool.utils.ImageLoader;
import org.jsqltool.utils.Options;
import org.jsqltool.gui.tableplugins.datatable.filter.*;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Window used to select a database connection.
 * This window allows to create/edit/delete connections.
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
public class ConnectionFrame extends JInternalFrame {


  JScrollPane scrollPane = new JScrollPane();
  Vector connNames = new Vector();
  JList connList = new JList(connNames);
  JPanel buttonsPanel = new JPanel();
  JButton delButton = new JButton();
  JButton newButton = new JButton();
  JButton editButton = new JButton();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JButton okButton = new JButton();
  JButton cancelButton = new JButton();
  JLabel connLabel = new JLabel();
  private ArrayList conns = new ArrayList();
  private MainFrame parent = null;
  JButton copyButton = new JButton();


  public ConnectionFrame(MainFrame parent) {
    this.parent = parent;
    try {
      this.setFrameIcon( ImageLoader.getInstance().getIcon("logo.gif") );
      this.setSize(350,300);
      jbInit();
      init();
      toFront();
      setSelected(true);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * Constructor called by Replication Dialog to fetch connections list.
   */
  public ConnectionFrame() {
    loadProfile();
  }


  /**
   * @return list of DbConnection objects
   */
  public final ArrayList getConnections() {
    return conns;
  }


  private void init() {
    loadProfile();
    if (connList.getModel().getSize()>0) {
      connList.setSelectedIndex(0);
    }
  }


  public final void updateList(DbConnection c,boolean isEdit) {
    if (!isEdit) {
      conns.add(c);
      connNames.add(c.getName());
    } else {
      connNames.setElementAt(c.getName(),connList.getSelectedIndex());
    }
    new DbConnectionUtil(parent,c).saveProfile(isEdit);

    scrollPane.getViewport().removeAll();
    DefaultListModel model = new DefaultListModel();
    for(int i=0;i<connNames.size();i++)
      model.addElement(connNames.get(i));
    connList.setModel(model);
    connList.revalidate();
    connList.repaint();
    scrollPane.getViewport().add(connList, null);

  }


  /**
   * Load all connection profile files (files profile/*.ini)
   */
  private void loadProfile() {
    try {
      conns.clear();
      connNames.clear();

      // retrieve .ini file list...
      File dir = new File("profile");
      dir.mkdir();
      File[] files = dir.listFiles(new FileFilter() {
        public boolean accept(File pathname) {
          return pathname.getName().endsWith(".ini");
        }
      });

      // load all .ini files...
      ConnectionProfile cProfile = new ConnectionProfile();
      for(int i=0;i<files.length;i++) {
        cProfile.loadProfile(this,files[i],conns,connNames);
      }

      connList.revalidate();
    } catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          this,
          Options.getInstance().getResource("error on loading connections profile files.")+":\n"+ex.getMessage(),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE
      );
    }
  }


  private void jbInit() throws Exception {
    this.setIcon(false);
    this.setTitle(Options.getInstance().getResource("JSqlTool login"));
    this.setBorder(BorderFactory.createRaisedBevelBorder());
    this.setDebugGraphicsOptions(0);
    this.getContentPane().setLayout(gridBagLayout1);
    delButton.setMnemonic(Options.getInstance().getResource("deleteconn.mnemonic").charAt(0));
    delButton.setText(Options.getInstance().getResource("deleteconn.text"));
    delButton.addActionListener(new ConnectionFrame_delButton_actionAdapter(this));
    newButton.setMnemonic(Options.getInstance().getResource("newconn.mnemonic").charAt(0));
    newButton.setText(Options.getInstance().getResource("newconn.text"));
    newButton.addActionListener(new ConnectionFrame_newButton_actionAdapter(this));
    editButton.setMnemonic(Options.getInstance().getResource("editconn.mnemonic").charAt(0));
    editButton.setText(Options.getInstance().getResource("editconn.text"));
    editButton.addActionListener(new ConnectionFrame_editButton_actionAdapter(this));
    buttonsPanel.setLayout(gridBagLayout2);
    okButton.setMnemonic(Options.getInstance().getResource("okbutton.mnemonic").charAt(0));
    okButton.setText(Options.getInstance().getResource("okbutton.text"));
    okButton.addActionListener(new ConnectionFrame_okButton_actionAdapter(this));
    cancelButton.setMnemonic(Options.getInstance().getResource("cancelbutton.mnemonic").charAt(0));
    cancelButton.setText(Options.getInstance().getResource("cancelbutton.text"));
    cancelButton.addActionListener(new ConnectionFrame_cancelButton_actionAdapter(this));
    connLabel.setText(Options.getInstance().getResource("connections"));
    copyButton.setMnemonic(Options.getInstance().getResource("copyconn.mnemonic").charAt(0));
    copyButton.setText(Options.getInstance().getResource("copyconn.text"));
    copyButton.addActionListener(new ConnectionFrame_copyButton_actionAdapter(this));
    buttonsPanel.setBorder(BorderFactory.createEtchedBorder());
    connList.addMouseListener(new ConnectionFrame_connList_mouseAdapter(this));
    connList.addKeyListener(new ConnectionFrame_connList_keyAdapter(this));
    scrollPane.getViewport().add(connList, null);
    this.getContentPane().add(buttonsPanel,     new GridBagConstraints(1, 0, 1, 2, 1.0, 1.0
            ,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    buttonsPanel.add(okButton,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    buttonsPanel.add(delButton,     new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    buttonsPanel.add(newButton,     new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    buttonsPanel.add(editButton,      new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    this.getContentPane().add(scrollPane,    new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
    this.getContentPane().add(connLabel,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    buttonsPanel.add(cancelButton,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    buttonsPanel.add(copyButton,    new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
  }


  void editButton_actionPerformed(ActionEvent e) {
    if (connList.getSelectedIndex()==-1)
      return;
    new ConnectionDialog(parent,this,(DbConnection)conns.get(connList.getSelectedIndex()),ConnectionDialog.EDIT);
  }


  void newButton_actionPerformed(ActionEvent e) {
    new ConnectionDialog(parent,this,new DbConnection(0,"","","","","",false,0,false,"",new Hashtable(),new ArrayList(),false),ConnectionDialog.INSERT);
  }


  void delButton_actionPerformed(ActionEvent e) {
    if (connList.getSelectedIndex()==-1)
      return;
    DbConnection c = (DbConnection)conns.remove(connList.getSelectedIndex());
    connNames.remove(connList.getSelectedIndex());

    scrollPane.getViewport().removeAll();
    connList = new JList(connNames);
    scrollPane.getViewport().add(connList, null);

    new File("profile/"+c.getName().replace(' ','_')+".ini").delete();
  }


  void okButton_actionPerformed(ActionEvent e) {
    if (connList.getSelectedIndex()==-1)
      return;
    new Thread() {
      public void run() {
        setVisible(false);
        DbConnection c = (DbConnection)conns.get(connList.getSelectedIndex());
        try {
          parent.createTableListFrame(new DbConnectionUtil(parent,c));
          dispose();
        }
        catch (Throwable ex) {
        }
      }
    }.start();
  }


  void cancelButton_actionPerformed(ActionEvent e) {
    this.setVisible(false);
    this.dispose();
  }

  void copyButton_actionPerformed(ActionEvent e) {
    if (connList.getSelectedIndex()==-1)
      return;
    DbConnection c = (DbConnection)conns.get(connList.getSelectedIndex());
    new ConnectionDialog(
        parent,
        this,
        new DbConnection(
          c.getDbType(),
          "",
          c.getClassName(),
          c.getUrl(),
          c.getUsername(),
          c.getPassword(),
          c.isAutoCommit(),
          c.getIsolationLevel(),
          c.isReadOnly(),
          c.getCatalog(),
          new Hashtable(),
          new ArrayList(),
          c.isQuotes()
        ),
        ConnectionDialog.COPY
    );
  }

  void connList_mouseClicked(MouseEvent e) {
    if (connList.getSelectedIndex()!=-1 && e.getClickCount()==2 && SwingUtilities.isLeftMouseButton(e))
      okButton_actionPerformed(null);
  }


  void connList_keyTyped(KeyEvent e) {
    if (e.getKeyChar()=='\n' &&
        connList.getSelectedIndex()!=-1)
      okButton_actionPerformed(null);
  }


  public void requestFocus() {
    connList.requestFocus();
  }


}

class ConnectionFrame_editButton_actionAdapter implements java.awt.event.ActionListener {
  ConnectionFrame adaptee;

  ConnectionFrame_editButton_actionAdapter(ConnectionFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.editButton_actionPerformed(e);
  }
}

class ConnectionFrame_newButton_actionAdapter implements java.awt.event.ActionListener {
  ConnectionFrame adaptee;

  ConnectionFrame_newButton_actionAdapter(ConnectionFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.newButton_actionPerformed(e);
  }
}

class ConnectionFrame_delButton_actionAdapter implements java.awt.event.ActionListener {
  ConnectionFrame adaptee;

  ConnectionFrame_delButton_actionAdapter(ConnectionFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.delButton_actionPerformed(e);
  }
}

class ConnectionFrame_okButton_actionAdapter implements java.awt.event.ActionListener {
  ConnectionFrame adaptee;

  ConnectionFrame_okButton_actionAdapter(ConnectionFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.okButton_actionPerformed(e);
  }
}

class ConnectionFrame_cancelButton_actionAdapter implements java.awt.event.ActionListener {
  ConnectionFrame adaptee;

  ConnectionFrame_cancelButton_actionAdapter(ConnectionFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cancelButton_actionPerformed(e);
  }
}

class ConnectionFrame_copyButton_actionAdapter implements java.awt.event.ActionListener {
  ConnectionFrame adaptee;

  ConnectionFrame_copyButton_actionAdapter(ConnectionFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.copyButton_actionPerformed(e);
  }
}

class ConnectionFrame_connList_mouseAdapter extends java.awt.event.MouseAdapter {
  ConnectionFrame adaptee;

  ConnectionFrame_connList_mouseAdapter(ConnectionFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseClicked(MouseEvent e) {
    adaptee.connList_mouseClicked(e);
  }
}

class ConnectionFrame_connList_keyAdapter extends java.awt.event.KeyAdapter {
  ConnectionFrame adaptee;

  ConnectionFrame_connList_keyAdapter(ConnectionFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void keyTyped(KeyEvent e) {
    adaptee.connList_keyTyped(e);
  }
}