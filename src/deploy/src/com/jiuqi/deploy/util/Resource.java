/*
 * @(#)Resource.java  
 */
package com.jiuqi.deploy.util;

import java.awt.Image;
import java.awt.Toolkit;

/**
 * @author: zenglizhi
 * @time: 2017Äê6ÔÂ30ÈÕ
 */
public class Resource {
	
	public static final String PLUGIN_PATH = "/resource";
	
	public static final String SQL_RESOURCE = "/sql";

	
	
	static public Image getImage(String imagename) {
		return Toolkit.getDefaultToolkit().getImage(Resource.class.getResource(PLUGIN_PATH + imagename));
	}
	
}
