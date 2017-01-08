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
import org.jsqltool.gui.tablepanel.*;
import org.jsqltool.*;
import org.jsqltool.gui.tableplugins.datatable.filter.*;
import org.jsqltool.gui.panel.*;
import org.jsqltool.utils.Options;
import java.io.*;
import org.jsqltool.utils.ImageLoader;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Panel used to show the table content.
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
public class DataTablePanel extends JPanel implements TableModelListener,TablePlugin {

  private String tableName = null;
  private Hashtable pk = null;
  JPanel buttonsPanel = new JPanel();
  JButton insertButton = new JButton();
  JButton delButton = new JButton();
  JButton findButton = new JButton();
  JButton cancelEditButton = new JButton();
  JButton refreshButton = new JButton();
  JButton firstButton = new JButton();
  JButton leftButton = new JButton();
  JButton rightButton = new JButton();
  JButton lastButton = new JButton();
  ImageIcon insertImage;
  ImageIcon delImage;
  ImageIcon findImage;
  ImageIcon find2Image;
  ImageIcon refreshImage;
  ImageIcon cancelEditImage;
  ImageIcon commitImage;
  ImageIcon firstImage;
  ImageIcon leftImage;
  ImageIcon rightImage;
  ImageIcon lastImage;
  FlowLayout flowLayout1 = new FlowLayout();
  DataPanel dataPanel = null;
  BorderLayout borderLayout1 = new BorderLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  private DbConnectionUtil dbConnUtil = null;
  private JFrame parent = null;
  private static final String ASC_SYMBOL = "ASC";
  private static final String DESC_SYMBOL = "DESC";
  private Object[] selectedRow = null;

  /** current changed row */
  private int currentRow = -1;

  /** current selected cell value */
  private JTextField filterTF = new JTextField(15);

  /** current selected column name */
  private String columnName = null;



  public DataTablePanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * Remove a column filter clause, if there exist.
   * @param colName column name
   * @param refresh flag used to force data table reloading
   */
  private void removeColumnFilter(String colName,boolean refresh) {
    FilterModel fm = (FilterModel)dbConnUtil.getDbConnection().getFilters().get(tableName);
    if (fm==null) {
      fm = new FilterModel();
      dbConnUtil.getDbConnection().getFilters().put(tableName,fm);
    }
    String sql = fm.getWhereClause();
    int i = -1;
    int j = -1;
    int j1 = -1;
    while((i=sql.indexOf(columnName))!=-1) {
      j = sql.toLowerCase().indexOf(" and ",i);
      j1 = sql.toLowerCase().indexOf(" or ",i);
      if (j1!=-1 && j1<j)
        j = j1;
      if (i==0) {
        if (j==-1)
          sql = "";
        else
          sql = sql.substring(j);
      }
      else {
        if (j==-1)
          sql = sql.substring(0,i);
        else
        sql = sql.substring(0,i)+sql.substring(j);
      }
    }
    if (sql.toLowerCase().trim().endsWith("and"))
      sql = sql.substring(0,sql.toLowerCase().lastIndexOf("and"));
    if (sql.toLowerCase().trim().endsWith("or"))
      sql = sql.substring(0,sql.toLowerCase().lastIndexOf("or"));

    i = sql.indexOf("WHERE");
    if (i==-1)
      i = 0;
    else
      i = i+5;
    if (sql.substring(i).toLowerCase().trim().startsWith("and"))
      sql = sql.substring(sql.toLowerCase().indexOf("and")+3);
    if (sql.substring(i).toLowerCase().trim().startsWith("or"))
      sql = sql.substring(sql.toLowerCase().indexOf("of")+2);

    if (sql.trim().equals("WHERE"))
      sql = "";

    fm.setWhereClause(sql);
    dbConnUtil.saveProfile(true);
    if (refresh)
      refreshButton_actionPerformed(null);
  }


