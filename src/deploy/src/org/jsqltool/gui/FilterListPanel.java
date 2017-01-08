package org.jsqltool.gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import org.jsqltool.utils.Options;
import java.util.HashSet;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Panel used in TableListFrame, inside the tabbed panes, to filter the lists.
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
public class FilterListPanel extends JPanel {
  JLabel skipLabel = new JLabel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JTextField skipTF = new JTextField();
  JLabel findLabel = new JLabel();
  JTextField findTF = new JTextField();

  /** controller used to re-load list or find inside it */
  private FilterListController controller = null;

  /** entities list */
  private JList list = null;


  public FilterListPanel(JList list,FilterListController controller) {
    this.list = list;
    this.controller = controller;
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  void jbInit() throws Exception {
    skipLabel.setText(Options.getInstance().getResource("skip with chars"));
    this.setLayout(gridBagLayout1);
    skipTF.setText("$/");
    skipTF.addKeyListener(new FilterListPanel_skipTF_keyAdapter(this));
    findLabel.setText(Options.getInstance().getResource("find"));
    findTF.setText("");
    findTF.addKeyListener(new FilterListPanel_findTF_keyAdapter(this));
    this.add(skipLabel,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    this.add(skipTF,  new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    this.add(findLabel,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    this.add(findTF,  new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
  }


  void findTF_keyTyped(KeyEvent e) {
    String pattern = findTF.getText();
    if (e.getKeyChar()!='\b')
      pattern += String.valueOf(e.getKeyChar());
    int index = -1;
    int len = 0;
    for(int i=0;i<list.getModel().getSize();i++)
      if (list.getModel().getElementAt(i).toString().toLowerCase().startsWith(pattern.toLowerCase())) {
        if (pattern.length()>len) {
          index = i;
          len = pattern.length();
        }
      }
    if (index!=-1) {
      list.setSelectedIndex(index);
      try {
        list.scrollRectToVisible(list.getCellBounds(index,index));
      }
      catch (Exception ex) {
      }
    }

  }


  void skipTF_keyTyped(KeyEvent e) {
    new Thread() {
      public void run() {
        ProgressDialog.getInstance().startProgress();
        try {
          controller.reloadList();
        }
        catch (Throwable ex) {
        }
        finally {
          ProgressDialog.getInstance().stopProgress();
        }
      }
    }.start();
  }


  public String getFilterPattern() {
    return skipTF.getText();
  }


}

class FilterListPanel_findTF_keyAdapter extends java.awt.event.KeyAdapter {
  FilterListPanel adaptee;

  FilterListPanel_findTF_keyAdapter(FilterListPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void keyTyped(KeyEvent e) {
    adaptee.findTF_keyTyped(e);
  }
}

class FilterListPanel_skipTF_keyAdapter extends java.awt.event.KeyAdapter {
  FilterListPanel adaptee;

  FilterListPanel_skipTF_keyAdapter(FilterListPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void keyTyped(KeyEvent e) {
    adaptee.skipTF_keyTyped(e);
  }
}