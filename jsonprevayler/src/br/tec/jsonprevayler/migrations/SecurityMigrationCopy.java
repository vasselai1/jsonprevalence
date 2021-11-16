package br.tec.jsonprevayler.migrations;

import java.io.File;

public class SecurityMigrationCopy implements Comparable<SecurityMigrationCopy>{
	
	private String dirName;
	private File directory;
	private Integer line;
	private boolean returned;
	private String temporaryName;
	private File newDiretory;
	
	public SecurityMigrationCopy(String dirName, Integer line, File directory, boolean returned, String temporaryName, File newDiretory) {	
		this.dirName = dirName;
		this.directory = directory;
		this.returned = returned;
	}
	
	public String getDirName() {
		return dirName;
	}
	public void setDirName(String dirName) {
		this.dirName = dirName;
	}
	public File getDirectory() {
		return directory;
	}
	public void setDirectory(File directory) {
		this.directory = directory;
	}
	public Integer getLine() {
		return line;
	}
	public void setLine(Integer line) {
		this.line = line;
	}	
	public boolean isReturned() {
		return returned;
	}
	public void setReturned(boolean returned) {
		this.returned = returned;
	}
	public String getTemporaryName() {
		return temporaryName;
	}
	public void setTemporaryName(String temporaryName) {
		this.temporaryName = temporaryName;
	}
	public File getNewDiretory() {
		return newDiretory;
	}
	public void setNewDiretory(File newDiretory) {
		this.newDiretory = newDiretory;
	}

	@Override
	public String toString() {
		return "SecurityRefatorationCopy [dirName=" + dirName + ", directory=" + directory + ", returned=" + returned+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dirName == null) ? 0 : dirName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SecurityMigrationCopy other = (SecurityMigrationCopy) obj;
		if (dirName == null) {
			if (other.dirName != null)
				return false;
		} else if (!dirName.equals(other.dirName))
			return false;
		return true;
	}

	@Override
	public int compareTo(SecurityMigrationCopy other) {
		String thisDirName = (dirName != null) ? dirName : "";
		String otherDirName = ((other != null) && (other.getDirName() != null)) ? other.getDirName() : "";
		return thisDirName.compareTo(otherDirName);
	}
	
}