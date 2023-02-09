package br.tec.jsonprevayler.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

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
