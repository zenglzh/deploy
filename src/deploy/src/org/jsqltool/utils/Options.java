package org.jsqltool.utils;

import java.util.*;
import javax.crypto.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import javax.swing.JOptionPane;


/**
 * <p>Title: JSqlTool Project</p>
 * <p>Description: This is a singleton class used to store application properties.
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
public class Options {

  /** unique instance of the class */
  private static Options opt = null;

  /** date format */
  private String dateFormat;

  /** Oracle PLAN table, used to execute query explain */
  private String oracleExplainPlanTable;

  /** flag used to allow record updating when no pk is defined */
  private boolean updateWhenNoPK;

  /** language id to be used inside the application */
  private String language = null;

  /** contains internationalizaton settings */
  private ResourceBundle resourceBundle = null;

  /** cipher object for password-based encryption */
  private Cipher encCipher = null;

  /** cipher object for password-based decryption */
  private Cipher decCipher = null;

  /** PBE parameter set */
  private PBEParameterSpec pbeParamSpec = null;

  /** SecretKey object */
  private SecretKey pbeKey = null;


  private Options() {
    try {
      // Salt
      byte[] salt = {
          (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
          (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
      };

      // Iteration count
      int count = 20;

      // Create PBE parameter set
      pbeParamSpec = new PBEParameterSpec(salt, count);

      // Prompt user for encryption password.
      // Collect user password as char array (using the
      // "readPasswd" method from above), and convert
      // it into a SecretKey object, using a PBE key
      // factory.
      PBEKeySpec pbeKeySpec = new PBEKeySpec(new char[]{'2','1','1','7','4'});
      SecretKeyFactory keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
      pbeKey = keyFac.generateSecret(pbeKeySpec);

      // get cipher object for password-based encryption
      encCipher = Cipher.getInstance("PBEWithMD5AndDES");

      // get cipher object for password-based decryption
      decCipher = Cipher.getInstance("PBEWithMD5AndDES");

    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }
  }


  /**
   * @return unique instance of the class
   */
  public static Options getInstance() {
    if (java.beans.Beans.isDesignTime())
      return new Options();
    if (opt==null)
      opt = new Options();
    return opt;
  }


  /**
   * @return date format
   */
  public String getDateFormat() {
    return dateFormat;
  }


  /**
   * @return Oracle PLAN table, used to execute query explain
   */
  public final String getOracleExplainPlanTable() {
    return oracleExplainPlanTable;
  }


  /**
   * @return allow record updating when no pk is defined
   */
  public final boolean isUpdateWhenNoPK() {
    return updateWhenNoPK;
  }


  /**
   * Allow record updating when no pk is defined
   * @param updateWhenNoPK allow record updating when no pk is defined
   */
  public final void setUpdateWhenNoPK(boolean updateWhenNoPK) {
    this.updateWhenNoPK = updateWhenNoPK;
  }


  /**
   * Set Oracle PLAN table, used to execute query explain.
   * @param oracleExplainPlanTable Oracle PLAN table, used to execute query explain
   */
  public final void setOracleExplainPlanTable(String oracleExplainPlanTable) {
    this.oracleExplainPlanTable = oracleExplainPlanTable;
  }


  /**
   * Set date format.
   * @param dateFormat date format
   */
  public final void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }


  /**
   * @return language id to be used inside the application
   */
  public final String getLanguage() {
    return language;
  }


  /**
   * Set language id to be used inside the application.
   * @param language language id to be used inside the application
   */
  public final void setLanguage(String language) {
    this.language = language;
    resourceBundle = ResourceBundle.getBundle("org.jsqltool.utils.Dictionary", new Locale(language));
  }


  /**
   * @param key key to translate
   * @return key translation
   */
  public final String getResource(String key) {
    if (java.beans.Beans.isDesignTime())
      return key;
    String value = null;
    try {
      value = resourceBundle.getString(key);
    }
    catch (Exception ex) {
      return key;
    }
    return value==null?key:value;
  }



  /**
   * Encrypt text.
   * @param text text to encrypt
   * @return encrypted text
   */
  public final String encode(String text) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
    // initialize cipher for encryption, without supplying
    // any parameters. Here, "myKey" is assumed to refer
    // to an already-generated key.
    encCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);
    byte[] cipherText = encCipher.doFinal(text.getBytes());
    return new String(cipherText);
  }


  /**
   * Decrypt text.
   * @param text text to decrypt
   * @return decrypted text
   */
  public final String decode(String text)  throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
    // initialize cipher for decryption, without supplying
    // any parameters. Here, "myKey" is assumed to refer
    // to an already-generated key.
    decCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
    byte[] cipherText = decCipher.doFinal(text.getBytes());
    return new String(cipherText);

  }


  /**
   * Encrypt text.
   * @param text text to encrypt
   * @return encrypted text
   */
  public final byte[] encodeToBytes(String text) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
    encCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);
    byte[] cipherText = encCipher.doFinal(text.getBytes());
    return cipherText;
  }


  /**
   * Decrypt text.
   * @param text text to decrypt
   * @return decrypted text
   */
  public final String decodeFromBytes(byte[] text)  throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
    // initialize cipher for decryption, without supplying
    // any parameters. Here, "myKey" is assumed to refer
    // to an already-generated key.
    decCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
    byte[] cipherText = decCipher.doFinal(text);
    return new String(cipherText);
  }




}