package org.jsqltool.conn.gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.Connection;
import org.jsqltool.conn.*;
import org.jsqltool.utils.Options;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Dialog used to edit/delete a connection.
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
public class ConnectionDialog extends JDialog {
  JPanel mainPanel = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel centralPanel = new JPanel();
  JPanel buttonsPanel = new JPanel();
  JButton cancelButton = new JButton();
  JButton okButton = new JButton();
  JPanel typePanel = new JPanel();
  JLabel connTypeLabel = new JLabel();
  JComboBox connTypeComboBox = new JComboBox(new String[]{
    Options.getInstance().getResource("oracle database"),
    Options.getInstance().getResource("ms sqlserver"),
    Options.getInstance().getResource("odbc source"),
    Options.getInstance().getResource("other source")
  });
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JPanel defaultPanel = new JPanel();
  JPanel odbcPanel = new JPanel();
  JPanel otherPanel = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel connPanel = new JPanel();
  JLabel usernameLabel = new JLabel();
  JTextField usernameTF = new JTextField();
  JLabel passwdLabel = new JLabel();
  JPasswordField passwdTF = new JPasswordField();
  JLabel SIDLabel = new JLabel();
  JTextField SIDTF = new JTextField();
  JLabel hostLabel = new JLabel();
  JTextField hostTF = new JTextField();
  JLabel portLabel = new JLabel();
  JTextField portTF = new JTextField();
  JLabel nameLabel = new JLabel();
  JTextField nameTF = new JTextField();
  JCheckBox autoCommitCheckBox = new JCheckBox();
  JLabel isolLabel = new JLabel();
  JComboBox isolComboBox = new JComboBox(new String[]{
                                         "TRANSACTION_NONE",
                                         "TRANSACTION_READ_COMMITTED",
                                         "TRANSACTION_READ_UNCOMMITTED",
                                         "TRANSACTION_REPEATABLE_READ",
                                         "TRANSACTION_SERIALIZABLE"});
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JLabel nameLabel1 = new JLabel();
  JTextField nameTF1 = new JTextField();
  JLabel classNameLabel = new JLabel();
  JTextField classNameTF = new JTextField();
  JLabel urlLabel = new JLabel();
  JTextField urlTF = new JTextField();
  JCheckBox autoCommitCheckBox1 = new JCheckBox();
  JLabel isolLabel1 = new JLabel();
  JComboBox isolComboBox1 = new JComboBox(new String[]{
                                         "TRANSACTION_NONE",
                                         "TRANSACTION_READ_COMMITTED",
                                         "TRANSACTION_READ_UNCOMMITTED",
                                         "TRANSACTION_REPEATABLE_READ",
                                         "TRANSACTION_SERIALIZABLE"});
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  ConnectionFrame parent = null;
  DbConnection c = null;
  JLabel nameLabel2 = new JLabel();
  JTextField nameTF2 = new JTextField();
  JLabel odbcLabel = new JLabel();
  JTextField odbcTF = new JTextField();
  JLabel usaernameLabel2 = new JLabel();
  JTextField usernameTF2 = new JTextField();
  JLabel passwdLabel2 = new JLabel();
  JPasswordField passwdTF2 = new JPasswordField();
  JCheckBox autoCommitCheckBox2 = new JCheckBox();
  JLabel isolLabel2 = new JLabel();
  JComboBox isol2ComboBox = new JComboBox(new String[]{
                                         "TRANSACTION_NONE",
                                         "TRANSACTION_READ_COMMITTED",
                                         "TRANSACTION_READ_UNCOMMITTED",
                                         "TRANSACTION_REPEATABLE_READ",
                                         "TRANSACTION_SERIALIZABLE"});
  GridBagLayout gridBagLayout4 = new GridBagLayout();
  private int mode;
  JCheckBox readonlyCheckBox = new JCheckBox();
  JCheckBox readonly2CheckBox = new JCheckBox();
  JCheckBox readonly1CheckBox = new JCheckBox();
  JCheckBox quotes2CheckBox = new JCheckBox();
  private JLabel username1Label = new JLabel();
  private JTextField username1TF = new JTextField();
  private JLabel passwd1Label = new JLabel();
  private JPasswordField passwd1TF = new JPasswordField();
  private JLabel catalogLabel = new JLabel();
  private JTextField catalog1TF = new JTextField();

  public static final int EDIT = 0;
  public static final int INSERT = 1;
  public static final int COPY = 2;

  CardLayout cardLayout = new CardLayout();


