package br.org.pr.jsonprevayler.searchfilter.processing;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import br.org.pr.jsonprevayler.PrevalentJsonRepository;
import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.searchfilter.PrevalenceFilter;

public class RemoteMultiThreadSearchPocessor extends SearchProcessor {

	@Override
	public <T extends PrevalenceEntity> void process(Class<T> classe, PrevalenceFilter<T> filter, List<T> retorno) throws ClassNotFoundException, IOException, ValidationPrevalenceException {
		// TODO Auto-generated method stub
		
	}

}
