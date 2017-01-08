package org.jsqltool.gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import org.jsqltool.utils.Options;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.Properties;


import org.jsqltool.utils.Options;
import org.jsqltool.utils.ImageLoader;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Dialog used to set application properties.
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
public class OptionsDialog extends JDialog {

  JPanel mainPanel = new JPanel();
  JPanel buttonsPanel = new JPanel();
  JPanel centerPanel = new JPanel();
  JButton cancelButton = new JButton();
  JButton okButton = new JButton();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel dateFormatLabel = new JLabel();
  JTextField dateFormatTF = new JTextField();
  JLabel planTableLabel = new JLabel();
  JTextField planTableTF = new JTextField();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  MainFrame frame = null;
  JLabel updNoPkLabel = new JLabel();
  JCheckBox updNoPK = new JCheckBox();
  JLabel languageLabel = new JLabel();
  JTextField languageTF = new JTextField();



  public OptionsDialog(MainFrame frame) {
    super(frame, Options.getInstance().getResource("options"), true);
    this.frame = frame;
    try {
      jbInit();
      pack();
      init();
      setSize(500,220);
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension frameSize = this.getSize();
      if (frameSize.height > screenSize.height) {
        frameSize.height = screenSize.height;
      }
      if (frameSize.width > screenSize.width) {
        frameSize.width = screenSize.width;
      }
      this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
      setVisible(true);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }


  public void init() {
    dateFormatTF.setText(Options.getInstance().getDateFormat());
    planTableTF.setText(Options.getInstance().getOracleExplainPlanTable());
    updNoPK.setSelected(Options.getInstance().isUpdateWhenNoPK());
    languageTF.setText(Options.getInstance().getLanguage());
  }


  public OptionsDialog() {
    this(null);
  }


  private void jbInit() throws Exception {
    mainPanel.setLayout(gridBagLayout1);
    cancelButton.setMnemonic(Options.getInstance().getResource("cancelbutton.mnemonic").charAt(0));
    cancelButton.setText(Options.getInstance().getResource("cancelbutton.text"));
    cancelButton.addActionListener(new OptionsDialog_cancelButton_actionAdapter(this));
    okButton.setMnemonic(Options.getInstance().getResource("okbutton.mnemonic").charAt(0));
    okButton.setText(Options.getInstance().getResource("okbutton.text"));
    okButton.addActionListener(new OptionsDialog_okButton_actionAdapter(this));
    buttonsPanel.setBorder(BorderFactory.createEtchedBorder());
    dateFormatLabel.setText(Options.getInstance().getResource("date format"));
    dateFormatTF.setText("");
    dateFormatTF.setColumns(20);
    planTableLabel.setText(Options.getInstance().getResource("oracle plan table name"));
    planTableTF.setText("");
    planTableTF.setColumns(20);
    centerPanel.setLayout(gridBagLayout2);
    centerPanel.setBorder(BorderFactory.createEtchedBorder());
    updNoPkLabel.setText(Options.getInstance().getResource("enable update when no pk found"));
    languageLabel.setText(Options.getInstance().getResource("language"));
    getContentPane().add(mainPanel);
    mainPanel.add(buttonsPanel,    new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    mainPanel.add(centerPanel,   new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    centerPanel.add(dateFormatLabel,       new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    buttonsPanel.add(okButton, null);
    buttonsPanel.add(cancelButton, null);
    centerPanel.add(dateFormatTF,        new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    centerPanel.add(planTableTF,        new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    centerPanel.add(planTableLabel,       new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
    centerPanel.add(updNoPkLabel,       new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
    centerPanel.add(updNoPK,      new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    centerPanel.add(languageLabel,   new GridBagConstraints(0, 3, 1, 1, 0.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    centerPanel.add(languageTF,  new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
  }


  void okButton_actionPerformed(ActionEvent e) {
    if (dateFormatTF.getText().length()==0) {
      JOptionPane.showMessageDialog(
          frame,
          Options.getInstance().getResource("date format not specified."),
          Options.getInstance().getResource("attention"),
          JOptionPane.WARNING_MESSAGE
      );
      return;
    }
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(dateFormatTF.getText());
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(
          frame,
          Options.getInstance().getResource("invalid date format.\nexample")+": dd-MM-yyyy hh:mm:ss,SSS",
          Options.getInstance().getResource("attention"),
          JOptionPane.WARNING_MESSAGE
      );
      return;
    }
    if (planTableTF.getText().length()==0) {
      JOptionPane.showMessageDialog(
          frame,
          Options.getInstance().getResource("explain plan table not specified."),
          Options.getInstance().getResource("attention"),
          JOptionPane.WARNING_MESSAGE
      );
      return;
    }
    Options.getInstance().setDateFormat( dateFormatTF.getText() );
    Options.getInstance().setOracleExplainPlanTable( planTableTF.getText() );
    Options.getInstance().setUpdateWhenNoPK( updNoPK.isSelected() );
    Options.getInstance().setLanguage( languageTF.getText() );
    try {
      File profileFile = new File("jsqltool.ini");
      Properties p = new Properties();
      p.setProperty("DATE_FORMAT",Options.getInstance().getDateFormat());
      p.setProperty("ORACLE_EXPLAIN_PLAN_TABLE",Options.getInstance().getOracleExplainPlanTable());
      p.setProperty("UPDATE_WHEN_NO_PK",String.valueOf(Options.getInstance().isUpdateWhenNoPK()));
      p.setProperty("LANGUAGE",Options.getInstance().getLanguage());
      p.store(new FileOutputStream(profileFile),"JSQLTOOL Properties");
    } catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          frame,
          Options.getInstance().getResource("error while saving")+" jsqltool.ini:\n"+ex.getMessage(),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE
      );
    }
    setVisible(false);
    dispose();
  }

  void cancelButton_actionPerformed(ActionEvent e) {
    setVisible(false);
    dispose();
  }


}


class OptionsDialog_okButton_actionAdapter implements java.awt.event.ActionListener {
  OptionsDialog adaptee;

  OptionsDialog_okButton_actionAdapter(OptionsDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.okButton_actionPerformed(e);
  }
}

class OptionsDialog_cancelButton_actionAdapter implements java.awt.event.ActionListener {
  OptionsDialog adaptee;

  OptionsDialog_cancelButton_actionAdapter(OptionsDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cancelButton_actionPerformed(e);
  }
}