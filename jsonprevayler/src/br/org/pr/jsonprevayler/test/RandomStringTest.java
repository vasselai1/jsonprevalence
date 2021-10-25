package br.org.pr.jsonprevayler.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import br.org.pr.jsonprevayler.util.HashUtil;

class RandomStringTest {

	@Test
	void test1() throws Exception {
		System.out.println(HashUtil.getRandomString(5));
	}

	@Test
	void test2() throws Exception {
		System.out.println(HashUtil.getRandomString(5));
	}	

	@Test
	void test3() throws Exception {
		System.out.println(HashUtil.getRandomString(6));
	}	

	@Test
	void test4() throws Exception {
		System.out.println(HashUtil.getRandomString(8));
	}	
	
	@Test
	void test5() throws Exception {
		System.out.println(HashUtil.getRandomString(10));
	}	
	
}
