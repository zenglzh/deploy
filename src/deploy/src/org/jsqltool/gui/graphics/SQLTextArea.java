package org.jsqltool.gui.graphics;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.Hashtable;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * <p>
 * Title: JSqlTool Project
 * </p>
 * <p>
 * Description: Scroll pane that contains a SQL text pane.
 * </p>
 * <p>
 * Copyright: Copyright (C) 2006 Mauro Carniel
 * </p>
 *
 * <p>
 * This file is part of JSqlTool project. This library is free software; you can
 * redistribute it and/or modify it under the terms of the (LGPL) Lesser General
 * Public License as published by the Free Software Foundation;
 *
 * GNU LESSER GENERAL PUBLIC LICENSE Version 2.1, February 1999
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * The author may be contacted at: maurocarniel@tin.it
 * </p>
 *
 * @author Mauro Carniel
 * @version 1.0
 */

public class SQLTextArea extends JScrollPane implements DocumentListener {

	/** contains SQL text */
	private JTextPane editor = new JTextPane();

	/** thread used to color the text according to the SQL syntax */
	Thread t = new ColoredThread();

	/** lock variable used by the colored thread */
	Object lock = new Object();

	/** flag used inside the colored thread */
	private boolean isAlive = false;

	/**
	 * hash table containing the text styles. Simple attribute sets are hashed
	 * by name (String)
	 */
	private Hashtable styles = new Hashtable();

	/** char pos to use to start text analysys */
	private int startpos = 0;

