package org.jsqltool.gui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.*;
import java.sql.*;
import java.awt.event.*;
import org.jsqltool.conn.DbConnectionUtil;
import org.jsqltool.conn.DbConnection;
import org.jsqltool.gui.panel.*;
import org.jsqltool.model.*;
import org.jsqltool.MainApp;
import org.jsqltool.utils.Options;
import org.jsqltool.utils.ImageLoader;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Window used to view current opened sessions.
 * Currently it supports only Oracle databases.
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
public class TraceSessionFrame extends JInternalFrame implements DbConnWindow,TableModelListener {

  private DbConnectionUtil dbConnUtil = null;
  private DataPanel dataPanel = null;
  private JFrame parent = null;
  JPanel topPanel = new JPanel();
  JPanel filterPanel = new JPanel();
  JPanel buttonsPanel = new JPanel();
  ImageIcon refreshImage = null;
  GridLayout gridLayout1 = new GridLayout();
  JButton refreshButton = new JButton();
  JLabel filterLabel = new JLabel();
  JLabel likeLabel = new JLabel();
  JTextField likeTF = new JTextField();
  JLabel refreshLabel = new JLabel();
  JTextField refreshTF = new JTextField();
  JButton sxButton = new JButton();
  JButton dxButton = new JButton();
  JCheckBox autoCheckBox = new JCheckBox();
  JComboBox filterComboBox = new JComboBox();
  FlowLayout flowLayout1 = new FlowLayout();
  FlowLayout flowLayout2 = new FlowLayout();
  double refreshTime = 20;
  RefreshThread refreshThread = null;
  private String select = null;
  private String from  = null;
  private String where = null;
  private String ordergroup = null;
  private String detailQuery = null;
  private int detailModelIndex = -1;
  private final String NO_FILTER = "No Filter";
  JPanel resultPanel = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JScrollPane sp = new JScrollPane();
  JTextArea resultTA = new JTextArea();
  JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);


  public TraceSessionFrame(JFrame parent,DbConnectionUtil dbConnUtil) {
    super(Options.getInstance().getResource("trace sessions")+" - "+dbConnUtil.getDbConnection().getName(),true,true,true,true);
    this.dbConnUtil = dbConnUtil;
    this.dataPanel = new DataPanel(dbConnUtil,this);
    this.parent = parent;
    init();
    try {
      jbInit();
      refreshThread = new RefreshThread();
      dataPanel.getTable().setCellSelectionEnabled(false);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    setSize(500,400);
    setVisible(true);
  }


  public TraceSessionFrame() {
    this(null,null);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  private void init() {
    if (dbConnUtil.getDbConnection().getDbType()==DbConnection.ORACLE_TYPE) {
      filterComboBox.addItem(NO_FILTER);
      filterComboBox.addItem("Oracle User");
      filterComboBox.addItem("OS User");
      filterComboBox.addItem("Program");
      filterComboBox.addItem("Status");
//      filterComboBox.addItem("Client User");
      filterComboBox.addItem("Machine");
      filterComboBox.addItem("Terminal");
      select =
             "SELECT   s.status \"Status\", s.serial# \"Serial#\", s.TYPE \"Type\","+
             "s.username \"DB User\", s.osuser \"Client User\", s.server \"Server\","+
             "s.machine \"Machine\", s.module \"Module\", s.client_info \"Client Info\","+
             "s.terminal \"Terminal\", s.program \"Program\", p.program \"O.S. Program\","+
             "s.logon_time \"Connect Time\", lockwait \"Lock Wait\","+
             "si.physical_reads \"Physical Reads\", si.block_gets \"Block Gets\","+
             "si.consistent_gets \"Consistent Gets\","+
             "si.block_changes \"Block Changes\","+
             "si.consistent_changes \"Consistent Changes\", s.process \"Process\","+
             "p.spid, p.pid, si.SID, s.audsid, s.sql_address \"Address\","+
             "s.sql_hash_value \"Sql Hash\", s.action,"+
             "SYSDATE - (s.last_call_et / 86400) \"Last Call\" ";
      from =
             "FROM v$session s, v$process p, SYS.v_$sess_io si ";
      where =
             "WHERE s.paddr = p.addr(+) AND si.SID(+) = s.SID ";
      ordergroup =
             "ORDER BY 5 DESC";

      detailQuery = "SELECT sql_text FROM v$sqltext_with_newlines WHERE hash_value = TO_NUMBER (?) ORDER BY piece";
      detailModelIndex = 25;
    }

    dataPanel.getTable().setListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (dataPanel.getTable().getSelectedRow()!=-1)
          dataPanel.getTable().setRowSelectionInterval(dataPanel.getTable().getSelectedRow(),dataPanel.getTable().getSelectedRow());
        if (detailQuery!=null &&
            detailModelIndex!=-1 &&
            dataPanel.getTable().getSelectedRow()!=-1) {
          String res = "";
          try {
            PreparedStatement pstmt = dbConnUtil.getConn().prepareStatement(detailQuery);
            pstmt.setObject(1,dataPanel.getTableModel().getValueAt(dataPanel.getTable().getSelectedRow(),detailModelIndex));
            ResultSet rset = pstmt.executeQuery();
            if (rset.next())
                res = rset.getString(1);
            rset.close();
            pstmt.close();
          }
          catch (SQLException ex) {
            ex.printStackTrace();
          }
          resultTA.setText(res);
        }
      }
    });

  }


  public DbConnectionUtil getDbConnectionUtil() {
    return dbConnUtil;
  }


  /**
   * This fine grain notification tells listeners the exact range
   * of cells, rows, or columns that changed.
   */
  public void tableChanged(TableModelEvent e) {

  }


  private void jbInit() throws Exception {
    topPanel.setLayout(gridLayout1);
    gridLayout1.setColumns(1);
    gridLayout1.setRows(2);
    filterLabel.setText(Options.getInstance().getResource("filter"));
    likeLabel.setText(Options.getInstance().getResource("like"));
    refreshLabel.setText(Options.getInstance().getResource("refresh (secs)"));
    refreshTF.setText(String.valueOf(refreshTime));
    refreshTF.setColumns(4);
    refreshTF.addFocusListener(new TraceSessionFrame_refreshTF_focusAdapter(this));
    sxButton.setText("<");
    sxButton.addActionListener(new TraceSessionFrame_sxButton_actionAdapter(this));
//    dxButton.setPreferredSize(new Dimension(41, 25));
    dxButton.setText(">");
    dxButton.addActionListener(new TraceSessionFrame_dxButton_actionAdapter(this));
    autoCheckBox.setSelected(true);
    autoCheckBox.setText(Options.getInstance().getResource("auto refresh data"));
    autoCheckBox.addItemListener(new TraceSessionFrame_autoCheckBox_itemAdapter(this));
    refreshButton.setText("");
    refreshButton.addActionListener(new TraceSessionFrame_refreshButton_actionAdapter(this));
    likeTF.setText("");
    likeTF.setColumns(20);
    likeTF.addActionListener(new TraceSessionFrame_likeTF_actionAdapter(this));
    buttonsPanel.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    filterPanel.setLayout(flowLayout2);
    flowLayout2.setAlignment(FlowLayout.LEFT);
    resultPanel.setLayout(borderLayout1);
    resultTA.setEditable(false);
    resultTA.setText("");
    resultTA.setColumns(200);
    resultTA.setRows(80);
    split.setOrientation(JSplitPane.VERTICAL_SPLIT);
    split.setBottomComponent(resultPanel);
    split.setDividerSize(5);
    split.setTopComponent(dataPanel);
    this.getContentPane().add(split,BorderLayout.CENTER);
    this.getContentPane().add(topPanel, BorderLayout.NORTH);
    topPanel.add(buttonsPanel, null);
    topPanel.add(filterPanel, null);
    buttonsPanel.add(refreshButton, null);
    buttonsPanel.add(refreshLabel, null);
    buttonsPanel.add(refreshTF, null);
    buttonsPanel.add(sxButton, null);
    buttonsPanel.add(dxButton, null);
    buttonsPanel.add(autoCheckBox, null);
    filterPanel.add(filterLabel, null);
    filterPanel.add(filterComboBox, null);
    filterPanel.add(likeLabel, null);
    filterPanel.add(likeTF, null);
    resultPanel.add(sp, BorderLayout.WEST);
    sp.getViewport().add(resultTA, null);
    refreshImage = ImageLoader.getInstance().getIcon("refresh2.gif");
    refreshButton.setBorder(null);
    refreshButton.setMaximumSize(new Dimension(24,24));
    refreshButton.setPreferredSize(new Dimension(24, 24));
    refreshButton.setIcon(refreshImage);
    refreshButton.setToolTipText(Options.getInstance().getResource("refresh"));
    split.setDividerLocation(200);

  }


  void refreshButton_actionPerformed(ActionEvent e) {
    if (select!=null && from!=null && where!=null && ordergroup!=null) {
      String filter = "";
      if (!filterComboBox.getSelectedItem().equals(NO_FILTER) && likeTF.getText().length()>0) {
        String fieldName = filterComboBox.getSelectedItem().toString();
        if (fieldName.equals("Oracle User"))
          fieldName = "s.username";
        else if (fieldName.equals("OS User"))
          fieldName = "s.osuser";
        else if (fieldName.equals("Program"))
          fieldName = "s.program";
        else if (fieldName.equals("Status"))
          fieldName = "s.status";
        else if (fieldName.equals("Machine"))
          fieldName = "s.machine";
        else if (fieldName.equals("Terminal"))
          fieldName = "s.terminal";
        filter =
          " AND '" +
          fieldName +
          "' like '" +
          likeTF.getText() +
          "%' ";
      }
      dataPanel.setQuery( select+from+where+filter+ordergroup,new Vector() );
      ((CustomTableModel)dataPanel.getTableModel()).setEditMode(CustomTableModel.DETAIL_REC);
    }
  }


  void sxButton_actionPerformed(ActionEvent e) {
    try {
      refreshTime--;
      if (refreshTime<=0)
        refreshTime = 1;
      refreshTF.setText(String.valueOf(refreshTime));
    }
    catch (NumberFormatException ex) {
      refreshTime = 20;
      refreshTF.setText(String.valueOf(refreshTime));
    }
  }


  void dxButton_actionPerformed(ActionEvent e) {
    try {
      refreshTime++;
      refreshTF.setText(String.valueOf(refreshTime));
    }
    catch (NumberFormatException ex) {
      refreshTime = 20;
      refreshTF.setText(String.valueOf(refreshTime));
    }
  }


  void autoCheckBox_itemStateChanged(ItemEvent e) {
    if (autoCheckBox.isSelected())
      refreshThread = new RefreshThread();
  }


  void likeTF_actionPerformed(ActionEvent e) {
    refreshButton_actionPerformed(null);
  }


  void refreshTF_focusLost(FocusEvent e) {
    try {
      refreshTime = Double.valueOf(refreshTF.getText()).doubleValue();
      if (refreshTime<=0) {
        refreshTime = 1;
        refreshTF.setText(String.valueOf(refreshTime));
      }
    }
    catch (NumberFormatException ex) {
      refreshTime = 20;
      refreshTF.setText(String.valueOf(refreshTime));
    }
  }


  /**
   * esegue un thread per l'aggiornamento automatico del contenuto della griglia,
   * ogni "refreshTime" secondi.
   */
  class RefreshThread extends Thread {

    public RefreshThread() {
      if (autoCheckBox.isSelected())
        start();
    }

    public void run() {
      while(isVisible() && autoCheckBox.isSelected()) {
        refreshButton_actionPerformed(null);
        try {
          this.sleep((int)(refreshTime*1000));
        }
        catch (Exception ex) {
        }
      }
    }

  }


}