  private void init() {
    // pop-up menu creation...
    final JPopupMenu tableMenu = new JPopupMenu();

    filterTF.addKeyListener(new KeyAdapter() {
      public void keyTyped(KeyEvent e) {
        if (e.getKeyChar()=='\n') {
          tableMenu.setVisible(false);

          if (columnName!=null) {
            // remove column filtering...
            removeColumnFilter(columnName,false);

            // reset table filtering...
            FilterModel fm = (FilterModel)dbConnUtil.getDbConnection().getFilters().get(tableName);
            if (fm==null) {
              fm = new FilterModel();
              dbConnUtil.getDbConnection().getFilters().put(tableName,fm);
            }
            String sql = fm.getWhereClause().trim();

            String value = filterTF.getText();
            try {
              Number n = new Double(value);
            }
            catch (Exception ex) {
              value = "'"+value+"'";
            }

            if (sql.length()==0) {
              sql = columnName+" = "+value;
            }
            else {
              sql += " and " + columnName + " = " + value;
            }
            fm.setWhereClause(sql);
            dbConnUtil.saveProfile(true);

            // reload data
            refreshButton_actionPerformed(null);
          }
        }
      }
    });
    JPanel filterPanel = new JPanel();
    filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT,20,5));
    JLabel filterLabel = new JLabel(Options.getInstance().getResource("filter by "));
    filterPanel.add(filterLabel,null);
    filterPanel.add(filterTF,null);
    JMenuItem removeFilterMenu = new JMenuItem(Options.getInstance().getResource("remove filter"));
    removeFilterMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // remove column filtering and reload data...
        if (columnName!=null)
          removeColumnFilter(columnName,true);
      }
    });
    tableMenu.add(filterPanel);
    tableMenu.add(removeFilterMenu);
    tableMenu.add(new JSeparator());
    filterLabel.setFont(removeFilterMenu.getFont());


    JMenuItem copyMenu = new JMenuItem(Options.getInstance().getResource("copy row"));
    JMenuItem countMenu = new JMenuItem(Options.getInstance().getResource("record count"));
    JMenuItem exportMenu = new JMenuItem(Options.getInstance().getResource("data export..."));
    final JMenuItem impoMenu = new JMenuItem(Options.getInstance().getResource("import file into..."));
    final JMenuItem expoMenu = new JMenuItem(Options.getInstance().getResource("export to file..."));
    impoMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          int index = dataPanel.getTable().getSelectedRow();
          int col = dataPanel.getTable().getSelectedColumn();
          if (index != -1 && col != -1) {
            ( (CustomTableModel) dataPanel.getTableModel()).setEditMode(CustomTableModel.EDIT_REC);
            JFileChooser chooser = new JFileChooser(".");
            int returnVal = chooser.showDialog(null,Options.getInstance().getResource("import file"));
            if(returnVal == JFileChooser.APPROVE_OPTION) {
              File f = chooser.getSelectedFile();
              BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
              byte[] bytes = new byte[(int)f.length()];
              in.read(bytes);
              in.close();

              // definisco la parte where...
              String wherePK = "";
              ArrayList pkValues = new ArrayList();
              Enumeration en = pk.keys();
              String colName = null;
              while(en.hasMoreElements()) {
                colName = en.nextElement().toString();
                wherePK += colName+"=? and ";
                pkValues.add(dataPanel.getTableModel().getValueAt(index,((Integer)pk.get(colName)).intValue()));
              }
              if (wherePK.length()>0)
                wherePK = wherePK.substring(0,wherePK.length()-4);
              else if (!Options.getInstance().isUpdateWhenNoPK() || selectedRow==null) {
                JOptionPane.showMessageDialog(
                    null,
                    Options.getInstance().getResource("can't update data: pk is not defined."),
                    Options.getInstance().getResource("error"),
                    JOptionPane.ERROR_MESSAGE
                );
                return;
              } else {
                for(int i=0;i<dataPanel.getTableModel().getColumnCount();i++) {
                  wherePK += dataPanel.getTableModel().getColumnName(i) + "=? AND ";
                  pkValues.add(selectedRow[i]);
                }
                wherePK = wherePK.substring(0,wherePK.length()-4);
              }
              colName = dataPanel.getTableModel().getColumnName(col);
              if (pk.containsKey(colName)) {
                JOptionPane.showMessageDialog(
                    null,
                    Options.getInstance().getResource("can't update pk!"),
                    Options.getInstance().getResource("error"),
                    JOptionPane.ERROR_MESSAGE
                );
                return;
              }
              PreparedStatement pstmt = dbConnUtil.getConn().prepareStatement(
                  "UPDATE " +tableName+ " SET "+colName+"=? WHERE "+wherePK
              );
              dbConnUtil.writeBlob(bytes,pstmt);
              for(int i=0;i<pkValues.size();i++)
                pstmt.setObject(i+2,pkValues.get(i));
              pstmt.execute();
              pstmt.close();
            }
          }
        }
        catch (Exception ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(
              null,
              Options.getInstance().getResource("error while setting blob content")+":\n"+ex.getMessage(),
              Options.getInstance().getResource("import file into blob field"),
              JOptionPane.ERROR_MESSAGE
          );
        }
      }
    });

    expoMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          int index = dataPanel.getTable().getSelectedRow();
          int col = dataPanel.getTable().getSelectedColumn();
          if (index != -1 && col != -1) {
            JFileChooser chooser = new JFileChooser(".");
            int returnVal = chooser.showDialog(null,Options.getInstance().getResource("export to file"));
            if(returnVal == JFileChooser.APPROVE_OPTION) {
              File f = chooser.getSelectedFile();

              // where...
              String wherePK = "";
              ArrayList pkValues = new ArrayList();
              Enumeration en = pk.keys();
              String colName = null;
              while(en.hasMoreElements()) {
                colName = en.nextElement().toString();
                wherePK += colName+"=? and ";
                pkValues.add(dataPanel.getTableModel().getValueAt(index,((Integer)pk.get(colName)).intValue()));
              }
              if (wherePK.length()>0)
                wherePK = wherePK.substring(0,wherePK.length()-4);
              else if (!Options.getInstance().isUpdateWhenNoPK() || selectedRow==null) {
                JOptionPane.showMessageDialog(
                    null,
                    Options.getInstance().getResource("can't fetch data: pk is not defined."),
                    Options.getInstance().getResource("error"),
                    JOptionPane.ERROR_MESSAGE
                );
                return;
              } else {
                for(int i=0;i<dataPanel.getTableModel().getColumnCount();i++) {
                  wherePK += dataPanel.getTableModel().getColumnName(i) + "=? AND ";
                  pkValues.add(selectedRow[i]);
                }
                wherePK = wherePK.substring(0,wherePK.length()-4);
              }
              colName = dataPanel.getTableModel().getColumnName(col);
              if (pk.containsKey(colName)) {
                JOptionPane.showMessageDialog(
                    null,
                    Options.getInstance().getResource("can't fetch pk!"),
                    Options.getInstance().getResource("error"),
                    JOptionPane.ERROR_MESSAGE
                );
                return;
              }
              PreparedStatement pstmt = dbConnUtil.getConn().prepareStatement(
                  "select "+
                  ((CustomTableModel) dataPanel.getTableModel()).getColumnName(col)+" from "+
                  tableName+" where "+
                  wherePK
              );
              for(int i=0;i<pkValues.size();i++)
                pstmt.setObject(i+1,pkValues.get(i));
              ResultSet rset = pstmt.executeQuery();
              if (rset.next()) {
                Blob blob = rset.getBlob(1);
                int l = (int)blob.length();
                byte[] bytes = blob.getBytes(1,l);
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
                out.write(bytes);
                out.flush();
                out.close();
              }
              rset.close();
              pstmt.close();
            }
          }
        }
        catch (Exception ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(
              null,
              Options.getInstance().getResource("error while getting blob content")+":\n"+ex.getMessage(),
              Options.getInstance().getResource("import file into blob field"),
              JOptionPane.ERROR_MESSAGE
          );
        }
      }
    });




    copyMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          int index = dataPanel.getTable().getSelectedRow();
          if (index != -1) {
            ( (CustomTableModel) dataPanel.getTableModel()).setEditMode(CustomTableModel.INSERT_REC);
            for(int i=0;i<( (CustomTableModel) dataPanel.getTableModel()).getColumnCount();i++) {
              if (!pk.containsKey(((CustomTableModel) dataPanel.getTableModel()).getColumnName(i)))
                ((CustomTableModel) dataPanel.getTableModel()).setValueAt(
                  ((CustomTableModel) dataPanel.getTableModel()).getValueAt(index,i),
                  ((CustomTableModel) dataPanel.getTableModel()).getRowCount()-1,
                  i
                );
            }
          }
        }
        catch (Exception ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(
              parent,
              Options.getInstance().getResource("error while copying record")+":\n"+ex.getMessage(),
              Options.getInstance().getResource("record count"),
              JOptionPane.ERROR_MESSAGE
          );
        }
      }
    });
    countMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          FilterModel fm = (FilterModel)dbConnUtil.getDbConnection().getFilters().get(tableName);
          if (fm==null) {
            fm = new FilterModel();
            dbConnUtil.getDbConnection().getFilters().put(tableName,fm);
          }

          String filter = fm.getWhereClause() + fm.getOrderClause();
          Statement stmt = dbConnUtil.getConn().createStatement();
          ResultSet rset = stmt.executeQuery("SELECT count(*) FROM " +
                                             tableName + filter);
          int count = 0;
          if (rset.next()) {
            count = rset.getInt(1);
          }
          rset.close();
          stmt.close();
          JOptionPane.showMessageDialog(
              parent,
              count+" "+
              Options.getInstance().getResource("records found."),
              Options.getInstance().getResource("record count"),
              JOptionPane.INFORMATION_MESSAGE
          );
        }
        catch (SQLException ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(
              parent,
              Options.getInstance().getResource("error while record counting")+":\n"+ex.getMessage(),
              Options.getInstance().getResource("record count"),
              JOptionPane.ERROR_MESSAGE
          );
        }
      }
    });
    exportMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        TableExportDialog f = new TableExportDialog(parent,dbConnUtil,tableName);
      }
    });
    tableMenu.add(impoMenu);
    tableMenu.add(expoMenu);
    tableMenu.add(new JSeparator());
    tableMenu.add(copyMenu);
    tableMenu.add(countMenu);
    tableMenu.add(exportMenu);

    dataPanel.getTable().addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) { // right mouse click
          impoMenu.setEnabled(
              ( (CustomTableModel)dataPanel.getTableModel()).getEditMode()==CustomTableModel.EDIT_REC &&
              dataPanel.getTable().getSelectedColumn() != -1 &&
              ( (CustomTableModel)dataPanel.getTableModel()).isBlob(dataPanel.getTable().getSelectedColumn())
          );
          expoMenu.setEnabled(
              ( (CustomTableModel)dataPanel.getTableModel()).getEditMode()==CustomTableModel.EDIT_REC &&
              dataPanel.getTable().getSelectedColumn() != -1 &&
              ( (CustomTableModel)dataPanel.getTableModel()).isBlob(dataPanel.getTable().getSelectedColumn())
          );
          // show pop-up menu...

          try {
            if (dataPanel.getTable().rowAtPoint(e.getPoint()) != -1 &&
                dataPanel.getTable().columnAtPoint(e.getPoint()) != -1) {
              Object value = dataPanel.getTable().getValueAt(
                dataPanel.getTable().rowAtPoint(e.getPoint()),
                dataPanel.getTable().columnAtPoint(e.getPoint())
              );
              columnName = dataPanel.getTableModel().getColumnName(dataPanel.getTable().columnAtPoint(e.getPoint()));
              filterTF.setText(value==null?"":value.toString());
            }
            else {
              columnName = null;
              filterTF.setText("");
            }
          }
          catch (Exception ex) {
          }
          tableMenu.show(e.getComponent(), e.getX(), e.getY());
          filterTF.requestFocus();
        }
      }
    });

