package jsonprevayler.test;

import java.util.List;

import org.junit.jupiter.api.Test;

import jsonprevayler.PersistenceRepository;
import jsonprevayler.PrevalenceRepository;
import jsonprevayler.search.AbstractPersistenceFilter;
import jsonprevayler.search.AbstractPrevalenceFilter;
import jsonprevayler.test.entity.OtherUser;
import jsonprevayler.test.entity.User;
import jsonprevayler.util.HashPasswdUtil;
import jsonprevayler.util.RecordPathUtil;

class PersistenceRepositoryTest {

	private static final String DIR_DADOS_TESTES = RecordPathUtil.getPath();
	
	@Test
	void test() throws Exception {
		PersistenceRepository persistence = new PersistenceRepository(DIR_DADOS_TESTES, "TESTES_ANUNCIOS2");
		
		
		OtherUser user = new OtherUser();
		user.setName("Ricardo Vasselai Paulino");
		user.setMail("adfasdf@gmail.com");
		user.setPasswd("sfgsdfg");
		
		persistence.save(OtherUser.class, user);
		

		OtherUser userEx = new OtherUser();
		userEx.setName("ssela");
		AbstractPersistenceFilter<OtherUser> listerUsuarioName = new AbstractPersistenceFilter<OtherUser>(userEx) {
			@Override
			public boolean isAcepted(OtherUser entity) {
				return entity.getName().toLowerCase().contains(userEx.getName());
			}

		};
		
		long initial = System.currentTimeMillis();
		List<OtherUser> users = persistence.listPojo(OtherUser.class, listerUsuarioName);
		System.out.println("Search time: " + ( System.currentTimeMillis() - initial) +"ms");
		
		for (OtherUser userLoop : users) {
			System.out.println(userLoop.getId() + " - " + userLoop.getName());
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
