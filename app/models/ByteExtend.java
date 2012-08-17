package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class ByteExtend extends Model{
	
	/**
	 * Generated serial UID
	 */
	private static final long serialVersionUID = 2755606310239588719L;

	@Required
	@GeneratedValue
	@Id
	private Long id;
	
	@Required
	private String attribute;
	
	@Required
	private Byte byt;

	@ManyToOne
	private UserAppLogItem logItem;
	
	public ByteExtend(Byte b, UserAppLogItem lI){
		byt = b;
		logItem = lI;
	}
	
	public ByteExtend(Byte b){
		byt = b;
	}
	
	public Byte getByte() {
		return byt;
	}

	public void setByte(Byte bool) {
		this.byt = bool;
	}
	@ManyToOne
	public UserAppLogItem getLogItem() {
		return logItem;
	}

	public void setLogItem(UserAppLogItem logItem) {
		this.logItem = logItem;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
