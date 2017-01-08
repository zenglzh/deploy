package org.jsqltool.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.ArrayList;
import org.jsqltool.conn.gui.*;
import org.jsqltool.gui.tablepanel.*;
import org.jsqltool.utils.Options;
import org.jsqltool.utils.ImageLoader;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: About Window.
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
public class AboutBox extends JDialog implements ActionListener {

  JPanel panel1 = new JPanel();
  JPanel panel2 = new JPanel();
  JPanel insetsPanel1 = new JPanel();
  JPanel insetsPanel3 = new JPanel();
  JButton button1 = new JButton();
  JLabel label1 = new JLabel();
  JLabel label2 = new JLabel();
  JLabel label3 = new JLabel();
  ImageIcon image1 = new ImageIcon();
  BorderLayout borderLayout1 = new BorderLayout();
  JScrollPane scrollPane = new JScrollPane();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JTextArea other = new JTextArea();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JButton iconButton = new JButton(ImageLoader.getInstance().getIcon("about.gif"));


  public AboutBox(MainFrame parent) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
      init();
      setSize(400,400);
      button1.requestFocus();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  private void init() {
    other.append(Options.getInstance().getResource("plugins")+":\n");
/*
    other.append("\nJCalendar\n");
    other.append("© Kai Toedter 1999-2002\n");
    other.append("Version 1.1.4\n");
    other.append("07/17/02\n");
*/
    ArrayList panels = TableDetailPanel.getSortPanels();
    TablePlugin panel = null;
    for(int i=0;i<panels.size();i++) {
      panel = (TablePlugin)panels.get(i);
      other.append("\n"+panel.getName()+"\n");
      other.append(Options.getInstance().getResource("version")+" "+panel.getVersion()+"\n");
      if (panel.getAuthor()!=null && !panel.getAuthor().equals(""))
        other.append(Options.getInstance().getResource("author")+" "+panel.getAuthor()+"\n");
    }
    other.setCaretPosition(0);
  }


  //Component initialization
  private void jbInit() throws Exception  {
    image1 = ImageLoader.getInstance().getIcon("logo.gif");
    this.setTitle(Options.getInstance().getResource("about"));
    panel1.setLayout(borderLayout1);
    panel2.setLayout(gridBagLayout1);
    label1.setFont(new java.awt.Font("Dialog", 1, 11));
    label1.setText("JSQLTool");
    label2.setText(Options.getInstance().getResource("release")+" 1.1");
    label3.setText("Copyright (C) 2006 Mauro Carniel");
    insetsPanel3.setLayout(gridBagLayout2);
    insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 10));
    button1.setMnemonic(Options.getInstance().getResource("okbutton.mnemonic").charAt(0));
    button1.setText(Options.getInstance().getResource("okbutton.text"));
    button1.addActionListener(this);
    other.setBackground(SystemColor.activeCaptionBorder);
    other.setEditable(false);
    other.setText("");
    iconButton.setBorder(null);
    this.getContentPane().add(panel1, null);
    insetsPanel3.add(label1,     new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    insetsPanel3.add(label2,    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    insetsPanel3.add(label3,     new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    insetsPanel3.add(iconButton,    new GridBagConstraints(0, 0, 1, 3, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    panel2.add(scrollPane,    new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
    panel2.add(insetsPanel3,   new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    insetsPanel1.add(button1, null);
    panel1.add(insetsPanel1, BorderLayout.SOUTH);
    panel1.add(panel2,  BorderLayout.CENTER);
    scrollPane.getViewport().add(other, null);
    setResizable(true);
  }


  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }


  //Close the dialog
  void cancel() {
    dispose();
  }


  //Close the dialog on a button event
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == button1) {
      cancel();
    }
  }
}