package br.org.pr.jsonprevayler.test;

import java.util.List;

import org.junit.jupiter.api.Test;

import br.org.pr.jsonprevayler.PrevalentJsonRepository;
import br.org.pr.jsonprevayler.search.AbstractPrevalenceFilter;
import br.org.pr.jsonprevayler.search.ProgressSearchObserver;
import br.org.pr.jsonprevayler.test.entity.User;
import br.org.pr.jsonprevayler.util.HashUtil;
import br.org.pr.jsonprevayler.util.RecordPathUtil;

class PrevalenceJsonRepositoryTest {

	private static final String DIR_DADOS_TESTES = RecordPathUtil.getPath();
	private static final String SYSTEM_TEST_NAME = "PREVALENCE_TEST"; 
	
	//@Test
	void testSaveList() throws Exception {
		PrevalentJsonRepository<User> prevalence = new PrevalentJsonRepository<User>(DIR_DADOS_TESTES, SYSTEM_TEST_NAME);
		
		User usuario = new User();
		usuario.setName("Ricardo Vasselai Teste");
		usuario.setMail("adfasdf@gmail.com");
		usuario.setPasswd("teste");
		
		prevalence.save(usuario);
		

		User userEx = new User();
		userEx.setName("ssela");
		AbstractPrevalenceFilter<User> listerUsuarioName = new AbstractPrevalenceFilter<User>(userEx) {
			@Override
			public boolean isAcepted(User entity) {
				return entity.getName().toLowerCase().contains(userEx.getName());
			}

			@Override
			public void setPrevalenceInstance(PrevalentJsonRepository pojoJsonRepository) {
			}

			@Override
			public ProgressSearchObserver getProgressSearchObserver() {
				return null;
			}
		};
		
		long initial = System.currentTimeMillis();
		List<User> users = prevalence.listPojo(User.class, listerUsuarioName);
		System.out.println("Search time: " + ( System.currentTimeMillis() - initial) +"ms");
		
		for (User user : users) {
			System.out.println(user.getId() + " - " + user.getName());
		}
	}

	//@Test
	void testUpdateList() throws Exception {
		PrevalentJsonRepository<User> prevalence = new PrevalentJsonRepository<>(DIR_DADOS_TESTES, SYSTEM_TEST_NAME);
	
		User user = prevalence.getPojo(User.class, 1L);
		user.setName("Outro nome de teste");
		prevalence.update(user);

		User userEx = new User();
		userEx.setName("utro");
		AbstractPrevalenceFilter<User> listerUsuarioName = new AbstractPrevalenceFilter<User>(userEx) {
			@Override
			public boolean isAcepted(User entity) {
				return entity.getName().toLowerCase().contains(userEx.getName());
			}

			@Override
			public void setPrevalenceInstance(PrevalentJsonRepository pojoJsonRepository) {
			}

			@Override
			public ProgressSearchObserver getProgressSearchObserver() {
				return null;
			}
		};
		
		long initial = System.currentTimeMillis();
		List<User> users = prevalence.listPojo(User.class, listerUsuarioName);
		System.out.println("Search time: " + ( System.currentTimeMillis() - initial) +"ms");
		
		for (User userLoop : users) {
			System.out.println(userLoop.getId() + " - " + userLoop.getName());
		}
	}	
	
	//@Test
	void testDeleteList() throws Exception {
		PrevalentJsonRepository<User> prevalence = new PrevalentJsonRepository<>(DIR_DADOS_TESTES, SYSTEM_TEST_NAME);

		List<User> users = prevalence.listPojo(User.class);
		System.out.println("Results: " + users.size());
		
		User user = null;
		for (User userLoop : users) {			
			user = userLoop;
		}
		
		prevalence.delete(user);
		System.out.println(user.getName() + " : " +user.getId() + " - Deleted");
		
		users = prevalence.listPojo(User.class);
		System.out.println("Results: " + users.size());
	}	
	
	
	//@Test
	void testMemoria() throws Exception {
		PrevalentJsonRepository<User> prevalence = new PrevalentJsonRepository<>(DIR_DADOS_TESTES, SYSTEM_TEST_NAME);		
		
		long initial = System.currentTimeMillis();
		int count = 1000;
		for (int i = 0; i < count; i++) { 
			String nome = HashUtil.getRandomString(70);
			String email = HashUtil.getRandomString(40);
			String senha = HashUtil.getRandomString(12);
			User usuario = new User();
			usuario.setName(nome);
			//usuario.setMail(email);
			usuario.setPasswd(senha);
			prevalence.save(usuario);
		}
				
//		PrevalenceJsonRepository prevalence2 = new PrevalenceJsonRepository(DIR_DADOS_TESTES, "TESTES_ANUNCIOS2");
//		for (String users : prevalence2.listJson(User.class)) {
//			System.out.println(users);
//		}
	}
	
	@Test
	void testList() throws Exception {
		PrevalentJsonRepository<User> prevalence = new PrevalentJsonRepository<>(DIR_DADOS_TESTES, SYSTEM_TEST_NAME);
		System.out.println(prevalence.listJson(User.class));
	}
	
	@Test
	void testListPojo() throws Exception {
		PrevalentJsonRepository<User> prevalence = new PrevalentJsonRepository<>(DIR_DADOS_TESTES, SYSTEM_TEST_NAME);
		System.out.println(prevalence.listPojo(User.class));
	}	
	
}
