package org.jsqltool.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.math.BigDecimal;
import org.jsqltool.utils.Options;
import org.jsqltool.utils.ImageLoader;
import org.jsqltool.gui.graphics.CalendarCombo;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Dialog used by SQLFrame to find out sql parameters and prompt user to set them.
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
public class ParametersDialog extends JDialog {

  JPanel mainPanel = new JPanel();
  JPanel buttonsPanel = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton okButton = new JButton();
  JLabel typeLabel = new JLabel();
  JComboBox typeComboBox = new JComboBox();
  JLabel valueLabel = new JLabel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();

  private static final String TEXT = Options.getInstance().getResource("text");
  private static final String NUM = Options.getInstance().getResource("numeric");
  private static final String DATE = Options.getInstance().getResource("date");

  private CalendarCombo date = new CalendarCombo();
  private JTextField text = new JTextField();
  private JTextField num = new JTextField();

  /** current input control */
  private Component currentControl = null;

  /** current value */
  private Object value = null;

  /** position of parameter */
  private int pos = -1;

  /** values used by query */
  private Vector values = null;

  /** sql to analyze */
  private String sql = null;

  /** parent frame */
  private SQLFrame frame = null;

  /** MDI frame */
  private JFrame parent = null;


  /**
   * Constructor called by SQLFrame.
   */
  public ParametersDialog(JFrame parent,SQLFrame frame,String sql,Vector values) {
    super(parent,Options.getInstance().getResource("variable"),false);
    this.parent = parent;
    this.frame = frame;
    this.sql = sql;
    this.values = values;
    try {
      init();
      jbInit();
      setSize(300,170);
      setLocation(parent.getWidth()/2-200,parent.getHeight()/2-70);

      // find thr first occurrence of ?
      findNextVar();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * Find the next occurrence of ? in sql.
   * If no other ? are found, then execute the sql, i.e. execute the method executeSQLWithValues.
   */
  private void findNextVar() {
    if (currentControl!=null)
      mainPanel.remove(currentControl);
    currentControl = null;
    typeComboBox.setSelectedIndex(-1);
    while((pos=sql.indexOf("?",pos+1))!=-1) {
      if (isParameter(sql,pos)) {
        break;
      }
    }
    if (pos!=-1)
      setVisible(true);
    else
      frame.executeSQLWithValues(sql,values);
  }


  public ParametersDialog() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * @param sql sql which contains patterns of type "=?" or ">?" or "<?"
   * @param pos position inside sql of ? character
   * @return <code>true</code> if no space character at the left of pos is "=" or "<" or ">"
   */
  private boolean isParameter(String sql,int pos) {
    if (pos==0)
      return false;
    for(int i=pos-1;i>=0;i--)
      if (sql.charAt(i)=='=' ||
          sql.charAt(i)=='<' ||
          sql.charAt(i)=='>')
        return true;
      else if (sql.charAt(i)!=' ')
        return false;
    return false;
  }


  /**
   * Initialize type combo.
   */
  private void init() {
    typeComboBox.addItem(TEXT);
    typeComboBox.addItem(NUM);
    typeComboBox.addItem(DATE);
    typeComboBox.setSelectedIndex(-1);

    text.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        if (e.getKeyChar()=='\n')
          okButton_actionPerformed(null);
      }
    });
  }


  private void jbInit() throws Exception {
    this.getContentPane().setLayout(borderLayout1);
    mainPanel.setBorder(BorderFactory.createEtchedBorder());
    mainPanel.setLayout(gridBagLayout1);
    buttonsPanel.setBorder(BorderFactory.createEtchedBorder());
    okButton.setText(Options.getInstance().getResource("okbutton.text"));
    okButton.setMnemonic(Options.getInstance().getResource("okbutton.mnemonic").charAt(0));
    okButton.addActionListener(new ParametersDialog_okButton_actionAdapter(this));
    typeLabel.setText(Options.getInstance().getResource("type"));
    typeComboBox.addItemListener(new ParametersDialog_typeComboBox_itemAdapter(this));
    valueLabel.setText(Options.getInstance().getResource("value"));
    this.getContentPane().add(mainPanel,  BorderLayout.CENTER);
    this.getContentPane().add(buttonsPanel,  BorderLayout.SOUTH);
    buttonsPanel.add(okButton, null);
    mainPanel.add(typeLabel,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    mainPanel.add(typeComboBox,   new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    mainPanel.add(valueLabel,     new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
  }


  void okButton_actionPerformed(ActionEvent e) {
    value = null;
    if (TEXT.equals(typeComboBox.getSelectedItem())) {
      value = text.getText();
      if (value.equals(""))
        value = null;
    }
    else if (NUM.equals(typeComboBox.getSelectedItem())) {
      value = new BigDecimal(num.getText());
    }
    else if (DATE.equals(typeComboBox.getSelectedItem())) {
      value = date.getDate();
      if (value!=null && value instanceof java.util.Date)
        value = new java.sql.Date(((java.util.Date)value).getTime());
    }
    if (value==null) {
      JOptionPane.showMessageDialog(
          parent,
          Options.getInstance().getResource("value is required"),
          Options.getInstance().getResource("attention"),
          JOptionPane.WARNING_MESSAGE
      );
      return;
    } else {
      values.add(value);
      text.setText(null);
      num.setText(null);
      date.setDate(null);
      setVisible(false);
      typeComboBox.setSelectedIndex(-1);
      findNextVar();
    }
  }


  void typeComboBox_itemStateChanged(ItemEvent e) {
    if (e.getStateChange()==e.SELECTED) {
      if (currentControl!=null)
        mainPanel.remove(currentControl);

      if (typeComboBox.getSelectedItem().equals(TEXT)) {
        currentControl = text;
      }
      else if (typeComboBox.getSelectedItem().equals(NUM)) {
        currentControl = num;
      }
      else if (typeComboBox.getSelectedItem().equals(DATE)) {
        if (currentControl!=null)
          mainPanel.remove(currentControl);
        currentControl = date;
      }

      if (currentControl!=null) {
        mainPanel.add(currentControl,
                      new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
                                             , GridBagConstraints.NORTHWEST,
                                             GridBagConstraints.HORIZONTAL,
                                             new Insets(5, 0, 5, 5), 0, 0));
        currentControl.requestFocus();
      }
      mainPanel.revalidate();
      mainPanel.repaint();
    }
  }






}

class ParametersDialog_okButton_actionAdapter implements java.awt.event.ActionListener {
  ParametersDialog adaptee;

  ParametersDialog_okButton_actionAdapter(ParametersDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.okButton_actionPerformed(e);
  }
}

class ParametersDialog_typeComboBox_itemAdapter implements java.awt.event.ItemListener {
  ParametersDialog adaptee;

  ParametersDialog_typeComboBox_itemAdapter(ParametersDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void itemStateChanged(ItemEvent e) {
    adaptee.typeComboBox_itemStateChanged(e);
  }
}
