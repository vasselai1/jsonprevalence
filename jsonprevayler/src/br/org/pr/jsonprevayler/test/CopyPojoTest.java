package br.org.pr.jsonprevayler.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import br.org.pr.jsonprevayler.test.entity.User;
import br.org.pr.jsonprevayler.util.ObjectCopyUtil;

class CopyPojoTest {

	@Test
	void test() throws ClassNotFoundException, IOException {
		User user1 = new User();
		user1.setName("Ricardo Vasselai Paulino");
		user1.setMail("adfasdf@gmail.com");
		user1.setPasswd("sfgsdfg");
		
		User user2 = ObjectCopyUtil.copyEntity(user1);
		
		assertEquals(user1.getName(), user2.getName());
		
		user2.setName("ABCD");
		assertNotEquals(user1.getName(), user2.getName());
	}

}
