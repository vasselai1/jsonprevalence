package br.org.pr.jsonprevayler.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import br.org.pr.jsonprevayler.entity.GeoPoint;
import br.org.pr.jsonprevayler.searchfilter.matchers.GeoDistanceCalculator;

class DistanceTest {

	@Test
	void test() {
		GeoPoint geoPoint1 = new GeoPoint(-25.4284, -49.2733);
		GeoPoint geoPoint2 = new GeoPoint(-23.5489, -46.6388);
		System.out.println(GeoDistanceCalculator.calculateDistance(geoPoint1, geoPoint2) + "kms");
	}

}
