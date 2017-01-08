package org.jsqltool.replication;

import java.io.*;
import javax.swing.*;
import java.util.*;
import org.jsqltool.utils.Options;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: This Value Object contains replication settings used in a Data Replication process.
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
public class ReplicationProfile {

  private String name;
  private String sourceDatabase;
  private String destDatabase;
  private java.util.ArrayList tablesList;
  private boolean recreateTablesContent;


  public ReplicationProfile() {
  }


  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getSourceDatabase() {
    return sourceDatabase;
  }
  public void setSourceDatabase(String sourceDatabase) {
    this.sourceDatabase = sourceDatabase;
  }
  public String getDestDatabase() {
    return destDatabase;
  }
  public void setDestDatabase(String destDatabase) {
    this.destDatabase = destDatabase;
  }
  public java.util.ArrayList getTablesList() {
    return tablesList;
  }
  public void setTablesList(java.util.ArrayList tablesList) {
    this.tablesList = tablesList;
  }
  public boolean isRecreateTablesContent() {
    return recreateTablesContent;
  }
  public void setRecreateTablesContent(boolean recreateTablesContent) {
    this.recreateTablesContent = recreateTablesContent;
  }


  /**
   * Load replication profile file (profile/filename.rep)
   */
  public void loadProfile(JInternalFrame parent,File file,ArrayList profiles,Vector profileNames) {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
      br.readLine(); // version...
      this.setName( br.readLine() );
      this.setSourceDatabase( br.readLine() );
      this.setDestDatabase( br.readLine() );
      this.setRecreateTablesContent( br.readLine().toLowerCase().equals("true") );

      String line = null;
      ArrayList tables = new ArrayList();
      while((line=br.readLine())!=null) {
        tables.add(line);
      }
      this.setTablesList(tables);

      br.close();

      profiles.add(this);
      profileNames.add(name);

    } catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          parent,
          Options.getInstance().getResource("error on loading replication profile file.")+":\n"+ex.getMessage(),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE
      );
    }
  }


 public void saveProfile(JFrame parent,boolean isEdit) {
   try {
     PrintWriter pw = new PrintWriter(new FileOutputStream(new File("profile/"+name.replace(' ','_')+".rep")));

     // save replicaton properties...
     pw.println( "VERSION 1.0.4" );
     pw.println( this.getName() );
     pw.println( this.getSourceDatabase() );
     pw.println( this.getDestDatabase() );
     pw.println( this.isRecreateTablesContent()?"true":"false" );
     for(int i=0;i<this.tablesList.size();i++)
       pw.println( this.tablesList.get(i).toString() );

     pw.close();

   } catch (Exception ex) {
     ex.printStackTrace();
     JOptionPane.showMessageDialog(
         parent,
         Options.getInstance().getResource("error on saving replication profile file.")+":\n"+ex.getMessage(),
         Options.getInstance().getResource("error"),
         JOptionPane.ERROR_MESSAGE
     );
   }
 }






}