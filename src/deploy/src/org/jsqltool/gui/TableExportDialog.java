package org.jsqltool.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import org.jsqltool.conn.DbConnectionUtil;
import org.jsqltool.model.CustomTableModel;
import java.util.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.awt.datatransfer.*;
import org.jsqltool.gui.tableplugins.datatable.filter.*;
import org.jsqltool.utils.Options;
import org.jsqltool.utils.ImageLoader;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.model.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.*;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Dialog used to export table content.
 * Table content can be exported as:
 * - SQL insert statements
 * - text data, with a custom delimiter (e.g. ",")
 * - XLS (Excel) file format
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
public class TableExportDialog extends JDialog implements TableModelListener {

  JPanel mainPanel = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel headPanel = new JPanel();
  JPanel tabPanel = new JPanel();
  JTabbedPane tabbedPane = new JTabbedPane();
  BorderLayout borderLayout2 = new BorderLayout();
  JTextField tableTF = new JTextField();
  JButton cancelButton = new JButton();
  JButton okButton = new JButton();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JPanel optionsPanel = new JPanel();
  JPanel colPanel = new JPanel();
  JLabel whereLabel = new JLabel();
  JScrollPane scrollPane = new JScrollPane();
  JTextPane wherePane = new JTextPane();
  JLabel destLabel = new JLabel();
  JRadioButton clipRadioButton = new JRadioButton();
  JRadioButton fileRadioButton = new JRadioButton();
  JLabel fileLabel = new JLabel();
  JTextField filenameTF = new JTextField();
  JButton filenameButton = new JButton();
  JCheckBox schemaCheckBox = new JCheckBox();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  ButtonGroup dest = new ButtonGroup();
  private String tableName = null;
  private JFrame frame = null;
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JScrollPane colsScrollPane = new JScrollPane();
  JTable colsTable = new JTable();
  JCheckBox nullCheckBox = new JCheckBox();
  JCheckBox pkCheckBox = new JCheckBox();
  JButton selButton = new JButton();
  JButton unselButton = new JButton();
  private DbConnectionUtil dbConnUtil = null;
  private Hashtable pk = null;
  private boolean enableListener = true;
  JLabel formatLabel = new JLabel();
  JRadioButton sqlRadioButton = new JRadioButton();
  ButtonGroup formatButtonGroup = new ButtonGroup();
  JRadioButton txtRadioButton = new JRadioButton();
  JTextField delimTF = new JTextField();
  JRadioButton xlsRadioButton = new JRadioButton();


