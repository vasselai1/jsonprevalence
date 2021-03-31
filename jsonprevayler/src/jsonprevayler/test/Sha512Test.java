package jsonprevayler.test;

import static org.junit.jupiter.api.Assertions.*;

import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;

import jsonprevayler.util.HashPasswdUtil;

class Sha512Test {

	//@Test
	void testRicardo1() throws NoSuchAlgorithmException {
		String teste = HashPasswdUtil.getSha512("Ricardo");
		System.out.println(teste);
	}

	//@Test
	void testRicardo2() throws NoSuchAlgorithmException {
		String teste = HashPasswdUtil.getSha512("ricardo");
		System.out.println(teste);	
	}	

	@Test
	void testAsdf1() throws NoSuchAlgorithmException {
		String teste = HashPasswdUtil.getSha512("asdf");
		System.out.println(teste);
	}

	@Test
	void testAsdf2() throws NoSuchAlgorithmException {
		String teste = HashPasswdUtil.getSha512("asdf");
		System.out.println(teste);	
	}	
	
}
