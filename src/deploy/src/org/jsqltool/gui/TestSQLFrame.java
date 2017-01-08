package org.jsqltool.gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.util.*;
import java.io.Reader;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class TestSQLFrame extends JFrame implements DocumentListener {

  JScrollPane jScrollPane1 = new JScrollPane();
  JScrollPane jScrollPane2 = new JScrollPane();
  JTextPane in = new JTextPane();
  JTextArea out = new JTextArea();
  Thread t = new ColoredThread();

  Object lock = new Object();

  private boolean isAlive = false;


  /**
   * <p>Description: Thread used to color the text.</p>
   */
  class ColoredThread extends Thread {

    public void run() {
      // the thread never ends: it suspends until document listener interrupt it...
      while(true) {
        // analyze the content...
        highlight();
        try {
            sleep (0xffffff);
        } catch (InterruptedException x){
        }
      }
    }

  }



  public TestSQLFrame() {
    try {
      in.getDocument().addDocumentListener(this);
      jbInit();
      initStyles();

      setSize(800,500);
      setVisible(true);
      in.requestFocus();
      t.start();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  public static void main(String[] args) {
    TestSQLFrame testSQLFrame = new TestSQLFrame();
  }


  private void jbInit() throws Exception {
    out.setEditable(false);
    out.setRows(5);
    this.getContentPane().add(jScrollPane1, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(in, null);
    this.getContentPane().add(jScrollPane2, BorderLayout.NORTH);
    jScrollPane2.getViewport().add(out, null);
  }



  /**
   * Gives notification that there was an insert into the document.  The
   * range given by the DocumentEvent bounds the freshly inserted region.
   *
   * @param e the document event
   */
  public void insertUpdate(DocumentEvent e) {
    try {
      synchronized(lock) {
        if (isAlive)
          return;
      }
      t.interrupt();

    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Gives notification that a portion of the document has been
   * removed.  The range is given in terms of what the view last
   * saw (that is, before updating sticky positions).
   *
   * @param e the document event
   */
  public void removeUpdate(DocumentEvent e) {
    try {
      synchronized(lock) {
        if (isAlive)
          return;
      }
      t.interrupt();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Gives notification that an attribute or set of attributes changed.
   *
   * @param e the document event
   */
  public void changedUpdate(DocumentEvent e) {

  }


  /**
    * A hash table containing the text styles.
    * Simple attribute sets are hashed by name (String)
    */
   private Hashtable styles = new Hashtable();

   /**
    * retrieve the style for the given type of text.
    *
    * @param styleName the label for the type of text ("tag" for example)
    *      or null if the styleName is not known.
    * @return the style
    */
   private SimpleAttributeSet getStyle(String styleName){
       return ((SimpleAttributeSet)styles.get(styleName));
   }

   /**
    * Create the styles and place them in the hash table.
    */
   private void initStyles(){
       SimpleAttributeSet style;

       style = new SimpleAttributeSet();
       StyleConstants.setFontFamily(style, "Monospaced");
       StyleConstants.setFontSize(style, 12);
       StyleConstants.setBackground(style, Color.white);
       StyleConstants.setForeground(style, Color.black);
       StyleConstants.setBold(style, true);
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
       StyleConstants.setForeground(style, Color.green.darker());
       StyleConstants.setBold(style, false);
       StyleConstants.setItalic(style, false);
       styles.put("comment", style);

   }








  private void highlight() {
    // wait for lock and change isAlive flag state...
   synchronized(lock) {
     isAlive = true;
   }
   // now document listener cannot fires events...

   try {
     Document doc = in.getDocument();
     String text = doc.getText(0, doc.getLength());
     doc.remove(0,text.length());
     doc.insertString(0,text,getStyle("text"));
     text = text.toUpperCase();
     int pos;

     // search for patterns...
     String pattern = null;
     String[] patterns = new String[] {"SELECT","FROM","WHERE","ORDER BY","GROUP BY","HAVING","||","+","-","*","/","(+)","IN"};
     for(int i=0;i<patterns.length;i++) {
       pos = 0;
       pattern = patterns[i];
       while ((pos = text.indexOf(pattern, pos)) >= 0) {
         if (
           (pos==0 || pos>0 && (text.charAt(pos-1)==' ' || text.charAt(pos-1)=='\t' || text.charAt(pos-1)=='\n')) &&
           (pos+pattern.length()==text.length() || pos+pattern.length()<text.length() && (text.charAt(pos+pattern.length())==' ' || text.charAt(pos+pattern.length())=='\t' || text.charAt(pos+pattern.length())=='\n'))
         ) {
           in.getDocument().remove(pos,pattern.length());
           in.getDocument().insertString(pos,pattern,getStyle("reservedWord"));
         }
         pos += pattern.length();
       }
     }

     // find out literals...
     pos = 0;
     int endpos;
     while ((pos = text.indexOf("'", pos)) >= 0) {
       endpos = text.indexOf("'",pos+1);
       if (endpos==-1)
         endpos = text.length()-1;
       in.getDocument().remove(pos,endpos-pos+1);
       in.getDocument().insertString(pos,text.substring(pos,endpos+1),getStyle("literal"));
       pos = endpos+1;
     }

     // find out comments...
     pos = 0;
     while ((pos = text.indexOf("//", pos)) >= 0) {
       endpos = text.indexOf("\n",pos);
       if (endpos==-1)
         endpos = text.length()-1;
       in.getDocument().remove(pos,endpos-pos+1);
       in.getDocument().insertString(pos,text.substring(pos,endpos+1),getStyle("comment"));
       pos = endpos+1;
     }

     pos = 0;
     while ((pos = text.indexOf("/*", pos)) >= 0) {
       endpos = text.indexOf("*/",pos);
       if (endpos==-1)
         endpos = text.length()-2;
       in.getDocument().remove(pos,endpos-pos+2);
       in.getDocument().insertString(pos,text.substring(pos,endpos+2),getStyle("comment"));
       pos += endpos+1;
     }


   } catch (Exception e) {
   }

   // wait for lock and change isAlive flag state...
   synchronized(lock) {
     isAlive = false;
   }
   // now document listener can fires events...

 }




}
