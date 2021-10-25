package br.org.pr.jsonprevayler.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import br.org.pr.jsonprevayler.PrevalentBinaryRepository;
import br.org.pr.jsonprevayler.util.RecordPathUtil;

class PrevalenceBinaryRepositoryTest {

	private static final String DIR_DADOS_TESTES = RecordPathUtil.getPath();
	private static final String SYSTEM_TEST_NAME = "PREVALENCE_TEST"; 
	
	@Test
	void testSaveUpdate() throws IOException, Exception {
		PrevalentBinaryRepository prevalence = new PrevalentBinaryRepository(DIR_DADOS_TESTES, SYSTEM_TEST_NAME); 
		
		Long id = prevalence.save("dasfçasldkfjasdç lfjasdçlfk jasdlçfkjasd".getBytes());
		prevalence.update(id, "alterado alterado alterado".getBytes());
	}

	//@Test
	void testDelete() throws IOException, Exception {
		PrevalentBinaryRepository prevalence = new PrevalentBinaryRepository(DIR_DADOS_TESTES, SYSTEM_TEST_NAME); 
		
		Long id = null;
		System.out.println("Quantidade : " + prevalence.list().size());
		for (Long idLoop : prevalence.list()) {
			id = idLoop;
		}
		prevalence.delete(id);
		System.out.println(id + " : deleted");
		System.out.println("Quantidade : " + prevalence.list().size());
	}
	
	
}
