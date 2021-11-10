package br.org.pr.jsonprevayler.test;

import static org.junit.jupiter.api.Assertions.*;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

import br.org.pr.jsonprevayler.pojojsonrepository.core.OperationType;
import br.org.pr.jsonprevayler.util.HashUtil;

class Sha512Test {

	//@Test
	void testRicardo1() throws NoSuchAlgorithmException {
		String teste = HashUtil.getSha512("Ricardo");
		System.out.println(teste);
	}

	//@Test
	void testRicardo2() throws NoSuchAlgorithmException {
		String teste = HashUtil.getSha512("ricardo");
		System.out.println(teste);	
	}	

	@Test
	void testAsdf1() throws NoSuchAlgorithmException {
		String teste = HashUtil.getSha512("asdf");
		System.out.println(teste);
	}

	@Test
	void testAsdf2() throws NoSuchAlgorithmException {
		String teste = HashUtil.getSha512("asdf");
		System.out.println(teste);	
	}	
	
	@Test
	void test3() throws NoSuchAlgorithmException {
		SimpleDateFormat SDF_HISTORY = new SimpleDateFormat("ddMMyyyyHHmmssS");
		StringBuilder journalEntity = new StringBuilder();
		journalEntity.append(OperationType.SAVE.name().substring(0, 1));
		journalEntity.append(";");
		journalEntity.append(SDF_HISTORY.format(new Date()));
		journalEntity.append(";");
		journalEntity.append(10);
		journalEntity.append(";");
		journalEntity.append(HashUtil.getMd5("asdofaospdifu pasdofi uasdpdofiuapsodfi uapsodiuf apsdof"));
		journalEntity.append(System.lineSeparator());		
		System.out.println(journalEntity.toString());	
	}	
		
	
}