class TraceSessionFrame_refreshButton_actionAdapter implements java.awt.event.ActionListener {
  TraceSessionFrame adaptee;

  TraceSessionFrame_refreshButton_actionAdapter(TraceSessionFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.refreshButton_actionPerformed(e);
  }
}

class TraceSessionFrame_sxButton_actionAdapter implements java.awt.event.ActionListener {
  TraceSessionFrame adaptee;

  TraceSessionFrame_sxButton_actionAdapter(TraceSessionFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.sxButton_actionPerformed(e);
  }
}

class TraceSessionFrame_dxButton_actionAdapter implements java.awt.event.ActionListener {
  TraceSessionFrame adaptee;

  TraceSessionFrame_dxButton_actionAdapter(TraceSessionFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.dxButton_actionPerformed(e);
  }
}

class TraceSessionFrame_autoCheckBox_itemAdapter implements java.awt.event.ItemListener {
  TraceSessionFrame adaptee;

  TraceSessionFrame_autoCheckBox_itemAdapter(TraceSessionFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void itemStateChanged(ItemEvent e) {
    adaptee.autoCheckBox_itemStateChanged(e);
  }
}

class TraceSessionFrame_likeTF_actionAdapter implements java.awt.event.ActionListener {
  TraceSessionFrame adaptee;

  TraceSessionFrame_likeTF_actionAdapter(TraceSessionFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.likeTF_actionPerformed(e);
  }
}

class TraceSessionFrame_refreshTF_focusAdapter extends java.awt.event.FocusAdapter {
  TraceSessionFrame adaptee;

  TraceSessionFrame_refreshTF_focusAdapter(TraceSessionFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void focusLost(FocusEvent e) {
    adaptee.refreshTF_focusLost(e);
  }
}