package br.tec.jsonprevayler.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class FriendlyIdUtil {

	public static String encript(Long id, String key) throws Exception {		
		return URLEncoder.encode(new EncryptorDecryptorUtil(key).encrypt("" + id), StandardCharsets.UTF_8);
	}
	
	public static Long decript(String id, String key) throws Exception {
		return Long.parseLong(new EncryptorDecryptorUtil(key).decrypt(URLDecoder.decode(id, StandardCharsets.UTF_8)));
	}
	
	public static String replaceInvalids(String idString) {
		return idString.replaceAll("[^a-zA-Z0-9]", "_");
	}
	
}