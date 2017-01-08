package org.jsqltool.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import org.jsqltool.conn.*;
import org.jsqltool.*;
import java.beans.*;
import org.jsqltool.conn.gui.*;
import org.jsqltool.replication.gui.*;
import java.util.*;
import java.sql.*;
import java.io.*;
import org.jsqltool.utils.Options;
import org.jsqltool.utils.ImageLoader;
import java.lang.reflect.*;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: This is the main frame.
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
public class MainFrame extends JFrame {


  JPanel contentPane;
  JMenuBar menuBar = new JMenuBar();
  JMenu menuFile = new JMenu();
  JMenuItem menuFileExit = new JMenuItem();
  JMenu menuHelp = new JMenu();
  JMenuItem menuHelpAbout = new JMenuItem();
  JToolBar toolbar = new JToolBar();
  JButton queryButton = new JButton();
  JButton listaButton = new JButton();
  JButton schemaButton = new JButton();
  JButton commitButton = new JButton();
  JButton rollbackButton = new JButton();
  ImageIcon queryImage;
  ImageIcon listaImage;
  ImageIcon schemaImage;
  ImageIcon commitImage;
  ImageIcon rollbackImage;
  JPanel statusBar = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JMenuItem menuFileNewConn = new JMenuItem();
  JDesktopPane desktop = new JDesktopPane();
  JMenuItem menuFileEndItem = new JMenuItem();
  JMenuItem menuFileEndAllItem = new JMenuItem();
  ConnWindows connWindowManager = new ConnWindows(this,desktop,statusBar);
  JMenu viewMenu = new JMenu();
  JMenu sqlMenu = new JMenu();
  JMenu dbMenu = new JMenu();
  JMenu winMenu = new JMenu();
  JMenuItem currSqlMenuItem = new JMenuItem();
  JMenuItem recallListMenuItem = new JMenuItem();
  JMenuItem explainPlanMenuItem = new JMenuItem();
  JMenuItem importSQLMenuItem = new JMenuItem();
  JMenuItem schemaBrowserMenuItem = new JMenuItem();
  JMenuItem dbSchemaMenuItem = new JMenuItem();
  JMenuItem sqlEditorMenuItem = new JMenuItem();
  JMenuItem commitMenuItem = new JMenuItem();
  JMenuItem rollbackMenuItem = new JMenuItem();
  JMenuItem optionsMenuItem = new JMenuItem();
  JMenuItem closeAllMenuItem = new JMenuItem();
  JMenuItem traceMenuItem = new JMenuItem();
  JMenuItem menuFileRep = new JMenuItem();
  FlowLayout flowLayout1 = new FlowLayout();

  /** instance of the class */
  private static MainFrame instance = null;


  /**
   * Construct the frame.
   */
  public MainFrame() {
    instance = this;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    this.setSize((int)screenSize.getWidth(),(int)screenSize.getHeight()-24);
//    this.setSize(screenSize);
    JFrame.setDefaultLookAndFeelDecorated(true);
    GraphicsEnvironment env =
      GraphicsEnvironment.getLocalGraphicsEnvironment();
    this.setExtendedState(this.getExtendedState() | this.MAXIMIZED_BOTH);

    try {
      init();
      jbInit();
      desktop.setBackground(menuBar.getBackground());
      menuFileNewConn_actionPerformed(null);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * @return instance of the class
   */
  public static MainFrame getInstance() {
    return instance;
  }


  /**
   * Load from file system the application profile file (profile/jsqltool.ini).
   * If profile file doesn't exists, then create a new one.
   */
  private void init() {
    try {
      Properties p = new Properties();
      File profileFile = new File("jsqltool.ini");
      if (!profileFile.exists()) {
        p.setProperty("DATE_FORMAT","dd-MM-yyyy hh:mm:ss");
        p.setProperty("ORACLE_EXPLAIN_PLAN_TABLE","TOAD_PLAN_TABLE");
        p.setProperty("UPDATE_WHEN_NO_PK","true");
        p.setProperty("LANGUAGE",Locale.getDefault().getLanguage());
        p.save(new FileOutputStream(profileFile),"JSQLTOOL Properties");
      }
      else
        p.load(new FileInputStream(profileFile));

      // set onto Options singleton the values retrieved from profile.
      Options.getInstance().setDateFormat(p.getProperty("DATE_FORMAT","dd-MM-yyyy hh:mm:ss"));
      Options.getInstance().setOracleExplainPlanTable(p.getProperty("ORACLE_EXPLAIN_PLAN_TABLE","TOAD_PLAN_TABLE"));
      Options.getInstance().setUpdateWhenNoPK(p.getProperty("UPDATE_WHEN_NO_PK","true").equals("true"));
      Options.getInstance().setLanguage(p.getProperty("LANGUAGE",Locale.getDefault().getLanguage()));

    } catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          this,
          Options.getInstance().getResource("error while loading jsqltool.ini")+":\n"+ex.getMessage(),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE
      );
    }
  }


