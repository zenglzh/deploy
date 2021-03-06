package org.jsqltool.gui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import org.jsqltool.conn.DbConnectionUtil;
import java.awt.event.*;
import org.jsqltool.gui.panel.*;
import org.jsqltool.gui.tablepanel.*;
import org.jsqltool.utils.Options;
import org.jsqltool.utils.ImageLoader;
import org.jgraph.JGraph;
import org.jsqltool.jgraph.gui.EntityVertexView;
import org.jgraph.graph.*;
import org.jsqltool.jgraph.gui.EntityPanel;
import java.awt.geom.*;
import java.util.HashSet;
import java.util.ArrayList;
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.awt.print.PrinterException;
import java.util.Hashtable;
import java.io.File;
import java.io.FileFilter;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.*;
import java.awt.geom.Rectangle2D;
import org.jsqltool.jgraph.gui.LoadFileDialog;
import java.util.StringTokenizer;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.dnd.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Window used to view in a schema a set of tables/views/synonyms (with related foreign key relations).
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
public class SchemaFrame extends JInternalFrame implements DbConnWindow, DragSourceListener, DropTargetListener {

  JPanel mainPanel = new JPanel();
  JSplitPane splitPane = new JSplitPane();
  JScrollPane schemaScrollPane = new JScrollPane();
  BorderLayout borderLayout1 = new BorderLayout();
  JTabbedPane tableTabbedPane = new JTabbedPane();
  JScrollPane tableScrollPane = new JScrollPane();
  JScrollPane viewScrollPane = new JScrollPane();
  JScrollPane sinScrollPane = new JScrollPane();
  JList tablesList = new JList();
  JList viewList = new JList();
  JList sinList = new JList();
  private DbConnectionUtil dbConnUtil = null;
  JPanel tablesPanel = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel catPanel = new JPanel();
  JLabel catLabel = new JLabel();
  JComboBox catComboBox = new JComboBox();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JPanel tablePanel = new JPanel();
  JPanel viewPanel = new JPanel();
  JPanel synPanel = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  BorderLayout borderLayout4 = new BorderLayout();
  BorderLayout borderLayout5 = new BorderLayout();
  BorderLayout borderLayout6 = new BorderLayout();
  JPanel toolbarPanel = new JPanel();
  JPanel schemaPanel = new JPanel();
  JButton printButton = new JButton();
  ImageIcon printImage;
  JButton printFitButton = new JButton();
  ImageIcon printFitImage;
  JButton loadButton = new JButton();
  ImageIcon loadImage;
  JButton saveButton = new JButton();
  ImageIcon saveImage;
  JButton expButton = new JButton();
  ImageIcon expImage;
  FlowLayout flowLayout1 = new FlowLayout();

  private FilterListPanel tableFilterListPanel = new FilterListPanel(tablesList,new TableFilterController());
  private FilterListPanel viewFilterListPanel = new FilterListPanel(viewList,new ViewFilterController());
  private FilterListPanel synFilterListPanel = new FilterListPanel(sinList,new SynFilterController());

  /** current selected schema name (if combo is empty, then schemaName is set to "" */
  private String schemaName = "";

  /** JGraph model */
  private GraphModel model = new DefaultGraphModel();

  /** JGraph view */
  private GraphLayoutCache view = new GraphLayoutCache(model,new DefaultCellViewFactory() {
    protected PortView createPortView(Object object) {
      return super.createPortView(object);
    }

    /**
     * This method id overridded to create a new vertex type
     */
    protected VertexView createVertexView(Object object) {
      return new EntityVertexView((DefaultGraphCell)object);
    }

  });

  /** JGraph diagram */
  private JGraph graph = new JGraph(model, view);

  /** entities already inserted into the graph */
  private Hashtable entitiesAlreadyInserted = new Hashtable();

  /** parent frame */
  private MainFrame parent = null;

  JComboBox zoomComboBox = new JComboBox();

  private static final String ZOOM200 = "Zoom 200%";
  private static final String ZOOM150 = "Zoom 150%";
  private static final String ZOOM100 = "Zoom 100%";
  private static final String ZOOM75 = "Zoom 75%";
  private static final String ZOOM50 = "Zoom 50%";
  private static final String ZOOM25 = "Zoom 25%";
  private static final String ZOOM10 = "Zoom 10%";

  /** drag sources */
  private DragSource dragSource1 = new DragSource();
  private DragSource dragSource2 = new DragSource();
  private DragSource dragSource3 = new DragSource();

  /** grid identifier, used for DnD */
  private String gridId = null;

  /** drop gesture */
  private DropTarget dropTarget = new DropTarget(graph, this);


