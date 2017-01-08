package org.jsqltool.gui.panel;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.util.Vector;
import org.jsqltool.conn.DbConnectionUtil;
import org.jsqltool.model.CustomTableModel;
import org.jsqltool.gui.graphics.DateCellEditor;
import org.jsqltool.gui.graphics.DateCellRenderer;
import org.jsqltool.utils.Options;
import org.jsqltool.utils.ImageLoader;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Panel which contains a query result (a block of records).
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
public final class DataPanel extends JPanel {

  BorderLayout borderLayout1 = new BorderLayout();
  JScrollPane scrollPane = new JScrollPane();
  private Table table = new Table();
  private String query = null;
  private Vector parameters = new Vector();
  private String originalQuery = null;

  /** number of records per block */
  private int BLOCK_SIZE = 100;

  /** current first row in block */
  private int inc = 0;
  private DbConnectionUtil dbConnUtil = null;
  private TableModelListener tableModelListener = null;
  private AdjustmentEvent adjEvent = null;
  private static final String NO_SORT_SYMBOL = "";
  private static final String ASC_SYMBOL = "ASC";
  private static final String DESC_SYMBOL = "DESC";

  /** table model column index, related to the current sorted column; -1 = no sorted column */
  private int sortColIndex = -1;

  /** sort versus: ascending or descending; "" no sort versus */
  private String sortColValue = "";


  public DataPanel(DbConnectionUtil dbConnUtil,TableModelListener tableModelListener) {
    this.dbConnUtil = dbConnUtil;
    this.tableModelListener = tableModelListener;
    try {
      jbInit();
      init();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  public DataPanel() {
    this(null,null);
  }


  private void init() {
    // listener used to set column sorting...
    MouseAdapter listMouseListener = new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        try {
          if (query!=null) {
            if ( (e.getClickCount() == 1) &&
                table.getModel().getRowCount() > 1) {
              int gridColIndex = table.getColumnModel().
                  getColumnIndexAtX(e.getX());
              int modelColIndex = table.getColumnModel().
                  getColumn(gridColIndex).getModelIndex();
              String colModelName = table.getModel().getColumnName(modelColIndex);
              String colGridName = table.getColumnModel().getColumn(gridColIndex).getIdentifier().toString();
              sortColIndex = modelColIndex;
              if (sortColValue.equals(NO_SORT_SYMBOL)) {
                sortColValue = ASC_SYMBOL;
              }
              else if (sortColValue.equals(ASC_SYMBOL)) {
                sortColValue = DESC_SYMBOL;
              }
              else if (sortColValue.equals(DESC_SYMBOL)) {
                sortColValue = NO_SORT_SYMBOL;
              }
              table.getColumnModel().getColumn(gridColIndex).setHeaderValue(colGridName);
              table.getColumnModel().getColumn(gridColIndex).setHeaderRenderer(table.getTableHeader().getDefaultRenderer());
              if (sortColValue.equals(NO_SORT_SYMBOL)) {
                if (originalQuery!=null) {
                  query = originalQuery;
                  sortColValue = "";
                }
                setQuery();
              }
              else {
                if (originalQuery==null)
                  originalQuery = query;
                int indexOrderBy = originalQuery.toUpperCase().lastIndexOf("ORDER BY");
                int indexGroupBy = originalQuery.toUpperCase().lastIndexOf("GROUP BY");
                if (indexOrderBy!=-1 && indexGroupBy==-1) {
                  query = originalQuery + "," +colModelName+" "+sortColValue;
                } else if (indexOrderBy!=-1 && indexOrderBy<indexGroupBy) {
                  query = originalQuery.substring(0,indexGroupBy)+","+colModelName+" "+sortColValue+" "+originalQuery.substring(indexGroupBy);
                } else if (indexOrderBy!=-1 && indexOrderBy>indexGroupBy) {
                  query = originalQuery + "," +colModelName+" "+sortColValue;
                } else
                  query = originalQuery + " ORDER BY " +colModelName+" "+sortColValue;
                setQuery();

              }
            }
          }
        }
        catch (Exception ex) {
        }
      }
    };
    JTableHeader th = table.getTableHeader();
    th.addMouseListener(listMouseListener);

    // key listener used to scroll table...
    table.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode()==e.VK_DOWN) {
          if (table.getSelectedRow()==table.getRowCount()-1 &&
              table.getRowCount()==BLOCK_SIZE) {
            // fetch next block...
            inc += BLOCK_SIZE;
            readBlock();
            if (table.getRowCount()>0)
              table.setRowSelectionInterval(0,0);
            table.scrollRectToVisible(table.getCellRect(table.getSelectedRow(),0,true));
          }
        }
        else if (e.getKeyCode()==e.VK_UP) {
          if (table.getSelectedRow()==0 && inc>0) {
            // fetch previous block...
            inc -= BLOCK_SIZE;
            if (inc<0)
              inc = 0;
            readBlock();
            table.setRowSelectionInterval(table.getRowCount()-1,table.getRowCount()-1);
            table.scrollRectToVisible(table.getCellRect(table.getSelectedRow(),0,true));
          }
        }
        else if (e.getKeyCode()==e.VK_PAGE_DOWN) {
          if (table.getSelectedRow()==table.getRowCount()-1 &&
              table.getRowCount()==BLOCK_SIZE) {
            // fetch next block...
            inc += BLOCK_SIZE;
            readBlock();
            if (table.getRowCount()>0)
              table.setRowSelectionInterval(0,0);
            table.scrollRectToVisible(table.getCellRect(table.getSelectedRow(),0,true));
          }
        }
        else if (e.getKeyCode()==e.VK_PAGE_UP) {
          if (table.getSelectedRow()==0 && inc>0) {
            // fetch previous block...
            inc -= BLOCK_SIZE;
            if (inc<0)
              inc = 0;
            readBlock();
            table.setRowSelectionInterval(table.getRowCount()-1,table.getRowCount()-1);
            table.scrollRectToVisible(table.getCellRect(table.getSelectedRow(),0,true));
          }
        }
      }
    });

  }


  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    this.add(scrollPane,  BorderLayout.CENTER);
    scrollPane.getViewport().add(table, null);
