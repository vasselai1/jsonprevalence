package jsonprevayler;

import jsonprevayler.entity.PersistenceEntity;

public interface PersistenceChangeObserver {

	<T extends PersistenceEntity> void receiveNew(Class<T> classe, Long id);
	<T extends PersistenceEntity> void receiveUpdate(Class<T> classe, Long id);
	<T extends PersistenceEntity> void receiveDelete(Class<T> classe, Long id);
	
}
