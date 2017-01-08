package org.jsqltool.gui.graphics;

import java.awt.*;
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Editor for date cell: it contains a date input field (read only) + combo box for set a date from a calendar.
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
public class DateCellEditor extends AbstractCellEditor implements TableCellEditor {

  protected java.sql.Timestamp startVal;
  private CalendarCombo tf = new CalendarCombo();


  public DateCellEditor() { }


  /**
   * @param table - the table we are in.
   * @param value - starting value, before editing
   * @param isSelected - true => the cell is selected.
   * @param row - the row of this cell, in case you cared
   * @param column - thye column, in case you cared.
   * @return The component that will be placed on the screen where the user
   * will enter a new value for the given cell.
   */
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    tf.setDate((Date)value);
    return tf;
  }


  public Object getCellEditorValue() {
    return tf.getDate();
  }


}