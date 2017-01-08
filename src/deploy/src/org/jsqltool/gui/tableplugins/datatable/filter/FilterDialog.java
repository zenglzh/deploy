package org.jsqltool.gui.tableplugins.datatable.filter;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.*;
import java.util.*;
import java.math.BigDecimal;
import javax.swing.event.*;
import org.jsqltool.conn.DbConnectionUtil;
import org.jsqltool.*;
import org.jsqltool.utils.ImageLoader;
import org.jsqltool.utils.Options;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Dialog used to filter/order table content.
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
public class FilterDialog extends JDialog implements ListSelectionListener {
  JPanel mainPanel = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel buttonsPanel = new JPanel();
  JButton cancelButton = new JButton();
  JButton okButton = new JButton();
  JTabbedPane tabbedPane = new JTabbedPane();
  JPanel orderPanel = new JPanel();
  JPanel filterPanel = new JPanel();
  JScrollPane orderScrollPane = new JScrollPane();
  JList orderList = new JList();
  JComboBox opComboBox = new JComboBox();
  JComboBox colComboBox = new JComboBox();
  JTextField valueTextField = new JTextField();
  JButton andButton = new JButton();
  JButton orButton = new JButton();
  JScrollPane whereScrollPane = new JScrollPane();
  JTextArea whereTextArea = new JTextArea();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  private FilterModel fm = null;
  private String tableName = null;
  JScrollPane orderScrollPane2 = new JScrollPane();
  JList orderList2 = new JList();
  JButton ascrightButton = new JButton();
  JButton leftButton = new JButton();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  private HashSet selectedColumns = new HashSet();
  JButton descRightButton = new JButton();
  JButton upButton = new JButton();
  JButton downButton = new JButton();
  ImageIcon upImage;
  ImageIcon downImage;
  ImageIcon leftImage;
  ImageIcon ascRightImage;
  ImageIcon descRightImage;
  JButton clearSortButton = new JButton();
  JButton clearFilterButton = new JButton();
  FlowLayout flowLayout1 = new FlowLayout();
  private DbConnectionUtil dbConnUtil = null;


