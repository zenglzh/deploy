package org.jsqltool.gui.graphics;

import javax.swing.*;
import javax.swing.text.*;
import com.toedter.calendar.*;
import org.jsqltool.utils.Options;
import org.jsqltool.utils.ImageLoader;
import java.awt.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Date input field (read only) + combo box for set a date from a calendar.
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
public class CalendarCombo extends JPanel {

  /** calendar */
  private JCalendar calendar = new JCalendar();

  /** date input field (read only) */
  private JTextField dateBox = new JTextField(12);

  /** button icon */
  private Icon icon = ImageLoader.getInstance().getIcon("down.gif");

  /** button used to opne the calendar */
  private JButton button = new JButton(icon);

  /** layout used in this panel */
  private GridBagLayout gridBagLayout1 = new GridBagLayout();

  /** popup menu used to host the calendar */
  private JPopupMenu menu = new JPopupMenu();

  /** date format used to set date input field content */
  private SimpleDateFormat sdf = new SimpleDateFormat(Options.getInstance().getDateFormat());

  /** flag used to set popup menu visibility */
  private boolean menuIsVisible = false;

  /** flag used to indicate popup just opened */
  private boolean firstTime = false;


  public CalendarCombo() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  private void jbInit() throws Exception {
    this.setLayout(gridBagLayout1);
    dateBox.setBackground(Color.white);
    button.addActionListener(new CalendarCombo_button_actionAdapter(this));
    this.add(dateBox,  new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    this.add(button,  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    button.setPreferredSize(new Dimension(icon.getIconWidth()+6,dateBox.getPreferredSize().height));
    button.setMaximumSize(new Dimension(icon.getIconWidth()+6,dateBox.getPreferredSize().height));
    button.setMinimumSize(new Dimension(icon.getIconWidth()+6,dateBox.getPreferredSize().height));
    dateBox.setEditable(false);
    menu.add(calendar);
    calendar.setLocale(new Locale(Options.getInstance().getLanguage()));
    calendar.getDayChooser().addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        if (menuIsVisible && firstTime) {
          menuIsVisible = false;
          firstTime = false;
          dateBox.setText(sdf.format(calendar.getCalendar().getTime()));
          menu.setVisible(false);
        }
        else if (!firstTime)
          firstTime = true;
      }
    });
  }


  /**
   * @return selected date
   */
  public Date getDate() {
    return calendar.getCalendar()==null?null:calendar.getCalendar().getTime();
  }


  /**
   * Set the date.
   * @param date date to set
   */
  public final void setDate(Date date) {
    if (date==null) {
      dateBox.setText("");
      return;
    }
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    calendar.setCalendar(cal);
    dateBox.setText(sdf.format(calendar.getCalendar().getTime()));
  }


  void button_actionPerformed(ActionEvent e) {
    if (menuIsVisible) {
      menu.setVisible(false);
      menuIsVisible = false;
    }
    else {
      menuIsVisible = true;
      firstTime = false;
      menu.show(button,button.getWidth()-calendar.getPreferredSize().width-6,button.getY()+button.getHeight());
      menu.setInvoker(null);
    }
  }


}

class CalendarCombo_button_actionAdapter implements java.awt.event.ActionListener {
  CalendarCombo adaptee;

  CalendarCombo_button_actionAdapter(CalendarCombo adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.button_actionPerformed(e);
  }
}