  public ConnectionDialog(JFrame frame,ConnectionFrame parent,DbConnection c,int mode) {
    super(frame, Options.getInstance().getResource("database connection"), true);
    this.parent = parent;
    this.c = c;
    this.mode = mode;
    try {
      jbInit();
      if (mode!=INSERT)
        init();
      pack();
      setSize(500,450);
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


  public ConnectionDialog() {
    this(null,null,null,0);
  }


  private void init() {

    nameTF.setText(c.getName());
    nameTF1.setText(c.getName());
    nameTF2.setText(c.getName());
    usernameTF.setText(c.getUsername());
    passwdTF.setText(c.getPassword());
    username1TF.setText(c.getUsername());
    passwd1TF.setText(c.getPassword());
    catalog1TF.setText(c.getCatalog());
    usernameTF2.setText(c.getUsername());
    passwdTF2.setText(c.getPassword());
    urlTF.setText(c.getUrl());
    classNameTF.setText(c.getClassName());
    connTypeComboBox.setSelectedIndex(c.getDbType());
    autoCommitCheckBox.setSelected(c.isAutoCommit());
    autoCommitCheckBox1.setSelected(c.isAutoCommit());
    autoCommitCheckBox2.setSelected(c.isAutoCommit());
    isolComboBox.setSelectedIndex(c.getIsolationLevel());
    isolComboBox1.setSelectedIndex(c.getIsolationLevel());
    isol2ComboBox.setSelectedIndex(c.getIsolationLevel());
    SIDTF.setText(c.getSID());
    hostTF.setText(c.getHost());
    portTF.setText(c.getPort());
    odbcTF.setText(c.getSID());
    readonlyCheckBox.setSelected(c.isReadOnly());
    readonly1CheckBox.setSelected(c.isReadOnly());
    readonly2CheckBox.setSelected(c.isReadOnly());
    quotes2CheckBox.setSelected(c.isQuotes());
  }


  private void jbInit() throws Exception {
    connPanel.setLayout(cardLayout);

    mainPanel.setLayout(borderLayout1);
    buttonsPanel.setBorder(BorderFactory.createEtchedBorder());
    cancelButton.setMnemonic(Options.getInstance().getResource("cancelbutton.mnemonic").charAt(0));
    cancelButton.setText(Options.getInstance().getResource("cancelbutton.text"));
    cancelButton.addActionListener(new ConnectionDialog_cancelButton_actionAdapter(this));
    okButton.setMnemonic(Options.getInstance().getResource("okbutton.mnemonic").charAt(0));
    okButton.setText(Options.getInstance().getResource("okbutton.text"));
    okButton.addActionListener(new ConnectionDialog_okButton_actionAdapter(this));
    connTypeLabel.setText(Options.getInstance().getResource("database type"));
    typePanel.setLayout(gridBagLayout1);
    connTypeComboBox.addItemListener(new ConnectionDialog_connTypeComboBox_itemAdapter(this));
    centralPanel.setLayout(borderLayout2);
    defaultPanel.setLayout(gridBagLayout2);
    usernameLabel.setText(Options.getInstance().getResource("username"));
    usernameTF.setText("");
    usernameTF.setColumns(20);
    passwdLabel.setText(Options.getInstance().getResource("password"));
    passwdTF.setText("");
    passwdTF.setColumns(20);
    SIDLabel.setText("SID");
    SIDTF.setText("");
    SIDTF.setColumns(20);
    hostLabel.setText(Options.getInstance().getResource("host"));
    hostTF.setText("");
    hostTF.setColumns(20);
    portLabel.setText(Options.getInstance().getResource("port"));
    portTF.setText("");
    portTF.setColumns(5);
    nameLabel.setText(Options.getInstance().getResource("connection name"));
    nameTF.setText("");
    nameTF.setColumns(20);
    nameTF.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        nameTF_focusLost(e);
      }
    });
    nameTF1.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        nameTF1_focusLost(e);
      }
    });
    nameTF2.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        nameTF2_focusLost(e);
      }
    });

    autoCommitCheckBox.setText(Options.getInstance().getResource("auto commit"));
    isolLabel.setText(Options.getInstance().getResource("transaction isolation level"));
    otherPanel.setLayout(gridBagLayout3);
    nameLabel1.setText(Options.getInstance().getResource("connection name"));
    nameTF1.setText("");
    nameTF1.setColumns(20);
    classNameLabel.setRequestFocusEnabled(true);
    classNameLabel.setText(Options.getInstance().getResource("jdbc driver name"));
    classNameTF.setText("");
    classNameTF.setColumns(20);
    urlLabel.setText(Options.getInstance().getResource("connection url"));
    urlTF.setText("");
    urlTF.setColumns(40);
    autoCommitCheckBox1.setText(Options.getInstance().getResource("auto commit"));
    isolLabel1.setText(Options.getInstance().getResource("transaction isolation level"));
    odbcPanel.setLayout(gridBagLayout4);
    nameLabel2.setText(Options.getInstance().getResource("connection name"));
    nameTF2.setText("");
    nameTF2.setColumns(20);
    odbcLabel.setText(Options.getInstance().getResource("odbc entry"));
    odbcTF.setText("");
    odbcTF.setColumns(20);
    usaernameLabel2.setText(Options.getInstance().getResource("username"));
    usernameTF2.setText("");
    usernameTF2.setColumns(20);
    passwdLabel2.setText(Options.getInstance().getResource("password"));
    passwdTF2.setOpaque(true);
    passwdTF2.setText("");
    passwdTF2.setColumns(20);
    autoCommitCheckBox2.setText(Options.getInstance().getResource("auto commit"));
    isolLabel2.setText(Options.getInstance().getResource("transaction isolation level"));
    readonlyCheckBox.setText(Options.getInstance().getResource("read only"));
    readonly2CheckBox.setText(Options.getInstance().getResource("read only"));
    quotes2CheckBox.setText(Options.getInstance().getResource("add quotes"));
    readonly1CheckBox.setText(Options.getInstance().getResource("read only"));
    username1Label.setText(Options.getInstance().getResource("username"));
    username1TF.setColumns(20);
    passwd1Label.setText(Options.getInstance().getResource("password"));
    passwd1TF.setColumns(20);
    passwd1TF.addActionListener(new ConnectionDialog_passwd1TF_actionAdapter(this));
    catalogLabel.setText(Options.getInstance().getResource("catalog"));
    catalog1TF.setColumns(20);
    getContentPane().add(mainPanel);
    mainPanel.add(centralPanel,  BorderLayout.CENTER);
    centralPanel.add(connPanel,  BorderLayout.CENTER);
    mainPanel.add(buttonsPanel,  BorderLayout.SOUTH);
    buttonsPanel.add(okButton, null);
    buttonsPanel.add(cancelButton, null);
    mainPanel.add(typePanel, BorderLayout.NORTH);
    typePanel.add(connTypeLabel,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    typePanel.add(connTypeComboBox,   new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    connPanel.add("DEFAULT",defaultPanel);
    connPanel.add("ODBC",odbcPanel);
    odbcPanel.add(nameLabel2,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    defaultPanel.add(usernameLabel,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    connPanel.add("OTHER",otherPanel);
    otherPanel.add(nameLabel1,        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    defaultPanel.add(usernameTF,    new GridBagConstraints(1, 1, 3, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    defaultPanel.add(passwdLabel,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    defaultPanel.add(passwdTF,    new GridBagConstraints(1, 2, 3, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    defaultPanel.add(SIDLabel,    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    defaultPanel.add(SIDTF,    new GridBagConstraints(1, 3, 3, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    defaultPanel.add(hostLabel,    new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    defaultPanel.add(hostTF,    new GridBagConstraints(1, 4, 3, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    defaultPanel.add(portLabel,    new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    defaultPanel.add(portTF,      new GridBagConstraints(1, 5, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    defaultPanel.add(nameLabel,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    defaultPanel.add(nameTF,    new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    defaultPanel.add(autoCommitCheckBox,    new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
    defaultPanel.add(isolLabel,     new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
    defaultPanel.add(isolComboBox,     new GridBagConstraints(3, 6, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    defaultPanel.add(readonlyCheckBox,   new GridBagConstraints(0, 7, 1, 1, 0.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    otherPanel.add(nameTF1,        new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    otherPanel.add(classNameLabel,        new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    otherPanel.add(classNameTF,        new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    otherPanel.add(urlLabel,        new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    otherPanel.add(urlTF,        new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    otherPanel.add(autoCommitCheckBox1,         new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
    odbcPanel.add(nameTF2,    new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    odbcPanel.add(odbcLabel,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    odbcPanel.add(odbcTF,    new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    odbcPanel.add(usaernameLabel2,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    odbcPanel.add(usernameTF2,    new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    odbcPanel.add(passwdLabel2,    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    odbcPanel.add(passwdTF2,    new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    odbcPanel.add(autoCommitCheckBox2,      new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    odbcPanel.add(isolLabel2,    new GridBagConstraints(1, 4, 1, 2, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    odbcPanel.add(isol2ComboBox,    new GridBagConstraints(2, 4, 1, 2, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    odbcPanel.add(readonly2CheckBox,   new GridBagConstraints(0, 5, 1, 1, 0.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    otherPanel.add(readonly1CheckBox,      new GridBagConstraints(0, 8, 1, 1, 0.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    otherPanel.add(username1Label,       new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    otherPanel.add(username1TF,      new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    otherPanel.add(passwd1Label,    new GridBagConstraints(0, 4, 1, 2, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    otherPanel.add(isolLabel1,      new GridBagConstraints(1, 7, 1, 2, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    otherPanel.add(passwd1TF,    new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    otherPanel.add(catalogLabel,  new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    otherPanel.add(catalog1TF,   new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    otherPanel.add(isolComboBox1,    new GridBagConstraints(2, 7, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    otherPanel.add(quotes2CheckBox,   new GridBagConstraints(0, 9, 1, 1, 0.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

    cardLayout.show(connPanel,"DEFAULT");
  }

  void okButton_actionPerformed(ActionEvent e) {
    if (connTypeComboBox.getSelectedIndex()==3) {
      if (nameTF1.getText().length()==0) {
        JOptionPane.showMessageDialog(
            this,
            Options.getInstance().getResource("you must specify a connection name."),
            Options.getInstance().getResource("attention"),
            JOptionPane.WARNING_MESSAGE
        );
        return;
      }
      if (classNameTF.getText().length()==0) {
        JOptionPane.showMessageDialog(
            this,
            Options.getInstance().getResource("you must specify a jdbc driver."),
            Options.getInstance().getResource("attention"),
            JOptionPane.WARNING_MESSAGE
        );
        return;
      }
      if (urlTF.getText().length()==0) {
        JOptionPane.showMessageDialog(
            this,
            Options.getInstance().getResource("you must specify a connection url."),
            Options.getInstance().getResource("attention"),
            JOptionPane.WARNING_MESSAGE
        );
        return;
      }
      if (username1TF.getText().length()==0) {
        JOptionPane.showMessageDialog(
            this,
            Options.getInstance().getResource("you must specify a connection username."),
            Options.getInstance().getResource("attention"),
            JOptionPane.WARNING_MESSAGE
        );
        return;
      }
/*
      if (passwd1TF.getText().length()==0) {
        JOptionPane.showMessageDialog(this,"E' necessario specificare la password di connessione.");
        return;
      }
*/
      c.setReadOnly(readonly1CheckBox.isSelected());
      c.setAutoCommit(autoCommitCheckBox1.isSelected());
      c.setClassName(classNameTF.getText());
      c.setDbType(c.OTHER_TYPE);
      c.setIsolationLevel(isolComboBox1.getSelectedIndex());
      c.setName(nameTF1.getText());
      c.setUrl(urlTF.getText());
      c.setUsername(username1TF.getText());
      c.setPassword(passwd1TF.getText());
      c.setCatalog(catalog1TF.getText());
      c.setQuotes(quotes2CheckBox.isSelected());
    } else if (connTypeComboBox.getSelectedIndex()==2) {
      if (nameTF2.getText().length()==0) {
        JOptionPane.showMessageDialog(
            this,
            Options.getInstance().getResource("you must specify a connection name."),
            Options.getInstance().getResource("attention"),
            JOptionPane.WARNING_MESSAGE
        );
        return;
      }
/*
      if (usernameTF2.getText().length()==0) {
        JOptionPane.showMessageDialog(this,"E' necessario specificare lo username di connessione.");
        return;
      }
*/
/*
      if (passwdTF2.getText().length()==0) {
        JOptionPane.showMessageDialog(this,"E' necessario specificare la password di connessione.");
        return;
      }
*/
      if (odbcTF.getText().length()==0) {
        JOptionPane.showMessageDialog(
            this,
            Options.getInstance().getResource("you must specify an odbc entry."),
            Options.getInstance().getResource("attention"),
            JOptionPane.WARNING_MESSAGE
        );
        return;
      }
      c.setReadOnly(readonly2CheckBox.isSelected());
      c.setAutoCommit(autoCommitCheckBox2.isSelected());
      c.setClassName(c.getClassName(connTypeComboBox.getSelectedIndex()));
      c.setDbType(c.ODBC_TYPE);
      c.setIsolationLevel(isol2ComboBox.getSelectedIndex());
      c.setName(nameTF2.getText());
      c.setUrl(c.getUrl(
          connTypeComboBox.getSelectedIndex(),
          "",
          "",
          odbcTF.getText())
      );
      c.setUsername(usernameTF2.getText());
      c.setPassword(passwdTF2.getText());
      c.setCatalog("");
    } else {
      if (nameTF.getText().length()==0) {
        JOptionPane.showMessageDialog(
            this,
            Options.getInstance().getResource("you must specify a connection name."),
            Options.getInstance().getResource("attention"),
            JOptionPane.WARNING_MESSAGE
        );
        return;
      }
      if (usernameTF.getText().length()==0) {
        JOptionPane.showMessageDialog(
            this,
            Options.getInstance().getResource("you must specify a connection username."),
            Options.getInstance().getResource("attention"),
            JOptionPane.WARNING_MESSAGE
        );
        return;
      }
/*
      if (passwdTF.getText().length()==0) {
        JOptionPane.showMessageDialog(this,"E' necessario specificare la password di connessione.");
        return;
      }
*/
      c.setReadOnly(readonlyCheckBox.isSelected());
      c.setAutoCommit(autoCommitCheckBox.isSelected());
      c.setClassName(c.getClassName(connTypeComboBox.getSelectedIndex()));
      c.setDbType(connTypeComboBox.getSelectedIndex());
      c.setIsolationLevel(isolComboBox.getSelectedIndex());
      c.setName(nameTF.getText());
      c.setUrl(c.getUrl(
          connTypeComboBox.getSelectedIndex(),
          hostTF.getText(),
          portTF.getText(),
          SIDTF.getText())
      );
      c.setUsername(usernameTF.getText());
      c.setPassword(passwdTF.getText());
      if (connTypeComboBox.getSelectedIndex()==DbConnection.SQLSERVER_TYPE)
        c.setCatalog("");
      else
        c.setCatalog(usernameTF.getText());
    }
    try {
      parent.updateList(c, mode == EDIT);
      setVisible(false);
      dispose();
    }
    catch (Exception ex) {
    }
  }

  void cancelButton_actionPerformed(ActionEvent e) {
    setVisible(false);
    dispose();
  }


  void connTypeComboBox_itemStateChanged(ItemEvent e) {
    if (connTypeComboBox.getSelectedIndex()==3) {
      cardLayout.show(connPanel,"OTHER");
    } else if (connTypeComboBox.getSelectedIndex()==2) {
      cardLayout.show(connPanel,"ODBC");
    } else {
      cardLayout.show(connPanel,"DEFAULT");
      if (mode==INSERT) {
        if (connTypeComboBox.getSelectedIndex()==DbConnection.ORACLE_TYPE)
          portTF.setText("1521");
        else if (connTypeComboBox.getSelectedIndex()==DbConnection.SQLSERVER_TYPE)
          portTF.setText("1434");

      }

    }
  }

  void passwd1TF_actionPerformed(ActionEvent e) {

  }


  void nameTF_focusLost(FocusEvent e) {
    nameTF.setText(nameTF.getText().replace(' ','_'));
  }

  void nameTF1_focusLost(FocusEvent e) {
    nameTF1.setText(nameTF1.getText().replace(' ','_'));
  }

  void nameTF2_focusLost(FocusEvent e) {
    nameTF2.setText(nameTF2.getText().replace(' ','_'));
  }

}

class ConnectionDialog_okButton_actionAdapter implements java.awt.event.ActionListener {
  ConnectionDialog adaptee;

  ConnectionDialog_okButton_actionAdapter(ConnectionDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.okButton_actionPerformed(e);
  }
}

class ConnectionDialog_cancelButton_actionAdapter implements java.awt.event.ActionListener {
  ConnectionDialog adaptee;

  ConnectionDialog_cancelButton_actionAdapter(ConnectionDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cancelButton_actionPerformed(e);
  }
}

class ConnectionDialog_connTypeComboBox_itemAdapter implements java.awt.event.ItemListener {
  ConnectionDialog adaptee;

  ConnectionDialog_connTypeComboBox_itemAdapter(ConnectionDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void itemStateChanged(ItemEvent e) {
    adaptee.connTypeComboBox_itemStateChanged(e);
  }
}

class ConnectionDialog_passwd1TF_actionAdapter implements java.awt.event.ActionListener {
  private ConnectionDialog adaptee;

  ConnectionDialog_passwd1TF_actionAdapter(ConnectionDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.passwd1TF_actionPerformed(e);
  }
}