  public SchemaFrame(MainFrame parent,DbConnectionUtil dbConnUtil) {
    super(Options.getInstance().getResource("database schema")+" - "+dbConnUtil.getDbConnection().getName(),true,true,true,true);
    this.dbConnUtil = dbConnUtil;
    this.parent = parent;
    try {
      jbInit();
      new Thread() {
        public void run() {
          ProgressDialog.getInstance().startProgress();
          try {
            SchemaFrame.this.init();
          }
          catch (Throwable ex) {
          }
          finally {
            ProgressDialog.getInstance().stopProgress();
          }
        }
      }.start();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  public SchemaFrame() {
    this(null,null);
  }


  private void init() {
    // fill in the zoom combo box...
    zoomComboBox.addItem(ZOOM200);
    zoomComboBox.addItem(ZOOM150);
    zoomComboBox.addItem(ZOOM100);
    zoomComboBox.addItem(ZOOM75);
    zoomComboBox.addItem(ZOOM50);
    zoomComboBox.addItem(ZOOM25);
    zoomComboBox.addItem(ZOOM10);
    zoomComboBox.setSelectedIndex(2);

    // catalogs list...
    java.util.List cats = dbConnUtil.getSchemas();
    for (int i = 0; i < cats.size(); i++)
      if (cats.get(i)!=null)
        catComboBox.addItem(cats.get(i));
    if (catComboBox.getItemCount()==0)
      catComboBox.addItem("");

    // listener to repaint lists on selecting a catalog...
    catComboBox.setSelectedItem(dbConnUtil.getDbConnection().getUsername().toUpperCase());
    updateLists();
    catComboBox.addItemListener(new SchemaFrame_catComboBox_itemAdapter(this));

    graph.addKeyListener(new KeyAdapter() {

      public void keyTyped(KeyEvent e) {
        if (e.getKeyChar()=='\b') {
          Object[] objs = view.getCells(false, true, false, false);
          objs = graph.getSelectionCells(objs);
          if (objs.length==0)
            return;
          graph.getGraphLayoutCache().remove(objs);
          EntityPanel panel = null;
          for(int i=0;i<objs.length;i++)
            if (objs[i] instanceof DefaultGraphCell) {
              panel = (EntityPanel)((DefaultGraphCell)objs[i]).getUserObject();
              entitiesAlreadyInserted.remove(panel.getEntityName());
            }
          objs = view.getCells(false, false, true, true);
          objs = graph.getSelectionCells(objs);
          graph.getGraphLayoutCache().remove(objs);
        }

      }
    });

    graph.addMouseListener(new MouseAdapter() {

      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount()==1) {
          Object[] objs = view.getCells(false, true, false, false);
          objs = graph.getSelectionCells(objs);
          if (objs.length>0)
            return;

          Object[] entities = null;
          if (tableTabbedPane.getSelectedIndex()==0)
            entities = tablesList.getSelectedValues();
          else if (tableTabbedPane.getSelectedIndex()==1)
            entities = viewList.getSelectedValues();
          else if (tableTabbedPane.getSelectedIndex()==2)
            entities = sinList.getSelectedValues();
//          if (entities.length==1 && !entitiesAlreadyInserted.containsKey(entities[0].toString())) {
//            // add the selected entity to the graph...
//            EntityPanel panel = new EntityPanel(dbConnUtil,entities[0].toString());
//            DefaultGraphCell cell = new DefaultGraphCell(panel);
//            GraphConstants.setEditable(cell.getAttributes(),false);
//            GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(
//                e.getX(),e.getY(),panel.getSize().width,panel.getSize().height
//            ));
//            Object insertCells[] = new Object[] { cell };
//            model.insert(insertCells, null, null, null, null);
//            entitiesAlreadyInserted.put(entities[0].toString(),cell);
//            addFKs(insertCells);
//          }
//          else if (entities.length>1) {
          if (entities.length>0) {
            // add all selected entities to the graph...
            addEntities(entities,e.getX(),e.getY());
          }
        }
      }


    });

    // enable drag 'n drop onto the three entity lists...
    dragSource1.createDefaultDragGestureRecognizer(
        tablesList,
        DnDConstants.ACTION_MOVE,
        new DragGestureAdapter(this)
    );
    dragSource2.createDefaultDragGestureRecognizer(
        viewList,
        DnDConstants.ACTION_MOVE,
        new DragGestureAdapter(this)
    );
    dragSource3.createDefaultDragGestureRecognizer(
        sinList,
        DnDConstants.ACTION_MOVE,
        new DragGestureAdapter(this)
    );

  }


