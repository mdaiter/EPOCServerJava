package models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.avaje.ebean.Ebean;


public class mainModel {
	private JSONModel jsonMod = new JSONModel();
		
	private enum CliUUIDStatus{
		isEmpty, isMultiple, isExisting, isNew
	}
	
	public JSONModel getJsonMod() {
		return jsonMod;
	}

	public ObjectNode processJSON(JsonNode j, ObjectNode objNode, User user){
		if (j != null){
			objNode.put("containsJSON", "true");
			this.jsonMod.setJson(j);
			String UUID = this.jsonMod.getUUIDFromJSON();
			String email = this.jsonMod.getEmailFromJSON();						
			if (UUID == null || UUID == "" || email == null || email == ""){
				objNode.put("appUUID", "null");
				objNode.put("email","null");
				return objNode;
			}
			else{
				objNode.put("email", email);
				objNode.put("appUUID", UUID);
				if(user.getUserAppHash().containsKey(UUID)){
//					//Need to access through User account (UserApps stored in UserId table)
//					UserApp tempUserApp = Ebean.find(User.class, email).getUserAppHash().get(UUID);
					UserApp tempUserApp = user.getUserAppHash().get(UUID);
					System.out.println(tempUserApp);
					System.out.println("The app hash to " + tempUserApp + " is " + tempUserApp.getClientLogItems());
					return this.processApp(tempUserApp, this.getJsonMod(), objNode, email, UUID);	
				}
				else{
					objNode.put("UserApp", "Nonexistent");
					return objNode;
				}
			}
		}
		else{
			objNode.put("containsJSON","false");
			return objNode;
		}
	}
	
	public ObjectNode processApp(UserApp app, JSONModel j, ObjectNode objN, String emai, String uuid){
		CliUUIDStatus cliStats = this.checkStats(j, app, emai, uuid);
		switch(cliStats){
		case isEmpty:
			objN.put("cliUUID", "Empty");
			break;
		case isExisting:
			objN.put("cliUUID", j.getJSON().findValue("clientUUID").toString());
			break;
		case isMultiple:
			objN.put("cliUUID", "Cannot process multiple clients at same time");
			break;
		case isNew:
			String newUUID = app.generateNewClient();
			objN.put("newClient", "true");
    		objN.put("cliUUID", newUUID);
    		
    		//Add new app to UserApp
    		ClientAppLog c = new ClientAppLog(newUUID, app);
    		//c.save();
    		Ebean.save(c);
    		System.out.println(Ebean.find(ClientAppLog.class, newUUID));
    		if (app.addToHash(newUUID, c)){
    			System.out.println("Added client to app hash" + app.getClientLogItems());
    			Ebean.save(app);
    			System.out.println(app.getClientLogItems().get(newUUID));
    		}
    		//app.getClientLogItems().put(newUUID, c);
    		
    		//User.search(emai).getUserAppHash().get(uuid).getClientLogItems().put(newUUID, c);
    		//User.search(emai).getUserAppHash().get(uuid).getClientLogItems().get(newUUID).save();
    		//User.search(emai).getUserAppHash().get(uuid).save();
    		//User.search(emai).save();
    		//app.save();
    		System.out.println(User.search(emai).getUserAppHash().get(uuid).getClientLogItems().get(newUUID));
    		break;
		}
		return objN;
	}
	
	public CliUUIDStatus checkStats(JSONModel t, UserApp ap, String email, String Uuid){
		//Get client stuff		
		short count = 0;
		
		CliUUIDStatus s;
		
		List<JsonNode> tempCliUUIDList = t.getJSON().findValues("clientUUID");
		if (tempCliUUIDList == null){
			s = CliUUIDStatus.isEmpty;
			return s;
		}
		String x = new String();
		for (JsonNode j : tempCliUUIDList){
			x += j.getTextValue();
			count++;
		}
		
		if (count > 1){
			s = CliUUIDStatus.isMultiple;
			System.out.println(count);
			return s;
		}
		
		if (User.search(email).getUserAppHash().get(Uuid).getClientLogItems().containsKey(x)){
			System.out.println(User.search(email).getUserAppHash().get(Uuid).getClientLogItems().containsKey(x));
			s = CliUUIDStatus.isExisting;
			System.out.println(count);
			return s;
		}
		else{
			s = CliUUIDStatus.isNew;			
			System.out.println(count);
			return s;
		}
	}

	public void setJsonMod(JSONModel jsonMod) {
		this.jsonMod = jsonMod;
	}
		
	public mainModel(){
	}
}
