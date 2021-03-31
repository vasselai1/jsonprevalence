package jsonprevayler.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import flexjson.JSONSerializer;
import jsonprevayler.dto.SaveNamePasswdDto;

class JsonSerializationTest {

	@Test
	void test() {
		SaveNamePasswdDto saveDto = new SaveNamePasswdDto();
		saveDto.setCode("senha21kgjhg4");
		saveDto.setConfirmPasswd("senha122");
		saveDto.setMail("email@email.com");
		saveDto.setName("Peter Astay");
		saveDto.setPasswd("senha21kgjhg4");
		System.out.println(new JSONSerializer().serialize(saveDto));
	}

}
