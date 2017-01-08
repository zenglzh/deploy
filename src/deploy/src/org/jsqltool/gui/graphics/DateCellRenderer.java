package org.jsqltool.gui.graphics;

import java.awt.*;
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import org.jsqltool.utils.Options;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Renderer for date cell: it shows a formatted date.
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
public class DateCellRenderer extends DefaultTableCellRenderer {

  /** date formatted */
  protected SimpleDateFormat dateFormat = new SimpleDateFormat(Options.getInstance().getDateFormat());


  public DateCellRenderer() { }


  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,boolean hasFocus,int row, int column) {
    JLabel tf = (JLabel)super.getTableCellRendererComponent(table, value, isSelected,hasFocus,row, column);
    if ( value==null )
      tf.setText("");
    else
      tf.setText( dateFormat.format((Date)value) );

    return tf;
  }


}