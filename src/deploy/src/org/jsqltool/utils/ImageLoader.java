package org.jsqltool.utils;

import java.awt.Image;
import javax.swing.ImageIcon;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: This singleton class can be used to load an icon image from file system.
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
public class ImageLoader {

  /** unique istance of the class */
  private static ImageLoader imgLoader = null;


  public static ImageLoader getInstance() {
    if (imgLoader==null)
      imgLoader = new ImageLoader();
    return imgLoader;
  }


  /**
   * @param imageName image file name
   * @return image icon object
   */
  public final ImageIcon getIcon(String imageName) {
    // org.jsqltool.MainApp.class.getResourceAsStream(...)
    return new ImageIcon( imgLoader.getClass().getClassLoader().getResource("images/"+imageName) );
  }



}