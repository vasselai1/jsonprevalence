package br.tec.jsonprevayler.pojojsonrepository.core.util;

import java.util.Date;

public class CurrentSytemDateProvider implements DateProvider {

	@Override
	public Date get() {
		return new Date();
	}

}
