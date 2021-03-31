package jsonprevayler.test;

import java.util.List;

import org.junit.jupiter.api.Test;

import jsonprevayler.PrevalenceRepository;
import jsonprevayler.search.AbstractPrevalenceFilter;
import jsonprevayler.test.entity.User;
import jsonprevayler.util.HashPasswdUtil;
import jsonprevayler.util.RecordPathUtil;

class PrevalenceJsonRepositoryTest {

	private static final String DIR_DADOS_TESTES = RecordPathUtil.getPath();
	
	@Test
	void test() throws Exception {
		PrevalenceRepository prevalence = new PrevalenceRepository(DIR_DADOS_TESTES, "TESTES_ANUNCIOS2");
		prevalence.initilize(User.class);
		
		User usuario = new User();
		usuario.setName("Ricardo Vasselai Paulino");
		usuario.setMail("adfasdf@gmail.com");
		usuario.setPasswd("sfgsdfg");
		
		prevalence.save(User.class, usuario);
		

		User userEx = new User();
		userEx.setName("ssela");
		AbstractPrevalenceFilter<User> listerUsuarioName = new AbstractPrevalenceFilter<User>(userEx) {
			@Override
			public boolean isAcepted(User entity) {
				return entity.getName().toLowerCase().contains(userEx.getName());
			}

			@Override
			public void setPrevalenceInstance(PrevalenceRepository pojoJsonRepository) {
				// TODO Auto-generated method stub
				
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
	void testMemoria() throws Exception {
		PrevalenceRepository prevalence = new PrevalenceRepository(DIR_DADOS_TESTES, "TESTES_ANUNCIOS3");
		prevalence.initilize(User.class);
		
		long initial = System.currentTimeMillis();
		int count = 1000;
		for (int i = 0; i < count; i++) { 
			String nome = HashPasswdUtil.getRandomString(70);
			String email = HashPasswdUtil.getRandomString(40);
			String senha = HashPasswdUtil.getRandomString(12);
			User usuario = new User();
			usuario.setName(nome);
			usuario.setMail(email);
			usuario.setPasswd(senha);
			prevalence.save(User.class, usuario);
		}
				
//		PrevalenceJsonRepository prevalence2 = new PrevalenceJsonRepository(DIR_DADOS_TESTES, "TESTES_ANUNCIOS2");
//		for (String users : prevalence2.listJson(User.class)) {
//			System.out.println(users);
//		}
	}
	
	//@Test
	void testList() throws Exception {
		PrevalenceRepository prevalence = new PrevalenceRepository(DIR_DADOS_TESTES, "TESTES_ANUNCIOS2");
		for (String users : prevalence.listJson(User.class)) {
			System.out.println(users);
		}
		System.out.println(prevalence.listJson(User.class).size());
	}	
	
}
