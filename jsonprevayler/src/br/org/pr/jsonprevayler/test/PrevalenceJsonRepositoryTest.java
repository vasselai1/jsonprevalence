package br.org.pr.jsonprevayler.test;

import java.util.List;

import org.junit.jupiter.api.Test;

import br.org.pr.jsonprevayler.PrevalentRepository;
import br.org.pr.jsonprevayler.searchfilter.AbstractPrevalenceFilter;
import br.org.pr.jsonprevayler.test.entity.User;
import br.org.pr.jsonprevayler.util.HashUtil;

class PrevalenceJsonRepositoryTest {
	
	//@Test
	public void testSaveList() throws Exception {
		PrevalentRepository prevalence = new PrevalentRepository(TestPrevalenceConfigurator.getConfigurator());
		
		User usuario = new User();
		usuario.setName("Selma Inoue da Silveira");
		usuario.setMail("selma.inoue12341234@gmail.com");
		usuario.setPasswd("teste");
		
		prevalence.save(usuario);
		

		User userEx = new User();
		userEx.setName("ssela");
		AbstractPrevalenceFilter<User> listerUsuarioName = new AbstractPrevalenceFilter<User>(userEx) {
			@Override
			public boolean isAcepted(User entity) {
				return entity.getName().toLowerCase().contains(userEx.getName());
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
	public void testUpdateList() throws Exception {
		PrevalentRepository prevalence = new PrevalentRepository(TestPrevalenceConfigurator.getConfigurator());
	
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
		};
		
		long initial = System.currentTimeMillis();
		List<User> users = prevalence.listPojo(User.class, listerUsuarioName);
		System.out.println("Search time: " + ( System.currentTimeMillis() - initial) +"ms");
		
		for (User userLoop : users) {
			System.out.println(userLoop.getId() + " - " + userLoop.getName());
		}
	}	
	
	@Test
	public void testDeleteList() throws Exception {
		PrevalentRepository prevalence = new PrevalentRepository(TestPrevalenceConfigurator.getConfigurator());

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
	public void testMemoria() throws Exception {
		PrevalentRepository prevalence = new PrevalentRepository(TestPrevalenceConfigurator.getConfigurator());		
		
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
	
	//@Test
	public void testList() throws Exception {
		PrevalentRepository prevalence = new PrevalentRepository(TestPrevalenceConfigurator.getConfigurator());
		System.out.println(prevalence.listJson(User.class));
	}
	
	//@Test
	public void testListPojo() throws Exception {
		PrevalentRepository prevalence = new PrevalentRepository(TestPrevalenceConfigurator.getConfigurator());
		System.out.println(prevalence.listPojo(User.class));
	}	
	
}
