package br.tec.jsonprevayler.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Set;

import org.junit.jupiter.api.Test;

import br.tec.jsonprevayler.PrevalentBinaryRepository;
import br.tec.jsonprevayler.util.RecordPathUtil;

class PrevalenceBinaryRepositoryTest {

	@Test
	void testSaveUpdate() throws IOException, Exception {
		PrevalentBinaryRepository prevalence = new PrevalentBinaryRepository(TestPrevalenceConfigurator.getConfigurator()); 
		
		Long id = prevalence.save("dasfçasldkfjasdç lfjasdçlfk jasdlçfkjasd".getBytes());
		prevalence.update(id, "alterado alterado alterado".getBytes());
	}

	@Test
	void testSaveList() throws IOException, Exception {
		PrevalentBinaryRepository prevalence = new PrevalentBinaryRepository(TestPrevalenceConfigurator.getConfigurator()); 
		
		for (int i = 0; i < 100; i++) {
			prevalence.save("lsadkf alskdfjhalksdfjha lksjdfhl kasdfashfd dasfçasldkfjasdç lfjasdçlfk jasdlçfkjasd".getBytes());
		}		
	}
	
	@Test
	void testList() throws IOException, Exception {
		PrevalentBinaryRepository prevalence = new PrevalentBinaryRepository(TestPrevalenceConfigurator.getConfigurator()); 
		
		Set<Long> ids = prevalence.list();
		
		for (Long idLoop : ids) {
			byte[] bytesFile = prevalence.get(idLoop);
			System.out.println(bytesFile);
		}		
	}
	
	@Test
	void testDelete() throws IOException, Exception {
		PrevalentBinaryRepository prevalence = new PrevalentBinaryRepository(TestPrevalenceConfigurator.getConfigurator()); 
		
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
