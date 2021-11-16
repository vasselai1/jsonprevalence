package br.tec.jsonprevayler.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.normalization.PrevalentAtributesValuesIdentificator;
import br.tec.jsonprevayler.test.entity.MapCollectionEntity;
import br.tec.jsonprevayler.test.entity.User;

class AtributesIdentificatorTest {

	@Test
	void test() throws Exception {
		MapCollectionEntity mapCollectionEntity = new MapCollectionEntity();
		PrevalentAtributesValuesIdentificator.getJsonSerializationInstructions(mapCollectionEntity);
		
	}

}
