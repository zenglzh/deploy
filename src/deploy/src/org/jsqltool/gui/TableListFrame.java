package org.jsqltool.gui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import org.jsqltool.conn.DbConnectionUtil;
import java.awt.event.*;
import org.jsqltool.gui.panel.*;
import org.jsqltool.gui.tablepanel.*;
import org.jsqltool.utils.Options;
import org.jsqltool.utils.ImageLoader;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Window used to view the list of tables, views and synonyms.
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
public class TableListFrame extends JInternalFrame implements DbConnWindow {

  JPanel mainPanel = new JPanel();
  JSplitPane splitPane = new JSplitPane();
  TableDetailPanel detailPane = null;
  BorderLayout borderLayout1 = new BorderLayout();
  JTabbedPane tableTabbedPane = new JTabbedPane();
  JScrollPane tableScrollPane = new JScrollPane();
  JScrollPane viewScrollPane = new JScrollPane();
  JScrollPane sinScrollPane = new JScrollPane();
  JList tablesList = new JList();
  JList viewList = new JList();
  JList sinList = new JList();
  private DbConnectionUtil dbConnUtil = null;
  JPanel tablesPanel = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel catPanel = new JPanel();
  JLabel catLabel = new JLabel();
  JComboBox catComboBox = new JComboBox();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JPanel tablePanel = new JPanel();
  JPanel viewPanel = new JPanel();
  JPanel synPanel = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  BorderLayout borderLayout4 = new BorderLayout();
  BorderLayout borderLayout5 = new BorderLayout();

  private FilterListPanel tableFilterListPanel = new FilterListPanel(tablesList,new TableFilterController());
  private FilterListPanel viewFilterListPanel = new FilterListPanel(viewList,new ViewFilterController());
  private FilterListPanel synFilterListPanel = new FilterListPanel(sinList,new SynFilterController());

  /** current selected schema name (if combo is empty, then schemaName is set to "" */
  private String schemaName = "";



  public TableListFrame(MainFrame parent,DbConnectionUtil dbConnUtil) {
    super(Options.getInstance().getResource("schema browser")+" - "+dbConnUtil.getDbConnection().getName(),true,true,true,true);
    this.dbConnUtil = dbConnUtil;
    this.detailPane = new TableDetailPanel(parent,dbConnUtil,this);
    try {
      jbInit();
      new Thread() {
        public void run() {
          ProgressDialog.getInstance().startProgress();
          try {
            TableListFrame.this.init();
          }
          catch (Throwable ex) {
          }
          finally {
            ProgressDialog.getInstance().stopProgress();
          }
        }
      }.start();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  public TableListFrame() {
    this(null,null);
  }


  private void init() {
    // catalogs list...
    java.util.List cats = dbConnUtil.getSchemas();
    for (int i = 0; i < cats.size(); i++)
      if (cats.get(i)!=null)
        catComboBox.addItem(cats.get(i));
    if (catComboBox.getItemCount()==0)
      catComboBox.addItem("");

    // listener to repaint lists on selecting a catalog...
    catComboBox.setSelectedItem(dbConnUtil.getDbConnection().getUsername().toUpperCase());
    updateLists();
    catComboBox.addItemListener(new TableListFrame_catComboBox_itemAdapter(this));

    tablesList.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && tablesList.getSelectedIndex()!=-1)
          detailPane.updateContent(schemaName+tablesList.getSelectedValue().toString());
//          new Thread() {
//            public void run() {
//              ProgressDialog.getInstance().startProgress();
//              try {
//                detailPane.updateContent(schemaName+tablesList.getSelectedValue().toString());
//              }
//              catch (Throwable ex) {
//              }
//              finally {
//                ProgressDialog.getInstance().stopProgress();
//              }
//            }
//          }.start();

      }
    });
    sinList.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && sinList.getSelectedIndex()!=-1)
          detailPane.updateContent(schemaName+sinList.getSelectedValue().toString());
//          new Thread() {
//            public void run() {
//              ProgressDialog.getInstance().startProgress();
//              try {
//                detailPane.updateContent(schemaName+sinList.getSelectedValue().toString());
//              }
//              catch (Throwable ex) {
//              }
//              finally {
//                ProgressDialog.getInstance().stopProgress();
//              }
//            }
//          }.start();

      }
    });
    viewList.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && viewList.getSelectedIndex()!=-1)

          detailPane.updateContent(schemaName+viewList.getSelectedValue().toString());