  public TableExportDialog(JFrame frame,DbConnectionUtil dbConnUtil,String tableName) {
    super(frame, Options.getInstance().getResource("data export"), true);
    this.frame = frame;
    this.tableName = tableName;
    this.dbConnUtil = dbConnUtil;
    try {
      init();
      jbInit();
      pack();
      setSize(450,400);
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


  public TableExportDialog() {
    this(null,null,null);
  }


  private void init() {
    // set table name...
    tableTF.setText(tableName);

    // fill in columns list...
    CustomTableModel model = (CustomTableModel)dbConnUtil.getTableColumns(tableName);
    Vector colData = new Vector();
    for(int i=0;i<model.getRowCount();i++)
      colData.add(new Boolean(false));
    model.addColumn(Options.getInstance().getResource("selection"),colData,Boolean.class,30);
    model.setEditableCols(new boolean[]{false,false,false,false,false,true});
    model.setEditMode(model.EDIT_REC);
    // identifiers of the model: "Column","Data Type","PK","Null?","Default","Selected"
    colsTable.setModel(model);
    colsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    colsTable.getColumnModel().moveColumn(5,0);
    colsTable.getColumnModel().removeColumn(colsTable.getColumnModel().getColumn(2));
    colsTable.getColumnModel().removeColumn(colsTable.getColumnModel().getColumn(2));
    colsTable.getColumnModel().removeColumn(colsTable.getColumnModel().getColumn(2));
    colsTable.getColumnModel().removeColumn(colsTable.getColumnModel().getColumn(2));
    colsTable.getColumnModel().getColumn(0).setPreferredWidth(30);
    colsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
    colsTable.setShowGrid(false);
    colsTable.setRowSelectionAllowed(false);
    colsTable.setColumnSelectionAllowed(false);

    // initialization of pk cols...
    pk = dbConnUtil.getPK(tableName);

    // select all columns...
    model.addTableModelListener(this);
    selButton_actionPerformed(null);
  }


  private void jbInit() throws Exception {
    mainPanel.setLayout(borderLayout1);
    tabPanel.setLayout(borderLayout2);
    cancelButton.setMnemonic(Options.getInstance().getResource("cancelbutton.mnemonic").charAt(0));
    cancelButton.setText(Options.getInstance().getResource("cancelbutton.text"));
    cancelButton.addActionListener(new TableExportDialog_cancelButton_actionAdapter(this));
    okButton.setMnemonic(Options.getInstance().getResource("okbutton.mnemonic").charAt(0));
    okButton.setText(Options.getInstance().getResource("okbutton.text"));
    okButton.addActionListener(new TableExportDialog_okButton_actionAdapter(this));
    headPanel.setLayout(gridBagLayout1);
    tableTF.setEnabled(true);
    tableTF.setDisabledTextColor(Color.gray);
    tableTF.setEditable(false);
    optionsPanel.setToolTipText(Options.getInstance().getResource("export options"));
    optionsPanel.setLayout(gridBagLayout2);
    colPanel.setToolTipText(Options.getInstance().getResource("exported columns"));
    colPanel.setVerifyInputWhenFocusTarget(true);
    colPanel.setLayout(gridBagLayout3);
    whereLabel.setText(Options.getInstance().getResource("Optional WHERE clause (you must include \'Where\')"));
    wherePane.setText("");
    destLabel.setText(Options.getInstance().getResource("destination"));
    clipRadioButton.setText(Options.getInstance().getResource("to clipboard"));
    clipRadioButton.addItemListener(new TableExportDialog_clipRadioButton_itemAdapter(this));
    fileRadioButton.setSelected(true);
    fileRadioButton.setText(Options.getInstance().getResource("to file"));
    fileRadioButton.addItemListener(new TableExportDialog_fileRadioButton_itemAdapter(this));
    fileLabel.setText(Options.getInstance().getResource("filename"));
    filenameButton.setText("...");
    filenameButton.addActionListener(new TableExportDialog_filenameButton_actionAdapter(this));
    filenameTF.setText("");
    schemaCheckBox.setText(Options.getInstance().getResource("include schema/owner name in insert statements"));
    nullCheckBox.setText(Options.getInstance().getResource("exclude null columns"));
    nullCheckBox.addItemListener(new TableExportDialog_nullCheckBox_itemAdapter(this));
    pkCheckBox.setText(Options.getInstance().getResource("exclude primary key columns"));
    pkCheckBox.addItemListener(new TableExportDialog_pkCheckBox_itemAdapter(this));
    selButton.setMnemonic(Options.getInstance().getResource("selectall.mnemonic").charAt(0));
    selButton.setText(Options.getInstance().getResource("selectall.text"));
    selButton.addActionListener(new TableExportDialog_selButton_actionAdapter(this));
    unselButton.setMnemonic(Options.getInstance().getResource("deselectall.menmonic").charAt(0));
    unselButton.setText(Options.getInstance().getResource("deselectall.text"));
    unselButton.addActionListener(new TableExportDialog_unselButton_actionAdapter(this));
    formatLabel.setText(Options.getInstance().getResource("export format"));
    sqlRadioButton.setSelected(true);
    sqlRadioButton.setText(Options.getInstance().getResource("SQL (insert)"));
    sqlRadioButton.addItemListener(new TableExportDialog_sqlRadioButton_itemAdapter(this));
    txtRadioButton.setText(Options.getInstance().getResource("txt with delim"));
    txtRadioButton.addItemListener(new TableExportDialog_txtRadioButton_itemAdapter(this));
    delimTF.setEditable(false);
    delimTF.setText(",");
    delimTF.setColumns(10);
    xlsRadioButton.setText(Options.getInstance().getResource("excel (xls)"));
    getContentPane().add(mainPanel);
    mainPanel.add(headPanel,  BorderLayout.NORTH);
    mainPanel.add(tabPanel, BorderLayout.CENTER);
    tabPanel.add(tabbedPane,  BorderLayout.CENTER);
    tabbedPane.add(optionsPanel,  Options.getInstance().getResource("options"));
    tabbedPane.add(colPanel,  Options.getInstance().getResource("columns"));
    tabbedPane.setSelectedIndex(1);
    colPanel.add(colsScrollPane,             new GridBagConstraints(0, 0, 1, 5, 1.0, 1.0
            ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    colPanel.add(nullCheckBox,      new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(50, 5, 5, 5), 0, 0));
    colPanel.add(pkCheckBox,    new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    colPanel.add(selButton,           new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 20, 5, 20), 12, 0));
    colPanel.add(unselButton,   new GridBagConstraints(1, 4, 1, 1, 0.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 20, 5, 20), 0, 0));
    colsScrollPane.getViewport().add(colsTable, null);
    headPanel.add(tableTF,   new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    headPanel.add(okButton,   new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    headPanel.add(cancelButton,   new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    optionsPanel.add(whereLabel,       new GridBagConstraints(0, 0, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    optionsPanel.add(scrollPane,       new GridBagConstraints(0, 1, 5, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    scrollPane.getViewport().add(wherePane, null);
    optionsPanel.add(destLabel,       new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    optionsPanel.add(clipRadioButton,       new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    optionsPanel.add(fileRadioButton,        new GridBagConstraints(1, 3, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    optionsPanel.add(fileLabel,        new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 5, 5), 0, 0));
    optionsPanel.add(filenameTF,       new GridBagConstraints(0, 7, 4, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    optionsPanel.add(filenameButton,       new GridBagConstraints(4, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    optionsPanel.add(schemaCheckBox,        new GridBagConstraints(0, 8, 4, 1, 0.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    optionsPanel.add(formatLabel,      new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 5, 5), 0, 0));
    dest.add(clipRadioButton);
    dest.add(fileRadioButton);
    optionsPanel.add(sqlRadioButton,    new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    formatButtonGroup.add(sqlRadioButton);
    optionsPanel.add(txtRadioButton,    new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
    formatButtonGroup.add(txtRadioButton);
    optionsPanel.add(delimTF,    new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
    optionsPanel.add(xlsRadioButton,   new GridBagConstraints(3, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    formatButtonGroup.add(xlsRadioButton);
  }


  public void tableChanged(TableModelEvent e) {
    if (!enableListener)
      return;
    CustomTableModel model = (CustomTableModel)colsTable.getModel();
    if (pkCheckBox.isSelected()) {
      enableListener = false;
      Iterator it = pk.values().iterator();
      while(it.hasNext())
        model.setValueAt(new Boolean(false),((Integer)it.next()).intValue(),5);
      enableListener = true;
    }
    if (nullCheckBox.isSelected()) {
      enableListener = false;
      for(int i=0;i<model.getRowCount();i++)
        if (((Boolean)model.getValueAt(i,3)).booleanValue())
          model.setValueAt(new Boolean(false),i,5);
      enableListener = true;
    }
  }


  void okButton_actionPerformed(ActionEvent e) {
    try {
      PrintWriter pw = null;
      HSSFWorkbook hssfworkbook = null;
      HSSFSheet sheet = null;
      HSSFRow row = null;
      HSSFCell cell = null;
      String clipText = "";
      if (txtRadioButton.isSelected() &&
          delimTF.getText().length()==0) {
        JOptionPane.showMessageDialog(
            frame,
            Options.getInstance().getResource("please define a field delimiter."),
            Options.getInstance().getResource("export rows"),
            JOptionPane.WARNING_MESSAGE
        );
        return;
      }
      if (fileRadioButton.isSelected()) {
        if (filenameTF.getText().length()==0) {
          JOptionPane.showMessageDialog(
              frame,
              Options.getInstance().getResource("please select filename to store exporting rows."),
              Options.getInstance().getResource("export rows"),
              JOptionPane.WARNING_MESSAGE
          );
          return;
        }

        if (xlsRadioButton.isSelected()) {
          hssfworkbook = new HSSFWorkbook();
          sheet = hssfworkbook.createSheet();
        }
        else
          pw = new PrintWriter(new FileOutputStream(filenameTF.getText()));
      }
      String query = "SELECT ";
      String select = "";
      CustomTableModel model = (CustomTableModel)colsTable.getModel();
      for(int i=0;i<model.getRowCount();i++)
        if (((Boolean)model.getValueAt(i,5)).booleanValue())
          select += model.getValueAt(i,0)+",";
      if (select.length()==0)
        return;
      select = select.substring(0,select.length()-1);
      query += select+" FROM "+tableName;
      FilterModel fm = (FilterModel)dbConnUtil.getDbConnection().getFilters().get(tableName);
      if (fm==null) {
        fm = new FilterModel();
        dbConnUtil.getDbConnection().getFilters().put(tableName,fm);
      }

      String filter = fm.getWhereClause();
      query += filter;
      if (wherePane.getText().length()>0) {
        if (filter.length()>0) {
          query += " AND "+wherePane.getText().replace('\n',' ').substring(6);
        } else
          query += " "+wherePane.getText().replace('\n',' ');
      }


      int count = 0;
      Statement stmt = dbConnUtil.getConn().createStatement();
      ResultSet rset = stmt.executeQuery(query);
      String values = "";
      Object obj = null;
      String tName = tableName;
      if (tName.indexOf(".")>-1 && !schemaCheckBox.isSelected())
        tName = tName.substring(tName.indexOf(".")+1);
      String line = null;
      SimpleDateFormat sdf = new SimpleDateFormat(Options.getInstance().getDateFormat());

      while(rset.next()) {
        if (sqlRadioButton.isSelected()) {
          // export in SQL format...
          values = "";
          for(int i=0;i<rset.getMetaData().getColumnCount();i++) {
            obj = rset.getObject(i+1);
            if (obj==null)
              obj = "null";
            else if (obj instanceof java.sql.Timestamp || obj instanceof java.sql.Date)
              obj = dbConnUtil.convertDateToString((java.util.Date)obj);
            else if (obj instanceof String)
              obj = "'"+obj+"'";
            values += obj+",";
          }
          values = values.substring(0,values.length()-1);
          line = "INSERT INTO " + tName + "(" + select + ") VALUES(" + values + ");";
        }
        else if (txtRadioButton.isSelected()) {
          // export in TXT format...
          values = "";
          for(int i=0;i<rset.getMetaData().getColumnCount();i++) {
            obj = rset.getObject(i+1);
            if (obj==null)
              obj = "";
            else if (obj instanceof java.sql.Timestamp || obj instanceof java.sql.Date)
              obj = sdf.format((java.util.Date)obj);
            values += obj+delimTF.getText();
          }
          values = values.substring(0,values.length()-delimTF.getText().length());
          line = values;
        }
        else if (xlsRadioButton.isSelected()) {
          row = sheet.createRow(count);
          for(int i=0;i<rset.getMetaData().getColumnCount();i++) {
            obj = rset.getObject(i+1);
            if (obj!=null) {
              cell = row.createCell((short)i);
              if (obj instanceof java.sql.Timestamp || obj instanceof java.sql.Date) {
                obj = sdf.format((java.util.Date)obj);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(obj.toString());
              }
              else if (obj instanceof String) {
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(obj.toString());
              }
              else if (obj instanceof Number) {
                cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                cell.setCellValue(((Number)obj).doubleValue());
              }
            }
          }
        }

        // write to file/clipboard...
        if (fileRadioButton.isSelected()) {
          if (!xlsRadioButton.isSelected()) {
            pw.println(line);
            pw.flush();
          }
        }
        else {
          clipText += line+"\n";
        }
        count++;
      }
      rset.close();
      stmt.close();
      if (fileRadioButton.isSelected()) {
       if (xlsRadioButton.isSelected()) {
         FileOutputStream fileOut = new FileOutputStream(filenameTF.getText());
         hssfworkbook.write(fileOut);
         fileOut.close();
      }
       else
        pw.close();
      } else {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        clip.setContents(new StringSelection(clipText),null);
      }
      JOptionPane.showMessageDialog(
          frame,
          Options.getInstance().getResource("process completed.")+" "+count+" "+Options.getInstance().getResource("rows were exported."),
          Options.getInstance().getResource("export rows"),
          JOptionPane.INFORMATION_MESSAGE
      );
      setVisible(false);
      dispose();
    }
    catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          frame,
          Options.getInstance().getResource("error while exporting data")+":\n"+ex.getMessage(),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE
      );
    }
  }



  void cancelButton_actionPerformed(ActionEvent e) {
    setVisible(false);
    dispose();
  }

  void filenameButton_actionPerformed(ActionEvent e) {
    JFileChooser chooser = null;
    try {
      String path = System.getProperty("user.dir");
      if (path.charAt(0)=='/' || path.charAt(0)=='\\')
        path = path.substring(1);
      if (!path.endsWith("/") && !path.endsWith("\\"))
      path += "/";
      chooser = new JFileChooser(path);
    }
    catch (NullPointerException ex) {
      chooser = new JFileChooser(".");
    }
    chooser.setDialogType(chooser.SAVE_DIALOG);
    int returnVal = chooser.showDialog(this,Options.getInstance().getResource("save data"));
    if(returnVal == JFileChooser.APPROVE_OPTION) {
      filenameTF.setText(chooser.getSelectedFile().toString());
    }
  }


  void clipRadioButton_itemStateChanged(ItemEvent e) {
    filenameTF.setEnabled(false);
    filenameTF.setBackground(Color.lightGray);
    filenameTF.setText("");
  }

  void fileRadioButton_itemStateChanged(ItemEvent e) {
    filenameTF.setEnabled(true);
    filenameTF.setBackground(Color.white);
    xlsRadioButton.setEnabled(fileRadioButton.isSelected());
  }

  void unselButton_actionPerformed(ActionEvent e) {
    CustomTableModel model = (CustomTableModel)colsTable.getModel();
    for(int i=0;i<model.getRowCount();i++)
      model.setValueAt(new Boolean(false),i,5);
  }

  void selButton_actionPerformed(ActionEvent e) {
    CustomTableModel model = (CustomTableModel)colsTable.getModel();
    for(int i=0;i<model.getRowCount();i++)
      model.setValueAt(new Boolean(true),i,5);
  }

  void nullCheckBox_itemStateChanged(ItemEvent e) {
    tableChanged(null);
  }

  void pkCheckBox_itemStateChanged(ItemEvent e) {
    tableChanged(null);
  }

  void txtRadioButton_itemStateChanged(ItemEvent e) {
    delimTF.setEditable(txtRadioButton.isSelected());
  }

  void sqlRadioButton_itemStateChanged(ItemEvent e) {
    schemaCheckBox.setEnabled(sqlRadioButton.isSelected());
  }


}

class TableExportDialog_okButton_actionAdapter implements java.awt.event.ActionListener {
  TableExportDialog adaptee;

  TableExportDialog_okButton_actionAdapter(TableExportDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.okButton_actionPerformed(e);
  }
}

class TableExportDialog_cancelButton_actionAdapter implements java.awt.event.ActionListener {
  TableExportDialog adaptee;

  TableExportDialog_cancelButton_actionAdapter(TableExportDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cancelButton_actionPerformed(e);
  }
}

class TableExportDialog_filenameButton_actionAdapter implements java.awt.event.ActionListener {
  TableExportDialog adaptee;

  TableExportDialog_filenameButton_actionAdapter(TableExportDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.filenameButton_actionPerformed(e);
  }
}

class TableExportDialog_clipRadioButton_itemAdapter implements java.awt.event.ItemListener {
  TableExportDialog adaptee;

  TableExportDialog_clipRadioButton_itemAdapter(TableExportDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void itemStateChanged(ItemEvent e) {
    adaptee.clipRadioButton_itemStateChanged(e);
  }
}

class TableExportDialog_fileRadioButton_itemAdapter implements java.awt.event.ItemListener {
  TableExportDialog adaptee;

  TableExportDialog_fileRadioButton_itemAdapter(TableExportDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void itemStateChanged(ItemEvent e) {
    adaptee.fileRadioButton_itemStateChanged(e);
  }
}

class TableExportDialog_unselButton_actionAdapter implements java.awt.event.ActionListener {
  TableExportDialog adaptee;

  TableExportDialog_unselButton_actionAdapter(TableExportDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.unselButton_actionPerformed(e);
  }
}

class TableExportDialog_selButton_actionAdapter implements java.awt.event.ActionListener {
  TableExportDialog adaptee;

  TableExportDialog_selButton_actionAdapter(TableExportDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.selButton_actionPerformed(e);
  }
}

class TableExportDialog_nullCheckBox_itemAdapter implements java.awt.event.ItemListener {
  TableExportDialog adaptee;

  TableExportDialog_nullCheckBox_itemAdapter(TableExportDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void itemStateChanged(ItemEvent e) {
    adaptee.nullCheckBox_itemStateChanged(e);
  }
}

class TableExportDialog_pkCheckBox_itemAdapter implements java.awt.event.ItemListener {
  TableExportDialog adaptee;

  TableExportDialog_pkCheckBox_itemAdapter(TableExportDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void itemStateChanged(ItemEvent e) {
    adaptee.pkCheckBox_itemStateChanged(e);
  }
}

class TableExportDialog_txtRadioButton_itemAdapter implements java.awt.event.ItemListener {
  TableExportDialog adaptee;

  TableExportDialog_txtRadioButton_itemAdapter(TableExportDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void itemStateChanged(ItemEvent e) {
    adaptee.txtRadioButton_itemStateChanged(e);
  }
}

class TableExportDialog_sqlRadioButton_itemAdapter implements java.awt.event.ItemListener {
  TableExportDialog adaptee;

  TableExportDialog_sqlRadioButton_itemAdapter(TableExportDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void itemStateChanged(ItemEvent e) {
    adaptee.sqlRadioButton_itemStateChanged(e);
  }
}