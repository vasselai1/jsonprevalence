package br.tec.jsonprevayler.pojojsonrepository.core;

import java.util.HashMap;
import java.util.Map;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.pojojsonrepository.core.util.DateProvider;

public class LockPrevalenceEntityTokenFactory {

	private static Map<Class<? extends PrevalenceEntity>, Map<Long, EntityTokenKey>> mapTokens = new HashMap<Class<? extends PrevalenceEntity>, Map<Long,EntityTokenKey>>();
	
	public static <T extends PrevalenceEntity> EntityTokenKey get(T entity, DateProvider dateProvider) {
		if ((entity == null)) {
			throw new IllegalStateException("Entity is null!");
		}
		if ((entity.getId() == null)) {
			throw new IllegalStateException("The entity's Id is null!");
		}
		Map<Long, EntityTokenKey> entityLockMap = getMap(entity.getClass());
		if (entityLockMap.containsKey(entity.getId())) {
			return entityLockMap.get(entity.getId());
		}
		synchronized (entityLockMap) {
			if (entityLockMap.containsKey(entity.getId())) {
				return entityLockMap.get(entity.getId());
			}
			entityLockMap.put(entity.getId(), new EntityTokenKey(entity.getClass(), entity.getId(), dateProvider));
		}
		return entityLockMap.get(entity.getId());
	}
	
	private static <T extends PrevalenceEntity> Map<Long, EntityTokenKey> getMap(Class<T> classe) {
		if (mapTokens.containsKey(classe)) {
			return mapTokens.get(classe);
		}
		synchronized (mapTokens) {
			if (mapTokens.containsKey(classe)) {
				return mapTokens.get(classe);
			}
			mapTokens.put(classe, new HashMap<Long, EntityTokenKey>());
		}
		return mapTokens.get(classe);
	}
	
}
