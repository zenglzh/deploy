package org.jsqltool.gui;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.Component;
import java.sql.*;
import java.beans.*;
import org.jsqltool.conn.*;
import org.jsqltool.utils.Options;
import org.jsqltool.utils.ImageLoader;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Class used to collect internal frames and to listen internal frame events.
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
public class ConnWindows implements InternalFrameListener {

  /** DbConnectionUtil objects list */
  private ArrayList conns = new ArrayList();

  /** DbConnWindow objects list */
  private ArrayList wins = new ArrayList();

  /** MDI frame desktop */
  private JDesktopPane desktop = null;

  /** MDI frame */
  private MainFrame frame = null;

  /** internal frame that currently has focus */
  private JInternalFrame currentFrame = null;

  /** database connection used by the internal frame that currently has focus */
  private DbConnectionUtil currentDbConnUtil = null;

  /** status bar viewed in the bottom area of the MDI frame */
  private JPanel statusBar = null;


  public ConnWindows(MainFrame frame,JDesktopPane desktop,JPanel statusBar) {
    this.frame = frame;
    this.desktop = desktop;
    this.statusBar = statusBar;
  }


  /**
   * Method used to register an internal frame.
   * @param window internal frame
   */
  public void addWindow(final DbConnWindow window) {
    // add a listener to the internal frame...
    ( (JInternalFrame) window).addInternalFrameListener(this);

    // make visibile the internal frame...
    ( (JInternalFrame) window).setSize(desktop.getSize().width,desktop.getSize().height-4);
    ( (JInternalFrame) window).setFrameIcon( ImageLoader.getInstance().getIcon("logo.gif"));
    desktop.add( (JInternalFrame) window);
    String title = ((JInternalFrame) window).getTitle();
    int num = 1;
    for(int i=0;i<statusBar.getComponentCount();i++)
      if (((JToggleButton)statusBar.getComponent(i)).getName().equals(title+(num==1?"":" ["+num+"]")))
        num++;
    if (num>1)
      title += " ["+num+"]";
    ( (JInternalFrame) window).setTitle(title);

    // add window name to the status bar...
    final JToggleButton button = new JToggleButton( title );
    button.setName(title);
    toggleButton(button);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          toggleButton(button);
          ( (JInternalFrame) window).toFront();
          ( (JInternalFrame) window).setSelected(true);
        }
        catch (PropertyVetoException ex) {
        }
      }
    });
    statusBar.add(button);

    // add window name to the "Window" menu...
    JMenuItem menu = new JMenuItem(title);
    frame.winMenu.add(menu);
    menu.setName(title);
    menu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          toggleButton(button);
          ( (JInternalFrame) window).toFront();
          ( (JInternalFrame) window).setSelected(true);
        }
        catch (PropertyVetoException ex) {
        }
      }
    });

    ( (JInternalFrame) window).setVisible(true);
    try {
      ( (JInternalFrame) window).setSelected(true);
    }
    catch (PropertyVetoException ex) {
    }

    // create the link between the internal frame and the connection...
    DbConnectionUtil c = window.getDbConnectionUtil();
    for (int i = 0; i < conns.size(); i++)
      if (conns.get(i).equals(c)) {
        ( (ArrayList) wins.get(i)).add(window);
        return;
      }
    conns.add(c);
    ArrayList winList = new ArrayList();
    winList.add(window);
    wins.add(winList);
  }


  private void toggleButton(JToggleButton button) {
    for(int i=0;i<statusBar.getComponentCount();i++)
      ((JToggleButton)statusBar.getComponent(i)).setSelected(false);
    button.setSelected(true);
  }


  public void removeWindow(DbConnWindow window) {
    // remove the current internal frame from the "Window" menu...
    Component[] menus = (Component[])frame.winMenu.getMenuComponents();
    for(int i=0;i<menus.length;i++)
      if (menus[i].getName()!=null && menus[i].getName().equals(((JInternalFrame)window).getTitle()))
        frame.winMenu.remove(i);

    // remove the window name from the status bar...
    Component[] buttons = (Component[])statusBar.getComponents();
    for(int i=0;i<buttons.length;i++)
      if (buttons[i].getName()!=null && buttons[i].getName().equals(((JInternalFrame)window).getTitle()))
        statusBar.remove(i);
    statusBar.revalidate();
    statusBar.repaint();

    DbConnectionUtil c = window.getDbConnectionUtil();
    for (int i = 0; i < conns.size(); i++)
      if (conns.get(i).equals(c)) {
        ArrayList winList = ( (ArrayList) wins.get(i));
        winList.remove(window);
        ((JInternalFrame)window).setVisible(false);
        desktop.remove((JInternalFrame)window);
        if (winList.size() == 0) {
          try {
            c.getConn().close();
          }
          catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                frame,
                Options.getInstance().getResource("error on closing connection."),
                Options.getInstance().getResource("error"),
                JOptionPane.ERROR_MESSAGE
            );
          }
          wins.remove(i);
          conns.remove(i);
        }
        return;
      }
  }


  public List getConnectionsList() {
    return conns;
  }


  public void closeConnectionAndWindows(DbConnectionUtil dbConnUtil) {
    for (int i = 0; i < conns.size(); i++)
      if (conns.get(i).equals(dbConnUtil)) {
        try {
          dbConnUtil.getConn().close();
        }
        catch (SQLException ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(
              frame,
              Options.getInstance().getResource("error on closing connection."),
              Options.getInstance().getResource("error"),
              JOptionPane.ERROR_MESSAGE
          );
        }
        conns.remove(i);
        ArrayList winList = (ArrayList)wins.get(i);
        for(int j=0;j<winList.size();j++) {
          Component[] menus = (Component[])frame.winMenu.getMenuComponents();
          for(int k=0;k<menus.length;k++)
            if (menus[k].getName()!=null && menus[k].getName().equals(((JInternalFrame) winList.get(j)).getTitle()))
              frame.winMenu.remove(k);

          // remove window name from status bar...
          Component[] buttons = (Component[])statusBar.getComponents();
          for(int k=0;k<buttons.length;k++)
            if (buttons[k].getName()!=null && buttons[k].getName().equals(((JInternalFrame) winList.get(j)).getTitle()))
              statusBar.remove(k);
          statusBar.revalidate();
          statusBar.repaint();

          ((JInternalFrame) winList.get(j)).setVisible(false);
          desktop.remove((JInternalFrame)winList.get(j));
        }
        wins.remove(i);
        return;
      }
  }


  public void closeAll() {
    while(conns.size()>0) {
      closeConnectionAndWindows((DbConnectionUtil)conns.get(0));
    }
    currentDbConnUtil = null;
    currentFrame = null;
  }



  /**
   * Invoked when an internal frame is activated. 
   */
  public void internalFrameActivated(InternalFrameEvent e) {
    currentDbConnUtil = ((DbConnWindow)e.getInternalFrame()).getDbConnectionUtil();
    currentFrame = e.getInternalFrame();

    Component[] buttons = (Component[])statusBar.getComponents();
    for(int i=0;i<buttons.length;i++)
      if (buttons[i].getName()!=null && buttons[i].getName().equals((e.getInternalFrame()).getTitle()))
        toggleButton((JToggleButton)buttons[i]);
  }


  /**
   * Invoked when an internal frame has been closed. 
   */
  public void internalFrameClosed(InternalFrameEvent e) {
    removeWindow((DbConnWindow)e.getInternalFrame());
//    currentDbConnUtil = null;
  }


  /**
   * Invoked when an internal frame is in the process of being closed.
   */
  public void internalFrameClosing(InternalFrameEvent e) {
    currentDbConnUtil = ((DbConnWindow)e.getInternalFrame()).getDbConnectionUtil();
    currentFrame = e.getInternalFrame();
    if (!currentDbConnUtil.getDbConnection().isAutoCommit()) {
      try {
        if (JOptionPane.showConfirmDialog(
            frame,
            Options.getInstance().getResource("commit before closing window?"),
            Options.getInstance().getResource("commit transaction"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE) == 0
          ) {
          if (currentDbConnUtil.getConn()!=null)
            currentDbConnUtil.getConn().commit();
        }
      }
      catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(
            frame,
            Options.getInstance().getResource("error on commit connection."),
            Options.getInstance().getResource("error"),
            JOptionPane.ERROR_MESSAGE
        );
      }
    }
  }


  /**
   * Invoked when an internal frame is de-activated. 
   */
  public void internalFrameDeactivated(InternalFrameEvent e) {
    currentDbConnUtil = null;
    currentFrame = null;
  }


  /**
   * Invoked when an internal frame is de-iconified. 
   */
  public void internalFrameDeiconified(InternalFrameEvent e) {

  }


  /**
   * Invoked when an internal frame is iconified. 
   */
   public void internalFrameIconified(InternalFrameEvent e) {

   }


   public void internalFrameOpened(InternalFrameEvent e) {

   }


  public DbConnectionUtil getCurrentDbConnUtil() {
    return currentDbConnUtil;
  }

  public JInternalFrame getCurrentFrame() {
    return currentFrame;
  }



}