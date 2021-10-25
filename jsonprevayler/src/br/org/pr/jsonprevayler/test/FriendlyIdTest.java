package br.org.pr.jsonprevayler.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import br.org.pr.jsonprevayler.util.FriendlyIdUtil;

class FriendlyIdTest {

	@Test
	void test1() throws Exception {
		System.out.println(FriendlyIdUtil.encript(5l, "ter"));
	}

	@Test
	void test2() throws Exception {
		assertEquals(5l, FriendlyIdUtil.decript("rvZTrT9u0rE%3D", "ter"));
	}	

	@Test
	void test3() throws Exception {
		assertEquals("abc_cd_o", FriendlyIdUtil.replaceInvalids("abc cdão"));
	}	
	
}