//    scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
//      public void adjustmentValueChanged(AdjustmentEvent e) {
//        adjEvent = e;
//      }
//    });
//    scrollPane.getVerticalScrollBar().addMouseListener(new MouseAdapter() {
//      public void mouseReleased(MouseEvent e){
//        if (javax.swing.SwingUtilities.isLeftMouseButton(e)){
//          scrollMov((JScrollBar)adjEvent.getAdjustable());
//        }
//      }
//    }); // mouse lister applied to the scrollbar...
//    (scrollPane.getVerticalScrollBar().getComponents())[0].addMouseListener(new MouseAdapter() {
//      public void mouseReleased(MouseEvent e){
//        if (javax.swing.SwingUtilities.isLeftMouseButton(e)){
//          scrollMov((JScrollBar)adjEvent.getAdjustable());
//        }
//      }
//    }); // mouse lister applied to the scrollbar up button...
//    (scrollPane.getVerticalScrollBar().getComponents())[1].addMouseListener(new MouseAdapter() {
//      public void mouseReleased(MouseEvent e){
//        if (javax.swing.SwingUtilities.isLeftMouseButton(e)){
//          scrollMov((JScrollBar)adjEvent.getAdjustable());
//        }
//      }
//    }); // mouse lister applied to the scrollbar down button...
  }


  public void setQuery() {
    this.inc = 0;
    readBlock();
  }


  public void setLastQuery() {
    this.inc = dbConnUtil.getLastQueryIndex(query,parameters,BLOCK_SIZE);
    readBlock();
  }


  private void readBlock() {
    if (query==null)
      return;
    updateSortIcons();
    table.getModel().removeTableModelListener(tableModelListener);
    table.setModel(dbConnUtil.getQuery(query,parameters,inc,BLOCK_SIZE));
    try {
      for(int i=0;i<table.getColumnCount();i++) {
        if (((CustomTableModel)table.getModel()).getColumnClass( table.convertColumnIndexToModel(i) ).equals(java.util.Date.class) ||
            ((CustomTableModel)table.getModel()).getColumnClass( table.convertColumnIndexToModel(i) ).equals(java.sql.Date.class) ||
            ((CustomTableModel)table.getModel()).getColumnClass( table.convertColumnIndexToModel(i) ).equals(java.sql.Timestamp.class)) {
          table.getColumnModel().getColumn(i).setCellEditor(new DateCellEditor());
          table.getColumnModel().getColumn(i).setCellRenderer(new DateCellRenderer());
          table.getColumnModel().getColumn(i).setPreferredWidth(6*Options.getInstance().getDateFormat().length());
        }
        else
          table.getColumnModel().getColumn(i).setPreferredWidth(
          ((CustomTableModel)table.getModel()).getColSizes()[table.convertColumnIndexToModel(i)]);
      }
      if (table.getRowCount()>0)
        table.setRowSelectionInterval(0,0);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    table.getModel().addTableModelListener(tableModelListener);
  }


  public void setQuery(String query,Vector parameters) {
    this.query = query;
    this.parameters = parameters;
    this.sortColIndex = -1;
    this.originalQuery = null;
    setQuery();
  }


  public void resetPanel() {
    this.inc = 0;
    table.getModel().removeTableModelListener(tableModelListener);
    table.setModel(new DefaultTableModel());
    table.getModel().addTableModelListener(tableModelListener);
  }


  /**
   * Method used to read the next/previous block of records.
   * It's called by mouseReleased method, when the user clicks on the scrollbar.
   */
  private void scrollMov(JScrollBar scrollBar){
    int scrollableZoneSize = scrollBar.getMaximum()-scrollBar.getVisibleAmount();
    if ((scrollBar.getValue()>scrollableZoneSize/4*3) && (true)) {
      // scrolling table over 75% of rows (near the end)
      table.getModel().removeTableModelListener(tableModelListener);
      inc += BLOCK_SIZE;
      table.setModel(dbConnUtil.getQuery(query,new Vector(),inc,BLOCK_SIZE));
      table.getModel().addTableModelListener(tableModelListener);
      scrollBar.setValue(250);
    } else if ((scrollBar.getValue()< scrollableZoneSize/4) && (inc>0)) {
      // scrolling table on 25% of rows (near the beginning)
      table.getModel().removeTableModelListener(tableModelListener);
      inc -= BLOCK_SIZE;
      if (inc<0)
        inc = 0;
      table.setModel(dbConnUtil.getQuery(query,new Vector(),inc,BLOCK_SIZE));
      table.getModel().addTableModelListener(tableModelListener);
      scrollBar.setValue(250);
    }
  }


  public TableModel getTableModel() {
    return table.getModel();
  }

  public Table getTable() {
    return table;
  }




 /**
  * Method used to draw sorting versus onto the table columns.
  */
  private void updateSortIcons(){
    try{
      Object o = new Object();
      String[] colOrder = new String[2];
      TableCellRenderer hr = null;
      Icon3 ic = null;
      // reset all columns...
      for(int i=0;i<getTable().getColumnCount();i++) {
        if((o = getTable().getColumnModel().getColumn(i)) == null)
          continue;
        if((hr = ((TableColumn)o).getHeaderRenderer()) == null)
          continue;
        if (hr instanceof JLabel)
          if (((JLabel)hr).getIcon()!=null)
            ((Icon3)((JLabel)hr).getIcon()).setOrder(NO_SORT_SYMBOL);
      }
      // re-set icons...
      if (sortColIndex!=-1) {
        String order = NO_SORT_SYMBOL;
        if (sortColValue.equals(ASC_SYMBOL))
          order = ASC_SYMBOL;
        else if (sortColValue.equals(DESC_SYMBOL))
          order = DESC_SYMBOL;

        TableColumn col = null;
        try {
          col = getTable().getColumn(getTableModel().getColumnName(sortColIndex));
        }
        catch (IllegalArgumentException ex) {
        }
        if((o = col) == null)
          return;

        if((hr = ((TableColumn)o).getHeaderRenderer()) == null)
          return;

//          if (hr instanceof JLabel) {
        if (hr instanceof DefaultTableCellRenderer || hr instanceof JLabel) {

          Component comp = col.getHeaderRenderer().
                           getTableCellRendererComponent(
                               null, col.getHeaderValue(),
                               false, false, 0, 0);

          ic = new Icon3(order);
          ((DefaultTableCellRenderer)hr).setIcon(ic);
          ((DefaultTableCellRenderer)hr).setHorizontalTextPosition(JLabel.LEFT);
        }
        getTable().getTableHeader().revalidate();
        getTable().getTableHeader().repaint();
      }
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }




  /**
   * <p>Description: Inner class used to draw the sorting icon.</p>
   */
  class Icon3 implements Icon {
    String m_order;
    public Icon3(String order)
        { m_order = order;}
    public int getIconWidth()
        { return 0; }
    public int getIconHeight()
        { return getIconWidth(); }
    public void setOrder(String order)
        { m_order = order; }
    public void paintIcon(Component c, Graphics g, int x, int y) {
      if (sortColIndex!=-1 && ((DefaultTableCellRenderer)c).getText().equals(table.getColumnName(sortColIndex)))
        drawSort(c, g, x, y, m_order);
    }
  }


  /**
   * Draw image of sort triangle.
   * @param c the 1st parameter of the "Icon.paintIcon(...)". It should
   * be an instance of the header renderer (STDLabel).
   * @param g the 2nd parameter of the "Icon.paintIcon(...)".
   * @param x the 3rd parameter of the "Icon.paintIcon(...)".
   * @param y the 4th parameter of the "Icon.paintIcon(...)".
   * @param iSize priority:
   * <UL>
   * 0 - primary sort (normal triangle)<br>
   *  </UL>
   * @param asc True: ascending sort, false: descending sort.
   */
  private void drawSort(Component c, Graphics g, int x, int y, String order){
    if (order.equals(NO_SORT_SYMBOL))
      return;
    int iSize = 7;
    Border b = ((JComponent)c).getBorder();
    int y1 = iSize + 3 + ((b == null) ? 1 : b.getBorderInsets(c).left);
    x = c.getSize().width - y1;
    y = c.getSize().height - ++y1;
    //------------
    y1 = order.equals(DESC_SYMBOL) ? y : y + iSize;
    y = order.equals(DESC_SYMBOL) ? y + iSize : y;
    //------------
    Color clr1 = getBackground().brighter();
    Color clr2 = getBackground().darker().darker();
    g.setColor(clr2);
    //------------
    // left line
    g.drawLine(x, y, x + iSize / 2, y1);
    //------------
    // horizontal line
    g.setColor(order.equals(DESC_SYMBOL) ? clr1 : clr2);
    x++;
    g.drawLine(x, y, x + iSize, y);
    //------------
    // right line
    g.setColor(clr1);
    g.drawLine(x + iSize / 2, y1, x + iSize, y);
  }



}