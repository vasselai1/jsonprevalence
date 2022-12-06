package br.tec.jsonprevayler.pojojsonrepository.core.util;

import java.io.File;
import java.io.FilenameFilter;

public class FileNameFilterClassId implements FilenameFilter{

	private String className;
	private Long id;
	
	public FileNameFilterClassId(String className, Long id) {
		super();
		this.className = className;
		this.id = id;
	}

	@Override
	public boolean accept(File dir, String name) {
		return name.startsWith(className + "_" + id);
	}
	
}