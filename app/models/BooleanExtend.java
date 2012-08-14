package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/*
 * Basically allows me to save booleans
 */

@Entity
public class BooleanExtend extends Model{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2155640047718454658L;

	@Required
	@Id
	@GeneratedValue
	private Long id;
	
	@Required
	private String attribute;
	
	@Required
	private Boolean bool;

	@ManyToOne
	private UserAppLogItem logItem;
	

	public BooleanExtend(Boolean b, UserAppLogItem lI){
		bool = b;
		logItem = lI;
	}
	
	public BooleanExtend(Boolean b){
		bool = b;
	}
	
	public Boolean getBool() {
		return bool;
	}

	public void setBool(Boolean bool) {
		this.bool = bool;
	}
	@ManyToOne
	public UserAppLogItem getLogItem() {
		return logItem;
	}

	public void setLogItem(UserAppLogItem logItem) {
		this.logItem = logItem;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
}
