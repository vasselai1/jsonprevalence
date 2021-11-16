package br.tec.jsonprevayler.util;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class EncryptorDecryptorUtil {

  private static final String CRYPTO_ALGORITHM = "PBEWithMD5AndDES";

  private String key;
  private Logger logger =  Logger.getLogger(getClass().getName());

  public EncryptorDecryptorUtil(String key) {
      this.key = key;
  }

  private PBEKeySpec getKs() {
      return new PBEKeySpec(key.toCharArray());
  }

  private PBEParameterSpec getPs() {
      return new PBEParameterSpec(new byte[]{2, 2, 3, 1, 4, 5, 1, 7}, 20);
  }

  private SecretKey getKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
      SecretKeyFactory skf = SecretKeyFactory.getInstance(CRYPTO_ALGORITHM);
      KeySpec ks = getKs();
      SecretKey skey = skf.generateSecret(ks);
      return skey;
  }

  public final String encrypt(String text) throws Exception {
      try {
          Cipher c = Cipher.getInstance(CRYPTO_ALGORITHM);
          SecretKey skey = getKey();
          PBEParameterSpec ps = getPs();
          c.init(Cipher.ENCRYPT_MODE, skey, ps);
          return Base64.getEncoder().encodeToString(c.doFinal(text.getBytes()));
      } catch (Exception ex) {
    	  logger.log(Level.SEVERE, "encrypt(" + text + ")", ex);
          throw ex;
      }
  }

  public String decrypt(String text) throws Exception {
      if (text == null || text.equals("")) {
          return "";
      }
      try {
          Cipher c = Cipher.getInstance(CRYPTO_ALGORITHM);
          SecretKey skey = getKey();
          PBEParameterSpec ps = getPs();
          c.init(Cipher.DECRYPT_MODE, skey, ps);
          return new String(c.doFinal(Base64.getDecoder().decode(text)));
      } catch (Exception ex) {
    	  logger.log(Level.SEVERE, "decrypt(" + text + ")", ex);
          throw ex;
      }
  }
  
}