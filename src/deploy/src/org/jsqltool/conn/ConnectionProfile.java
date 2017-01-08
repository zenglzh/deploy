package org.jsqltool.conn;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.sql.*;
import org.jsqltool.gui.tableplugins.datatable.filter.FilterModel;
import org.jsqltool.utils.Options;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Class for load/save a connection profile.
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
public class ConnectionProfile {

  /** max mumber of old queries to store in the connection profile file */
  private static final int MAX_OLD_QUERIES = 20;


  /**
   * Load connection profile file (profile/filename.ini)
   */
  public void loadProfile(JInternalFrame parent,File file,ArrayList conns,Vector connNames) {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(new
          FileInputStream(file)));
      String line = br.readLine();
      if (line.equals("VERSION 1.0.7")) {
        loadProfileV107(parent,file,conns,connNames);
      }
      else if (line.equals("VERSION 1.0.3")) {
        loadProfileV103(parent,file,conns,connNames);
      } else
        loadProfileV102(parent,file,conns,connNames);
      br.close();
    } catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          parent,
          Options.getInstance().getResource("error on loading connections profile files.")+":\n"+ex.getMessage(),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE
      );
    }

  }



  /**
   * Load connection profile file (profile/filename.ini) in JSQLTool <=1.0.2 version.
   */
  private void loadProfileV102(JInternalFrame parent,File file,ArrayList conns,Vector connNames) {
    try {
      // load .ini file...
      String line = null;
      int dbType;
      String name = null;
      String driver = null;
      String url = null;
      String username = null;
      String password = null;
      boolean autoCommit;
      int isolationLevel;
      boolean readOnly;
      String catalog = null;
      String tableName = null;
      FilterModel fm = null;
      String whereC = null;
      Hashtable filters = null;
      ArrayList oldQueries = null;
      BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(file) ));

      // read connection properties...
      dbType = Integer.parseInt(br.readLine());
      name = br.readLine();
      driver = br.readLine();
      url = br.readLine();
      username = br.readLine();
      password = Options.getInstance().decode(br.readLine());
      autoCommit = br.readLine().toLowerCase().equals("true");
      isolationLevel = Integer.parseInt(br.readLine());
      readOnly = br.readLine().toLowerCase().equals("true");
      catalog = br.readLine();

      // skip one row...
      br.readLine();

      // read filters/orderers...
      filters = new Hashtable();
      while(!(line=br.readLine()).equals("")) {
        tableName = line;
        fm = new FilterModel();
        fm.setOrderClause(br.readLine());
        whereC = br.readLine();
        if (whereC.length()>0)
          whereC = whereC.substring(7);
        fm.setWhereClause(whereC);
        filters.put(tableName,fm);
      }

      // read old queries...
      oldQueries = new ArrayList();
      while((line=br.readLine())!=null) {
        oldQueries.add(line);
      }
      br.close();

      conns.add(new DbConnection(dbType,name,driver,url,username,password,autoCommit,isolationLevel,readOnly,catalog,filters,oldQueries,false));
      connNames.add(name);

    } catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          parent,
          Options.getInstance().getResource("error on loading connections profile files.")+":\n"+ex.getMessage(),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE
      );
    }
  }


  /**
   * Load connection profile file (profile/filename.ini) in JSQLTool >=1.0.3 version.
   */
  private void loadProfileV103(JInternalFrame parent,File file,ArrayList conns,Vector connNames) {
    try {
      // load .ini file...
      String line = null;
      int dbType;
      String name = null;
      String driver = null;
      String url = null;
      String username = null;
      boolean autoCommit;
      int isolationLevel;
      boolean readOnly;
      String catalog = null;
      String tableName = null;
      FilterModel fm = null;
      String whereC = null;
      Hashtable filters = null;
      ArrayList oldQueries = null;
      BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(file) ));

      // read connection properties...
      String iniVersion = br.readLine();
      dbType = Integer.parseInt(br.readLine());
      name = br.readLine();
      driver = br.readLine();
      url = br.readLine();
      username = br.readLine();
      autoCommit = br.readLine().toLowerCase().equals("true");
      isolationLevel = Integer.parseInt(br.readLine());
      readOnly = br.readLine().toLowerCase().equals("true");
      catalog = br.readLine();

      // skip one row...
      br.readLine();

      // read filters/orderers...
      filters = new Hashtable();
      while(!(line=br.readLine()).equals("")) {
        tableName = line;
        fm = new FilterModel();
        fm.setOrderClause(br.readLine());
        whereC = br.readLine();
        if (whereC.length()>0)
          whereC = whereC.substring(7);
        fm.setWhereClause(whereC);
        filters.put(tableName,fm);
      }

      // read old queries...
      oldQueries = new ArrayList();
      while((line=br.readLine())!=null) {
        oldQueries.add(line);
      }
      br.close();

      File passwdFile = new File(file.getAbsolutePath().substring(0,file.getAbsolutePath().length()-4)+".pwd");
      FileInputStream in = new FileInputStream(passwdFile);
      byte[] bb = new byte[(int)passwdFile.length()];
      in.read(bb);
      String password = Options.getInstance().decodeFromBytes(bb);
      in.close();

      conns.add(new DbConnection(dbType,name,driver,url,username,password,autoCommit,isolationLevel,readOnly,catalog,filters,oldQueries,false));
      connNames.add(name);

    } catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          parent,
          Options.getInstance().getResource("error on loading connections profile files.")+":\n"+ex.getMessage(),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE
      );
    }
  }


  /**
   * Load connection profile file (profile/filename.ini) in JSQLTool >=1.0.7 version.
   */
  private void loadProfileV107(JInternalFrame parent,File file,ArrayList conns,Vector connNames) {
    try {
      // load .ini file...
      String line = null;
      int dbType;
      String name = null;
      String driver = null;
      String url = null;
      String username = null;
      boolean autoCommit;
      int isolationLevel;
      boolean readOnly;
      boolean quotes;
      String catalog = null;
      String tableName = null;
      FilterModel fm = null;
      String whereC = null;
      Hashtable filters = null;
      ArrayList oldQueries = null;
      BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(file) ));

      // read connection properties...
      String iniVersion = br.readLine();
      dbType = Integer.parseInt(br.readLine());
      name = br.readLine();
      driver = br.readLine();
      url = br.readLine();
      username = br.readLine();
      autoCommit = br.readLine().toLowerCase().equals("true");
      isolationLevel = Integer.parseInt(br.readLine());
      readOnly = br.readLine().toLowerCase().equals("true");
      catalog = br.readLine();
      quotes = br.readLine().toLowerCase().equals("true");

      // skip one row...
      br.readLine();

      // read filters/orderers...
      filters = new Hashtable();
      while(!(line=br.readLine()).equals("")) {
        tableName = line;
        fm = new FilterModel();
        fm.setOrderClause(br.readLine());
        whereC = br.readLine();
        if (whereC.length()>0)
          whereC = whereC.substring(7);
        fm.setWhereClause(whereC);
        filters.put(tableName,fm);
      }

      // read old queries...
      oldQueries = new ArrayList();
      while((line=br.readLine())!=null) {
        oldQueries.add(line);
      }
      br.close();

      File passwdFile = new File(file.getAbsolutePath().substring(0,file.getAbsolutePath().length()-4)+".pwd");
      FileInputStream in = new FileInputStream(passwdFile);
      byte[] bb = new byte[(int)passwdFile.length()];
      in.read(bb);
      String password = Options.getInstance().decodeFromBytes(bb);
      in.close();

      conns.add(new DbConnection(dbType,name,driver,url,username,password,autoCommit,isolationLevel,readOnly,catalog,filters,oldQueries,quotes));
      connNames.add(name);

    } catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          parent,
          Options.getInstance().getResource("error on loading connections profile files.")+":\n"+ex.getMessage(),
          Options.getInstance().getResource("error"),
          JOptionPane.ERROR_MESSAGE
      );
    }
  }


 public void saveProfile(JFrame parent,DbConnection c,boolean isEdit) {
   try {
     PrintWriter pw = new PrintWriter(new FileOutputStream(new File("profile/"+c.getName().replace(' ','_')+".ini")));

     // save connection properties...
     pw.println( "VERSION 1.0.7" );
     pw.println( c.getDbType() );
     pw.println( c.getName() );
     pw.println( c.getClassName() );
     pw.println( c.getUrl() );
     pw.println( c.getUsername() );
     pw.println( c.isAutoCommit() );
     pw.println( c.getIsolationLevel() );
     pw.println( c.isReadOnly() );
     pw.println( c.getCatalog() );
     pw.println( c.isQuotes() );

     // save one empty row...
     pw.println( "" );

     // save filters/orderers...
     Enumeration en = c.getFilters().keys();
     FilterModel fm = null;
     String tableName = null;
     while(en.hasMoreElements()) {
       tableName = en.nextElement().toString();
       pw.println( tableName );
       fm = (FilterModel)c.getFilters().get(tableName);
       pw.println( fm.getOrderClause() );
       pw.println( fm.getWhereClause() );
     }

     // save one empty row...
     pw.println( "" );

     // save old queries...
     while(c.getOldQueries().size()>MAX_OLD_QUERIES)
       c.getOldQueries().remove(0);
     for(int i=0;i<c.getOldQueries().size();i++)
       pw.println( c.getOldQueries().get(i) );

     pw.close();

     File passwdFile = new File("profile/"+c.getName().replace(' ','_')+".pwd");

     FileOutputStream out = new FileOutputStream(passwdFile);
     out.write(Options.getInstance().encodeToBytes(c.getPassword()));
     out.close();


   } catch (Exception ex) {
     ex.printStackTrace();
     JOptionPane.showMessageDialog(
         parent,
         Options.getInstance().getResource("error on saving connections profile files.")+":\n"+ex.getMessage(),
         Options.getInstance().getResource("error"),
         JOptionPane.ERROR_MESSAGE
     );
   }
 }




}