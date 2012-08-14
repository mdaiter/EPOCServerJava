package models;

import java.util.*;

import javax.persistence.*;


import play.db.ebean.*;
import play.data.format.Formats.NonEmpty;
import play.data.validation.Constraints.*;


@Entity
@Table(name = "user")
public class User extends Model{
	
	@Required
	@NonEmpty
	@Email
	@Id
	private String username;

	@Required
	@NonEmpty
	@MinLength(6)
	private String password;
	
	@Required
	@NonEmpty
	private String firstName;

	@Required
	@NonEmpty
	private String lastName;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@MapKey(name = "appUUID")
	private Map <String, UserApp> userAppHash;
		
	public User(){
		this.lastName = null;
		this.firstName = null;
		this.password = null;
		this.username = null;
		this.userAppHash = new HashMap<String, UserApp>();

	}
	
	public User(String lastN, String firstN, String pword, String uname){
		this.lastName = lastN;
		this.firstName = firstN;
		this.password = pword;
		this.username = uname;
		this.userAppHash = new HashMap<String, UserApp>();
	}
	
	public boolean addValueToHash(String key, UserApp app){
		System.out.println("This is the hashmap we're searching for the key: " + this.getUserAppHash());
		if (this.getUserAppHash().containsKey(key) == false && this.getUserAppHash().size() <= 5){
			this.getUserAppHash().put(key, app);
			System.out.println("This is the new hash: " + getUserAppHash());
			return true;
		}
		else{
			return false;
		}
	}
		
	//Get a finder to search for specific elements
	private static Finder<String,User> find = new Finder(
			String.class, User.class
		);
	
	public void setFirstName(String x){
		firstName = x;
	}
	public void setLastName(String x){
		lastName = x;
	}
	public void setUserName(String x){
		username = x;
	}
	public void setPassword(String x){
		password = x;
	}
	
	public static void delete(User user){
		find.ref(user.getUsername()).delete();
	}
	
	public static void create(User user){
		user.save();
		System.out.println("The new hashmap for " + User.search(user.username).getFirstName() + " equals " + User.search(user.username).getUserAppHash());
	}
	
	public void setUserAppHash(Map<String, UserApp> map){
		this.userAppHash = map;
	}
	
	public static User search(String x){
		System.out.println("Starting user database search");
		try{
			return find.ref(x);
		}
		catch (NullPointerException e){
			return null;
		}
	}
	
	
	
	public int getSizeUserAppHash(){
		try{
			return this.getUserAppHash().size();
		}
		catch (NullPointerException e){
			return 0;
		}
	}
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@MapKey(name = "appUUID")
	public Map <String, UserApp> getUserAppHash(){
		try{
			return userAppHash;
		}
		catch (NullPointerException e){
			userAppHash = new HashMap<String, UserApp>();
			return userAppHash;
		}
	}
	
	public Set<String> getUserAppHashKeys(){
		try{
			return userAppHash.keySet();
		}
		catch(NullPointerException e){
			return null;
		}
	}

	public String getPassword(){
		return password;
	}

	public String getUsername(){
		return username;
	}

	public String getFirstName(){
		return firstName;
	}

	public String getLastName(){
		return lastName;
	}
}
