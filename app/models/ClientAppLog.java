package models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

import play.data.format.Formats.NonEmpty;
import play.data.validation.Constraints.Required;

import play.db.ebean.Model;

@Entity
public class ClientAppLog extends Model {

	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;

	@Required
	@NonEmpty
	@Id
	private String clientUUID;
	
	@ManyToOne
	private UserApp uApp = new UserApp();
	
	//Holds all of the apps with data on them
	@OneToMany(mappedBy="cAppLog", cascade = CascadeType.ALL)
	@MapKey(name = "dateOfRecording")
	private Map<Date, UserAppLogItem> appToEmotion = new HashMap<Date, UserAppLogItem>();
	
	public ClientAppLog(){
		uApp = new UserApp();
		clientUUID = new String();
		appToEmotion = new HashMap<Date, UserAppLogItem>();
	}
	
	public boolean addToAppToEmotion(Date x, UserAppLogItem s){
		if (appToEmotion.containsKey(x) == false){
			appToEmotion.put(x, s);
			System.out.println("Addded: " + appToEmotion.get(x) + " to clientLogItem: " + this);
			return true;
		}
		else{
			System.out.println("Couldn't add appLogItem to clientAppLog");
			return false;
		}
	}
	
	public ClientAppLog(String uuid, UserApp u ){
		uApp = u;
		clientUUID = uuid;
		appToEmotion = new HashMap<Date, UserAppLogItem>();
	}
	
	@OneToMany(mappedBy="cAppLog", cascade = CascadeType.ALL)
	@MapKey(name = "appName")
	public Map<Date, UserAppLogItem> getAppToEmotion(){
		try{
			System.out.println("Got the AppToEmotion");
			return appToEmotion;
		}
		catch (NullPointerException e){
			System.out.println("Couldn't get the appToEmotion");
			appToEmotion = new HashMap<Date, UserAppLogItem>();
			return appToEmotion;
		}
	}
	
	public static void create(ClientAppLog c){
		c = new ClientAppLog();
		//c.save();
	}

	public String getClientUUID() {
		return clientUUID;
	}

	public void setClientUUID(String clientUUID) {
		this.clientUUID = clientUUID;
	}

	public UserApp getuApp() {
		return uApp;
	}

	public void setuApp(UserApp uApp) {
		this.uApp = uApp;
	}
}