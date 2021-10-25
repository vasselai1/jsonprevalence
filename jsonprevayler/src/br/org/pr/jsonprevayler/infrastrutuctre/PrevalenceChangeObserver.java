package br.org.pr.jsonprevayler.infrastrutuctre;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;

public interface PrevalenceChangeObserver {

	<T extends PrevalenceEntity> void receiveNew(Class<T> classe, Long id);
	<T extends PrevalenceEntity> void receiveUpdate(Class<T> classe, Long id);
	<T extends PrevalenceEntity> void receiveDelete(Class<T> classe, Long id);
	
}