  /**
   * Initialize graphics components.
   */
  private void jbInit() throws Exception  {
    try {
      queryImage = ImageLoader.getInstance().getIcon("query.gif");
      listaImage = ImageLoader.getInstance().getIcon("tables.gif");
      schemaImage = ImageLoader.getInstance().getIcon("schema.gif");
      commitImage = ImageLoader.getInstance().getIcon("commit.gif");
      rollbackImage = ImageLoader.getInstance().getIcon("rollback.gif");
      setIconImage( ImageLoader.getInstance().getIcon("logo.gif").getImage());
    }
    catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          this,
          Options.getInstance().getResource("image files not found."),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE
      );
      System.exit(1);
    }
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setTitle("JSqlTool");
    menuFile.setMnemonic(Options.getInstance().getResource("file.mnemonic").charAt(0));
    menuFile.setText(Options.getInstance().getResource("file.text"));
    menuFileExit.setMnemonic(Options.getInstance().getResource("exit.mnemonic").charAt(0));
    menuFileExit.setText(Options.getInstance().getResource("exit.text"));
    menuFileExit.addActionListener(new MainFrame_menuFileExit_ActionAdapter(this));
    menuFileRep.setMnemonic(Options.getInstance().getResource("exit.mnemonic").charAt(0));
    menuFileRep.setText(Options.getInstance().getResource("data replication.text"));
    menuFileRep.addActionListener(new MainFrame_menuFileRep_ActionAdapter(this));

    menuHelp.setMnemonic(Options.getInstance().getResource("help.mnemonic").charAt(0));
    menuHelp.setText(Options.getInstance().getResource("help.text"));
    menuHelpAbout.setMnemonic(Options.getInstance().getResource("about.mnemonic").charAt(0));
    menuHelpAbout.setText(Options.getInstance().getResource("about.text"));
    menuHelpAbout.addActionListener(new MainFrame_menuHelpAbout_ActionAdapter(this));
    queryButton.setIcon(queryImage);
    queryButton.addActionListener(new MainFrame_queryButton_actionAdapter(this));
    queryButton.setToolTipText(Options.getInstance().getResource("querybutton.tooltip"));
    queryButton.setBorder(null);
    queryButton.setMaximumSize(new Dimension(24,24));
    queryButton.setPreferredSize(new Dimension(24, 24));
    listaButton.setBorder(null);
    listaButton.setMaximumSize(new Dimension(24,24));
    listaButton.setPreferredSize(new Dimension(24, 24));
    schemaButton.setBorder(null);
    schemaButton.setMaximumSize(new Dimension(24,24));
    schemaButton.setPreferredSize(new Dimension(24, 24));
    commitButton.setBorder(null);
    commitButton.setMaximumSize(new Dimension(24,24));
    commitButton.setPreferredSize(new Dimension(24, 24));
    rollbackButton.setBorder(null);
    rollbackButton.setMaximumSize(new Dimension(24,24));
    rollbackButton.setPreferredSize(new Dimension(24, 24));
    listaButton.setIcon(listaImage);
    listaButton.addActionListener(new MainFrame_listaButton_actionAdapter(this));
    listaButton.setToolTipText(Options.getInstance().getResource("listabutton.tooltip"));
    schemaButton.setIcon(schemaImage);
    schemaButton.addActionListener(new MainFrame_schemaButton_actionAdapter(this));
    schemaButton.setToolTipText(Options.getInstance().getResource("schemabutton.tooltip"));
    commitButton.setIcon(commitImage);
    commitButton.addActionListener(new MainFrame_commitButton_actionAdapter(this));
    commitButton.setToolTipText(Options.getInstance().getResource("commitbutton.tooltip"));
    rollbackButton.setIcon(rollbackImage);
    rollbackButton.addActionListener(new MainFrame_rollbackButton_actionAdapter(this));
    rollbackButton.setToolTipText(Options.getInstance().getResource("rollbackbutton.tooltip"));
    menuFileNewConn.setMnemonic(Options.getInstance().getResource("newconn.mnemonic").charAt(0));
    menuFileNewConn.setText(Options.getInstance().getResource("newconn.text"));
    menuFileNewConn.addActionListener(new MainFrame_menuFileNewConn_actionAdapter(this));
    menuFileEndItem.setMnemonic(Options.getInstance().getResource("endconn.mnemonic").charAt(0));
    menuFileEndItem.setText(Options.getInstance().getResource("endconn.text"));
    menuFileEndItem.addActionListener(new MainFrame_menuFileEndItem_actionAdapter(this));
    menuFileEndAllItem.setMnemonic(Options.getInstance().getResource("endallconn.mnemonic").charAt(0));
    menuFileEndAllItem.setText(Options.getInstance().getResource("endallconn.text"));
    menuFileEndAllItem.addActionListener(new MainFrame_menuFileEndAllItem_actionAdapter(this));
    sqlMenu.setMnemonic(Options.getInstance().getResource("sqlwindow.mnemonic").charAt(0));
    sqlMenu.setText(Options.getInstance().getResource("sqlwindow.text"));
    dbMenu.setMnemonic(Options.getInstance().getResource("database.mnemonic").charAt(0));
    dbMenu.setText(Options.getInstance().getResource("database.text"));
    viewMenu.setMnemonic(Options.getInstance().getResource("view.mnemonic").charAt(0));
    viewMenu.setText(Options.getInstance().getResource("view.text"));
    winMenu.setMnemonic(Options.getInstance().getResource("window.mnemonic").charAt(0));
    winMenu.setText(Options.getInstance().getResource("window.text"));
    currSqlMenuItem.setMnemonic(Options.getInstance().getResource("sql.mnemonic").charAt(0));
    currSqlMenuItem.setText(Options.getInstance().getResource("sql.text"));
    currSqlMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_F9, java.awt.event.KeyEvent.SHIFT_MASK, false));
    currSqlMenuItem.addActionListener(new MainFrame_currSqlMenuItem_actionAdapter(this));
    recallListMenuItem.setToolTipText("");
    recallListMenuItem.setMnemonic(Options.getInstance().getResource("oldsqllist.mnemonic").charAt(0));
    recallListMenuItem.setText(Options.getInstance().getResource("oldsqllist.text"));
    recallListMenuItem.addActionListener(new MainFrame_recallListMenuItem_actionAdapter(this));
    explainPlanMenuItem.setMnemonic(Options.getInstance().getResource("plan.mnemonic").charAt(0));
    explainPlanMenuItem.setText(Options.getInstance().getResource("plan.text"));
    explainPlanMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke('E', java.awt.event.KeyEvent.CTRL_MASK, false));
    explainPlanMenuItem.addActionListener(new MainFrame_explainPlanMenuItem_actionAdapter(this));

    importSQLMenuItem.setMnemonic(Options.getInstance().getResource("importsql.mnemonic").charAt(0));
    importSQLMenuItem.setText(Options.getInstance().getResource("importsql.text"));
    importSQLMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke('S', java.awt.event.KeyEvent.CTRL_MASK, false));
    importSQLMenuItem.addActionListener(new MainFrame_importSQLMenuItem_actionAdapter(this));

    schemaBrowserMenuItem.setMnemonic(Options.getInstance().getResource("schemabrowser.mnemonic").charAt(0));
    schemaBrowserMenuItem.setText(Options.getInstance().getResource("schemabrowser.text"));
    schemaBrowserMenuItem.addActionListener(new MainFrame_schemaBrowserMenuItem_actionAdapter(this));

    dbSchemaMenuItem.setMnemonic(Options.getInstance().getResource("dbschema.mnemonic").charAt(0));
    dbSchemaMenuItem.setText(Options.getInstance().getResource("dbschema.text"));
    dbSchemaMenuItem.addActionListener(new MainFrame_dbSchemaMenuItem_actionAdapter(this));

    sqlEditorMenuItem.setMnemonic(Options.getInstance().getResource("sqleditor.mnemonic").charAt(0));
    sqlEditorMenuItem.setText(Options.getInstance().getResource("sqleditor.text"));
    sqlEditorMenuItem.addActionListener(new MainFrame_sqlEditorMenuItem_actionAdapter(this));
    commitMenuItem.setMnemonic(Options.getInstance().getResource("commit.mnemonic").charAt(0));
    commitMenuItem.setText(Options.getInstance().getResource("commit.text"));
    commitMenuItem.addActionListener(new MainFrame_commitMenuItem_actionAdapter(this));
    rollbackMenuItem.setMnemonic(Options.getInstance().getResource("rollback.mnemonic").charAt(0));
    rollbackMenuItem.setText(Options.getInstance().getResource("rollback.text"));
    rollbackMenuItem.addActionListener(new MainFrame_rollbackMenuItem_actionAdapter(this));
    optionsMenuItem.setMnemonic(Options.getInstance().getResource("options.mnemonic").charAt(0));
    optionsMenuItem.setText(Options.getInstance().getResource("options.text"));
    optionsMenuItem.addActionListener(new MainFrame_optionsMenuItem_actionAdapter(this));
    closeAllMenuItem.setMnemonic(Options.getInstance().getResource("closeall.mnemonic").charAt(0));
    closeAllMenuItem.setText(Options.getInstance().getResource("closeall.text"));
    closeAllMenuItem.addActionListener(new MainFrame_closeAllMenuItem_actionAdapter(this));
    traceMenuItem.setMnemonic(Options.getInstance().getResource("trace.mnemonic").charAt(0));
    traceMenuItem.setText(Options.getInstance().getResource("trace.text"));
    traceMenuItem.addActionListener(new MainFrame_traceMenuItem_actionAdapter(this));
    statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
    statusBar.setPreferredSize(new Dimension(800, 30));
    statusBar.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    flowLayout1.setHgap(0);
    flowLayout1.setVgap(0);
    desktop.setBackground(Color.lightGray);
    desktop.setBorder(BorderFactory.createLoweredBevelBorder());
    toolbar.add(queryButton);
    toolbar.add(listaButton);
    toolbar.add(schemaButton);
    toolbar.add(commitButton);
    toolbar.add(rollbackButton);
    menuFile.add(menuFileNewConn);
    menuFile.add(menuFileEndItem);
    menuFile.add(menuFileEndAllItem);
    menuFile.addSeparator();
    menuFile.add(menuFileRep);
    menuFile.addSeparator();
    menuFile.add(menuFileExit);
    menuHelp.add(menuHelpAbout);
    menuBar.add(menuFile);
    menuBar.add(sqlMenu);
    menuBar.add(dbMenu);
    menuBar.add(viewMenu);
    menuBar.add(winMenu);
    menuBar.add(menuHelp);
    this.setJMenuBar(menuBar);
    contentPane.add(toolbar, BorderLayout.NORTH);
    contentPane.add(statusBar, BorderLayout.SOUTH);
    contentPane.add(desktop, BorderLayout.CENTER);
    sqlMenu.add(currSqlMenuItem);
    sqlMenu.addSeparator();
    sqlMenu.add(explainPlanMenuItem);
    sqlMenu.add(importSQLMenuItem);
    sqlMenu.addSeparator();
    sqlMenu.add(recallListMenuItem);
    dbMenu.add(schemaBrowserMenuItem);
    dbMenu.add(sqlEditorMenuItem);
    dbMenu.add(dbSchemaMenuItem);
    dbMenu.addSeparator();
    dbMenu.add(traceMenuItem);
    dbMenu.addSeparator();
    dbMenu.add(commitMenuItem);
    dbMenu.add(rollbackMenuItem);
    viewMenu.add(optionsMenuItem);
    winMenu.add(closeAllMenuItem);
    winMenu.addSeparator();
  }


  //File | Exit action performed
  public void menuFileExit_actionPerformed(ActionEvent e) {
    if (JOptionPane.showConfirmDialog(
        this,
        Options.getInstance().getResource("quit application?"),
        Options.getInstance().getResource("attention"),
        JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE
      )==0) {
      System.exit(0);
    }
  }


  //File | Data Replication action performed
  public void menuFileRep_actionPerformed(ActionEvent e) {
    final JInternalFrame f = new ReplicationFrame(this);
    desktop.add(f);
    Dimension mdiSize = this.getSize();
    Dimension frameSize = f.getSize();
    if (frameSize.height > mdiSize.height) {
      frameSize.height = mdiSize.height;
    }
    if (frameSize.width > mdiSize.width) {
      frameSize.width = mdiSize.width;
    }
    f.setLocation((mdiSize.width - frameSize.width) / 2, (mdiSize.height - frameSize.height) / 2);
    f.setVisible(true);

  }


  //Help | About action performed
  public void menuHelpAbout_actionPerformed(ActionEvent e) {
    AboutBox dlg = new AboutBox(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.show();
  }


  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      menuFileExit_actionPerformed(null);
    }
  }


  public void createTableListFrame(DbConnectionUtil dbConnUtil) {
    TableListFrame frame = new TableListFrame(this,dbConnUtil);
    connWindowManager.addWindow(frame);
  }


  public void createSchemaFrame(DbConnectionUtil dbConnUtil) {
    SchemaFrame frame = new SchemaFrame(this,dbConnUtil);
    connWindowManager.addWindow(frame);
  }


  public void createSQLFrame(DbConnectionUtil dbConnUtil) {
    SQLFrame frame = new SQLFrame(this,dbConnUtil);
    connWindowManager.addWindow(frame);
  }


  void menuFileNewConn_actionPerformed(ActionEvent e) {
    final JInternalFrame f = new ConnectionFrame(this);
    desktop.add(f);
    Dimension mdiSize = this.getSize();
    Dimension frameSize = f.getSize();
    if (frameSize.height > mdiSize.height) {
      frameSize.height = mdiSize.height;
    }
    if (frameSize.width > mdiSize.width) {
      frameSize.width = mdiSize.width;
    }
    f.setLocation((mdiSize.width - frameSize.width) / 2, (mdiSize.height - frameSize.height) / 2);
    f.setVisible(true);

    new Thread() {
      public void run() {
        try {
          try {
            sleep(500);
          }
          catch (InterruptedException ex1) {
          }
          f.toFront();
          f.setSelected(true);
          f.requestFocus();
        }
        catch (PropertyVetoException ex) {
        }
      }
    }.start();
  }


  void menuFileEndAllItem_actionPerformed(ActionEvent e) {
    if (connWindowManager.getCurrentDbConnUtil()!=null) {
      int answer = JOptionPane.showConfirmDialog(
          this,
          Options.getInstance().getResource("close all connections windows?"),
          Options.getInstance().getResource("close all connections"),
          JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE
      );
      if (answer==0)
        connWindowManager.closeAll();
    }
  }

  void menuFileEndItem_actionPerformed(ActionEvent e) {
    if (connWindowManager.getCurrentDbConnUtil()!=null) {
      int answer = JOptionPane.showConfirmDialog(
          this,
          Options.getInstance().getResource("close connection and related windows?"),
          Options.getInstance().getResource("close connection"),
          JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE
      );
      if (answer==0)
        connWindowManager.closeConnectionAndWindows(connWindowManager.getCurrentDbConnUtil());
    }
  }

  void queryButton_actionPerformed(ActionEvent e) {
    if (connWindowManager.getCurrentDbConnUtil()!=null)
      createSQLFrame(connWindowManager.getCurrentDbConnUtil());
  }

  void listaButton_actionPerformed(ActionEvent e) {
    if (connWindowManager.getCurrentDbConnUtil()!=null)
      createTableListFrame(connWindowManager.getCurrentDbConnUtil());
  }

  void schemaButton_actionPerformed(ActionEvent e) {
    if (connWindowManager.getCurrentDbConnUtil()!=null)
      createSchemaFrame(connWindowManager.getCurrentDbConnUtil());
  }

  void commitButton_actionPerformed(ActionEvent e) {
    try {
      if (connWindowManager.getCurrentDbConnUtil() != null) {
        connWindowManager.getCurrentDbConnUtil().getConn().commit();
      }
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          this,
          Options.getInstance().getResource("error on commit changes")+":\n"+ex.getMessage(),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE
      );
    }
  }

  void rollbackButton_actionPerformed(ActionEvent e) {
    try {
      if (connWindowManager.getCurrentDbConnUtil() != null) {
        connWindowManager.getCurrentDbConnUtil().getConn().rollback();
      }
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          this,
          Options.getInstance().getResource("error on rollback changes")+":\n"+ex.getMessage(),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE
      );
    }
  }

  void optionsMenuItem_actionPerformed(ActionEvent e) {
    new OptionsDialog(this);
  }

  void commitMenuItem_actionPerformed(ActionEvent e) {
    try {
      if (connWindowManager.getCurrentDbConnUtil() != null) {
        connWindowManager.getCurrentDbConnUtil().getConn().commit();
      }
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          this,
          Options.getInstance().getResource("error on commit changes")+":\n"+ex.getMessage(),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE
      );
    }
  }

  void rollbackMenuItem_actionPerformed(ActionEvent e) {
    try {
      if (connWindowManager.getCurrentDbConnUtil() != null) {
        connWindowManager.getCurrentDbConnUtil().getConn().rollback();
      }
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          this,
          Options.getInstance().getResource("error on rollback changes")+":\n"+ex.getMessage(),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE
      );
    }
  }

  void sqlEditorMenuItem_actionPerformed(ActionEvent e) {
    if (connWindowManager.getCurrentDbConnUtil()!=null)
      createSQLFrame(connWindowManager.getCurrentDbConnUtil());
  }

  void schemaBrowserMenuItem_actionPerformed(ActionEvent e) {
    if (connWindowManager.getCurrentDbConnUtil()!=null)
      createTableListFrame(connWindowManager.getCurrentDbConnUtil());
  }

  void dbSchemaMenuItem_actionPerformed(ActionEvent e) {
    if (connWindowManager.getCurrentDbConnUtil()!=null)
      createSchemaFrame(connWindowManager.getCurrentDbConnUtil());
  }


  void closeAllMenuItem_actionPerformed(ActionEvent e) {
    if (connWindowManager.getCurrentDbConnUtil()!=null) {
      int answer = JOptionPane.showConfirmDialog(
          this,
          Options.getInstance().getResource("close all windows and related connections?"),
          Options.getInstance().getResource("close all windows"),
          JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE
      );
      if (answer==0)
        connWindowManager.closeAll();
    }
  }

  void currSqlMenuItem_actionPerformed(ActionEvent e) {
    if (connWindowManager.getCurrentFrame()!=null && connWindowManager.getCurrentFrame() instanceof SQLFrame) {
      SQLFrame f = (SQLFrame)connWindowManager.getCurrentFrame();
      f.executeButton_actionPerformed(null);
    }
  }


  void explainPlanMenuItem_actionPerformed(ActionEvent e) {
    if (connWindowManager.getCurrentFrame()!=null && connWindowManager.getCurrentFrame() instanceof SQLFrame) {
      SQLFrame f = (SQLFrame)connWindowManager.getCurrentFrame();
      f.explainPlanButton_actionPerformed(null);
    }
  }

  void importSQLMenuItem_actionPerformed(ActionEvent e) {
    if (connWindowManager.getCurrentFrame()!=null && connWindowManager.getCurrentFrame() instanceof SQLFrame) {
      SQLFrame f = (SQLFrame)connWindowManager.getCurrentFrame();
      f.importSQLButton_actionPerformed(null);
    }
  }

  void recallListMenuItem_actionPerformed(ActionEvent e) {
    if (connWindowManager.getCurrentFrame()!=null && connWindowManager.getCurrentFrame() instanceof SQLFrame) {
      SQLFrame f = (SQLFrame)connWindowManager.getCurrentFrame();
      f.executeHistoryButton_actionPerformed(null);
    }
  }

  void traceMenuItem_actionPerformed(ActionEvent e) {
    if (connWindowManager.getCurrentDbConnUtil()!=null) {
      TraceSessionFrame frame = new TraceSessionFrame(this,connWindowManager.getCurrentDbConnUtil());
      connWindowManager.addWindow(frame);
    }
  }




}