//          new Thread() {
//            public void run() {
//              ProgressDialog.getInstance().startProgress();
//              try {
//                detailPane.updateContent(schemaName+viewList.getSelectedValue().toString());
//              }
//              catch (Throwable ex) {
//              }
//              finally {
//                ProgressDialog.getInstance().stopProgress();
//              }
//            }
//          }.start();

      }
    });

  }

  public void updateLists() {
    if (tableTabbedPane.getSelectedIndex()==0) {
      new Thread() {
        public void run() {
          loadTables();
          loadViews();
          loadSyns();
//          if (tablesList.getModel().getSize()>0)
//            tablesList.setSelectedIndex(0);
          tablesList.requestFocus();
        }
      }.start();
    }
    else if (tableTabbedPane.getSelectedIndex()==1) {
      new Thread() {
        public void run() {
          loadViews();
          loadTables();
          loadSyns();
//          if (viewList.getModel().getSize()>0)
//            viewList.setSelectedIndex(0);
          viewList.requestFocus();
        }
      }.start();
    }
    else if (tableTabbedPane.getSelectedIndex()==2) {
      new Thread() {
        public void run() {
          loadSyns();
          loadTables();
          loadViews();
//          if (sinList.getModel().getSize()>0)
//            sinList.setSelectedIndex(0);
          sinList.requestFocus();
        }
      }.start();
    }

  }


  /**
   * Load tables.
   */
  private void loadTables() {
    java.util.List tables = dbConnUtil.getTables(catComboBox.getSelectedItem().toString(),"TABLE");
    DefaultListModel model = new DefaultListModel();
    String name = null;
    boolean ok = true;
    for(int i=0;i<tables.size();i++) {
      name = tables.get(i).toString();
      ok = true;
      for(int j=0;j<tableFilterListPanel.getFilterPattern().length();j++)
        if (name.indexOf(tableFilterListPanel.getFilterPattern().charAt(j))!=-1) {
          ok = false;
          break;
        }
      if (ok)
        model.addElement(name);
    }
    tablesList.setModel(model);
    tablesList.revalidate();
//    if (tablesList.getModel().getSize()>0)
//      tablesList.setSelectedIndex(0);
    tablesList.requestFocus();
  }


  /**
   * Load views.
   */
  private void loadViews() {
    java.util.List views = dbConnUtil.getTables(catComboBox.getSelectedItem().toString(),"VIEW");
    DefaultListModel model = new DefaultListModel();
    String name = null;
    boolean ok = true;
    for(int i=0;i<views.size();i++) {
      name = views.get(i).toString();
      ok = true;
      for(int j=0;j<viewFilterListPanel.getFilterPattern().length();j++)
        if (name.indexOf(viewFilterListPanel.getFilterPattern().charAt(j))!=-1) {
          ok = false;
          break;
        }
      if (ok)
        model.addElement(name);
    }
    viewList.setModel(model);
    viewList.revalidate();
//    if (viewList.getModel().getSize()>0)
//      viewList.setSelectedIndex(0);
    viewList.requestFocus();
  }


  /**
   * Load synonyms.
   */
  private void loadSyns() {
    java.util.List sin = dbConnUtil.getTables(catComboBox.getSelectedItem().toString(),"SYNONYM");
    DefaultListModel model = new DefaultListModel();
    String name = null;
    boolean ok = true;
    for(int i=0;i<sin.size();i++) {
      name = sin.get(i).toString();
      ok = true;
      for(int j=0;j<synFilterListPanel.getFilterPattern().length();j++)
        if (name.indexOf(synFilterListPanel.getFilterPattern().charAt(j))!=-1) {
          ok = false;
          break;
        }
      if (ok)
        model.addElement(name);
    }
    sinList.setModel(model);
    sinList.revalidate();
//    if (sinList.getModel().getSize()>0)
//      sinList.setSelectedIndex(0);
    sinList.requestFocus();
  }


  private void jbInit() throws Exception {
    mainPanel.setLayout(borderLayout1);
    splitPane.setDebugGraphicsOptions(0);
    tableScrollPane.setToolTipText(Options.getInstance().getResource("tables list"));
    viewScrollPane.setRowHeader(null);
    viewScrollPane.setToolTipText(Options.getInstance().getResource("views list"));
    sinScrollPane.setToolTipText(Options.getInstance().getResource("synonyms list"));
    tablesPanel.setLayout(borderLayout2);
    catLabel.setText(Options.getInstance().getResource("catalog"));
    catPanel.setLayout(gridBagLayout1);

    tablesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    viewList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    sinList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

/*
    tablesList.addKeyListener(new TableListFrame_tablesList_keyAdapter(this));
    tablesList.addMouseListener(new TableListFrame_tablesList_mouseAdapter(this));
    viewList.addMouseListener(new TableListFrame_viewList_mouseAdapter(this));
    sinList.addMouseListener(new TableListFrame_sinList_mouseAdapter(this));
    viewList.addKeyListener(new TableListFrame_viewList_keyAdapter(this));
    sinList.addKeyListener(new TableListFrame_sinList_keyAdapter(this));
*/
    tableTabbedPane.addChangeListener(new TableListFrame_tableTabbedPane_changeAdapter(this));
    this.getContentPane().add(mainPanel, BorderLayout.CENTER);
    mainPanel.add(splitPane,  BorderLayout.CENTER);
    splitPane.add(detailPane, JSplitPane.RIGHT);
    splitPane.add(tablesPanel, JSplitPane.LEFT);
    tablePanel.setLayout(borderLayout3);
    viewPanel.setLayout(borderLayout4);
    synPanel.setLayout(borderLayout5);
    tablePanel.add(tableFilterListPanel,BorderLayout.NORTH);
    tablePanel.add(tableScrollPane,BorderLayout.CENTER);
    viewPanel.add(viewFilterListPanel,BorderLayout.NORTH);
    viewPanel.add(viewScrollPane,BorderLayout.CENTER);
    synPanel.add(synFilterListPanel,BorderLayout.NORTH);
    synPanel.add(sinScrollPane,BorderLayout.CENTER);

    tableTabbedPane.add(tablePanel, Options.getInstance().getResource( "tables"));
    tableTabbedPane.add(viewPanel, Options.getInstance().getResource( "views"));
    tableTabbedPane.add(synPanel,  Options.getInstance().getResource( "synonyms"));
    tablesPanel.add(catPanel, BorderLayout.NORTH);
    tableScrollPane.getViewport().add(tablesList, null);
    viewScrollPane.getViewport().add(viewList, null);
    sinScrollPane.getViewport().add(sinList, null);
    splitPane.setDividerLocation(200);
    tablesPanel.add(tableTabbedPane, BorderLayout.CENTER);
    catPanel.add(catLabel,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    catPanel.add(catComboBox,   new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
  }


  public DbConnectionUtil getDbConnectionUtil() {
    return dbConnUtil;
  }

  void catComboBox_itemStateChanged(ItemEvent e) {
    if (e.getStateChange()==e.SELECTED) {
      if (catComboBox.getSelectedItem()!=null && !catComboBox.getSelectedItem().equals(""))
        schemaName = catComboBox.getSelectedItem()+".";
      else
        schemaName = "";

      new Thread() {
        public void run() {
          ProgressDialog.getInstance().startProgress();
          try {
            // update lists...
            updateLists();
          }
          catch (Throwable ex) {
          }
          finally {
            ProgressDialog.getInstance().stopProgress();
          }
        }
      }.start();
    }
  }

  void tableTabbedPane_stateChanged(ChangeEvent e) {
    if (tableTabbedPane.getSelectedIndex()==0 && tablesList.getSelectedIndex()!=-1)
      detailPane.updateContent(schemaName+tablesList.getSelectedValue().toString());
    else if (tableTabbedPane.getSelectedIndex()==1 && viewList.getSelectedIndex()!=-1)
      detailPane.updateContent(schemaName+viewList.getSelectedValue().toString());
    else if (tableTabbedPane.getSelectedIndex()==2 && sinList.getSelectedIndex()!=-1)
      detailPane.updateContent(schemaName+sinList.getSelectedValue().toString());
  }


/*
  void tablesList_mouseClicked(MouseEvent e) {
    if (tablesList.getSelectedIndex()!=-1)
      detailPane.updateContent(tablesList.getSelectedValue().toString());
  }

  void viewList_mouseClicked(MouseEvent e) {
    if (viewList.getSelectedIndex()!=-1)
      detailPane.updateContent(viewList.getSelectedValue().toString());
  }

  void sinList_mouseClicked(MouseEvent e) {
    if (sinList.getSelectedIndex()!=-1)
      detailPane.updateContent(sinList.getSelectedValue().toString());
  }

  void tablesList_keyTyped(KeyEvent e) {
    if (e.getKeyChar()=='\n' && tablesList.getSelectedIndex()!=-1)
      detailPane.updateContent(tablesList.getSelectedValue().toString());

  }

  void viewList_keyTyped(KeyEvent e) {
    if (e.getKeyChar()=='\n' && viewList.getSelectedIndex()!=-1)
      detailPane.updateContent(viewList.getSelectedValue().toString());

  }

  void sinList_keyTyped(KeyEvent e) {
    if (e.getKeyChar()=='\n' && sinList.getSelectedIndex()!=-1)
      detailPane.updateContent(sinList.getSelectedValue().toString());
  }
*/


/**
 * <p>Description: Inner class which manages filter events on tables.</p>
 */
  class TableFilterController implements FilterListController {

    /**
     * Reload the list, which will be filtered by the specified pattern
     */
    public void reloadList() {
      loadTables();
    }

  }


/**
 * <p>Description: Inner class which manages filter events on views.</p>
 */
  class ViewFilterController implements FilterListController {

    /**
     * Reload the list, which will be filtered by the specified pattern
     */
    public void reloadList() {
      loadViews();
    }

  }


/**
 * <p>Description: Inner class which manages filter events on synonyms.</p>
 */
  class SynFilterController implements FilterListController {

    /**
     * Reload the list, which will be filtered by the specified pattern
     */
    public void reloadList() {
      loadSyns();
    }


  }




}

class TableListFrame_catComboBox_itemAdapter implements java.awt.event.ItemListener {
  TableListFrame adaptee;

  TableListFrame_catComboBox_itemAdapter(TableListFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void itemStateChanged(ItemEvent e) {
    adaptee.catComboBox_itemStateChanged(e);
  }
}
/*
class TableListFrame_tablesList_mouseAdapter extends java.awt.event.MouseAdapter {
  TableListFrame adaptee;

  TableListFrame_tablesList_mouseAdapter(TableListFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseClicked(MouseEvent e) {
    adaptee.tablesList_mouseClicked(e);
  }
}

class TableListFrame_viewList_mouseAdapter extends java.awt.event.MouseAdapter {
  TableListFrame adaptee;

  TableListFrame_viewList_mouseAdapter(TableListFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseClicked(MouseEvent e) {
    adaptee.viewList_mouseClicked(e);
  }
}

class TableListFrame_sinList_mouseAdapter extends java.awt.event.MouseAdapter {
  TableListFrame adaptee;

  TableListFrame_sinList_mouseAdapter(TableListFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseClicked(MouseEvent e) {
    adaptee.sinList_mouseClicked(e);
  }
}

class TableListFrame_tablesList_keyAdapter extends java.awt.event.KeyAdapter {
  TableListFrame adaptee;

  TableListFrame_tablesList_keyAdapter(TableListFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void keyTyped(KeyEvent e) {
    adaptee.tablesList_keyTyped(e);
  }
}

class TableListFrame_viewList_keyAdapter extends java.awt.event.KeyAdapter {
  TableListFrame adaptee;

  TableListFrame_viewList_keyAdapter(TableListFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void keyTyped(KeyEvent e) {
    adaptee.viewList_keyTyped(e);
  }
}

class TableListFrame_sinList_keyAdapter extends java.awt.event.KeyAdapter {
  TableListFrame adaptee;

  TableListFrame_sinList_keyAdapter(TableListFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void keyTyped(KeyEvent e) {
    adaptee.sinList_keyTyped(e);
  }
}


*/

class TableListFrame_tableTabbedPane_changeAdapter implements javax.swing.event.ChangeListener {
  TableListFrame adaptee;

  TableListFrame_tableTabbedPane_changeAdapter(TableListFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void stateChanged(ChangeEvent e) {
    adaptee.tableTabbedPane_stateChanged(e);
  }
}