package models;

import java.util.*;

import play.data.format.Formats.NonEmpty;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import javax.persistence.*;

@Entity
public class UserApp extends Model {
	
	@Required
	@NonEmpty
	private String appNam;
	
	@Required
	@NonEmpty
	@Id
	private String appUUID;
	
	@ManyToOne
	private User user = new User();
	
	//Where to store all of the data from clients
	@OneToMany(mappedBy = "uApp", cascade = CascadeType.ALL, targetEntity=ClientAppLog.class)
	@MapKey(name = "clientUUID")
	private Map<String, ClientAppLog> clientLogItems = new HashMap<String, ClientAppLog>();

	public String getAppNam(){
		return appNam;
	}
	
	public String generateNewClient(){
		String newUUIDCli = UUID.randomUUID().toString();
		clientLogItems.put(newUUIDCli, new ClientAppLog());
		return newUUIDCli;
	}

	public boolean addToHash(String key, ClientAppLog app){
		if (!this.getClientLogItems().containsKey(key)){
			this.getClientLogItems().put(key, app);
			System.out.println("Could add app to client hash");
			System.out.println("Here's the hash: " + this.getClientLogItems().get(key));
			return true;
		}
		else{
			System.out.println("Couldn't add the app to client hash");
			return false;
		}
	}
	
	@OneToMany(mappedBy = "uApp", cascade = CascadeType.ALL, targetEntity=ClientAppLog.class)
	@MapKey(name = "clientUUID")
	public Map<String, ClientAppLog> getClientLogItems(){
		try{
			return clientLogItems;
		}
		catch (PersistenceException e){
			return null;
		}
		catch (NullPointerException e){
			clientLogItems = new HashMap<String, ClientAppLog>();
			return clientLogItems;
		}
	}
	
	private static Finder<String,UserApp> find = new Finder(
			String.class, UserApp.class
		);

	public String getappUUID(){
		return appUUID;
	}

	public void setAppUUID(String x){
		appUUID = x;
	}
	
	public static UserApp findAppWithUUID(String i){
		try{
			return find.ref(i);
		}
		catch (PersistenceException e){
			return null;
		}
	}
	
	public UserApp(){
		this.setAppUUID(UUID.randomUUID().toString());
		//this.save();
	}
	
	public UserApp(String x){
		this.setAppUUID(UUID.randomUUID().toString());
		this.appNam = x;
		this.clientLogItems = new HashMap<String, ClientAppLog>();
		//this.save();
	}

	//Creates userApp object
	public static void create(UserApp userApp){
		userApp.setAppUUID(UUID.randomUUID().toString());
		userApp.save();
	}
	
	public static void delete(Long id){
		//find.ref(id).delete();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
