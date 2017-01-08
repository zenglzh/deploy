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
 * <p>Description: Dialog used to add a column to a table.
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
public class AddColumnDialog extends JDialog {

  JPanel mainPnel = new JPanel();

  /** current table name */
  private String tableName = null;

  /** database connection util */
  private DbConnectionUtil dbConnUtil = null;

  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel colNameLabel = new JLabel();
  JTextField colNameTF = new JTextField();
  JLabel colTypeLabel = new JLabel();
  JComboBox colTypeComboBox = new JComboBox();
  JLabel sizeLabel = new JLabel();
  JTextField sizeTF = new JTextField();
  JLabel decLabel = new JLabel();
  JTextField decTF = new JTextField();
  JLabel defLabel = new JLabel();
  JTextField defTF = new JTextField();
  JPanel buttonsPanel = new JPanel();
  JButton execButton = new JButton();
  JButton cancelButton = new JButton();
  private JFrame parent = null;
  JCheckBox notNullCheckBox = new JCheckBox();


  public AddColumnDialog(JFrame parent,String tableName,DbConnectionUtil dbConnUtil) {
    super(parent, Options.getInstance().getResource("add column"), true);
    this.parent = parent;
    this.tableName = tableName;
    this.dbConnUtil = dbConnUtil;
    try {
      jbInit();
      init();
      setSize(400,200);
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      setLocation((int)screenSize.getWidth()/2-170,(int)screenSize.getHeight()/2-30);
      setVisible(true);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Initialize column type combo box.
   */
  private void init() {
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    model.addElement("CHAR");
    model.addElement("VARCHAR");
    model.addElement("VARCHAR2");
    model.addElement("LONG");
    model.addElement("LONGRAW");
    model.addElement("LONGVARCHAR");
    model.addElement("NUMBER");
    model.addElement("NUMERIC");
    model.addElement("INTEGER");
    model.addElement("INT");
    model.addElement("DECIMAL");
    model.addElement("BIGINT");
    model.addElement("REAL");
    model.addElement("DOUBLE");
    model.addElement("FLOAT");
    model.addElement("SMALLINT");
    model.addElement("BOOLEAN");
    model.addElement("DATE");
    model.addElement("DATETIME");
    model.addElement("TIMESTAMP");
    model.addElement("LONGVARBINARY");
    model.addElement("BLOB");
    model.addElement("CLOB");

    colTypeComboBox.setModel(model);
  }


  private void jbInit() throws Exception {
    mainPnel.setLayout(gridBagLayout1);
    colNameLabel.setText(Options.getInstance().getResource("column name"));
    colTypeLabel.setToolTipText("");
    colTypeLabel.setText(Options.getInstance().getResource("column type"));
    sizeLabel.setToolTipText("");
    sizeLabel.setText(Options.getInstance().getResource("size"));
    sizeTF.setColumns(5);
    decLabel.setText(",");
    decTF.setColumns(5);
    defLabel.setText(Options.getInstance().getResource("default"));
    mainPnel.setBorder(BorderFactory.createEtchedBorder());
    execButton.setText(Options.getInstance().getResource("execute"));
    execButton.addActionListener(new AddColumnDialog_execButton_actionAdapter(this));
    cancelButton.setText(Options.getInstance().getResource("cancelbutton.text"));
    cancelButton.addActionListener(new AddColumnDialog_cancelButton_actionAdapter(this));
    notNullCheckBox.setText(Options.getInstance().getResource("not null"));
    getContentPane().add(mainPnel);
    mainPnel.add(colNameLabel,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    mainPnel.add(colNameTF,     new GridBagConstraints(1, 0, 4, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    mainPnel.add(colTypeLabel,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    mainPnel.add(colTypeComboBox,       new GridBagConstraints(1, 1, 4, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 150, 0));
    mainPnel.add(sizeLabel,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    mainPnel.add(sizeTF,    new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    mainPnel.add(decLabel,    new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    mainPnel.add(decTF,    new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
    mainPnel.add(defLabel,    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    mainPnel.add(defTF,      new GridBagConstraints(1, 3, 4, 1, 1.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    mainPnel.add(notNullCheckBox,  new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    this.getContentPane().add(buttonsPanel,  BorderLayout.SOUTH);
    buttonsPanel.add(execButton, null);
    buttonsPanel.add(cancelButton, null);
  }


  void execButton_actionPerformed(ActionEvent e) {
    if (colNameTF.getText().trim().length()==0) {
      JOptionPane.showMessageDialog(
          parent,
          Options.getInstance().getResource("please specify the column name"),
          Options.getInstance().getResource("attention"),
          JOptionPane.WARNING_MESSAGE);
      return;
    }
    if (colTypeComboBox.getSelectedIndex()==-1) {
      JOptionPane.showMessageDialog(
          parent,
          Options.getInstance().getResource("please specify the column type"),
          Options.getInstance().getResource("attention"),
          JOptionPane.WARNING_MESSAGE);
      return;
    }
    if (sizeTF.getText().trim().length()==0 && !(
        colTypeComboBox.getSelectedItem().equals("DATE") ||
        colTypeComboBox.getSelectedItem().equals("DATETIME") ||
        colTypeComboBox.getSelectedItem().equals("TIMESTAMP") ||
        colTypeComboBox.getSelectedItem().equals("BLOB") ||
        colTypeComboBox.getSelectedItem().equals("CLOB") ||
        colTypeComboBox.getSelectedItem().equals("NUMBER")
        )) {
      JOptionPane.showMessageDialog(
          parent,
          Options.getInstance().getResource("please specify the column size"),
          Options.getInstance().getResource("attention"),
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    try {
      String sql = "ALTER TABLE " + tableName+ " ADD "+colNameTF.getText()+" "+colTypeComboBox.getSelectedItem();
      if (!(colTypeComboBox.getSelectedItem().equals("DATE") ||
            colTypeComboBox.getSelectedItem().equals("DATETIME") ||
            colTypeComboBox.getSelectedItem().equals("TIMESTAMP") ||
            colTypeComboBox.getSelectedItem().equals("BLOB") ||
            colTypeComboBox.getSelectedItem().equals("CLOB")) &&
          sizeTF.getText().trim().length()>0) {
         sql += "("+sizeTF.getText().trim();
         if (decTF.getText().trim().length()>0)
           sql += ","+decTF.getText().trim();
        sql += ") ";
        if (defTF.getText().length()>0)
          sql += " DEFAULT "+defTF.getText();
        if (notNullCheckBox.isSelected())
          sql += " NOT NULL";
      }


      PreparedStatement pstmt = dbConnUtil.getConn().prepareStatement(sql);
      pstmt.execute();
      pstmt.close();
      setVisible(false);
      dispose();
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(
          parent,
          Options.getInstance().getResource("error while adding column:")+"\n"+ex.getMessage(),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE);
    }


  }

  void cancelButton_actionPerformed(ActionEvent e) {
    setVisible(false);
    dispose();
  }



}

class AddColumnDialog_execButton_actionAdapter implements java.awt.event.ActionListener {
  AddColumnDialog adaptee;

  AddColumnDialog_execButton_actionAdapter(AddColumnDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.execButton_actionPerformed(e);
  }
}

class AddColumnDialog_cancelButton_actionAdapter implements java.awt.event.ActionListener {
  AddColumnDialog adaptee;

  AddColumnDialog_cancelButton_actionAdapter(AddColumnDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cancelButton_actionPerformed(e);
  }
}