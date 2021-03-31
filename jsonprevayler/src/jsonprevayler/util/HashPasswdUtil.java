package jsonprevayler.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class HashPasswdUtil {

	public static final String UPPER_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWER_CHARACTERS = UPPER_CHARACTERS.toLowerCase();
    public static final String DIGITS = "0123456789";
    public static final String ALPHANUM = UPPER_CHARACTERS + LOWER_CHARACTERS + DIGITS;
	
    public static String getSha512(String texto) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		byte[] messageDigest = md.digest(texto.getBytes());
		BigInteger no = new BigInteger(1, messageDigest);
		return String.format("%0128x", no);
	}
	
	public static String getRandomString(int size) throws Exception {
		if (size < 5) {
			throw new Exception("Size lower of 5!");
		}
		Random random = new SecureRandom();
		StringBuilder retorno = new StringBuilder(size);
		for(int i = 0; i < size; i++) {
			int posix = random.nextInt(ALPHANUM.length());
			retorno.append(ALPHANUM.charAt(posix));
		}
		return retorno.toString();
	}
	
}