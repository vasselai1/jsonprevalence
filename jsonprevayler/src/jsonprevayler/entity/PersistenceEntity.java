package jsonprevayler.entity;

import java.io.Serializable;

public interface PersistenceEntity extends Serializable {
	
	void setId(Long id);
	Long getId();
	
}
