package org.jsqltool.jgraph.gui;

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
import java.util.ArrayList;
import java.awt.geom.Point2D;
import org.jgraph.graph.*;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: Vertex View, used by JGraph to represent an entity (table/view/synonymn).
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
public class EntityVertexView  extends VertexView {

  private DefaultGraphCell data = null;

  private EntityPanel node = null;

  private VertexRenderer renderer = new VertexRenderer() {
    public Component getRendererComponent(JGraph graph, CellView view, boolean sel, boolean focus, boolean preview) {
      gridColor = graph.getGridColor();
      highlightColor = graph.getHighlightColor();
      lockedHandleColor = graph.getLockedHandleColor();
      isDoubleBuffered = graph.isDoubleBuffered();
      hasFocus = focus;
      childrenSelected = graph.getSelectionModel().isChildrenSelected(view.getCell());
      selected = sel;
      this.preview = preview;
      if(EntityVertexView.this.isLeaf() || GraphConstants.isGroupOpaque(view.getAllAttributes()))
          installAttributes(view);
      else
          resetAttributes();

      node.setSize(200,200);
      return node;
    }

  };


  public CellViewRenderer getRenderer()
  {
      return renderer;
  }


  public Point2D getPerimeterPoint(EdgeView edge, Point2D source,Point2D p) {
    return ((VertexRenderer)renderer).getPerimeterPoint(this,source, p);
  }


  /**
   * Costructor called by  SchemaFrame class to create an "entity node".
   */
  public EntityVertexView(DefaultGraphCell data) {
    this.data = data;
    this.node = (EntityPanel)data.getUserObject();
    cell = null;
    parent = null;
    childViews = new ArrayList(0);
    allAttributes = createAttributeMap();
    attributes = allAttributes;
    groupBounds = VertexView.defaultBounds;
    setCell(data);
  }



}
