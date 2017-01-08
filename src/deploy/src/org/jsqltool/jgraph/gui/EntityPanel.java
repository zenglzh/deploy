package org.jsqltool.jgraph.gui;

import javax.swing.*;
import java.awt.*;
import org.jsqltool.conn.DbConnectionUtil;
import javax.swing.table.TableModel;
import java.util.*;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Panel used to represent a database entity (table/view/synonymn).
 * Used inside a Vertex View.
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
public class EntityPanel extends JPanel {

  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JPanel pkPanel = new JPanel();
  JPanel attrsPanel = new JPanel();
  JPanel namePanel = new JPanel();
  JLabel nameLabel = new JLabel();
  JTextArea pks = new JTextArea();
  JTextArea attrs = new JTextArea();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  GridBagLayout gridBagLayout4 = new GridBagLayout();


  public EntityPanel(DbConnectionUtil dbUtil,String tableName) {
    try {
      jbInit();
      init(dbUtil,tableName);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * Costructor not used.
   */
  public EntityPanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * Initialize the panel content.
   */
  private void init(DbConnectionUtil dbUtil,String tableName) {
    try {
      nameLabel.setText(tableName);
      TableModel model = dbUtil.getTableColumns(tableName);
      // column, data type, pk, null?, default...
      Hashtable primaryKeys = new Hashtable();
      FontMetrics fm = nameLabel.getFontMetrics(nameLabel.getFont());
      int h = fm.getHeight();
      int len,maxLen=0;
      String line = null;
      for (int i = 0; i < model.getRowCount(); i++) {
        if (model.getValueAt(i, 2) == null) {
          // column is NOT a pk...
          line = model.getValueAt(i, 0) + " : " + model.getValueAt(i, 1);
          if (model.getValueAt(i, 3) != null &&
              ! ( (Boolean) model.getValueAt(i, 3)).booleanValue()) {
            line += " NOT NULL";
          }
          line += "\n";
          len = fm.stringWidth(line);
          if (len>maxLen)
            maxLen = len;
          attrs.append(line);
        }
        else {
          // column is pk...
          primaryKeys.put(model.getValueAt(i, 2), new Integer(i));
        }
      }
      if (attrs.getText().length() > 0) {
        attrs.setText(attrs.getText().substring(0, attrs.getText().length() - 1));
      }
      int j = -1;
      for (int i = 0; i < primaryKeys.size(); i++) {
        j = ( (Integer) primaryKeys.get(new Integer(i + 1))).intValue();
        line = model.getValueAt(i, 0) + " : " + model.getValueAt(i, 1);
        if (model.getValueAt(i, 3) != null &&
            ! ( (Boolean) model.getValueAt(i, 3)).booleanValue()) {
          line += " NOT NULL";
        }
        line += "\n";
        len = fm.stringWidth(line);
        if (len>maxLen)
          maxLen = len;
        pks.append(line);
      }
      if (pks.getText().length() > 0) {
        pks.setText(pks.getText().substring(0, pks.getText().length() - 1));
      }
      if (primaryKeys.size() == 0) {
        pkPanel.setPreferredSize(new Dimension(0, 0));
        pkPanel.revalidate();
      }
      setSize(maxLen+20,model.getRowCount() * (h) + 25+h + (primaryKeys.size() > 0 ? 10+h : 0));
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

  }


  private void jbInit() throws Exception {
    this.setLayout(gridBagLayout1);
    attrsPanel.setBackground(Color.white);
    attrsPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    attrsPanel.setLayout(gridBagLayout3);
    pkPanel.setBackground(Color.white);
    pkPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    pkPanel.setLayout(gridBagLayout2);
    this.setOpaque(false);
    namePanel.setBorder(BorderFactory.createLineBorder(Color.black));
    namePanel.setOpaque(false);
    namePanel.setLayout(gridBagLayout4);
    attrs.setOpaque(false);
    attrs.setEditable(false);
    attrs.setText("");
    attrs.setFont(nameLabel.getFont());
    pks.setOpaque(false);
    pks.setEditable(false);
    pks.setText("");
    pks.setFont(nameLabel.getFont());
    nameLabel.setBorder(null);
    this.add(pkPanel,     new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    pkPanel.add(pks,    new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    this.add(attrsPanel,    new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    attrsPanel.add(attrs,   new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    this.add(namePanel,        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 10, 0));
    namePanel.add(nameLabel,    new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
  }


  public String getEntityName() {
    return nameLabel.getText();
  }

}
