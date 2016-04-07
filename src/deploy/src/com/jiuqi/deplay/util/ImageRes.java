package com.jiuqi.deplay.util;

import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

public class ImageRes {

	public static final String PLUGIN_PATH = "/com/jiuqi/deplay";

	public static final String ICO_DEPLAY = "/images/deplay.ico";
	public static final String PNG_CLOSE = "/images/close.png";
	public static final String PNG_EXIT = "/images/exit.png";
	public static final String PNG_BIRD = "/images/bird.png";
	public static final String PNG_BIRD72 = "/images/bird72.png";
	public static final String PNG_CONNECT = "/images/connect.png";
	public static final String PNG_SAVE = "/images/save.png";
	public static final String PNG_ADVANCED = "/images/advanced.png";

	static public Image getImage(String imagename) {
		return Toolkit.getDefaultToolkit().getImage(ImageRes.class.getResource(PLUGIN_PATH + imagename));
	}

	static public ImageIcon getIcon(String iconname) {
		return new ImageIcon(ImageRes.class.getResource(PLUGIN_PATH + iconname));
	}
}