class MainFrame_menuFileExit_ActionAdapter implements ActionListener {
  MainFrame adaptee;

  MainFrame_menuFileExit_ActionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.menuFileExit_actionPerformed(e);
  }
}

class MainFrame_menuFileRep_ActionAdapter implements ActionListener {
  MainFrame adaptee;

  MainFrame_menuFileRep_ActionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.menuFileRep_actionPerformed(e);
  }
}

class MainFrame_menuHelpAbout_ActionAdapter implements ActionListener {
  MainFrame adaptee;

  MainFrame_menuHelpAbout_ActionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.menuHelpAbout_actionPerformed(e);
  }
}

class MainFrame_menuFileNewConn_actionAdapter implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_menuFileNewConn_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.menuFileNewConn_actionPerformed(e);
  }
}

class MainFrame_menuFileEndAllItem_actionAdapter implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_menuFileEndAllItem_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.menuFileEndAllItem_actionPerformed(e);
  }
}

class MainFrame_menuFileEndItem_actionAdapter implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_menuFileEndItem_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.menuFileEndItem_actionPerformed(e);
  }
}

class MainFrame_queryButton_actionAdapter implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_queryButton_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.queryButton_actionPerformed(e);
  }
}

class MainFrame_listaButton_actionAdapter implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_listaButton_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.listaButton_actionPerformed(e);
  }
}

