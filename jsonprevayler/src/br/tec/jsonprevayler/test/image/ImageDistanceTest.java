package br.tec.jsonprevayler.test.image;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import br.tec.jsonprevayler.searchfilter.matchers.ImagePHashDistance;

class ImageDistanceTest {

	@Test
	void test1() throws Exception {
		byte[] javaImage1 = getClass().getResourceAsStream("java1.png").readAllBytes();
		ImagePHashDistance imagePHashDistance = new ImagePHashDistance();
		int val = imagePHashDistance.distance(javaImage1, javaImage1);
		System.out.println(val);
		assertEquals(0, val);
	}

	@Test
	void test2() throws Exception {
		byte[] javaImage1 = getClass().getResourceAsStream("java1.png").readAllBytes();
		byte[] javaImage2 = getClass().getResourceAsStream("java2.png").readAllBytes();
		ImagePHashDistance imagePHashDistance = new ImagePHashDistance();
		int val = imagePHashDistance.distance(javaImage1, javaImage2);
		System.out.println(val);
		assertNotEquals(0, val);
	}	
	
	@Test
	void test3() throws Exception {
		byte[] javaImage1 = getClass().getResourceAsStream("java1.png").readAllBytes();
		byte[] javaImage3 = getClass().getResourceAsStream("java3.png").readAllBytes();
		ImagePHashDistance imagePHashDistance = new ImagePHashDistance();
		int val = imagePHashDistance.distance(javaImage1, javaImage3);
		System.out.println(val);
		assertNotEquals(0, val);
	}	
	
	@Test
	void test4() throws Exception {
		byte[] javaImage1 = getClass().getResourceAsStream("java1.png").readAllBytes();
		byte[] c_Image2 = getClass().getResourceAsStream("c#.png").readAllBytes();
		ImagePHashDistance imagePHashDistance = new ImagePHashDistance();
		int val = imagePHashDistance.distance(javaImage1, c_Image2);
		System.out.println(val);
		assertNotEquals(0, val);
	}	

	
	
}
