package br.tec.jsonprevayler.searchfilter.processing;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import br.tec.jsonprevayler.PrevalentRepository;
import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.searchfilter.PrevalenceFilter;

public class RemoteMultiThreadSearchPocessor extends SearchProcessor {

	@Override
	public <T extends PrevalenceEntity> void process(Class<T> classe, PrevalenceFilter<T> filter, List<T> retorno) throws ClassNotFoundException, IOException, ValidationPrevalenceException {
		// TODO Auto-generated method stub
		
	}

}
