package org.jsqltool.gui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import org.jsqltool.conn.DbConnectionUtil;
import org.jsqltool.conn.DbConnection;
import java.awt.event.*;
import org.jsqltool.gui.panel.*;
import org.jsqltool.model.*;
import java.util.*;
import org.jsqltool.MainApp;
import java.sql.*;
import org.jsqltool.utils.Options;
import org.jsqltool.utils.ImageLoader;
import org.jsqltool.gui.graphics.SQLTextArea;
import java.io.*;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Window used to write and execute a SQL script.
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
public class SQLFrame extends JInternalFrame implements DbConnWindow,TableModelListener {

  private DbConnectionUtil dbConnUtil = null;
  JPanel mainPanel = new JPanel();
  JPanel buttonsPanel = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JSplitPane splitPane = new JSplitPane();
  JPanel statePanel = new JPanel();
  SQLTextArea editor = new SQLTextArea();
  DataPanel dataPanel = new DataPanel();

  JButton executeButton = new JButton();
  JButton executeHistoryButton = new JButton();
  JButton explainPlanButton = new JButton();
  JButton importSQLButton = new JButton();
  ImageIcon executeImage;
  ImageIcon executeHistoryImage;
  ImageIcon explainPlanImage;
  ImageIcon importSQLImage;
  FlowLayout flowLayout1 = new FlowLayout();
  JLabel statusLabel = new JLabel();
  BorderLayout borderLayout2 = new BorderLayout();
  private String lastSQL = null;
  private JFrame parent = null;
  JWindow tableMenu = new JWindow();
  JWindow colMenu = new JWindow();
  private boolean dotPressed = false;
  JList tables = new JList();
  JList cols = new JList();
  JScrollPane tableScrollPane = new JScrollPane(tables);
  JScrollPane colScrollPane = new JScrollPane(cols);
  DefaultListModel tablesModel = new DefaultListModel();
  DefaultListModel colsModel = new DefaultListModel();


