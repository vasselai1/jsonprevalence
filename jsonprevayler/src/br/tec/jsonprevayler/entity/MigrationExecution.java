package br.tec.jsonprevayler.entity;

import java.util.Date;

public class MigrationExecution implements PrevalenceEntity {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Integer lineNuber;
	private String originalLine;
	private Date date;
	private String detail;
	
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	@Override
	public Long getId() {		
		return id;
	}
	public Integer getLineNuber() {
		return lineNuber;
	}
	public void setLineNuber(Integer lineNuber) {
		this.lineNuber = lineNuber;
	}
	public String getOriginalLine() {
		return originalLine;
	}
	public void setOriginalLine(String originalLine) {
		this.originalLine = originalLine;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		MigrationExecution other = (MigrationExecution) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (originalLine == null) {
			if (other.originalLine != null)
				return false;
		} else if (!originalLine.equals(other.originalLine))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "MigrationExecution [id=" + id + ", lineNuber=" + lineNuber + ", originalLine=" + originalLine + ", date=" + date + ", detail=" + detail + "]";
	}
		
}