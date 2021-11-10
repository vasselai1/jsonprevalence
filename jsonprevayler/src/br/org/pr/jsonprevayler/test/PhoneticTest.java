package br.org.pr.jsonprevayler.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import br.org.pr.jsonprevayler.searchfilter.matchers.PhoneticComparator;

class PhoneticTest {

	@Test
	void test1() {
		assertTrue(PhoneticComparator.isEquals("Vasselai", "vacelai"));		
	}

	@Test
	void test2() {
		assertTrue(PhoneticComparator.isEquals("sessão", "seção"));
	}	
	
	@Test
	void test3() {
		assertTrue(PhoneticComparator.isEquals("sessão", "cessão"));
	}
	
	@Test
	void test4() {
		assertTrue(PhoneticComparator.isEquals("conserto", "concerto"));
	}
	
	@Test
	void test5() {
		assertEquals(2, PhoneticComparator.countEquals("Ricardo Vasselai Paulino", "Arthur Vacelai Paolino"));
	}	
	
	
}