  /**
   * Add tables/vies/synonims to the graph.
   * @param entities entities to add
   */
  private void addEntities(final Object[] entities,final int x,final int y) {
    ProgressDialog.getInstance().startProgressNoClose();

    // execute adding entities in another thread to allow viewing a waiting dialog...
    new Thread() {
      public void run() {
        try {
          EntityPanel panel = null;
          DefaultGraphCell cell = null;
          ArrayList cells = new ArrayList();
          int deltaX = 0;
          int deltaY = 0;
          int maxH = 0;
          for(int i=0;i<entities.length;i++) {
            if (!entitiesAlreadyInserted.containsKey(entities[i].toString())) {
              panel = new EntityPanel(dbConnUtil, entities[i].toString());

              cell = new DefaultGraphCell(panel);
              GraphConstants.setEditable(cell.getAttributes(),false);
              GraphConstants.setBounds(cell.getAttributes(),new Rectangle2D.Double(
                  x+deltaX, y+deltaY, panel.getSize().width,
                  panel.getSize().height
              ));
  //                GraphConstants.setOpaque(cell.getAttributes(), true);
  //            GraphConstants.setBorder(cell.getAttributes(),BorderFactory.createMatteBorder(2,2,2,2,Color.red));
  //                graph.getGraphLayoutCache().insert(cell);
              cells.add(cell);
              entitiesAlreadyInserted.put(entities[i].toString(),cell);

              if (panel.getSize().height>maxH)
                maxH = panel.getSize().height;
              if (deltaX>graph.getWidth()) {
                deltaX = 0;
                deltaY = maxH+20;
              }
              else
                deltaX += panel.getSize().width+40;

            }

          }
          model.insert(cells.toArray(), null, null, null, null);
          addFKs(cells.toArray());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        ProgressDialog.getInstance().forceStopProgress();
      }
    }.start();

  }


  /**
   * Method called by LoadFileDialog class:
   * load the specified profile file.
   * @param fileName profile file name
   */
  public final void loadProfile(String fileName) {
    try {
      // remove existing schema objects...
      Object[] objs = view.getCells(true, true, true, true);
      graph.getGraphLayoutCache().remove(objs);
      EntityPanel panel = null;
      for(int i=0;i<objs.length;i++)
        if (objs[i] instanceof DefaultGraphCell) {
          panel = (EntityPanel)((DefaultGraphCell)objs[i]).getUserObject();
          if (panel!=null)
            entitiesAlreadyInserted.remove(panel.getEntityName());
        }

      // load schema objects...
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
      String line = null;
      StringTokenizer st = null;
      String tableName = null;
      double x,y,w,h;
      DefaultGraphCell cell = null;
      Object insertCells[] = null;
      while((line=br.readLine())!=null) {
        if (line.trim().length()>0) {
          st = new StringTokenizer(line,",");
          while(st.hasMoreTokens()) {
            // add an entity to the schema...
            tableName = st.nextToken();
            x = Double.parseDouble(st.nextToken());
            y = Double.parseDouble(st.nextToken());
            w = Double.parseDouble(st.nextToken());
            h = Double.parseDouble(st.nextToken());

            panel = new EntityPanel(dbConnUtil,tableName);
            cell = new DefaultGraphCell(panel);
            GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(x,y,w,h));
            insertCells = new Object[] { cell };
            model.insert(insertCells, null, null, null, null);
            entitiesAlreadyInserted.put(tableName,cell);
            addFKs(insertCells);
          }
        }
      }
      br.close();
      graph.setSelectionCells(new Object[0]);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          parent,
          ex.getMessage(),
          Options.getInstance().getResource("error on loading"),
          JOptionPane.ERROR_MESSAGE
      );
    }
  }


  private void addFKs(Object[] insertCells) {
    // determine fks...
    String tableName = null;
    TableModel fks = null;
    String fkTName = null;
    DefaultPort port0,port1;
    DefaultGraphCell node0,node1;
    DefaultEdge edge = null;
    ArrayList cells = new ArrayList();
    EntityPanel panel = null;
    for(int j=0;j<insertCells.length;j++) {
      panel = (EntityPanel)((DefaultGraphCell)insertCells[j]).getUserObject();
      tableName = panel.getEntityName();
      node0 = (DefaultGraphCell)entitiesAlreadyInserted.get(tableName);
      port0 = new DefaultPort();
      node0.add(port0);

      fks = dbConnUtil.getCrossReference(tableName);
      /*
            PKTABLE_CAT String => primary key table catalog (may be null)
            PKTABLE_SCHEM String => primary key table schema (may be null)
            PKTABLE_NAME String => primary key table name
            PKCOLUMN_NAME String => primary key column name
            FKTABLE_CAT String => foreign key table catalog (may be null) being exported (may be null)
            FKTABLE_SCHEM String => foreign key table schema (may be null) being exported (may be null)
            FKTABLE_NAME String => foreign key table name being exported
            FKCOLUMN_NAME String => foreign key column name being exported
            KEY_SEQ short => sequence number within foreign key
            UPDATE_RULE short => What happens to foreign key when primary is updated: importedNoAction - do not allow update of primary key if it has been imported importedKeyCascade - change imported key to agree with primary key update importedKeySetNull - change imported key to NULL if its primary key has been updated importedKeySetDefault - change imported key to default values if its primary key has been updated importedKeyRestrict - same as importedKeyNoAction (for ODBC 2.x compatibility)
            DELETE_RULE short => What happens to the foreign key when primary is deleted. importedKeyNoAction - do not allow delete of primary key if it has been imported importedKeyCascade - delete rows that import a deleted key importedKeySetNull - change imported key to NULL if its primary key has been deleted importedKeyRestrict - same as importedKeyNoAction (for ODBC 2.x compatibility) importedKeySetDefault - change imported key to default if its primary key has been deleted
            FK_NAME String => foreign key name (may be null)
            PK_NAME String => primary key name (may be null)
            DEFERRABILITY short => can the evaluation of foreign key constraints be deferred until commit importedKeyInitiallyDeferred - see SQL92 for definition importedKeyInitiallyImmediate - see SQL92 for definition importedKeyNotDeferrable - see SQL92 for definition
      */

      for(int i=0;i<fks.getRowCount();i++) {
        fkTName = fks.getValueAt(i,2).toString();
        if (entitiesAlreadyInserted.containsKey(fkTName)) {
          // add an edge...

          node1 = (DefaultGraphCell)entitiesAlreadyInserted.get(fkTName);
          port1 = new DefaultPort();
          node1.add(port1);

          edge = new DefaultEdge();
          edge.setSource(port0);
          edge.setTarget(port1);

//          edge.setSource(node0.getChildAt(0));
//          edge.setTarget(node1.getChildAt(0));

//          GraphConstants.setLineBegin(edge.getAttributes(), GraphConstants.ARROW_DIAMOND);
          GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_SIMPLE);
//          GraphConstants.setEndFill(edge.getAttributes(), true);
          cells.add(edge);
        }
      }
      graph.getGraphLayoutCache().insert(cells.toArray());

    }
  }


  public void updateLists() {
    if (tableTabbedPane.getSelectedIndex()==0) {
      new Thread() {
        public void run() {
          loadTables();
          loadViews();
          loadSyns();
          tablesList.requestFocus();
        }
      }.start();
    }
    else if (tableTabbedPane.getSelectedIndex()==1) {
      new Thread() {
        public void run() {
          loadViews();
          loadTables();
          loadSyns();
          viewList.requestFocus();
        }
      }.start();
    }
    else if (tableTabbedPane.getSelectedIndex()==2) {
      new Thread() {
        public void run() {
          loadSyns();
          loadTables();
          loadViews();
          sinList.requestFocus();
        }
      }.start();
    }

  }


  /**
   * Load tables.
   */
  private void loadTables() {
    java.util.List tables = dbConnUtil.getTables(catComboBox.getSelectedItem().toString(),"TABLE");
    DefaultListModel model = new DefaultListModel();
    String name = null;
    boolean ok = true;
    for(int i=0;i<tables.size();i++) {
      name = tables.get(i).toString();
      ok = true;
      for(int j=0;j<tableFilterListPanel.getFilterPattern().length();j++)
        if (name.indexOf(tableFilterListPanel.getFilterPattern().charAt(j))!=-1) {
          ok = false;
          break;
        }
      if (ok)
        model.addElement(name);
    }
    tablesList.setModel(model);
    tablesList.revalidate();
    tablesList.requestFocus();
  }


  /**
   * Load views.
   */
  private void loadViews() {
    java.util.List views = dbConnUtil.getTables(catComboBox.getSelectedItem().toString(),"VIEW");
    DefaultListModel model = new DefaultListModel();
    String name = null;
    boolean ok = true;
    for(int i=0;i<views.size();i++) {
      name = views.get(i).toString();
      ok = true;
      for(int j=0;j<viewFilterListPanel.getFilterPattern().length();j++)
        if (name.indexOf(viewFilterListPanel.getFilterPattern().charAt(j))!=-1) {
          ok = false;
          break;
        }
      if (ok)
        model.addElement(name);
    }
    viewList.setModel(model);
    viewList.revalidate();
    viewList.requestFocus();
  }


  /**
   * Load synonyms.
   */
  private void loadSyns() {
    java.util.List sin = dbConnUtil.getTables(catComboBox.getSelectedItem().toString(),"SYNONYM");
    DefaultListModel model = new DefaultListModel();
    String name = null;
    boolean ok = true;
    for(int i=0;i<sin.size();i++) {
      name = sin.get(i).toString();
      ok = true;
      for(int j=0;j<synFilterListPanel.getFilterPattern().length();j++)
        if (name.indexOf(synFilterListPanel.getFilterPattern().charAt(j))!=-1) {
          ok = false;
          break;
        }
      if (ok)
        model.addElement(name);
    }
    sinList.setModel(model);
    sinList.revalidate();
    sinList.requestFocus();
  }


  void loadButton_actionPerformed(ActionEvent e) {
    LoadFileDialog d = new LoadFileDialog(parent,this,dbConnUtil.getDbConnection().getName());
  }


  void saveButton_actionPerformed(ActionEvent e) {
    // view an input dialog to specifiy a profile file name...
    String fileName = JOptionPane.showInputDialog(
        parent,
        Options.getInstance().getResource("please specify schema profile file name: "),
        Options.getInstance().getResource("save database schema"),
        JOptionPane.QUESTION_MESSAGE
    );
    if (fileName==null || fileName.trim().length()==0) {
      return;
    }
    // create the profile file...
    fileName = fileName.trim().replace(' ','_');

    File fileToSave = new File("profile/"+dbConnUtil.getDbConnection().getName()+"_"+fileName+".sch");
    if (fileToSave.exists()) {
      int answer = JOptionPane.showConfirmDialog(
          parent,
          Options.getInstance().getResource("the specified file already exists.\noverwrite it?"),
          Options.getInstance().getResource("save not allowed"),
          JOptionPane.YES_NO_OPTION
      );
      if (answer==JOptionPane.NO_OPTION)
        return;
      else
        fileToSave.delete();
    }

    try {
      Object[] objs = view.getCells(false, true, false, false);

      // save database schema in the profile file...
      PrintWriter pw = new PrintWriter(new FileOutputStream(fileToSave));

      EntityPanel panel = null;
      DefaultGraphCell cell = null;
      Rectangle2D rect = null;
      for(int j=0;j<objs.length;j++) {
        cell = (DefaultGraphCell)objs[j];
        panel = (EntityPanel)cell.getUserObject();
        rect = GraphConstants.getBounds(cell.getAttributes());
        pw.println(panel.getEntityName()+","+rect.getX()+","+rect.getY()+","+rect.getWidth()+","+rect.getHeight());
      }

      pw.close();
    }
    catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
          parent,
          ex.getMessage(),
          Options.getInstance().getResource("error on saving"),
          JOptionPane.ERROR_MESSAGE
      );
    }

  }



  void expButton_actionPerformed(ActionEvent e) {
    final JFileChooser f = new JFileChooser();
    int res = f.showSaveDialog(parent);
    if (res==f.CANCEL_OPTION || f.getSelectedFile()==null)
      return;

    try {
      ProgressDialog.getInstance().startProgress();
    }
    catch (Throwable ex5) {
    }
    new Thread() {
      public void run() {

        try {
          f.getSelectedFile().delete();

          int w = graph.getPreferredSize().width;
          int h = graph.getPreferredSize().height;
          Document document = new Document();
          document.setPageSize(new com.lowagie.text.Rectangle(0,0,w+30,h+30));
          PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(f.getSelectedFile()));
          document.open();
          DefaultFontMapper mapper = new DefaultFontMapper();
          FontFactory.registerDirectories();
          PdfContentByte cb = writer.getDirectContent();
          PdfTemplate tp = cb.createTemplate(w, h);
          Graphics2D g2 = tp.createGraphics(w, h, mapper);
          tp.setWidth(w);
          tp.setHeight(h);
          graph.paint(g2);

          g2.dispose();
          cb.addTemplate(tp, 0, 30);
          document.close();


        }
        catch (Throwable ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(
              parent,
              Options.getInstance().getResource("error while exporting schema diagram")+":\n"+(ex.getMessage()==null?ex.toString():ex.getMessage()),
              Options.getInstance().getResource("error"),
              JOptionPane.ERROR_MESSAGE
          );
        }
        try {
          ProgressDialog.getInstance().stopProgress();
        }
        catch (Throwable ex5) {
        }

      }
    }.start();

  }

  void printFitButton_actionPerformed(ActionEvent e) {
    new Thread() {
      public void run() {
        print(true);
      }
    }.start();

  }

  void printButton_actionPerformed(ActionEvent e) {
    new Thread() {
      public void run() {
        print(false);
      }
    }.start();
  }


  private void print(final boolean fitToPage) {
    graph.setSelectionCells(new Object[0]);

    Printable printable = new Printable() {

      public int print(Graphics g, PageFormat printFormat, int page) {
        if (!fitToPage) {
          // print all pages...
          double pageScale = 1.0d;
          Dimension pSize = graph.getPreferredSize(); // graph is a JGraph
          int w = (int) (printFormat.getWidth() * pageScale);
          int h = (int) (printFormat.getHeight() * pageScale);
          int cols = (int) Math.max(Math.ceil((double) (pSize.width - 5) / (double) w), 1);
          int rows = (int) Math.max(Math.ceil((double) (pSize.height - 5) / (double) h), 1);
          if (page < cols * rows) {
            // Configures graph for printing
            RepaintManager currentManager = RepaintManager.currentManager(graph);
            currentManager.setDoubleBufferingEnabled(false);
            double oldScale = graph.getScale();
            graph.setScale(1 / pageScale);
            int dx = (int) ((page % cols) * printFormat.getWidth());
            int dy = (int) ((page % rows) * printFormat.getHeight());
            g.translate(-dx, -dy);
            g.setClip(dx, dy, (int) (dx + printFormat.getWidth()),
            (int) (dy + printFormat.getHeight()));
            // Prints the graph on the graphics.
            graph.paint(g);
            // Restores graph
            g.translate(dx, dy);
            graph.setScale(oldScale);
            currentManager.setDoubleBufferingEnabled(true);
            return PAGE_EXISTS;
          } else {
            return NO_SUCH_PAGE;
          }
        }
        else {
          if (page>0)
            return NO_SUCH_PAGE;
            Dimension pSize = graph.getPreferredSize(); // graph is a JGraph
            RepaintManager currentManager = RepaintManager.currentManager(graph);
            currentManager.setDoubleBufferingEnabled(false);
            double oldScale = graph.getScale();
            double w = printFormat.getWidth();
            graph.setScale(w/(pSize.width+50));

            g.setClip(0,0, graph.getPreferredSize().width, graph.getPreferredSize().height);
            graph.paint(g);
            graph.setScale(oldScale);
            currentManager.setDoubleBufferingEnabled(true);
            return PAGE_EXISTS;
        }
      }

    };

    PrinterJob printJob = PrinterJob.getPrinterJob();
    printJob.setPrintable(printable);
    if (printJob.printDialog()) {
      try {
        // start progress bar...
        try {
          ProgressDialog.getInstance().startProgress();
        }
        catch (Throwable ex5) {
        }
        printJob.print();
      }
      catch (PrinterException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(
            this,
            Options.getInstance().getResource("error while printing schema")+":\n"+ex.getMessage(),
            Options.getInstance().getResource("error"),
            JOptionPane.ERROR_MESSAGE
        );
      }
    }

    // start progress bar...
    try {
      ProgressDialog.getInstance().stopProgress();
    }
    catch (Throwable ex5) {
    }

  }


  private void jbInit() throws Exception {
    toolbarPanel.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);

    loadImage = ImageLoader.getInstance().getIcon("load.gif");
    loadButton.setBorder(null);
    loadButton.setMaximumSize(new Dimension(24,24));
    loadButton.setPreferredSize(new Dimension(24, 24));
    loadButton.setIcon(loadImage);
    loadButton.addActionListener(new SchemaFrame_loadButton_actionAdapter(this));
    loadButton.setToolTipText(Options.getInstance().getResource("loadbutton.tooltip"));
    zoomComboBox.addItemListener(new SchemaFrame_zoomComboBox_itemAdapter(this));
    toolbarPanel.add(loadButton,null);

    saveImage = ImageLoader.getInstance().getIcon("save.gif");
    saveButton.setBorder(null);
    saveButton.setMaximumSize(new Dimension(24,24));
    saveButton.setPreferredSize(new Dimension(24, 24));
    saveButton.setIcon(saveImage);
    saveButton.addActionListener(new SchemaFrame_saveButton_actionAdapter(this));
    saveButton.setToolTipText(Options.getInstance().getResource("savebutton.tooltip"));
    toolbarPanel.add(saveButton,null);

    printImage = ImageLoader.getInstance().getIcon("print.gif");
    printButton.setBorder(null);
    printButton.setMaximumSize(new Dimension(24,24));
    printButton.setPreferredSize(new Dimension(24, 24));
    printButton.setIcon(printImage);
    printButton.addActionListener(new SchemaFrame_printButton_actionAdapter(this));
    printButton.setToolTipText(Options.getInstance().getResource("printbutton.tooltip"));
    toolbarPanel.add(printButton,null);

    printFitImage = ImageLoader.getInstance().getIcon("printfit.gif");
    printFitButton.setBorder(null);
    printFitButton.setMaximumSize(new Dimension(24,24));
    printFitButton.setPreferredSize(new Dimension(24, 24));
    printFitButton.setIcon(printFitImage);
    printFitButton.addActionListener(new SchemaFrame_printFitButton_actionAdapter(this));
    printFitButton.setToolTipText(Options.getInstance().getResource("printfitbutton.tooltip"));
    toolbarPanel.add(printFitButton,null);

    expImage = ImageLoader.getInstance().getIcon("export.gif");
    expButton.setBorder(null);
    expButton.setMaximumSize(new Dimension(24,24));
    expButton.setPreferredSize(new Dimension(24, 24));
    expButton.setIcon(expImage);
    expButton.addActionListener(new SchemaFrame_expButton_actionAdapter(this));
    expButton.setToolTipText(Options.getInstance().getResource("expbutton.tooltip"));
    toolbarPanel.add(expButton,null);
    toolbarPanel.add(zoomComboBox, null);

    mainPanel.setLayout(borderLayout1);
    splitPane.setDebugGraphicsOptions(0);
    tableScrollPane.setToolTipText(Options.getInstance().getResource("tables list"));
    viewScrollPane.setRowHeader(null);
    viewScrollPane.setToolTipText(Options.getInstance().getResource("views list"));
    sinScrollPane.setToolTipText(Options.getInstance().getResource("synonyms list"));
    tablesPanel.setLayout(borderLayout2);
    catLabel.setText(Options.getInstance().getResource("catalog"));
    catPanel.setLayout(gridBagLayout1);

    tablesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    viewList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    sinList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    this.getContentPane().add(mainPanel, BorderLayout.CENTER);
    mainPanel.add(splitPane,  BorderLayout.CENTER);
    splitPane.add(schemaPanel, JSplitPane.RIGHT);
    schemaPanel.setLayout(borderLayout6);
    schemaPanel.add(toolbarPanel,BorderLayout.NORTH);
    schemaPanel.add(schemaScrollPane,BorderLayout.CENTER);
    schemaScrollPane.getViewport().add(graph,null);
    splitPane.add(tablesPanel, JSplitPane.LEFT);
    tablePanel.setLayout(borderLayout3);
    viewPanel.setLayout(borderLayout4);
    synPanel.setLayout(borderLayout5);
    tablePanel.add(tableFilterListPanel,BorderLayout.NORTH);
    tablePanel.add(tableScrollPane,BorderLayout.CENTER);
    viewPanel.add(viewFilterListPanel,BorderLayout.NORTH);
    viewPanel.add(viewScrollPane,BorderLayout.CENTER);
    synPanel.add(synFilterListPanel,BorderLayout.NORTH);
    synPanel.add(sinScrollPane,BorderLayout.CENTER);

    tableTabbedPane.add(tablePanel,  "tablePanel");
    tableTabbedPane.add(viewPanel,  "viewPanel");
    tableTabbedPane.add(synPanel,   "synPanel");
    tablesPanel.add(catPanel, BorderLayout.NORTH);
    tableScrollPane.getViewport().add(tablesList, null);
    viewScrollPane.getViewport().add(viewList, null);
    sinScrollPane.getViewport().add(sinList, null);
    splitPane.setDividerLocation(200);
    tablesPanel.add(tableTabbedPane, BorderLayout.CENTER);
    catPanel.add(catLabel,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    catPanel.add(catComboBox,   new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
  }


  public DbConnectionUtil getDbConnectionUtil() {
    return dbConnUtil;
  }

  void catComboBox_itemStateChanged(ItemEvent e) {
    if (e.getStateChange()==e.SELECTED) {
      if (catComboBox.getSelectedItem()!=null && !catComboBox.getSelectedItem().equals(""))
        schemaName = catComboBox.getSelectedItem()+".";
      else
        schemaName = "";

      new Thread() {
        public void run() {
          ProgressDialog.getInstance().startProgress();
          try {
            // update lists...
            updateLists();
          }
          catch (Throwable ex) {
          }
          finally {
            ProgressDialog.getInstance().stopProgress();
          }
        }
      }.start();
    }
  }



/**
 * <p>Description: Inner class which manages filter events on tables.</p>
 */
  class TableFilterController implements FilterListController {

    /**
     * Reload the list, which will be filtered by the specified pattern
     */
    public void reloadList() {
      loadTables();
    }

  }


/**
 * <p>Description: Inner class which manages filter events on views.</p>
 */
  class ViewFilterController implements FilterListController {

    /**
     * Reload the list, which will be filtered by the specified pattern
     */
    public void reloadList() {
      loadViews();
    }

  }


/**
 * <p>Description: Inner class which manages filter events on synonyms.</p>
 */
  class SynFilterController implements FilterListController {

    /**
     * Reload the list, which will be filtered by the specified pattern
     */
    public void reloadList() {
      loadSyns();
    }


  }


  void zoomComboBox_itemStateChanged(ItemEvent e) {
    if (e.getStateChange()==e.SELECTED) {
      double zoom = 1;
      if (zoomComboBox.getSelectedItem().equals(ZOOM200))
        zoom = 2;
      else if (zoomComboBox.getSelectedItem().equals(ZOOM150))
        zoom = 1.5;
      else if (zoomComboBox.getSelectedItem().equals(ZOOM100))
        zoom = 1;
      else if (zoomComboBox.getSelectedItem().equals(ZOOM75))
        zoom = 0.75;
      else if (zoomComboBox.getSelectedItem().equals(ZOOM50))
        zoom = 0.5;
      else if (zoomComboBox.getSelectedItem().equals(ZOOM25))
        zoom = 0.25;
      else if (zoomComboBox.getSelectedItem().equals(ZOOM10))
        zoom = 0.1;

      if (graph.getScale()!=zoom)
        graph.setScale(zoom);
    }
  }

















  /********************************************************************
   *
   *             DRAG 'N DROP MANAGEMENTS METHODS
   *
   ********************************************************************/



  /************************************************************
   * DRAG MANAGEMENT
   ************************************************************/


  class DragGestureAdapter implements DragGestureListener {

    private DragSourceListener dragListener = null;

    public DragGestureAdapter(DragSourceListener dragListener) {
      this.dragListener = dragListener;
    }


    /**
     * A drag gesture has been initiated.
     */
    public final void dragGestureRecognized( DragGestureEvent event) {
      if (tableTabbedPane.getSelectedIndex()==0 &&
          tablesList.getSelectedIndices().length>0) {
        try {
          parent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        catch (Exception ex) {
        }
        dragSource1.startDrag (event, DragSource.DefaultMoveDrop, new StringSelection(""), dragListener);
      }
      else if (tableTabbedPane.getSelectedIndex()==1 &&
          viewList.getSelectedIndices().length>0) {
        try {
          parent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        catch (Exception ex) {
        }
        dragSource2.startDrag (event, DragSource.DefaultMoveDrop, new StringSelection(""), dragListener);
      }
      else if (tableTabbedPane.getSelectedIndex()==2 &&
          sinList.getSelectedIndices().length>0) {
        try {
          parent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        catch (Exception ex) {
        }
        dragSource3.startDrag (event, DragSource.DefaultMoveDrop, new StringSelection(""), dragListener);
      }
    }

  } // end inner-class...


  /**
   * This message goes to DragSourceListener, informing it that the dragging has entered the DropSite
   */
  public final void dragEnter (DragSourceDragEvent event) {
  }


  /**
   * This message goes to DragSourceListener, informing it that the dragging has exited the DropSite.
   */
  public final void dragExit (DragSourceEvent event) {
  }


  /**
   * This message goes to DragSourceListener, informing it that the dragging is currently ocurring over the DropSite.
   */
  public final void dragOver (DragSourceDragEvent event) {
 }


  /**
   * This method is invoked when the user changes the dropAction.
   */
  public final void dropActionChanged ( DragSourceDragEvent event) { }


  /**
   * This message goes to DragSourceListener, informing it that the dragging has ended.
   */
  public final void dragDropEnd (DragSourceDropEvent event) {
  }



  /************************************************************
   * DROP MANAGEMENT
   ************************************************************/

  /**
   * This method is invoked when you are dragging over the DropSite.
   */
  public final void dragEnter (DropTargetDragEvent event) {
    event.acceptDrag (DnDConstants.ACTION_MOVE);
  }


  /**
   * This method is invoked when you are exit the DropSite without dropping.
   */
  public final void dragExit (DropTargetEvent event) {
  }

  /**
   * This method is invoked when a drag operation is going on.
   */
  public final void dragOver (DropTargetDragEvent event) {
  }


  /**
   * This method is invoked when a drop event has occurred.
   */
  public final void drop(DropTargetDropEvent event) {
    try {
      Transferable transferable = event.getTransferable();
      if (transferable.isDataFlavorSupported (DataFlavor.stringFlavor)){
      event.acceptDrop(DnDConstants.ACTION_MOVE);
      event.getDropTargetContext().dropComplete(true);

      // add entities to the graph...
      Object[] entities = null;
      if (tableTabbedPane.getSelectedIndex()==0)
        entities = tablesList.getSelectedValues();
      else if (tableTabbedPane.getSelectedIndex()==1)
        entities = viewList.getSelectedValues();
      else if (tableTabbedPane.getSelectedIndex()==2)
        entities = sinList.getSelectedValues();

      addEntities(entities,event.getLocation().x,event.getLocation().y);

      } else{
        event.rejectDrop();
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      event.rejectDrop();
    } finally {
      try {
        parent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      }
      catch (Exception ex) {
      }

    }
  }

  /**
   * This method is invoked if the use modifies the current drop gesture.
   */
  public final void dropActionChanged ( DropTargetDragEvent event ) {
  }





}

class SchemaFrame_catComboBox_itemAdapter implements java.awt.event.ItemListener {
  SchemaFrame adaptee;

  SchemaFrame_catComboBox_itemAdapter(SchemaFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void itemStateChanged(ItemEvent e) {
    adaptee.catComboBox_itemStateChanged(e);
  }
}

class SchemaFrame_printButton_actionAdapter implements java.awt.event.ActionListener {
  SchemaFrame adaptee;

  SchemaFrame_printButton_actionAdapter(SchemaFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.printButton_actionPerformed(e);
  }
}

class SchemaFrame_printFitButton_actionAdapter implements java.awt.event.ActionListener {
  SchemaFrame adaptee;

  SchemaFrame_printFitButton_actionAdapter(SchemaFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.printFitButton_actionPerformed(e);
  }
}

class SchemaFrame_loadButton_actionAdapter implements java.awt.event.ActionListener {
  SchemaFrame adaptee;

  SchemaFrame_loadButton_actionAdapter(SchemaFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.loadButton_actionPerformed(e);
  }
}

class SchemaFrame_saveButton_actionAdapter implements java.awt.event.ActionListener {
  SchemaFrame adaptee;

  SchemaFrame_saveButton_actionAdapter(SchemaFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.saveButton_actionPerformed(e);
  }
}

class SchemaFrame_expButton_actionAdapter implements java.awt.event.ActionListener {
  SchemaFrame adaptee;

  SchemaFrame_expButton_actionAdapter(SchemaFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.expButton_actionPerformed(e);
  }
}

class SchemaFrame_zoomComboBox_itemAdapter implements java.awt.event.ItemListener {
  SchemaFrame adaptee;

  SchemaFrame_zoomComboBox_itemAdapter(SchemaFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void itemStateChanged(ItemEvent e) {
    adaptee.zoomComboBox_itemStateChanged(e);
  }
}



