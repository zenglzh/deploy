package org.jsqltool.jgraph.gui;

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
 * <p>Description: Window used to load a database schema profile file.
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
public class LoadFileDialog extends JDialog {
  JPanel mainPanel = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();

  /** main frame */
  private MainFrame frame = null;

  /** parent frame */
  private SchemaFrame parentFrame = null;
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel profilesLabel = new JLabel();
  JScrollPane scrollPane = new JScrollPane();
  JList profilesList = new JList();
  JButton okButton = new JButton();
  JButton cancelButton = new JButton();

  /** connection name (used as prefix in a profile file name) */
  private String dbConnName = null;


  public LoadFileDialog(MainFrame frame,SchemaFrame parentFrame,String dbConnName) {
    super(frame, Options.getInstance().getResource("load database schema profile file"), true);
    this.frame = frame;
    this.parentFrame = parentFrame;
    this.dbConnName = dbConnName;
    init();
    try {
      jbInit();
      setSize(400,300);
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


  /**
   * Constructor not supported.
   */
  public LoadFileDialog() {
    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }


  /**
   * Fill in the profiles list.
   */
  private void init() {
    // retrieve .ini file list...
    File dir = new File("profile");
    dir.mkdir();
    File[] files = dir.listFiles(new FileFilter() {
      public boolean accept(File pathname) {
        return
            pathname.getName().startsWith(dbConnName+"_") &&
            pathname.getName().endsWith(".sch");
      }
    });
    DefaultListModel model = new DefaultListModel();
    for(int i=0;i<files.length;i++)
      model.addElement(files[i].getName().substring((dbConnName+"_").length(),files[i].getName().length()-4).replace('_',' '));
    profilesList.setModel(model);
    profilesList.revalidate();

    profilesList.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount()==2 && profilesList.getSelectedIndex()!=-1) {
          parentFrame.loadProfile(
              "profile/" +
              dbConnName + "_" +
              profilesList.getSelectedValue().toString().replace(' ', '_') +
              ".sch"
          );
          setVisible(false);
        }
      }
    });

  }


  private void jbInit() throws Exception {
    mainPanel.setLayout(gridBagLayout1);
    profilesLabel.setText(Options.getInstance().getResource("profiles"));
    profilesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    okButton.setToolTipText("");
    okButton.setText(Options.getInstance().getResource("okbutton.text"));
    okButton.addActionListener(new LoadFileDialog_okButton_actionAdapter(this));
    okButton.setMnemonic(Options.getInstance().getResource("okbutton.mnemonic").charAt(0));
    cancelButton.setText(Options.getInstance().getResource("cancelbutton.text"));
    cancelButton.addActionListener(new LoadFileDialog_cancelButton_actionAdapter(this));
    cancelButton.setMnemonic(Options.getInstance().getResource("cancelbutton.mnemonic").charAt(0));
    getContentPane().add(mainPanel);
    mainPanel.add(profilesLabel,     new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    mainPanel.add(scrollPane,    new GridBagConstraints(0, 1, 1, 2, 1.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    scrollPane.getViewport().add(profilesList, null);
    mainPanel.add(okButton,    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    mainPanel.add(cancelButton,   new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
  }


  void okButton_actionPerformed(ActionEvent e) {
    if (profilesList.getSelectedIndex()!=-1) {
      parentFrame.loadProfile("profile/"+dbConnName+"_"+profilesList.getSelectedValue().toString().replace(' ','_')+".sch");
      setVisible(false);
    }
  }


  void cancelButton_actionPerformed(ActionEvent e) {
    setVisible(false);
  }

}

class LoadFileDialog_okButton_actionAdapter implements java.awt.event.ActionListener {
  LoadFileDialog adaptee;

  LoadFileDialog_okButton_actionAdapter(LoadFileDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.okButton_actionPerformed(e);
  }
}

class LoadFileDialog_cancelButton_actionAdapter implements java.awt.event.ActionListener {
  LoadFileDialog adaptee;

  LoadFileDialog_cancelButton_actionAdapter(LoadFileDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cancelButton_actionPerformed(e);
  }
}