//    dataPanel.getTable().setColumnSelectionAllowed(false);
//    dataPanel.getTable().setRowSelectionAllowed(false);
    dataPanel.getTable().setListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (currentRow!=-1 &&
            dataPanel.getTable().getSelectedRow()!=currentRow) {
          if (((CustomTableModel)dataPanel.getTableModel()).getEditMode()==CustomTableModel.INSERT_REC)
            insertRecord(currentRow);
          else if (((CustomTableModel)dataPanel.getTableModel()).getEditMode()==CustomTableModel.EDIT_REC)
            updateRecord(currentRow);

        }
        if (dataPanel.getTable().getSelectedRow()==-1) {
          selectedRow = null;
          return;
        }
        selectedRow = new Object[dataPanel.getTableModel().getColumnCount()];
        for(int i=0;i<dataPanel.getTableModel().getColumnCount();i++)
          selectedRow[i] = dataPanel.getTableModel().getValueAt(dataPanel.getTable().getSelectedRow(),i);
      }
    });

  }


  public void setQuery(String tableName) {
    if (tableName==null)
      return;
    selectedRow = null;
    this.tableName = tableName;
    FilterModel fm = (FilterModel)dbConnUtil.getDbConnection().getFilters().get(tableName);
    if (fm==null) {
      fm = new FilterModel();
      dbConnUtil.getDbConnection().getFilters().put(tableName,fm);
    }

    String filter = fm.getWhereClause()+fm.getOrderClause();
    dataPanel.setQuery("SELECT * FROM "+tableName+" "+filter,new Vector());
    pk = dbConnUtil.getPK(tableName);
    if (filter.length()>0)
      findButton.setIcon(find2Image);
    else
      findButton.setIcon(findImage);

    // set editability...
    ((CustomTableModel)dataPanel.getTableModel()).setEditMode(CustomTableModel.EDIT_REC);
    boolean[] editableCols = new boolean[dataPanel.getTableModel().getColumnCount()];
    for(int i=0;i<dataPanel.getTableModel().getColumnCount();i++)
      editableCols[i] =
          !pk.contains(dataPanel.getTableModel().getColumnName(i)) &&
          (Options.getInstance().isUpdateWhenNoPK() || pk.size()>0);
    ((CustomTableModel)dataPanel.getTableModel()).setEditableCols(editableCols);
  }


  /**
   * Insert a record in the table.
   * @param rowNum row index which contains the new row that will be inserted
   */
  private void insertRecord(int rowNum) {
    try {
      // verifico se i campi PK sono stati tutti valorizzati, altrimenti esco...
      String names = "";
      String values = "";
      Object value = null;
      ArrayList bindValues = new ArrayList();
      for(int i=0;i<dataPanel.getTableModel().getColumnCount();i++)
        if (dataPanel.getTableModel().getValueAt(rowNum,i)!=null) {
          if (!dbConnUtil.getDbConnection().isQuotes())
            names += dataPanel.getTableModel().getColumnName(i)+", ";
          else
            names += "\""+dataPanel.getTableModel().getColumnName(i)+"\", ";
          values += "?, ";
          value = dataPanel.getTableModel().getValueAt(rowNum,i);
          if (value!=null && value.getClass().getName().equals("java.util.Date"))
            value = new java.sql.Timestamp(((java.util.Date)value).getTime());
          bindValues.add(value);
        }
      names = names.substring(0,names.length()-2);
      values = values.substring(0,values.length()-2);

      PreparedStatement pstmt = dbConnUtil.getConn().prepareStatement(
          !dbConnUtil.getDbConnection().isQuotes()?
          ("INSERT INTO " +tableName+ "("+names+") VALUES("+values+")"):
          ("INSERT INTO \"" +tableName+ "\"("+names+") VALUES("+values+")")
      );
      for(int i=0;i<bindValues.size();i++)
        pstmt.setObject(i+1,bindValues.get(i));
      pstmt.execute();
      pstmt.close();
      ((CustomTableModel)dataPanel.getTableModel()).setEditMode(CustomTableModel.EDIT_REC);
      currentRow = -1;
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          this,
          Options.getInstance().getResource("error on inserting data")+":\n"+ex.getMessage(),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE
      );
      dataPanel.getTable().setRowSelectionInterval(rowNum,rowNum);
    }

  }


  /**
   * Update a record in the table.
   * @param rowNum row index which contains the row that will be updated
   */
  private void updateRecord(int rowNum) {
    try {
      // prepare SET...
      String colNames = "";
      ArrayList values = new ArrayList();
      Object value = null;
      for(int i=0;i<dataPanel.getTableModel().getColumnCount();i++) {
        value = dataPanel.getTableModel().getValueAt(rowNum,i);
        if (value!=null) {
          if (!dbConnUtil.getDbConnection().isQuotes())
            colNames += dataPanel.getTableModel().getColumnName(i) + "=?, ";
          else
            colNames += "\""+dataPanel.getTableModel().getColumnName(i) + "\"=?, ";

          if (value!=null && value.getClass().getName().equals("java.util.Date"))
            value = new java.sql.Timestamp(((java.util.Date)value).getTime());
          values.add(value);
        }
        else {
          if (!dbConnUtil.getDbConnection().isQuotes())
            colNames += dataPanel.getTableModel().getColumnName(i) + "=null, ";
          else
            colNames += "\""+dataPanel.getTableModel().getColumnName(i) + "\"=null, ";
        }
      }
      colNames = colNames.substring(0,colNames.length()-2);

      // prepare WHERE...
      String wherePK = "";
      Enumeration en = pk.keys();
      String colName = null;
      while(en.hasMoreElements()) {
        colName = en.nextElement().toString();
        if (!dbConnUtil.getDbConnection().isQuotes())
          wherePK += colName+"=? and ";
        else
          wherePK += "\""+colName+"\"=? and ";
        value = dataPanel.getTableModel().getValueAt(rowNum,((Integer)pk.get(colName)).intValue());
        if (value!=null && value.getClass().getName().equals("java.util.Date"))
          value = new java.sql.Timestamp(((java.util.Date)value).getTime());
        values.add(value);
      }
      if (wherePK.length()>0)
        wherePK = wherePK.substring(0,wherePK.length()-4);
      else if (!Options.getInstance().isUpdateWhenNoPK()/* || selectedRow==null*/) {
        dataPanel.getTableModel().removeTableModelListener(this);
        for(int i=0;i<dataPanel.getTableModel().getColumnCount();i++) {
          dataPanel.getTableModel().setValueAt(selectedRow[i],rowNum,i);
        }
        dataPanel.getTableModel().addTableModelListener(this);
        JOptionPane.showMessageDialog(
            this,
            Options.getInstance().getResource("can't update data: pk is not defined."),
            Options.getInstance().getResource("error"),
            JOptionPane.ERROR_MESSAGE
        );
        return;
      } else {
        for(int i=0;i<dataPanel.getTableModel().getColumnCount();i++) {
          wherePK += dataPanel.getTableModel().getColumnName(i) + "=? AND ";
          values.add(selectedRow[i]);
        }
        wherePK = wherePK.substring(0,wherePK.length()-4);
      }

      PreparedStatement pstmt = dbConnUtil.getConn().prepareStatement(
          !dbConnUtil.getDbConnection().isQuotes()?
          ("UPDATE " +tableName+ " SET "+colNames+" WHERE "+wherePK) :
          ("UPDATE \"" +tableName+ "\" SET "+colNames+" WHERE "+wherePK)

      );

      for(int i=0;i<values.size();i++)
        pstmt.setObject(i+1,values.get(i));
      pstmt.execute();
      pstmt.close();
      currentRow = -1;
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          this,
          Options.getInstance().getResource("error on updating data")+":\n"+ex.getMessage(),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE
      );
      dataPanel.getTable().setRowSelectionInterval(rowNum,rowNum);
    }
  }


  /**
   *  This fine grain notification tells listeners the exact range of cells,
   * rows, or columns that changed.
   */
  public void tableChanged(TableModelEvent e){
    if (dbConnUtil.getDbConnection().isReadOnly())
      return;
    if (e.getType()==e.UPDATE && ((CustomTableModel)dataPanel.getTableModel()).getEditMode()==CustomTableModel.EDIT_REC) {
      if (currentRow==-1)
        currentRow = e.getFirstRow();
      else if (currentRow!=e.getFirstRow()) {
        updateRecord(currentRow);
        currentRow = e.getFirstRow();
      }

    } else if (e.getType()==e.UPDATE && ((CustomTableModel)dataPanel.getTableModel()).getEditMode()==CustomTableModel.INSERT_REC) {
      if (currentRow==-1)
        currentRow = e.getFirstRow();
      else if (currentRow!=e.getFirstRow()) {
        insertRecord(currentRow);
        currentRow = e.getFirstRow();
      }
    }
  }


  public String getTableName() {
    return tableName;
  }


  /**
   * This method is called from the table detail to set entity name.
   * @param tableName table name (edventualy including catalog name) that table plugin have to show
   */
  public final void setTableName(String tableName) {
    this.tableName = tableName;
  }


  public void setEditMode(int editMode) {
    if (dbConnUtil.getDbConnection().isReadOnly())
      return;
    ((CustomTableModel)dataPanel.getTableModel()).setEditMode(editMode);
  }


  public int getEditMode() {
    return ((CustomTableModel)dataPanel.getTableModel()).getEditMode();
  }


  void insertButton_actionPerformed(ActionEvent e) {
    if (this.getEditMode()!=CustomTableModel.INSERT_REC) {
      this.setEditMode(CustomTableModel.INSERT_REC);
      dataPanel.getTable().setRowSelectionInterval(dataPanel.getTable().getRowCount()-1,dataPanel.getTable().getRowCount()-1);
      try {
        dataPanel.getTable().scrollRectToVisible(dataPanel.getTable().getCellRect(dataPanel.getTable().getRowCount() - 1, 0, true));
      }
      catch (Exception ex) {
      }
      dataPanel.getTable().editCellAt(dataPanel.getTable().getRowCount(),0);
    }
  }


  void delButton_actionPerformed(ActionEvent e) {
    if (dbConnUtil.getDbConnection().isReadOnly())
      return;
    if (((CustomTableModel)dataPanel.getTableModel()).getEditMode()==CustomTableModel.INSERT_REC)
      return;
    if (dataPanel.getTable().getSelectedRow()==-1)
      return;
    try {
      String wherePK = "";
      ArrayList pkValues = new ArrayList();
      Enumeration en = pk.keys();
      String colName = null;
      while(en.hasMoreElements()) {
        colName = en.nextElement().toString();
        if (!dbConnUtil.getDbConnection().isQuotes())
          wherePK += colName+"=? and ";
        else
          wherePK += "\""+colName+"\"=? and ";
        pkValues.add(dataPanel.getTableModel().getValueAt(dataPanel.getTable().getSelectedRow(),((Integer)pk.get(colName)).intValue()));
      }
      if (wherePK.length()>0)
        wherePK = wherePK.substring(0,wherePK.length()-4);
      else {
        JOptionPane.showMessageDialog(
            this,
            Options.getInstance().getResource("can't delete data: pk is not defined."),
            Options.getInstance().getResource("error"),
            JOptionPane.ERROR_MESSAGE
        );
        return;
      }
      PreparedStatement pstmt = dbConnUtil.getConn().prepareStatement(
          !dbConnUtil.getDbConnection().isQuotes()?
          ("DELETE from " +tableName+ " WHERE "+wherePK) :
          ("DELETE from \"" +tableName+ "\" WHERE "+wherePK)
      );
      for(int i=0;i<pkValues.size();i++)
        pstmt.setObject(i+1,pkValues.get(i));
      pstmt.execute();
      pstmt.close();
      ((CustomTableModel)dataPanel.getTableModel()).removeRow(dataPanel.getTable().getSelectedRow());
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          this,
          Options.getInstance().getResource("error on deleting data")+":\n"+ex.getMessage(),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE
      );
    }

  }


  void refreshButton_actionPerformed(ActionEvent e) {
    if (this.getTableName()!=null)
      this.setQuery(this.getTableName());
  }


  class DataTablePanel_insertButton_actionAdapter implements java.awt.event.ActionListener {
    DataTablePanel adaptee;

    DataTablePanel_insertButton_actionAdapter(DataTablePanel adaptee) {
      this.adaptee = adaptee;
    }
    public void actionPerformed(ActionEvent e) {
      adaptee.insertButton_actionPerformed(e);
    }
  }

  class DataTablePanel_delButton_actionAdapter implements java.awt.event.ActionListener {
    DataTablePanel adaptee;

    DataTablePanel_delButton_actionAdapter(DataTablePanel adaptee) {
      this.adaptee = adaptee;
    }
    public void actionPerformed(ActionEvent e) {
      adaptee.delButton_actionPerformed(e);
    }
  }

  class DataTablePanel_findButton_actionAdapter implements java.awt.event.ActionListener {
    DataTablePanel adaptee;

    DataTablePanel_findButton_actionAdapter(DataTablePanel adaptee) {
      this.adaptee = adaptee;
    }
    public void actionPerformed(ActionEvent e) {
      adaptee.findButton_actionPerformed(e);
    }
  }


  class DataTablePanel_refreshButton_actionAdapter implements java.awt.event.ActionListener {
    DataTablePanel adaptee;

    DataTablePanel_refreshButton_actionAdapter(DataTablePanel adaptee) {
      this.adaptee = adaptee;
    }
    public void actionPerformed(ActionEvent e) {
      adaptee.refreshButton_actionPerformed(e);
    }

  }


  private void jbInit() throws Exception {
    this.setLayout(borderLayout2);
    cancelEditButton.addActionListener(new DataTablePanel_cancelEditButton_actionAdapter(this));
    this.add(buttonsPanel,  BorderLayout.NORTH);
    insertButton.setBorder(null);
    insertButton.setPreferredSize(new Dimension(24, 24));
    insertButton.addActionListener(new DataTablePanel_insertButton_actionAdapter(this));
    delButton.setBorder(null);
    delButton.setPreferredSize(new Dimension(24, 24));
    delButton.addActionListener(new DataTablePanel_delButton_actionAdapter(this));
    findButton.setBorder(null);
    findButton.setPreferredSize(new Dimension(24, 24));
    findButton.addActionListener(new DataTablePanel_findButton_actionAdapter(this));

    cancelEditButton.setBorder(null);
    cancelEditButton.setPreferredSize(new Dimension(24, 24));
    refreshButton.setBorder(null);
    refreshButton.setPreferredSize(new Dimension(24, 24));
    refreshButton.addActionListener(new DataTablePanel_refreshButton_actionAdapter(this));
    firstButton.setBorder(null);
    firstButton.setPreferredSize(new Dimension(24, 24));
    firstButton.addActionListener(new DataTablePanel_firstButton_actionAdapter(this));
    leftButton.setBorder(null);
    leftButton.setPreferredSize(new Dimension(24, 24));
    leftButton.addActionListener(new DataTablePanel_leftButton_actionAdapter(this));
    rightButton.setBorder(null);
    rightButton.setPreferredSize(new Dimension(24, 24));
    rightButton.addActionListener(new DataTablePanel_rightButton_actionAdapter(this));
    lastButton.setBorder(null);
    lastButton.setPreferredSize(new Dimension(24, 24));
    lastButton.addActionListener(new DataTablePanel_lastButton_actionAdapter(this));

    buttonsPanel.setLayout(flowLayout1);
    insertButton.addActionListener(new DataTablePanel_insertButton_actionAdapter(this));
    delButton.addActionListener(new DataTablePanel_delButton_actionAdapter(this));
    flowLayout1.setAlignment(FlowLayout.LEFT);
    flowLayout1.setHgap(0);
    refreshButton.addActionListener(new DataTablePanel_refreshButton_actionAdapter(this));
    insertImage = ImageLoader.getInstance().getIcon("insert.gif");
    delImage = ImageLoader.getInstance().getIcon("delete.gif");
    findImage = ImageLoader.getInstance().getIcon("find.gif");
    find2Image = ImageLoader.getInstance().getIcon("find2.gif");
    cancelEditImage = ImageLoader.getInstance().getIcon("canceledit.gif");
// -MC 02/05/2006    refreshImage = ImageLoader.getInstance().getIcon("refreshdata.gif");
    refreshImage = ImageLoader.getInstance().getIcon("refresh2.gif");
    commitImage = ImageLoader.getInstance().getIcon("commit.gif");
    firstImage = ImageLoader.getInstance().getIcon("first.gif");
    leftImage = ImageLoader.getInstance().getIcon("left.gif");
    rightImage = ImageLoader.getInstance().getIcon("right.gif");
    lastImage = ImageLoader.getInstance().getIcon("last.gif");

    insertButton.setIcon(insertImage);
    insertButton.setToolTipText(Options.getInstance().getResource("insert record"));
    insertButton.setMaximumSize(new Dimension(24,24));
    delButton.setIcon(delImage);
    delButton.setToolTipText(Options.getInstance().getResource("delete record"));
    delButton.setMaximumSize(new Dimension(24,24));
    findButton.setMaximumSize(new Dimension(24,24));

    cancelEditButton.setIcon(cancelEditImage);
    cancelEditButton.setToolTipText(Options.getInstance().getResource("cancel edit"));
    cancelEditButton.setMaximumSize(new Dimension(24,24));
    refreshButton.setIcon(refreshImage);
    refreshButton.setToolTipText(Options.getInstance().getResource("refresh data"));
    refreshButton.setMaximumSize(new Dimension(24,24));
    firstButton.setMaximumSize(new Dimension(24,24));
    leftButton.setMaximumSize(new Dimension(24,24));
    rightButton.setMaximumSize(new Dimension(24,24));
    lastButton.setMaximumSize(new Dimension(24,24));
    findButton.setIcon(findImage);
    findButton.setToolTipText(Options.getInstance().getResource("filter data"));
    firstButton.setIcon(firstImage);
    firstButton.setToolTipText(Options.getInstance().getResource("first records"));
    leftButton.setIcon(leftImage);
    leftButton.setToolTipText(Options.getInstance().getResource("previous record"));
    rightButton.setIcon(rightImage);
    rightButton.setToolTipText(Options.getInstance().getResource("next record"));
    lastButton.setIcon(lastImage);
    lastButton.setToolTipText(Options.getInstance().getResource("last records"));
    buttonsPanel.add(findButton, null);
    buttonsPanel.add(insertButton, null);
    buttonsPanel.add(delButton, null);
    buttonsPanel.add(cancelEditButton, null);
    buttonsPanel.add(refreshButton, null);
    buttonsPanel.add(firstButton, null);
    buttonsPanel.add(leftButton, null);
    buttonsPanel.add(rightButton, null);
    buttonsPanel.add(lastButton, null);
  }


   public void resetPanel() {
     dataPanel.resetPanel();
   }

  void findButton_actionPerformed(ActionEvent e) {
    FilterDialog f = new FilterDialog(parent,tableName,dbConnUtil);
    f.setVisible(true);
    refreshButton_actionPerformed(null);
  }


  void firstButton_actionPerformed(ActionEvent e) {
    dataPanel.setQuery();
    if (dataPanel.getTableModel().getRowCount()>0) {
      dataPanel.getTable().setRowSelectionInterval(0,0);
      dataPanel.getTable().scrollRectToVisible(dataPanel.getTable().getCellRect(dataPanel.getTable().getSelectedRow(),0,true));
    }
    dataPanel.getTable().requestFocus();
  }

  void leftButton_actionPerformed(ActionEvent e) {
    if (dataPanel.getTable().getSelectedRow()!=-1 &&
        dataPanel.getTable().getSelectedRow()>0)
      dataPanel.getTable().setRowSelectionInterval(dataPanel.getTable().getSelectedRow()-1,dataPanel.getTable().getSelectedRow()-1);
    dataPanel.getTable().requestFocus();
  }

  void rightButton_actionPerformed(ActionEvent e) {
    if (dataPanel.getTable().getSelectedRow()!=-1 &&
        dataPanel.getTable().getSelectedRow()<dataPanel.getTableModel().getRowCount()-1)
      dataPanel.getTable().setRowSelectionInterval(dataPanel.getTable().getSelectedRow()+1,dataPanel.getTable().getSelectedRow()+1);
    dataPanel.getTable().requestFocus();
  }

  void lastButton_actionPerformed(ActionEvent e) {
    dataPanel.setLastQuery();

    if (dataPanel.getTableModel().getRowCount()>0) {
      dataPanel.getTable().setRowSelectionInterval(dataPanel.getTableModel().getRowCount() - 1, dataPanel.getTableModel().getRowCount() - 1);
      dataPanel.getTable().scrollRectToVisible(dataPanel.getTable().getCellRect(dataPanel.getTable().getSelectedRow(), 0, true));
    }
    dataPanel.getTable().requestFocus();
  }

  void cancelEditButton_actionPerformed(ActionEvent e) {
    try {
//      dbConnUtil.getConn().rollback();
//      refreshButton_actionPerformed(null);
      if ( ((CustomTableModel)dataPanel.getTableModel()).getEditMode()==CustomTableModel.INSERT_REC ) {
        ((CustomTableModel)dataPanel.getTableModel()).removeRow(((CustomTableModel)dataPanel.getTableModel()).getRowCount()-1);
        dataPanel.getTable().requestFocus();
        dataPanel.getTable().setRowSelectionInterval(dataPanel.getTable().getRowCount()-1,dataPanel.getTable().getRowCount()-1);
        ((CustomTableModel)dataPanel.getTableModel()).setEditMode(CustomTableModel.EDIT_REC);
      }
      else if ( ((CustomTableModel)dataPanel.getTableModel()).getEditMode()==CustomTableModel.EDIT_REC &&
                currentRow!=-1 && selectedRow!=null) {
        for(int i=0;i<dataPanel.getTableModel().getColumnCount();i++)
          dataPanel.getTableModel().setValueAt(selectedRow[i],currentRow,i);
        dataPanel.getTable().requestFocus();
        dataPanel.getTable().setRowSelectionInterval(currentRow,currentRow);
      }
      currentRow = -1;
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }




  /**
   * @return indica la posizione del pannello all'interno del JTabbedPane associato al dettaglio tabella
   */
  public int getTabbedPosition() {
    return 1;
  }


  /**
   * @return indica il nome del folder che ospita il pannello del plugin, all'interno del JTabbedPane associato al dettaglio tabella
   */
  public String getTabbedName() {
    return Options.getInstance().getResource("data");
  }


  /**
   * Metodo chiamato in automatico dal dettaglio tabella per iniziazzare il plugin
   */
  public void initPanel(MainFrame parent,DbConnectionUtil dbConnUtil) {
    this.parent = parent;
    this.dbConnUtil = dbConnUtil;
    this.dataPanel = new DataPanel(dbConnUtil,this);
    this.add(dataPanel,  BorderLayout.CENTER);
    init();
  }


  /**
   * Metodo chiamato dal dettaglio tabella per aggiornare il contenuto del pannello del plugin.
   * @param tableName nome tabella (eventualmente comprensivo di nome catalogo) che il pannello del plugin deve aggiornare
   */
  public void updateContent() {
    this.setQuery(tableName);
  }


  /**
   * @return informazioni relative all'autore del plugin; "" oppure null non riportera' tali informazioni nella finestra di about
   */
  public String getAuthor() {
    return "";
  }


  /**
   * @return indica la versione del plugin
   */
  public String getVersion() {
    return "1.0";
  }


  /**
   * @return nome del plugin, riportato nella finestra di about
   */
  public String getName() {
    return Options.getInstance().getResource("table data plugin");
  }


  public boolean equals(Object o) {
    return (o instanceof TablePlugin &&
            ((TablePlugin)o).getName().equals(getName()));
  }


}


