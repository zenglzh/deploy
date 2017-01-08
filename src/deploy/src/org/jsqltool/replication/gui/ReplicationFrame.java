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


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Window used to create, edit or delete Data Replication Profiles.
 * It also allows to start a Data Replication process.
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
public class ReplicationFrame extends JInternalFrame {


  JScrollPane scrollPane = new JScrollPane();
  Vector profileNames = new Vector();
  JList profileList = new JList(profileNames);
  JPanel buttonsPanel = new JPanel();
  JButton delButton = new JButton();
  JButton newButton = new JButton();
  JButton editButton = new JButton();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JButton okButton = new JButton();
  JButton cancelButton = new JButton();
  JLabel profileLabel = new JLabel();
  private ArrayList profiles = new ArrayList();
  private MainFrame parent = null;


  public ReplicationFrame(MainFrame parent) {
    this.parent = parent;
    try {
      this.setFrameIcon( ImageLoader.getInstance().getIcon("logo.gif") );
      this.setSize(450,300);
      jbInit();
      init();
      toFront();
      setSelected(true);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  public ReplicationFrame() {
    this(null);
  }


  /**
   * Load ReplicationProfile objecs e fill in the profile list.
   */
  private void init() {
    loadProfiles();
    if (profileList.getModel().getSize()>0)
      profileList.setSelectedIndex(0);

  }


  private void jbInit() throws Exception {
    this.setIcon(false);
    this.setTitle(Options.getInstance().getResource("data replication profiles"));
    this.setBorder(BorderFactory.createRaisedBevelBorder());
    this.setDebugGraphicsOptions(0);
    this.getContentPane().setLayout(gridBagLayout1);
    delButton.setMnemonic(Options.getInstance().getResource("deleteprofiles.mnemonic").charAt(0));
    delButton.setText(Options.getInstance().getResource("deleteprofile.text"));
    delButton.addActionListener(new ReplicationFrame_delButton_actionAdapter(this));
    newButton.setMnemonic(Options.getInstance().getResource("newprofile.mnemonic").charAt(0));
    newButton.setText(Options.getInstance().getResource("newprofile.text"));
    newButton.addActionListener(new ReplicationFrame_newButton_actionAdapter(this));
    editButton.setMnemonic(Options.getInstance().getResource("editprofile.mnemonic").charAt(0));
    editButton.setText(Options.getInstance().getResource("editprofile.text"));
    editButton.addActionListener(new ReplicationFrame_editButton_actionAdapter(this));
    buttonsPanel.setLayout(gridBagLayout2);
    okButton.setMnemonic(Options.getInstance().getResource("execbutton.mnemonic").charAt(0));
    okButton.setText(Options.getInstance().getResource("execbutton.text"));
    okButton.addActionListener(new ReplicationFrame_okButton_actionAdapter(this));
    cancelButton.setMnemonic(Options.getInstance().getResource("cancelbutton.mnemonic").charAt(0));
    cancelButton.setText(Options.getInstance().getResource("cancelbutton.text"));
    cancelButton.addActionListener(new ReplicationFrame_cancelButton_actionAdapter(this));
    profileLabel.setText(Options.getInstance().getResource("profiles"));
    buttonsPanel.setBorder(BorderFactory.createEtchedBorder());
    profileList.addMouseListener(new ReplicationFrame_profileList_mouseAdapter(this));
    profileList.addKeyListener(new ReplicationFrame_profileList_keyAdapter(this));
    scrollPane.getViewport().add(profileList, null);
    this.getContentPane().add(buttonsPanel,     new GridBagConstraints(1, 0, 1, 2, 1.0, 1.0
            ,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    buttonsPanel.add(okButton,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    buttonsPanel.add(delButton,     new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    buttonsPanel.add(newButton,     new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    buttonsPanel.add(editButton,      new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    this.getContentPane().add(scrollPane,    new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
    this.getContentPane().add(profileLabel,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    buttonsPanel.add(cancelButton,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
  }


  void editButton_actionPerformed(ActionEvent e) {
    if (profileList.getSelectedIndex()==-1)
      return;
    new ReplicationDialog(parent,this,(ReplicationProfile)profiles.get(profileList.getSelectedIndex()),ReplicationDialog.EDIT);
  }


  void newButton_actionPerformed(ActionEvent e) {
    new ReplicationDialog(parent,this,new ReplicationProfile(),ReplicationDialog.INSERT);
  }


  void delButton_actionPerformed(ActionEvent e) {
    if (profileList.getSelectedIndex()==-1)
      return;
    ReplicationProfile p = (ReplicationProfile)profiles.remove(profileList.getSelectedIndex());
    profileNames.remove(profileList.getSelectedIndex());

    scrollPane.getViewport().removeAll();
    profileList = new JList(profileNames);
    scrollPane.getViewport().add(profileList, null);

    new File("profile/"+p.getName().replace(' ','_')+".rep").delete();
  }


  void okButton_actionPerformed(ActionEvent e) {
    if (profileList.getSelectedIndex()==-1)
      return;
    new Thread() {
      public void run() {
        setVisible(false);
        ReplicationProfile profile = (ReplicationProfile)profiles.get(profileList.getSelectedIndex());
        new Replication(parent,profile);
      }
    }.start();
  }


  void cancelButton_actionPerformed(ActionEvent e) {
    this.setVisible(false);
    this.dispose();
  }


  void profileList_mouseClicked(MouseEvent e) {
    if (profileList.getSelectedIndex()!=-1 && e.getClickCount()==2 && SwingUtilities.isLeftMouseButton(e))
      okButton_actionPerformed(null);
  }


  void profileList_keyTyped(KeyEvent e) {
    if (e.getKeyChar()=='\n' &&
        profileList.getSelectedIndex()!=-1)
      okButton_actionPerformed(null);
  }


  public void requestFocus() {
    profileList.requestFocus();
  }


  public final void updateList(ReplicationProfile profile,boolean isEdit) {
    if (!isEdit) {
      profiles.add(profile);
      profileNames.add(profile.getName());
    } else {
      profileNames.setElementAt(profile.getName(),profileList.getSelectedIndex());
    }
    profile.saveProfile(parent,isEdit);

    scrollPane.getViewport().removeAll();
    DefaultListModel model = new DefaultListModel();
    for(int i=0;i<profileNames.size();i++)
      model.addElement(profileNames.get(i));
    profileList.setModel(model);
    profileList.revalidate();
    profileList.repaint();
    scrollPane.getViewport().add(profileList, null);

  }


  /**
   * Load all replication profile files (files profile/*.rep)
   */
  private void loadProfiles() {
    try {
      profiles.clear();
      profileNames.clear();

      // retrieve .ini file list...
      File dir = new File("profile");
      dir.mkdir();
      File[] files = dir.listFiles(new FileFilter() {
        public boolean accept(File pathname) {
          return pathname.getName().endsWith(".rep");
        }
      });

      // load all .rep files...
      ReplicationProfile cProfile = null;
      for(int i=0;i<files.length;i++) {
        cProfile = new ReplicationProfile();
        cProfile.loadProfile(this,files[i],profiles,profileNames);
      }

      profileList.revalidate();
    } catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          this,
          Options.getInstance().getResource("error on loading replication profile files.")+":\n"+ex.getMessage(),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE
      );
    }
  }



}

class ReplicationFrame_editButton_actionAdapter implements java.awt.event.ActionListener {
  ReplicationFrame adaptee;

  ReplicationFrame_editButton_actionAdapter(ReplicationFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.editButton_actionPerformed(e);
  }
}

class ReplicationFrame_newButton_actionAdapter implements java.awt.event.ActionListener {
  ReplicationFrame adaptee;

  ReplicationFrame_newButton_actionAdapter(ReplicationFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.newButton_actionPerformed(e);
  }
}

class ReplicationFrame_delButton_actionAdapter implements java.awt.event.ActionListener {
  ReplicationFrame adaptee;

  ReplicationFrame_delButton_actionAdapter(ReplicationFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.delButton_actionPerformed(e);
  }
}

class ReplicationFrame_okButton_actionAdapter implements java.awt.event.ActionListener {
  ReplicationFrame adaptee;

  ReplicationFrame_okButton_actionAdapter(ReplicationFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.okButton_actionPerformed(e);
  }
}

class ReplicationFrame_cancelButton_actionAdapter implements java.awt.event.ActionListener {
  ReplicationFrame adaptee;

  ReplicationFrame_cancelButton_actionAdapter(ReplicationFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cancelButton_actionPerformed(e);
  }
}

class ReplicationFrame_profileList_mouseAdapter extends java.awt.event.MouseAdapter {
  ReplicationFrame adaptee;

  ReplicationFrame_profileList_mouseAdapter(ReplicationFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseClicked(MouseEvent e) {
    adaptee.profileList_mouseClicked(e);
  }
}

class ReplicationFrame_profileList_keyAdapter extends java.awt.event.KeyAdapter {
  ReplicationFrame adaptee;

  ReplicationFrame_profileList_keyAdapter(ReplicationFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void keyTyped(KeyEvent e) {
    adaptee.profileList_keyTyped(e);
  }
}