	public SQLTextArea() {
		try {
			this.getViewport().add(editor, null);
			initStyles();
			t.start();

			editor.getDocument().addDocumentListener(this);
			Document doc = editor.getDocument();
			doc.insertString(startpos, "", getStyle("text"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public final void setText(String text) {
		editor.setText(text);
	}

	public final String getText() {
		return editor.getText();
	}

	public void requestFocus() {
		editor.requestFocus();
	}

	public final void setCaretPosition(int caretPosition) {
		editor.setCaretPosition(caretPosition);
	}

	public final int getCaretPosition() {
		return editor.getCaretPosition();
	}

	public final String getSelectedText() {
		return editor.getSelectedText();
	}

	public final Rectangle modelToView(int pos) throws BadLocationException {
		return editor.modelToView(pos);
	}

	/**
	 * @param pos
	 *            char index currently added/deleted
	 * @return char pos to use to start analysys
	 */
	private int getStartPos(int pos) {
		try {
			for (int i = pos - 1; i > 0; i--) {
				if (i < 0)
					return 0;
				else if (editor.getText().charAt(i) == ' ' || editor.getText().charAt(i) == '\t' || editor.getText().charAt(i) == '\n') {
					return i;
				}
			}
		} catch (Exception ex) {
			return 0;
		}
		return 0;
	}

	/**
	 * Gives notification that there was an insert into the document. The range
	 * given by the DocumentEvent bounds the freshly inserted region.
	 *
	 * @param e
	 *            the document event
	 */
	public void insertUpdate(DocumentEvent e) {
		try {
			synchronized (lock) {
				if (isAlive)
					return;
			}
			startpos = getStartPos(e.getOffset());
			t.interrupt();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Gives notification that a portion of the document has been removed. The
	 * range is given in terms of what the view last saw (that is, before
	 * updating sticky positions).
	 *
	 * @param e
	 *            the document event
	 */
	public void removeUpdate(DocumentEvent e) {
		try {
			synchronized (lock) {
				if (isAlive)
					return;
			}
			startpos = getStartPos(e.getOffset());
			t.interrupt();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Gives notification that an attribute or set of attributes changed.
	 *
	 * @param e
	 *            the document event
	 */
	public void changedUpdate(DocumentEvent e) {

	}

	/**
	 * retrieve the style for the given type of text.
	 *
	 * @param styleName
	 *            the label for the type of text ("tag" for example) or null if
	 *            the styleName is not known.
	 * @return the style
	 */
	private SimpleAttributeSet getStyle(String styleName) {
		return ((SimpleAttributeSet) styles.get(styleName));
	}

	/**
	 * Create the styles and place them in the hash table.
	 */
	private void initStyles() {
		SimpleAttributeSet style;

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, 12);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, Color.black);
		StyleConstants.setBold(style, false);
		StyleConstants.setItalic(style, false);
		styles.put("text", style);

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, 12);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, Color.blue);
		StyleConstants.setBold(style, false);
		StyleConstants.setItalic(style, false);
		styles.put("reservedWord", style);

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, 12);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, Color.red);
		StyleConstants.setBold(style, false);
		StyleConstants.setItalic(style, false);
		styles.put("literal", style);

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, 12);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, Color.red);
		StyleConstants.setBold(style, false);
		StyleConstants.setItalic(style, false);
		styles.put("type", style);

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, 12);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, Color.blue);
		StyleConstants.setBold(style, false);
		StyleConstants.setItalic(style, false);
		styles.put("operator", style);

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, 12);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, Color.green.darker());
		StyleConstants.setBold(style, false);
		StyleConstants.setItalic(style, false);
		styles.put("comment", style);

	}

	/**
	 * Method called by document listener to update text color, according to SQL
	 * syntax.
	 */
	private synchronized void highlight() {
		// wait for lock and change isAlive flag state...
		synchronized (lock) {
			isAlive = true;
		}
		// now document listener cannot fires events...

		try {
			Document doc = editor.getDocument();
			String text = doc.getText(startpos, doc.getLength() - startpos);
			doc.remove(startpos, text.length());
			doc.insertString(startpos, text, getStyle("text"));
			text = doc.getText(0, doc.getLength()).toUpperCase();
			int pos;
			int endpos;

			// search for patterns...
			String pattern = null;
			String[] patterns = new String[] { "SELECT", "FROM", "WHERE", "ORDER BY", "GROUP BY", "HAVING", "IS", "NOT", "NULL", "CREATE", "ALTER", "DROP", "ADD", "TABLE", "INDEX", "FOREIGN", "KEY",
					"REFERENCES", "CONSTRAINT", "PRIMARY", "ON", "INTO", "UNIQUE" };
			for (int i = 0; i < patterns.length; i++) {
				pos = 0;
				pattern = patterns[i];
				while ((pos = text.indexOf(pattern, pos)) >= 0) {
					if ((pos == 0 || pos > 0 && (text.charAt(pos - 1) == ' ' || text.charAt(pos - 1) == '\t' || text.charAt(pos - 1) == '\n' || text.charAt(pos - 1) == '('))
							&& (pos + pattern.length() == text.length() || pos + pattern.length() < text.length()
									&& (text.charAt(pos + pattern.length()) == ' ' || text.charAt(pos + pattern.length()) == '\t' || text.charAt(pos + pattern.length()) == '\n' || text.charAt(pos
											+ pattern.length()) == '('))) {
						editor.getDocument().remove(pos, pattern.length());
						editor.getDocument().insertString(pos, pattern, getStyle("reservedWord"));
					}
					pos += pattern.length();
				}
			}

			// search for types...
			patterns = new String[] { "NUMBER", "INTEGER", "INT", "DOUBLE", "DECIMAL", "NUMERIC", "VARCHAR", "VARCHAR2", "CHAR", "DATE", "TIMESTAMP", "DATETIME", "BOOLEAN", "LONG", "BLOB", "CLOB",
					"LONGRAW", "REAL", "FLOAT", "LONGVARCHAR", "SMALLINT", "LONGVARBINARY", "BIGINT" };
			for (int i = 0; i < patterns.length; i++) {
				pos = 0;
				pattern = patterns[i];
				while ((pos = text.indexOf(pattern, pos)) >= 0) {
					if ((pos == 0 || pos > 0 && (text.charAt(pos - 1) == ' ' || text.charAt(pos - 1) == '\t' || text.charAt(pos - 1) == '\n' || text.charAt(pos - 1) == '('))
							&& (pos + pattern.length() == text.length() || pos + pattern.length() < text.length()
									&& (text.charAt(pos + pattern.length()) == ' ' || text.charAt(pos + pattern.length()) == '\t' || text.charAt(pos + pattern.length()) == '\n' || text.charAt(pos
											+ pattern.length()) == '('))) {
						editor.getDocument().remove(pos, pattern.length());
						editor.getDocument().insertString(pos, pattern, getStyle("type"));
					}
					pos += pattern.length();
				}
			}

			// search for operators...
			patterns = new String[] { "||", "+", "-", "*", "/", "(+)" };
			for (int i = 0; i < patterns.length; i++) {
				pos = startpos;
				pattern = patterns[i];
				while ((pos = text.indexOf(pattern, pos)) >= 0) {
					editor.getDocument().remove(pos, pattern.length());
					editor.getDocument().insertString(pos, pattern, getStyle("operator"));
					pos += pattern.length();
				}
			}

			// find out literals...
			pos = startpos;
			while ((pos = text.indexOf("'", pos)) >= 0) {
				endpos = text.indexOf("'", pos + 1);
				if (endpos == -1)
					endpos = text.length() - 1;
				editor.getDocument().remove(pos, endpos - pos + 1);
				editor.getDocument().insertString(pos, text.substring(pos, endpos + 1), getStyle("literal"));
				pos = endpos + 1;
			}

			// find out comments...
			pos = startpos;
			while ((pos = text.indexOf("--", pos)) >= 0) {
				endpos = text.indexOf("\n", pos);
				if (endpos == -1)
					endpos = text.length() - 1;
				editor.getDocument().remove(pos, endpos - pos + 1);
				editor.getDocument().insertString(pos, text.substring(pos, endpos + 1), getStyle("comment"));
				pos = endpos + 1;
			}

			pos = startpos;
			while ((pos = text.indexOf("/*", pos)) >= 0) {
				endpos = text.indexOf("*/", pos);
				if (endpos == -1)
					endpos = text.length() - 2;
				editor.getDocument().remove(pos, endpos - pos + 2);
				editor.getDocument().insertString(pos, text.substring(pos, endpos + 2), getStyle("comment"));
				pos += endpos + 1;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// wait for lock and change isAlive flag state...
		synchronized (lock) {
			isAlive = false;
		}
		// now document listener can fires events...

	}

	/**
	 * <p>
	 * Description: Thread used to color the text.
	 * </p>
	 */
	class ColoredThread extends Thread {

		public void run() {
			// the thread never ends: it suspends until document listener
			// interrupt it...
			while (true) {
				// analyze the content...
				highlight();
				try {
					sleep(0xffffff);
				} catch (InterruptedException x) {
				}
			}
		}

	}

	public final void setEditable(boolean editable) {
		editor.setEditable(editable);
	}

	public void addFocusListener(FocusListener listener) {
		if (editor != null)
			editor.addFocusListener(listener);
	}

	public void addMouseListener(MouseListener listener) {
		if (editor != null)
			editor.addMouseListener(listener);
	}

	public void addKeyListener(KeyListener listener) {
		if (editor != null)
			editor.addKeyListener(listener);
	}

}