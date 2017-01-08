package org.jsqltool.gui;

import java.awt.*;
import javax.swing.*;
import org.jsqltool.utils.Options;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Dialog used to view a progress bar to indicate a work in progress.
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
public class ProgressDialog extends JDialog {
  JPanel panel1 = new JPanel();
  JProgressBar progressBar = new JProgressBar();
  GridBagLayout gridBagLayout1 = new GridBagLayout();

  /** flag used to start progress */
  private boolean goOn = false;

  /** thread used to start progress bar */
  private ProgressThread thread = new ProgressThread();

  /** true to increment progress bar, false to decrement it */
  private boolean inc = true;

  /** unique instance of the class */
  private static ProgressDialog instance = null;

  /** flag used to disable progress dialog closing */
  private boolean canClose = true;


  /**
   * @return unique instance of the class
   */
  public static ProgressDialog getInstance() {
    if (instance==null)
      instance = new ProgressDialog();
    return instance;
  }


  /**
   * Private constructor, called by the static initilizer.
   */
  private ProgressDialog() {
    super(MainFrame.getInstance(),false);
    setSize(340,70);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation((int)screenSize.getWidth()/2-170,(int)screenSize.getHeight()/2-30);
    setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    progressBar.setMinimum(0);
    progressBar.setMaximum(20);
    try {
      jbInit();
    }
    catch (Exception ex) {
    }
  }


  /**
   * Method used to start the progress.
   */
  public void startProgress() {
    if (!goOn) {
      setVisible(true);
      goOn = true;
      thread.start();
    }

  }


  /**
   * Method used to start the progress and disable progress closing by means of stopProgress.
   */
  public void startProgressNoClose() {
    if (!goOn) {
      canClose = false;
      setVisible(true);
      goOn = true;
      thread.start();
    }

  }


  /**
   * Method used to stop the progress.
   */
  public void stopProgress() {
    if (!canClose)
      return;
    if (goOn) {
      goOn = false;
      setVisible(false);
    }
  }


  /**
   * Method used to force progress stopping.
   */
  public void forceStopProgress() {
    canClose = true;
    if (goOn) {
      goOn = false;
      setVisible(false);
    }
  }



  private void jbInit() throws Exception {
    panel1.setLayout(gridBagLayout1);
    progressBar.setPreferredSize(new Dimension(250, 25));
    this.setTitle(Options.getInstance().getResource("work in progress..."));
    getContentPane().add(panel1);
    panel1.add(progressBar,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
  }


  class ProgressThread extends Thread {

    public void run() {
      while(goOn) {
        if (progressBar.getValue()==progressBar.getMaximum())
          inc = false;
        else
        if (progressBar.getValue()==progressBar.getMinimum())
          inc = true;
        progressBar.setValue(progressBar.getValue()+(inc?1:-1));
        try {
          sleep(100);
        }
        catch (InterruptedException ex) {
        }
      }
    }


  }


}