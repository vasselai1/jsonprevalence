package br.tec.jsonprevayler.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.tec.jsonprevayler.infrastrutuctre.HistoryWriter;
import br.tec.jsonprevayler.pojojsonrepository.core.util.DateProvider;

public class DeclaredDateProvider implements DateProvider {
	
	public static final SimpleDateFormat SDF_HISTORY = HistoryWriter.SDF_HISTORY;
	
	private Date date;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public Date get() {
		return date;
	}
	
}