package jsonprevayler.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jsonprevayler.util.PhoneticUtil;

class PhoneticTest {

	@Test
	void test1() {
		assertTrue(PhoneticUtil.isEquals("Vasselai", "vacelai"));		
	}

	@Test
	void test2() {
		assertTrue(PhoneticUtil.isEquals("sessão", "seção"));
	}	
	
	@Test
	void test3() {
		assertTrue(PhoneticUtil.isEquals("sessão", "cessão"));
	}
	
	@Test
	void test4() {
		assertTrue(PhoneticUtil.isEquals("conserto", "concerto"));
	}
	
	@Test
	void test5() {
		assertEquals(2, PhoneticUtil.countEquals("Ricardo Vasselai Paulino", "Arthur Vacelai Paolino"));
	}	
	
	
}