  public SQLFrame(JFrame parent,DbConnectionUtil dbConnUtil) {
    super(Options.getInstance().getResource("sql editor")+" - "+dbConnUtil.getDbConnection().getName(),true,true,true,true);
    this.dbConnUtil = dbConnUtil;
    this.dataPanel = new DataPanel(dbConnUtil,this);
    this.parent = parent;
    try {
      jbInit();
      new Thread() {
        public void run() {
          initialize();
          try {
            sleep(500);
          }
          catch (InterruptedException ex) {
          }
          editor.requestFocus();
        }
      }.start();

    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * Prepare a tables combo-box.
   */
  private void initialize() {
    // create the tables combo-box...
    java.util.List tablesList = dbConnUtil.getTables(dbConnUtil.getDbConnection().getCatalog(),"TABLE");
    String tableName = null;
    for(int i=0;i<tablesList.size();i++) {
      tableName = tablesList.get(i).toString();
      tablesModel.addElement(tableName);
    }
    tables.setModel(tablesModel);
    tables.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tables.revalidate();
    tables.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        int pos = editor.getCaretPosition();
        editor.setText(
          editor.getText().substring(0,pos)+
          tables.getSelectedValue()+
          editor.getText().substring(pos)
        );
        tableMenu.setVisible(false);
        editor.requestFocus();
        editor.setCaretPosition(pos+tables.getSelectedValue().toString().length());
      }
    });
    tables.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        try {
            tables.scrollRectToVisible(tables.getCellBounds(tables.getSelectedIndex(), tables.getSelectedIndex()));
        }
        catch (Exception ex) {
        }
      }
    });
    tableMenu.getContentPane().add(tableScrollPane,BorderLayout.CENTER);

    // create the columns combo-box...
    cols.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    cols.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        int pos = editor.getCaretPosition();
        editor.setText(
            editor.getText().substring(0,pos)+
            cols.getSelectedValue()+
            editor.getText().substring(pos)
        );
        colMenu.setVisible(false);
        editor.requestFocus();
        editor.setCaretPosition(pos+ cols.getSelectedValue().toString().length());
      }
    });
    cols.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        try {
            cols.scrollRectToVisible(cols.getCellBounds(cols.getSelectedIndex(), cols.getSelectedIndex()));
        }
        catch (Exception ex) {
        }
      }
    });

    colMenu.getContentPane().add(colScrollPane,BorderLayout.CENTER);
  }


  /**
   * Method called when user press the "." key.
   * @param tableName table name to use to fetch its columns
   */
  private void initCols(String tableName) {
    // populate the columns combo-box...
    if (tableName.indexOf(".")>-1)
      tableName = tableName.substring(tableName.indexOf(".")+1);
    TableModel colsList = dbConnUtil.getTableColumns(tableName);
    colsModel.removeAllElements();
    for(int i=0;i<colsList.getRowCount();i++) {
      String colName = colsList.getValueAt(i,0).toString();
      colsModel.addElement(colName);
    }
    cols.setModel(colsModel);
    cols.revalidate();
    cols.repaint();
  }


  public SQLFrame() {
    this(null,null);
  }


  public DbConnectionUtil getDbConnectionUtil() {
    return dbConnUtil;
  }


  private void jbInit() throws Exception {
    mainPanel.setLayout(borderLayout1);
    splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    statePanel.setBorder(BorderFactory.createEtchedBorder());
    statePanel.setMinimumSize(new Dimension(14, 24));
    statePanel.setPreferredSize(new Dimension(14, 24));
    statePanel.setLayout(borderLayout2);
    editor.setFont(new java.awt.Font("Monospaced", 0, 11));
    editor.setPreferredSize(new Dimension(372, 150));
    editor.setText("");
    editor.addFocusListener(new SQLFrame_editor_focusAdapter(this));
    editor.addMouseListener(new SQLFrame_editor_mouseAdapter(this));
    editor.addKeyListener(new SQLFrame_editor_keyAdapter(this));
    buttonsPanel.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    flowLayout1.setHgap(0);
    executeButton.addActionListener(new SQLFrame_executeButton_actionAdapter(this));
    executeHistoryButton.addActionListener(new SQLFrame_executeHistoryButton_actionAdapter(this));
    explainPlanButton.addActionListener(new SQLFrame_explainPlanButton_actionAdapter(this));
    importSQLButton.addActionListener(new SQLFrame_importSQLButton_actionAdapter(this));
    this.getContentPane().add(mainPanel, BorderLayout.CENTER);
    mainPanel.add(buttonsPanel, BorderLayout.NORTH);
    mainPanel.add(splitPane,  BorderLayout.CENTER);
    mainPanel.add(statePanel,  BorderLayout.SOUTH);
    statePanel.add(statusLabel, BorderLayout.CENTER);
    splitPane.add(editor, JSplitPane.TOP);
    splitPane.add(dataPanel, JSplitPane.BOTTOM);
    splitPane.setDividerLocation(150);

    executeButton.setBorder(null);
    executeButton.setPreferredSize(new Dimension(24, 24));
    executeHistoryButton.setBorder(null);
    executeHistoryButton.setPreferredSize(new Dimension(24, 24));
    explainPlanButton.setBorder(null);
    explainPlanButton.setPreferredSize(new Dimension(24, 24));
    importSQLButton.setBorder(null);
    importSQLButton.setPreferredSize(new Dimension(24, 24));
    executeImage = ImageLoader.getInstance().getIcon("execute.gif");
    executeHistoryImage = ImageLoader.getInstance().getIcon("executehistory.gif");
    explainPlanImage = ImageLoader.getInstance().getIcon("explainplan.gif");
    executeButton.setIcon(executeImage);
    importSQLImage = ImageLoader.getInstance().getIcon("importscript.gif");
    executeButton.setToolTipText(Options.getInstance().getResource("execute shell content (or selected content)"));
    executeButton.setMaximumSize(new Dimension(24,24));
    executeHistoryButton.setIcon(executeHistoryImage);
    executeHistoryButton.setToolTipText(Options.getInstance().getResource("old sql statements"));
    executeHistoryButton.setMaximumSize(new Dimension(24,24));
    explainPlanButton.setIcon(explainPlanImage);
    explainPlanButton.setToolTipText(Options.getInstance().getResource("explain plan"));
    explainPlanButton.setMaximumSize(new Dimension(24,24));
    importSQLButton.setIcon(importSQLImage);
    importSQLButton.setToolTipText(Options.getInstance().getResource("importsql.text"));
    importSQLButton.setMaximumSize(new Dimension(24,24));
    buttonsPanel.add(executeButton, null);
    buttonsPanel.add(executeHistoryButton, null);
    buttonsPanel.add(explainPlanButton, null);
    buttonsPanel.add(importSQLButton, null);
  }


  /**
   * This fine grain notification tells listeners the exact range
   * of cells, rows, or columns that changed.
   */
  public void tableChanged(TableModelEvent e) {

  }

  public void executeButton_actionPerformed(ActionEvent e) {
    if (editor.getSelectedText()!=null)
      executeSQL(editor.getSelectedText());
    else if (editor.getText().trim().length()>0)
      executeSQL(editor.getText());
  }


  public void executeHistoryButton_actionPerformed(ActionEvent e) {
    SQLStatementRecallDialog f = new SQLStatementRecallDialog(parent,dbConnUtil,this);
    f.setVisible(true);
    editor.requestFocus();
  }


  private String parseRows(String sql) {
    lastSQL = sql;
    String temp = "";
    String newsql = "";
    StringTokenizer st = new StringTokenizer(sql,"\n");
    boolean isComment = false;
    while(st.hasMoreTokens()) {
      temp = st.nextToken();
      if (temp.indexOf("*/")!=-1) {
        temp = " "+temp.substring(temp.indexOf("*/")+2);
        isComment = false;
      }
      if (temp.indexOf("--")!=-1)
        temp = temp.substring(0,temp.indexOf("--"));
      if (temp.indexOf("/*")!=-1) {
        newsql += " "+temp.substring(0,temp.indexOf("/*"));
        isComment = true;
      } else if (!isComment)
        newsql += " "+temp;
      if (temp.indexOf("*/")!=-1) {
        newsql += " "+temp.substring(temp.indexOf("*/")+2);
        isComment = false;
      }
    }
    newsql = newsql.trim();
    if (newsql.endsWith(";"))
      newsql = newsql.substring(0,newsql.length()-1);
    return newsql;
  }


  /**
   * Execute the sql with parameters.
   */
  public final void executeSQLWithValues(String newsql,Vector values) {
    if (newsql.trim().toUpperCase().startsWith("SELECT ")) {
      long time = System.currentTimeMillis();
      dataPanel.setQuery(newsql,values);
      dataPanel.getTable().setShowGrid(true);
      ((CustomTableModel)dataPanel.getTableModel()).setEditMode(CustomTableModel.DETAIL_REC);
      statusLabel.setText(Options.getInstance().getResource("query execution")+": "+(System.currentTimeMillis()-time)+" ms");
    } else {
      statusLabel.setText(dbConnUtil.executeStmt(newsql,values)+" "+Options.getInstance().getResource("records processed."));
    }

    String oldQuery = newsql.replace('\n',' ');
    if (!dbConnUtil.getDbConnection().getOldQueries().contains(oldQuery)) {
      // save query in query history...
      dbConnUtil.getDbConnection().getOldQueries().add(oldQuery);
      dbConnUtil.saveProfile(true);
    }
  }


  private void executeSQL(String sql) {
    String newsql = parseRows(sql);

    // parse sql to find out parameters...
    Vector values = new Vector();
    ParametersDialog dialog = new ParametersDialog(parent,this,newsql,values);
  }


  public void editor_keyPressed(KeyEvent e) {
    try {
      dotPressed = false;
      if (e.getKeyCode() == e.VK_F9) {
        executeButton_actionPerformed(null);
      }
      if (e.getKeyCode() == e.VK_SPACE && e.isControlDown() && !tableMenu.isVisible()) {
        e.consume();
        // view popup menu containing tables list...
        tableMenu.setLocation(
            editor.modelToView(editor.getCaretPosition()).x,
            editor.modelToView(editor.getCaretPosition()).y+150
            );
        tableMenu.setSize(300, 150);
        tableMenu.setVisible(true);
        tables.requestFocus();
        tables.setSelectedIndex(0);
      }
      else if (tableMenu.isVisible()) {
        e.consume();
        if (e.getKeyCode()==e.VK_UP && tables.getSelectedIndex()>0)
          tables.setSelectedIndex(tables.getSelectedIndex()-1);
        else if (e.getKeyCode()==e.VK_DOWN && tables.getSelectedIndex()<tablesModel.getSize()-1)
          tables.setSelectedIndex(tables.getSelectedIndex()+1);
        else if (e.getKeyCode()==e.VK_PAGE_UP) {
          int i = tables.getSelectedIndex();
          int w = tables.getLastVisibleIndex()-tables.getFirstVisibleIndex()+1;
          i = i-w;
          if (i<0)
            i=0;
          tables.setSelectedIndex(i);
        }
        else if (e.getKeyCode()==e.VK_PAGE_DOWN) {
          int i = tables.getSelectedIndex();
          int w = tables.getLastVisibleIndex()-tables.getFirstVisibleIndex()+1;
          i = i+w;
          if (i-1>tables.getModel().getSize())
            i=tables.getModel().getSize()-1;
          tables.setSelectedIndex(i);
        }
        else if (e.getKeyCode()==e.VK_ESCAPE) {
          tableMenu.setVisible(false);
          editor.requestFocus();
        }
        else if (e.getKeyCode()==e.VK_ENTER) {
          int pos = editor.getCaretPosition();
          editor.setText(
            editor.getText().substring(0,pos)+
            tables.getSelectedValue()+
            editor.getText().substring(pos)
          );
          tableMenu.setVisible(false);
          editor.requestFocus();
          editor.setCaretPosition(pos+tables.getSelectedValue().toString().length());
        }
        else {
          for(int i=0;i<tables.getModel().getSize();i++)
            if (tables.getModel().getElementAt(i).toString().toLowerCase().startsWith(String.valueOf(e.getKeyChar()).toLowerCase())) {
              tables.setSelectedIndex(i);
              break;
            }
          e.consume();
        }
      }
      else if (e.getKeyChar() == '.' && !colMenu.isVisible()) {
        dotPressed = true;
        Thread t = new Thread() {
          public void run() {
            try {
              sleep(500);
            }
            catch (InterruptedException ex) {
            }
            if (dotPressed) {
              try {
                // view popup menu containing columns list...
                String text = editor.getText();
                int start = 0;
                while (start < text.length() &&
                       text.indexOf(" ", start + 1) > -1) {
                  if (text.indexOf(" ", start + 1) < editor.getCaretPosition()) {
                    start = text.indexOf(" ", start + 1);
                  }
                  else {
                    break;
                  }
                }
                String table = text.substring(start, editor.getCaretPosition() - 1);
                if (table.indexOf(",")!=-1)
                  table = table.substring(table.indexOf(",")+1);
                initCols(table);

                colMenu.setLocation(
                    editor.modelToView(editor.getCaretPosition()).x,
                    editor.modelToView(editor.getCaretPosition()).y+150
                    );
                colMenu.setSize(300, 150);
                colMenu.setVisible(true);
                cols.requestFocus();
                cols.setSelectedIndex(0);

              }
              catch (Exception ex) {
              }
            }
          }
        };
        t.start();
      }
      else if (colMenu.isVisible()) {
        e.consume();
        if (e.getKeyCode()==e.VK_UP && cols.getSelectedIndex()>0)
          cols.setSelectedIndex(cols.getSelectedIndex()-1);
        else if (e.getKeyCode()==e.VK_DOWN && cols.getSelectedIndex()<colsModel.getSize()-1)
          cols.setSelectedIndex(cols.getSelectedIndex()+1);
        else if (e.getKeyCode()==e.VK_PAGE_UP) {
          int i = cols.getSelectedIndex();
          int w = cols.getLastVisibleIndex()-cols.getFirstVisibleIndex()+1;
          i = i-w;
          if (i<0)
            i=0;
          cols.setSelectedIndex(i);
        }
        else if (e.getKeyCode()==e.VK_PAGE_DOWN) {
          int i = cols.getSelectedIndex();
          int w = cols.getLastVisibleIndex()-cols.getFirstVisibleIndex()+1;
          i = i+w;
          if (i-1>cols.getModel().getSize())
            i=cols.getModel().getSize()-1;
          cols.setSelectedIndex(i);
        }
        else if (e.getKeyCode()==e.VK_ESCAPE) {
          colMenu.setVisible(false);
          editor.requestFocus();
        }
        else if (e.getKeyCode()==e.VK_ENTER) {
          int pos = editor.getCaretPosition();
          editor.setText(
            editor.getText().substring(0,pos)+
            cols.getSelectedValue()+
            editor.getText().substring(pos)
          );
          colMenu.setVisible(false);
          editor.requestFocus();
          editor.setCaretPosition(pos+cols.getSelectedValue().toString().length());
        }
        else {
          for(int i=0;i<cols.getModel().getSize();i++)
            if (cols.getModel().getElementAt(i).toString().toLowerCase().startsWith(String.valueOf(e.getKeyChar()).toLowerCase())) {
              cols.setSelectedIndex(i);
              break;
            }
          e.consume();
        }
      }

    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  public final void setEditorContent(String content) {
    editor.setText(content);
  }


  public void explainPlanButton_actionPerformed(ActionEvent e) {
    String newsql = parseRows(editor.getText());
    long time = System.currentTimeMillis();
    String id = "EP_"+String.valueOf(Math.random()*time);
    if (id.length()>16)
      id = id.substring(0,16);
    if (dbConnUtil.getDbConnection().getDbType()==DbConnection.ORACLE_TYPE)
      // ORACLE database type...
      try {
        Statement stmt = dbConnUtil.getConn().createStatement();
        stmt.execute("EXPLAIN PLAN SET STATEMENT_ID = '" + id +
                     "' INTO "+Options.getInstance().getOracleExplainPlanTable()+" FOR " + newsql);
        stmt.close();
        dataPanel.setQuery("SELECT LPAD(' ',2*(LEVEL-1))||operation operation, options,"+
                           "object_name, position "+
                           "FROM "+Options.getInstance().getOracleExplainPlanTable()+" "+
                           "START WITH id = 0 AND statement_id = '"+id+"'"+
                           "CONNECT BY PRIOR id = parent_id AND statement_id = '"+id+"'",
                           new Vector());
        dataPanel.getTable().setShowGrid(false);
        ( (CustomTableModel) dataPanel.getTableModel()).setEditMode(
            CustomTableModel.DETAIL_REC);
      }
      catch (SQLException ex) {
      }
    else
      JOptionPane.showMessageDialog(
          parent,
          Options.getInstance().getResource("feature not supported for this database type."),
          Options.getInstance().getResource("attention"),
          JOptionPane.WARNING_MESSAGE
      );
  }

  public void editor_mouseClicked(MouseEvent e) {
    tableMenu.setVisible(false);
    colMenu.setVisible(false);

    if (SwingUtilities.isRightMouseButton(e)) {
      // view popup menu...
      JPopupMenu menu = new JPopupMenu();
      JMenuItem formatCodeOnOneRowMenu = new JMenuItem(Options.getInstance().getResource("format sql on one row"));
      formatCodeOnOneRowMenu.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          try {
            String sql = editor.getText();
            int pos = 0;
            while (pos < sql.length()) {
              if (sql.charAt(pos) == '\n') {
                if (pos < sql.length() - 1) {
                  sql = sql.substring(0, pos) + sql.substring(pos + 1);
                }
                else {
                  sql = sql.substring(0, pos);
                }
              }
              else {
                pos++;
              }
            }
            editor.setText(sql);
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
      });
      JMenuItem formatCodeOnMoreRowsMenu = new JMenuItem(Options.getInstance().getResource("format sql on more rows"));
      formatCodeOnMoreRowsMenu.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          try {
            String sql = editor.getText();
            int count = 0;
            int pos = 0;
            int apixCount = 0;
            while (pos < sql.length()) {
              if (sql.charAt(pos) == '\'') {
                apixCount++;
                if (pos>0 && sql.charAt(pos-1)=='\'')
                  apixCount = apixCount -2;
              }
              if (count>80) {
                if ((sql.charAt(pos)==' ' || sql.charAt(pos)==',') && apixCount%2==0) {
                  count = 0;
                  if (pos<sql.length()-1)
                    sql = sql.substring(0,pos+1)+"\n"+sql.substring(pos+1);
                  else
                    sql = sql.substring(0,pos);
                }
              }
              pos++;
              count++;
            }

            editor.setText(sql);
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
      });
      menu.add(formatCodeOnOneRowMenu);
      menu.add(formatCodeOnMoreRowsMenu);
      menu.show(e.getComponent(),e.getX(), e.getY());

    }

  }

  public void editor_focusLost(FocusEvent e) {
    tableMenu.setVisible(false);
    colMenu.setVisible(false);
  }

  public void editor_keyTyped(KeyEvent e) {
    if (colMenu.isVisible())
      e.consume();
    else if (tableMenu.isVisible())
      e.consume();
  }


  /**
   * Import a SQL Script from file and execute it in batch mode.
   */
  public void importSQLButton_actionPerformed(ActionEvent e) {
    final JFileChooser f = new JFileChooser(".");
    f.setDialogTitle(Options.getInstance().getResource("import sql script"));
    int res = f.showDialog(parent,Options.getInstance().getResource("import file"));
    if (res==f.APPROVE_OPTION) {
      ProgressDialog.getInstance().startProgress();
      new Thread() {
        public void run() {
          importSQL(f.getSelectedFile());
        }
      }.start();
    }
  }


  /**
   * Import the selected file.
   * @param file file to import
   */
  private void importSQL(File file) {
    Statement stmt = null;
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
      StringBuffer sql = new StringBuffer();
      String line = null;
      stmt = dbConnUtil.getConn().createStatement();
      int count = 0;
      while((line=br.readLine())!=null) {
        if (line.endsWith(";")) {
          sql.append(line.substring(0,line.length()-1));
          stmt.addBatch(sql.toString());
          sql.delete(0,sql.length());
          count++;
          if (count>1000) {
            count = 0;
            stmt.executeBatch();
          }
        }
        else {
          sql.append(" ");
          sql.append(line);
        }
      }
      if (sql.length()>0)
        stmt.addBatch(sql.toString());
      stmt.executeBatch();
      br.close();
      ProgressDialog.getInstance().stopProgress();
      JOptionPane.showMessageDialog(
          parent,
          Options.getInstance().getResource(Options.getInstance().getResource("loading completed.")),
          Options.getInstance().getResource("loading script file"),
          JOptionPane.INFORMATION_MESSAGE
      );

    }
    catch (Exception ex) {
      ex.printStackTrace();
      ProgressDialog.getInstance().stopProgress();
      JOptionPane.showMessageDialog(
          parent,
          Options.getInstance().getResource(Options.getInstance().getResource("error while loading script file:")+"\n"+ex.getMessage()),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE
      );
    }
    finally {
      try {
        if (stmt != null) {
          stmt.close();
        }
      }
      catch (Exception ex1) {
        ex1.printStackTrace();
      }
    }
  }



}