class MainFrame_schemaButton_actionAdapter implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_schemaButton_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.schemaButton_actionPerformed(e);
  }
}

class MainFrame_commitButton_actionAdapter implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_commitButton_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.commitButton_actionPerformed(e);
  }
}

class MainFrame_rollbackButton_actionAdapter implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_rollbackButton_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.rollbackButton_actionPerformed(e);
  }
}

class MainFrame_optionsMenuItem_actionAdapter implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_optionsMenuItem_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.optionsMenuItem_actionPerformed(e);
  }
}

class MainFrame_commitMenuItem_actionAdapter implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_commitMenuItem_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.commitMenuItem_actionPerformed(e);
  }
}

class MainFrame_rollbackMenuItem_actionAdapter implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_rollbackMenuItem_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.rollbackMenuItem_actionPerformed(e);
  }
}

class MainFrame_sqlEditorMenuItem_actionAdapter implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_sqlEditorMenuItem_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.sqlEditorMenuItem_actionPerformed(e);
  }
}

class MainFrame_schemaBrowserMenuItem_actionAdapter implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_schemaBrowserMenuItem_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.schemaBrowserMenuItem_actionPerformed(e);
  }
}

class MainFrame_dbSchemaMenuItem_actionAdapter implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_dbSchemaMenuItem_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.dbSchemaMenuItem_actionPerformed(e);
  }
}

class MainFrame_closeAllMenuItem_actionAdapter implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_closeAllMenuItem_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.closeAllMenuItem_actionPerformed(e);
  }
}

class MainFrame_currSqlMenuItem_actionAdapter implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_currSqlMenuItem_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.currSqlMenuItem_actionPerformed(e);
  }
}

class MainFrame_explainPlanMenuItem_actionAdapter implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_explainPlanMenuItem_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.explainPlanMenuItem_actionPerformed(e);
  }
}

class MainFrame_importSQLMenuItem_actionAdapter implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_importSQLMenuItem_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.importSQLMenuItem_actionPerformed(e);
  }
}

class MainFrame_recallListMenuItem_actionAdapter implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_recallListMenuItem_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.recallListMenuItem_actionPerformed(e);
  }
}

class MainFrame_traceMenuItem_actionAdapter implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_traceMenuItem_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.traceMenuItem_actionPerformed(e);
  }
}