  public FilterDialog(JFrame parent,String tableName,DbConnectionUtil dbConnUtil) {
    super(parent, Options.getInstance().getResource("table sort/filter"), true);
    this.tableName = tableName;
    this.dbConnUtil = dbConnUtil;
    try {
      jbInit();
//      pack();
      Dimension frameSize = new Dimension(500,400);
      setSize(frameSize);
      Dimension screenSize = parent.getSize();
      if (frameSize.height > screenSize.height) {
        frameSize.height = screenSize.height;
      }
      if (frameSize.width > screenSize.width) {
        frameSize.width = screenSize.width;
      }
      setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
      initFolders(dbConnUtil);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }


  public FilterDialog() {
    this(null,null,null);
  }


  private void initFolders(DbConnectionUtil dbConnUtil) {
    fm = (FilterModel)dbConnUtil.getDbConnection().getFilters().get(tableName);
    if (fm==null) {
      fm = new FilterModel();
      dbConnUtil.getDbConnection().getFilters().put(tableName,fm);
    }
    TableModel tm = dbConnUtil.getTableColumns(tableName);
    DefaultComboBoxModel listModel = new DefaultComboBoxModel();
    for(int i=0;i<tm.getRowCount();i++) {
      colComboBox.addItem(tm.getValueAt(i,0));
      listModel.addElement(tm.getValueAt(i,0));
    }
    opComboBox.addItem("=");
    opComboBox.addItem("LIKE");
    opComboBox.addItem("<");
    opComboBox.addItem("<=");
    opComboBox.addItem(">");
    opComboBox.addItem(">=");
    opComboBox.addItem("IS");
    orderList.setModel(listModel);
    orderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    String orderClause = fm.getOrderClause();
    if (orderClause.length()>0)
      orderClause = orderClause.substring(10);
    else
      orderClause = "";
    DefaultComboBoxModel listModel2 = new DefaultComboBoxModel();
    StringTokenizer st = new StringTokenizer(orderClause,",");
    String colName = null;
    while(st.hasMoreTokens()) {
      colName = st.nextToken();
      listModel2.addElement(colName);
      selectedColumns.add(colName);
    }
    orderList2.setModel(listModel2);
    orderList2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    String whereClause = fm.getWhereClause();
    if (whereClause.length()>0)
      whereClause = whereClause.substring(whereClause.indexOf("WHERE")+6);
    else
      whereClause = "";
    whereTextArea.setText(whereClause);
    orderList.addListSelectionListener(this);
    orderList2.addListSelectionListener(this);
  }


  void jbInit() throws Exception {
    mainPanel.setLayout(borderLayout1);
    cancelButton.setMnemonic(Options.getInstance().getResource("cancelbutton.mnemonic").charAt(0));
    cancelButton.setText(Options.getInstance().getResource("cancelbutton.text"));
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    okButton.setMnemonic(Options.getInstance().getResource("okbutton.mnemonic").charAt(0));
    okButton.setText(Options.getInstance().getResource("okbutton.text"));
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    orderPanel.setLayout(gridBagLayout2);
    valueTextField.setColumns(10);
    andButton.setText("And");
    andButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        andButton_actionPerformed(e);
      }
    });
    orButton.setMnemonic('0');
    orButton.setText("Or");
    orButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        orButton_actionPerformed(e);
      }
    });
    whereTextArea.setColumns(60);
    whereTextArea.setRows(20);
    filterPanel.setLayout(gridBagLayout1);
    ascrightButton.setText("ASC");
    ascrightButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ascRightButton_actionPerformed(e);
      }
    });
    leftButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        leftButton_actionPerformed(e);
      }
    });
    descRightButton.setText("DESC");
    descRightButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        descRightButton_actionPerformed(e);
      }
    });
    leftButton.setText("");
    downButton.setText("");
    downButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        downButton_actionPerformed(e);
      }
    });
    upButton.setText("");
    upButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        upButton_actionPerformed(e);
      }
    });
    clearSortButton.setMnemonic('S');
    clearSortButton.setText(Options.getInstance().getResource("clear sort"));
    clearSortButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        clearSortButton_actionPerformed(e);
      }
    });
    clearFilterButton.setMnemonic('F');
    clearFilterButton.setText(Options.getInstance().getResource("clear filter"));
    clearFilterButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        clearFilterButton_actionPerformed(e);
      }
    });
    buttonsPanel.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.RIGHT);
    getContentPane().add(mainPanel);
    mainPanel.add(buttonsPanel,  BorderLayout.NORTH);
    buttonsPanel.add(clearSortButton, null);
    buttonsPanel.add(clearFilterButton, null);
    buttonsPanel.add(cancelButton, null);
    buttonsPanel.add(okButton, null);
    mainPanel.add(tabbedPane,  BorderLayout.CENTER);
    tabbedPane.add(orderPanel,    Options.getInstance().getResource("orderPanel"));
    tabbedPane.add(filterPanel,    Options.getInstance().getResource("filterPanel"));
    orderPanel.add(orderScrollPane,                    new GridBagConstraints(0, 0, 1, 5, 1.0, 1.0
            ,GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(5, 5, 5, 5), 100, 0));
    orderPanel.add(orderScrollPane2,                  new GridBagConstraints(2, 0, 1, 5, 1.0, 1.0
            ,GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(5, 5, 5, 5), 100, 0));
    filterPanel.add(colComboBox,          new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    filterPanel.add(opComboBox,     new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    filterPanel.add(valueTextField,    new GridBagConstraints(2, 0, 2, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    filterPanel.add(andButton,   new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    filterPanel.add(orButton,   new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    filterPanel.add(whereScrollPane,   new GridBagConstraints(0, 2, 4, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    whereScrollPane.getViewport().add(whereTextArea, null);
    orderScrollPane2.getViewport().add(orderList2, null);
    orderPanel.add(leftButton,                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    orderScrollPane.getViewport().add(orderList, null);
    orderPanel.add(downButton,          new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    orderPanel.add(upButton,       new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    orderPanel.add(descRightButton,     new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    orderPanel.add(ascrightButton,   new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    upButton.setPreferredSize(new Dimension(24, 24));
    downButton.setPreferredSize(new Dimension(24, 24));
    leftButton.setPreferredSize(new Dimension(24, 24));
    ascrightButton.setPreferredSize(new Dimension(24, 24));
    descRightButton.setPreferredSize(new Dimension(24, 24));
    upImage = ImageLoader.getInstance().getIcon("moveup.gif");
    downImage = ImageLoader.getInstance().getIcon("movedown.gif");
    leftImage = ImageLoader.getInstance().getIcon("movesx.gif");
    ascRightImage = ImageLoader.getInstance().getIcon("movedx.gif");
    descRightImage = ImageLoader.getInstance().getIcon("movedx.gif");
    leftButton.setIcon(leftImage);
    leftButton.setToolTipText(Options.getInstance().getResource("move to left list"));
    leftButton.setMaximumSize(new Dimension(24,24));
    ascrightButton.setIcon(ascRightImage);
    ascrightButton.setToolTipText(Options.getInstance().getResource("sort by ascending order"));
    ascrightButton.setMaximumSize(new Dimension(24,24));
    descRightButton.setIcon(descRightImage);
    descRightButton.setToolTipText(Options.getInstance().getResource("sort by descending order"));
    descRightButton.setMaximumSize(new Dimension(24,24));
    upButton.setIcon(upImage);
    upButton.setToolTipText(Options.getInstance().getResource("move up"));
    upButton.setMaximumSize(new Dimension(24,24));
    downButton.setIcon(downImage);
    downButton.setToolTipText(Options.getInstance().getResource("move down"));
    downButton.setMaximumSize(new Dimension(24,24));


  }


  void cancelButton_actionPerformed(ActionEvent e) {
    setVisible(false);
    dispose();
  }


  void okButton_actionPerformed(ActionEvent e) {
    ListModel lm = orderList2.getModel();
    Object[] oc = new Object[lm.getSize()];
    for(int i=0;i<lm.getSize();i++)
      oc[i]= lm.getElementAt(i);
    fm.setOrderClauseFromColums(oc);
    fm.setWhereClause(whereTextArea.getText());
    dbConnUtil.saveProfile(true);
    setVisible(false);
    dispose();
  }


  void andButton_actionPerformed(ActionEvent e) {
    if (colComboBox.getSelectedIndex()==-1 || opComboBox.getSelectedIndex()==-1 || valueTextField.getText().length()==0)
      return;
    try {
      BigDecimal bd = new BigDecimal(valueTextField.getText());
    } catch (NumberFormatException ex) {
      valueTextField.setText("'"+valueTextField.getText()+"'");
    }
    String w = whereTextArea.getText();
    if (w.length()>0)
      w += " AND ";
    w += colComboBox.getSelectedItem().toString()+" "+
          opComboBox.getSelectedItem().toString()+" "+
         valueTextField.getText();
    whereTextArea.setText(w.replace('\n',' '));
  }


  void orButton_actionPerformed(ActionEvent e) {
    if (colComboBox.getSelectedIndex()==-1 || opComboBox.getSelectedIndex()==-1 || valueTextField.getText().length()==0)
      return;
    try {
      BigDecimal bd = new BigDecimal(valueTextField.getText());
    } catch (NumberFormatException ex) {
      valueTextField.setText("'"+valueTextField.getText()+"'");
    }
    String w = whereTextArea.getText();
    if (w.length()>0)
      w += " OR ";
    w += colComboBox.getSelectedItem().toString()+" "+
          opComboBox.getSelectedItem().toString()+" "+
         valueTextField.getText();
    whereTextArea.setText(w.replace('\n',' '));
  }


  void leftButton_actionPerformed(ActionEvent e) {
    if (orderList2.getSelectedValue()==null)
      return;
    selectedColumns.remove(orderList2.getSelectedValue());
    ((DefaultComboBoxModel)orderList2.getModel()).removeElement(orderList2.getSelectedValue());
    orderList2.setSelectedIndex(-1);
  }

  public void valueChanged(ListSelectionEvent e) {
  }


  void descRightButton_actionPerformed(ActionEvent e) {
    if (orderList.getSelectedValue()==null)
      return;
    selectedColumns.add(orderList.getSelectedValue()+ " DESC");
    ((DefaultComboBoxModel)orderList2.getModel()).addElement(orderList.getSelectedValue()+ " DESC");
    orderList.setSelectedIndex(-1);
  }

  void ascRightButton_actionPerformed(ActionEvent e) {
    if (orderList.getSelectedValue()==null)
      return;
    selectedColumns.add(orderList.getSelectedValue()+ " ASC");
    ((DefaultComboBoxModel)orderList2.getModel()).addElement(orderList.getSelectedValue()+ " ASC");
    orderList.setSelectedIndex(-1);
  }

  void upButton_actionPerformed(ActionEvent e) {
    if (orderList2.getSelectedValue()==null)
      return;
    if (orderList2.getSelectedIndex()==0)
      return;
    int index = orderList2.getSelectedIndex();
    Object[] values = selectedColumns.toArray();
    Object value = orderList2.getSelectedValue();
    Object prevValue = orderList2.getModel().getElementAt(orderList2.getSelectedIndex()-1);
    values[orderList2.getSelectedIndex()-1] = value;
    values[orderList2.getSelectedIndex()] = prevValue;
    ArrayList newValues = new ArrayList();
    for(int i=0;i<values.length;i++)
      newValues.add(values[i]);
    selectedColumns.clear();
    selectedColumns.addAll(newValues);
    ((DefaultComboBoxModel)orderList2.getModel()).removeElement(value);
    ((DefaultComboBoxModel)orderList2.getModel()).insertElementAt(value,index-1);
  }

  void downButton_actionPerformed(ActionEvent e) {
    if (orderList2.getSelectedValue()==null)
      return;
    if (orderList2.getSelectedIndex()==orderList2.getModel().getSize()-1)
      return;
    int index = orderList2.getSelectedIndex();
    Object[] values = selectedColumns.toArray();
    Object value = orderList2.getSelectedValue();
    Object nextValue = orderList2.getModel().getElementAt(orderList2.getSelectedIndex()+1);
    values[orderList2.getSelectedIndex()+1] = value;
    values[orderList2.getSelectedIndex()] = nextValue;
    ArrayList newValues = new ArrayList();
    for(int i=0;i<values.length;i++)
      newValues.add(values[i]);
    selectedColumns.clear();
    selectedColumns.addAll(newValues);
    ((DefaultComboBoxModel)orderList2.getModel()).removeElement(value);
    ((DefaultComboBoxModel)orderList2.getModel()).insertElementAt(value,index+1);
  }

  void clearFilterButton_actionPerformed(ActionEvent e) {
    whereTextArea.setText("");
  }

  void clearSortButton_actionPerformed(ActionEvent e) {
    selectedColumns.clear();
    ((DefaultComboBoxModel)orderList2.getModel()).removeAllElements();
  }



}