package br.tec.jsonprevayler.test;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.test.entity.User;

class SequenceUtilTest {
	
	private List<Long> ids = new ArrayList<Long>();
	
	private static final String DIR_DADOS_TESTES = "/home/ricardo/Documentos/projetos_freelanc/dados_sistema";

	//@Test
	void test() throws InternalPrevalenceException, ValidationPrevalenceException {
		SequenceProvider sequenceUtil = new SequenceProvider(DIR_DADOS_TESTES);
		
		System.out.println(sequenceUtil.get(User.class));
		System.out.println(sequenceUtil.get(User.class));
		System.out.println(sequenceUtil.get(User.class));
		System.out.println(sequenceUtil.get(User.class));
	}

	@Test
	void test2() throws IOException, InterruptedException {
		List<ThreadId> thredsTeste = new ArrayList<SequenceUtilTest.ThreadId>();
		for (int i = 0; i < 10000; i++) {
			thredsTeste.add(new ThreadId(ids));
		}
		for (ThreadId threadLoop : thredsTeste) {
			threadLoop.start();
		}
		for (ThreadId threadLoop : thredsTeste) {
			threadLoop.join();
		}
		assertFalse(temRepetido());
	}
	
	class ThreadId extends Thread {
		private List<Long> ids;
		public ThreadId(List<Long> ids) {
			this.ids = ids;
		}

		@Override
		public void run() {
			SequenceProvider sequenceUtil = new SequenceProvider(DIR_DADOS_TESTES);
			try {
				ids.add(sequenceUtil.get(User.class));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean temRepetido() {
		for (int i = 0; i < ids.size(); i++) {
			long idLoop = ids.get(i);
			System.out.println(idLoop);
			for (int j = 0; j < ids.size(); j++) {
				long idVerificacao = ids.get(j);
				if ((idLoop == idVerificacao) && (i != j)) {
					return true;
				}
			}
		}
		return false;
	}
	
}
