package br.org.pr.jsonprevayler.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import br.org.pr.jsonprevayler.infrastrutuctre.normalization.JsonSerializationInstructions;
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.MapPrevalenceTransformer;
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.PrevalentAtributesValuesIdentificator;
import br.org.pr.jsonprevayler.test.entity.MapCollectionEntity;
import br.org.pr.jsonprevayler.test.entity.User;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

class JsonSerializationTest {
	
	@Test
	void test() throws Exception {
		List<String> names = new ArrayList<String>();
		names.add("Ricardo");
		names.add("João");		
		
		Map<Long, String> mapNames = new HashMap<Long, String>();
		mapNames.put(1L, "Maria");
		mapNames.put(2L, "Rafaela");
		
		User user1 = new User();
		user1.setName("Marcelo");
		user1.setId(1L);
		
		User user2 = new User();
		user2.setName("Pedro");
		user2.setId(2L);
		
		List<User> userList = new ArrayList<User>();
		userList.add(user1);
		userList.add(user2);
		
		User user3 = new User();
		user3.setName("Rocha");
		user3.setId(3L);
		
		Map<Long, User> userMap = new HashMap<Long, User>();
		userMap.put(5L, user3);
		userMap.put(6L, user1);
		
		MapCollectionEntity mapCollectionEntity = new MapCollectionEntity();
		mapCollectionEntity.setId(1L);
		mapCollectionEntity.setCodesNames(mapNames);
		mapCollectionEntity.setNames(names);
		mapCollectionEntity.setUser(user3);
		mapCollectionEntity.setUsersList(userList);
		mapCollectionEntity.setUsersMap(userMap);
		
		JsonSerializationInstructions jsonInstructions = PrevalentAtributesValuesIdentificator.getJsonSerializationInstructions(mapCollectionEntity);
		JSONSerializer jsonSerializer = new JSONSerializer();
		jsonSerializer.transform(new MapPrevalenceTransformer(), Map.class);		
		for (String add : jsonInstructions.getAdds()) {
			jsonSerializer.include(add);
		}
		for (String ignore : jsonInstructions.getIgnores()) {
			jsonSerializer.exclude(ignore);
		}
		String json = jsonSerializer.deepSerialize(mapCollectionEntity);
		System.out.println(json);
		
		JSONDeserializer<MapCollectionEntity> deserializer = new JSONDeserializer<MapCollectionEntity>();
		MapCollectionEntity other = deserializer.deserialize(json);
		
		assertTrue(other.getUsersMap().size() > 1);
		assertTrue(mapCollectionEntity.getId().equals(other.getId()));
	}

}
