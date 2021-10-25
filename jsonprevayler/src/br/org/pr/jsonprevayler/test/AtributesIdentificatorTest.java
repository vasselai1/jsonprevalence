package br.org.pr.jsonprevayler.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.PrevalentAtributesValuesIdentificator;
import br.org.pr.jsonprevayler.test.entity.MapCollectionEntity;
import br.org.pr.jsonprevayler.test.entity.User;

class AtributesIdentificatorTest {

	@Test
	void test() throws Exception {
		MapCollectionEntity mapCollectionEntity = new MapCollectionEntity();
		PrevalentAtributesValuesIdentificator.getJsonSerializationInstructions(mapCollectionEntity);
		
	}

}