class SQLFrame_executeButton_actionAdapter implements java.awt.event.ActionListener {
  SQLFrame adaptee;

  SQLFrame_executeButton_actionAdapter(SQLFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.executeButton_actionPerformed(e);
  }
}


class SQLFrame_executeHistoryButton_actionAdapter implements java.awt.event.ActionListener {
  SQLFrame adaptee;

  SQLFrame_executeHistoryButton_actionAdapter(SQLFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.executeHistoryButton_actionPerformed(e);
  }
}

class SQLFrame_editor_keyAdapter extends java.awt.event.KeyAdapter {
  SQLFrame adaptee;

  SQLFrame_editor_keyAdapter(SQLFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void keyPressed(KeyEvent e) {
    adaptee.editor_keyPressed(e);
  }
  public void keyTyped(KeyEvent e) {
    adaptee.editor_keyTyped(e);
  }
}

class SQLFrame_explainPlanButton_actionAdapter implements java.awt.event.ActionListener {
  SQLFrame adaptee;

  SQLFrame_explainPlanButton_actionAdapter(SQLFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.explainPlanButton_actionPerformed(e);
  }
}

class SQLFrame_importSQLButton_actionAdapter implements java.awt.event.ActionListener {
  SQLFrame adaptee;

  SQLFrame_importSQLButton_actionAdapter(SQLFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.importSQLButton_actionPerformed(e);
  }
}

class SQLFrame_editor_mouseAdapter extends java.awt.event.MouseAdapter {
  SQLFrame adaptee;

  SQLFrame_editor_mouseAdapter(SQLFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseClicked(MouseEvent e) {
    adaptee.editor_mouseClicked(e);
  }
}

class SQLFrame_editor_focusAdapter extends java.awt.event.FocusAdapter {
  SQLFrame adaptee;

  SQLFrame_editor_focusAdapter(SQLFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void focusLost(FocusEvent e) {
    adaptee.editor_focusLost(e);
  }
}