class DataTablePanel_findButton_actionAdapter implements java.awt.event.ActionListener {
  DataTablePanel adaptee;

  DataTablePanel_findButton_actionAdapter(DataTablePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.findButton_actionPerformed(e);
  }
}

class DataTablePanel_firstButton_actionAdapter implements java.awt.event.ActionListener {
  DataTablePanel adaptee;

  DataTablePanel_firstButton_actionAdapter(DataTablePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.firstButton_actionPerformed(e);
  }
}

class DataTablePanel_leftButton_actionAdapter implements java.awt.event.ActionListener {
  DataTablePanel adaptee;

  DataTablePanel_leftButton_actionAdapter(DataTablePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.leftButton_actionPerformed(e);
  }
}

class DataTablePanel_rightButton_actionAdapter implements java.awt.event.ActionListener {
  DataTablePanel adaptee;

  DataTablePanel_rightButton_actionAdapter(DataTablePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.rightButton_actionPerformed(e);
  }
}

class DataTablePanel_lastButton_actionAdapter implements java.awt.event.ActionListener {
  DataTablePanel adaptee;

  DataTablePanel_lastButton_actionAdapter(DataTablePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.lastButton_actionPerformed(e);
  }
}

class DataTablePanel_insertButton_actionAdapter implements java.awt.event.ActionListener {
  DataTablePanel adaptee;

  DataTablePanel_insertButton_actionAdapter(DataTablePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.insertButton_actionPerformed(e);
  }
}

class DataTablePanel_delButton_actionAdapter implements java.awt.event.ActionListener {
  DataTablePanel adaptee;

  DataTablePanel_delButton_actionAdapter(DataTablePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.delButton_actionPerformed(e);
  }
}

class DataTablePanel_refreshButton_actionAdapter implements java.awt.event.ActionListener {
  DataTablePanel adaptee;

  DataTablePanel_refreshButton_actionAdapter(DataTablePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.refreshButton_actionPerformed(e);
  }
}

class DataTablePanel_cancelEditButton_actionAdapter implements java.awt.event.ActionListener {
  DataTablePanel adaptee;

  DataTablePanel_cancelEditButton_actionAdapter(DataTablePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cancelEditButton_actionPerformed(e);
  }
}