package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Locale;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import com.avaje.ebean.Ebean;

import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import models.BooleanExtend;
import models.ByteExtend;
import models.ClientAppLog;
import models.JSONModel;
import models.User;
import models.UserApp;
import models.UserAppLogItem;

public class JSONController extends Controller{
	
	private static JSONModel jsonMod = new JSONModel();
	
	private static CliUUIDStatus cliStats;
	
	private enum CliUUIDStatus{
		isEmpty, isMultiple, isExisting, isNew
	}
	
	private enum ReceiveAndPostValue{
		date, emotions, post, emoAndDate, noValue
	}
	
	private static ReceiveAndPostValue recAndPostVal;
	
	public static JSONModel getJsonMod() {
		return jsonMod;
	}
	
	//private static JSONModel modeller = new JSONModel();
	
	//private static mainModel modelInterface = new mainModel();
	
	@play.db.ebean.Transactional
	public static Result generate(){
		JsonNode json = request().body().asJson();
		ObjectNode result = Json.newObject();
		if (json.toString() == ""){
			result.put("message", "null");
			return badRequest(result);
		}
		else {
			//Set modeler to process and store JSON stuff...
			ObjectNode n = JSONController.processJSON(request().body().asJson(), result);
			return ok(n);
		}
	}
	
	@Transactional
	public static ObjectNode processApp(UserApp app, JSONModel j, ObjectNode objN, String emai, String uuid){
		cliStats = JSONController.checkStats(j, app, emai, uuid);
		switch(cliStats){
		case isEmpty:
			objN.put("cliUUID", "Empty");
			break;
		case isExisting:
			String currUUID =  j.getJSON().findValue("clientUUID").toString();
			currUUID = currUUID.substring(1, currUUID.length() - 1);
			objN.put("cliUUID", currUUID);			
			
			JSONController.checkAppLog(Ebean.find(ClientAppLog.class, currUUID), objN, j);
			
			break;
		case isMultiple:
			objN.put("cliUUID", "Cannot process multiple clients at same time");
			break;
		case isNew:
			String newUUID = app.generateNewClient();
			objN.put("newClient", "true");
    		objN.put("cliUUID", newUUID);
    		
    		//Add new app to UserApp
    		ClientAppLog cl = new ClientAppLog(newUUID, app);
    		//c.save();
    		Ebean.save(cl);
    		System.out.println("Ebean found this ClientAppLog: " + Ebean.find(ClientAppLog.class, newUUID));
//    		if (app.addToHash(newUUID, c)){
//    			System.out.println("Added client to app hash" + app.getClientLogItems());
//    			Ebean.save(app);
//    			System.out.println(app.getClientLogItems().get(newUUID));
//    		}
//    		System.out.println(Ebean.find(User.class, emai).getUserAppHash().get(uuid).getClientLogItems().get(newUUID));
//    		System.out.println(Ebean.find(User.class, emai).getUserAppHash().get(uuid).getClientLogItems().get(newUUID).getAppToEmotion());
    		break;
		}
		return objN;
	}
	
	public static void putKeysAndValuesInJSON(String emoList, ObjectNode oN, String appName, ClientAppLog clientApp, boolean isDate, Date beginDate, Date endDate, JsonNodeFactory factory, boolean isAverage){
		ArrayNode timeBundler = new ArrayNode(factory);
		if (emoList.equals("All")){
			for (UserAppLogItem i : clientApp.getAppToEmotion().values()){
				System.out.println(i);
				System.out.println("Keys are: " + i.getEmotions().keySet());
				System.out.println("Values are: " + i.getEmotions().values());
				ArrayNode arrAttrBundler = new ArrayNode(factory);
												
				for (Long key : i.getEmotions().keySet()){
					System.out.println("Going through keyset with key: " + key + " " + i.getEmotions().get(key).getByte());
					ObjectNode obj = new ObjectNode(factory);
					obj.put(i.getEmotions().get(key).getAttribute(), i.getEmotions().get(key).getByte());
					
					arrAttrBundler.add(obj);
				}
				for (Long key : i.getImmediateFacialValues().keySet()){
					System.out.println(key + " " + i.getImmediateFacialValues().get(key).getBool());
					ObjectNode obj = new ObjectNode(factory);
				
					obj.put(i.getImmediateFacialValues().get(key).getAttribute(), i.getImmediateFacialValues().get(key).getBool());
					
					arrAttrBundler.add(obj);
				}
				if (isDate == false){
					
					if (i.getAppName() != null && !i.getAppName().isEmpty()){
						ObjectNode appNameObj = new ObjectNode(factory);
						appNameObj.put("appName", i.getAppName());
						arrAttrBundler.add(appNameObj);
					}
					
					ObjectNode timeObj = new ObjectNode(factory);
					if (appName != null && !appName.isEmpty()){
						if (appName.equals(i.getAppName())){
							timeObj.put(i.getDateOfRecording().toString(), arrAttrBundler);	
							timeBundler.add(timeObj);
							System.out.println("New timeBundler: " + timeBundler);
						}
						else{
							System.out.println("Not the right name for the application");
						}
					}
					else{
						timeObj.put(i.getDateOfRecording().toString(), arrAttrBundler);	
						timeBundler.add(timeObj);
						System.out.println("New timeBundler: " + timeBundler);
					}
				}
				else if (i.getDateOfRecording().before(endDate) && i.getDateOfRecording().after(beginDate)){
					System.out.println("Qualified for the date!");
					if (i.getAppName() != null && !i.getAppName().isEmpty()){
						ObjectNode appNameObj = new ObjectNode(factory);
						appNameObj.put("appName", i.getAppName());
						arrAttrBundler.add(appNameObj);
					}
					ObjectNode timeObj = new ObjectNode(factory);
					if (appName != null && !appName.isEmpty()){
						if (appName.equals(i.getAppName())){
							timeObj.put(i.getDateOfRecording().toString(), arrAttrBundler);	
							timeBundler.add(timeObj);
							System.out.println("New timeBundler: " + timeBundler);
						}
					}
					else{
						timeObj.put(i.getDateOfRecording().toString(), arrAttrBundler);	
						timeBundler.add(timeObj);
						System.out.println("New timeBundler: " + timeBundler);
					}
				}
			}
			ObjectNode dataNode = new ObjectNode(factory);
			dataNode.put("data", timeBundler);
			
			oN.put(clientApp.getClientUUID().toString(), dataNode);
		}
		else if (emoList.equals("")){
			oN.put("Error", "No emotion specifier detected");
		}
		else{
				for (UserAppLogItem i : clientApp.getAppToEmotion().values()){
					boolean isEqual = false;
					System.out.println(i);
					System.out.println("Keys are: " + i.getEmotions().keySet());
					System.out.println("Values are: " + i.getEmotions().values());
					
					ArrayNode arrAttrBundler = new ArrayNode(factory);
										
					for (Long key : i.getEmotions().keySet()){
						if (i.getEmotions().containsKey(key) && i.getEmotions().get(key).getAttribute().equals(emoList)){
							System.out.println("Going through keyset with key: " + key + " " + i.getEmotions().get(key).getByte());
							ObjectNode obj = new ObjectNode(factory);
							obj.put(i.getEmotions().get(key).getAttribute(), i.getEmotions().get(key).getByte());
							//Add to array
							arrAttrBundler.add(obj);
							isEqual = true;
						}
					}
					for (Long key : i.getImmediateFacialValues().keySet()){
						if (i.getImmediateFacialValues().containsKey(key) && i.getImmediateFacialValues().get(key).getAttribute().equals(emoList)){
							System.out.println(key + " " + i.getImmediateFacialValues().get(key).getBool());
							ObjectNode obj = new ObjectNode(factory);
						
							obj.put(i.getImmediateFacialValues().get(key).getAttribute(), i.getImmediateFacialValues().get(key).getBool());
							
							arrAttrBundler.add(obj);
							isEqual = true;
						}
					}
					if (isEqual){
						if (isDate == false || (i.getDateOfRecording().after(beginDate) && i.getDateOfRecording().before(endDate))){
							
							if (i.getAppName() != null && !i.getAppName().isEmpty()){
								ObjectNode appNameObj = new ObjectNode(factory);
								appNameObj.put("appName", i.getAppName());
								arrAttrBundler.add(appNameObj);
							}
							ObjectNode timeObj = new ObjectNode(factory);
							if (appName != null && !appName.isEmpty()){
								System.out.println("App name specified: " + appName);
								if (appName.equals(i.getAppName())){
									timeObj.put(i.getDateOfRecording().toString(), arrAttrBundler);	
									timeBundler.add(timeObj);
								}
								else{
									System.out.println("Match " + appName + " with " + i.getAppName() + "was: " + new Boolean(appName.equals(i.getAppName())));
								}
							}
							else{
								System.out.println("No app name specified");
								timeObj.put(i.getDateOfRecording().toString(), arrAttrBundler);	
								timeBundler.add(timeObj);
							}
						}
					}
				}
			ObjectNode dataNode = new ObjectNode(factory);
			dataNode.put("data", timeBundler);
			oN.put(clientApp.getClientUUID().toString(), dataNode);	
		}
	}
	
	public static void putEmoInReturn(JSONModel j, String emoList, ObjectNode oN, ClientAppLog clientApp, boolean isDate, Date beginDate, Date endDate){
		System.out.println(clientApp.getAppToEmotion().values());
		//Data bundler for data creation
		JsonNodeFactory factory = JsonNodeFactory.instance;
		//Bundles groups of time data together
		
		JsonNode userAllJson = j.getJSON().findValue("UserAll");
		JsonNode appNameJson = j.getJSON().findValue("AppName");
		
		boolean isAverage = false;
		if (j.getJSON().findValue("Average") != null){
			isAverage = j.getJSON().findValue("Average").asBoolean();
		}
		
		if (userAllJson != null && userAllJson.asBoolean() == true){
			System.out.println("UserAll equals true");
			if (appNameJson != null){
				for (ClientAppLog c : clientApp.getuApp().getClientLogItems().values()){
					JSONController.putKeysAndValuesInJSON(emoList, oN, appNameJson.asText(), c, isDate, beginDate, endDate, factory, isAverage);
				}
			}
			else{
				for (ClientAppLog c : clientApp.getuApp().getClientLogItems().values()){
					JSONController.putKeysAndValuesInJSON(emoList, oN, new String(), c, isDate, beginDate, endDate, factory, isAverage);
				}
			}
		}
		else{
			System.out.println("UserAll equals false");
			if (appNameJson != null){
				JSONController.putKeysAndValuesInJSON(emoList, oN, appNameJson.asText(), clientApp, isDate, beginDate, endDate, factory, isAverage);
			}
			else{
				JSONController.putKeysAndValuesInJSON(emoList, oN, new String(), clientApp, isDate, beginDate, endDate, factory, isAverage);
			}
		}
	}

	public static void dateInReturn(ClientAppLog clientApp, ObjectNode oN, JSONModel jModel, String searchString){
		System.out.println("Starting EmoAndDate");
		System.out.println(clientApp);
		List<JsonNode> dateLBegin = jModel.getJSON().findValues("DateBegin");
		List<JsonNode> dateLEnd = jModel.getJSON().findValues("DateEnd");
		System.out.println("Date begin" + dateLBegin);
		System.out.println("Date end" + dateLEnd);
		if (dateLBegin.isEmpty() || dateLEnd.isEmpty()){
			oN.put("Error", "No date value found");
		}
		else if (dateLBegin.size() > 1 || dateLEnd.size() > 1){
			oN.put("Error", "Your dates specified are larger than two (the maximum)");
		}
		else if(dateLBegin.size() < 1 || dateLEnd.size() < 1){
			oN.put("Error", "You didn't specify enough dates (2).");
		}
		else{
			/*
			 * Date part
			 */
			String firstDateString = dateLBegin.get(0).toString();
			String secondDateString = dateLEnd.get(0).toString();
			
			Date firstDate = null;
			Date secondDate = null;
			try {
				firstDate = new SimpleDateFormat("\"MMM dd, yyyy\"", Locale.ENGLISH).parse(firstDateString);
				
				secondDate = new SimpleDateFormat("\"MMM dd, yyyy\"", Locale.ENGLISH).parse(secondDateString);
				System.out.println(firstDate + " to " + secondDate);
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Make sure we actually have 
			//if (clientApp.getAppToEmotion().keySet().contains(firstDate) && clientApp.getAppToEmotion().keySet().contains(secondDate)){
				System.out.println("Valid dates");
				List<JsonNode> emoL = jModel.getJSON().findValues("Emotions");
				System.out.println("EmoList is: " + emoL.get(0).toString());
				JSONController.putEmoInReturn(jModel, searchString, oN, clientApp, true, firstDate, secondDate);
			//}
		}
	}
	
	//Do stuff based on what we need to send back
	@play.db.ebean.Transactional
	public static void checkAppLog(ClientAppLog clientApp, ObjectNode oN, JSONModel jModel){
		recAndPostVal = JSONController.checkReceiveAndPost(jModel);
		System.out.println(recAndPostVal);
		switch (recAndPostVal){
		case emotions:
			System.out.println("Triggered emotion return");
			List<JsonNode> emoList = jModel.getJSON().findValues("Emotions");
			
			String cliUUID = jsonMod.getJSON().findValue("clientUUID").toString();
			cliUUID = cliUUID.substring(1, cliUUID.length() - 1);
			
			JSONController.putEmoInReturn(jModel, emoList.get(0).toString().substring(1, emoList.get(0).toString().length()-1), oN, Ebean.find(ClientAppLog.class, cliUUID), false, null, null);
			System.out.println(emoList.get(0).toString().substring(1, emoList.get(0).toString().length()-1));
			break;
		//If there's an emotion and date
		case emoAndDate:
			List<JsonNode> emoL = jModel.getJSON().findValues("Emotions");
			System.out.println("EmoList is: " + emoL.get(0).toString());
			JSONController.dateInReturn(clientApp, oN, jModel, emoL.get(0).toString().substring(1, emoL.get(0).toString().length()-1));
			
			break;
			
		case post:
			JsonNode postList = jModel.getJSON().findValue("Post");
			
			System.out.println(postList);
			Map<Long, BooleanExtend> tempWebCamMap = new HashMap<Long, BooleanExtend>();
			Map <Long, ByteExtend> tempEPOCMap = new HashMap<Long, ByteExtend>();
				
			User user = User.search(jsonMod.getEmailFromJSON());
			
			String clientUUID = jsonMod.getJSON().findValue("clientUUID").toString();
			clientUUID = clientUUID.substring(1, clientUUID.length() - 1);
			UserAppLogItem u = new UserAppLogItem(user.getUserAppHash().get(jsonMod.getUUIDFromJSON()).getClientLogItems().get(clientUUID));
			
			u.save();
			
			Iterator<String> keys = postList.getFieldNames();
			while (keys.hasNext()){
				String tempKeyMaster = keys.next().toString();
				System.out.println(tempKeyMaster);
				//System.out.println("Key is: " + keys.next().toString());
				Iterator<String> keySpecific = postList.get(tempKeyMaster).getFieldNames();
				System.out.println("Created keySpecific");
				if (tempKeyMaster == "EPOC"){
					System.out.println("Has EPOC");
					while (keySpecific.hasNext()){
						String tempKeyAttr = keySpecific.next().toString();
						Byte b = new Byte(postList.get(tempKeyMaster).get(tempKeyAttr).asText());
						System.out.println("EPOC has next: " + tempKeyAttr + "\nvalue: " + b);
						
						ByteExtend bEx = new ByteExtend(b, u);
						bEx.setAttribute(tempKeyAttr);
						
						System.out.println(bEx.getByte() + " " + bEx.getLogItem());
						
						System.out.println(bEx.getAttribute());
						
						bEx.save();
						
						tempEPOCMap.put(bEx.getId(), bEx);								
					}
				}
				else if (tempKeyMaster == "WebCam"){
					System.out.println("Has WebCam");
					while (keySpecific.hasNext()){
						String tempKeyAttr = keySpecific.next().toString();
						Boolean b = new Boolean(postList.get(tempKeyMaster).get(tempKeyAttr).asText());
						System.out.println("WebCam has next with value: " + b);
						
						BooleanExtend bEx = new BooleanExtend(b, u);
						bEx.setAttribute(tempKeyAttr);
						bEx.save();
						
						tempWebCamMap.put(bEx.getId(), bEx);
					}
				}
				else{
					System.out.println("Didn't fall under anything");
				}
			}
			System.out.println("Starting the save");
			System.out.println(user.getUserAppHash());
			System.out.println(user.getUserAppHash().get(jsonMod.getUUIDFromJSON()).getClientLogItems());
			System.out.println(user.getUserAppHash().get(jsonMod.getUUIDFromJSON()).getClientLogItems().get(clientUUID));
			//System.out.println(user.getUserAppHash().get(jsonMod.getUUIDFromJSON()).getClientLogItems().get(jsonMod.getJSON().findValue("clientUUID").toString()).getAppToEmotion());
			
			System.out.println(tempEPOCMap);
			System.out.println(tempWebCamMap);
			
			u.setEmotions(tempEPOCMap);
			u.setImmediateFacialValues(tempWebCamMap);
			
			System.out.println(u.getEmotions());
			
			//So you don't need to set the app name
			if (!jsonMod.getJSON().findValues("appName").isEmpty()){
				u.setAppName(jsonMod.getJSON().findValues("appName").get(0).asText());
			}
			
			if (!jsonMod.getJSON().findValues("Date").isEmpty()){
				if (!jsonMod.getJSON().findValues("ActualDate").isEmpty()){
					Date parsed = new Date();
					try {
					    SimpleDateFormat format =
					        new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
					    parsed = format.parse(jsonMod.getJSON().findValue("Date").asText());
					}
					catch(ParseException pe) {
					    throw new IllegalArgumentException();
					}
					finally{
						u.setDateOfRecording(parsed);
					}
				}
				else{
					Date parsed = new Date(jsonMod.getJSON().findValue("Date").asLong());
					u.setDateOfRecording(parsed);
				}
			}
			
			u.save();
			
			Ebean.find(UserAppLogItem.class, u.getDateOfRecording()).setEmotions(tempEPOCMap);
			
			Ebean.find(UserAppLogItem.class, u.getDateOfRecording()).save();
			
			Ebean.find(UserAppLogItem.class, u.getDateOfRecording()).setImmediateFacialValues(tempWebCamMap);
			
			Ebean.find(UserAppLogItem.class, u.getDateOfRecording()).save();
			
			System.out.println("After save: " + Ebean.find(UserAppLogItem.class, u.getDateOfRecording()).getEmotions() + " " + Ebean.find(UserAppLogItem.class, u.getDateOfRecording()).getDateOfRecording());
			
			System.out.println(clientUUID);
			
//			System.out.println(user.getUserAppHash().get(jsonMod.getUUIDFromJSON()).getClientLogItems().get(clientUUID).getAppToEmotion());
//			user.save();
//			System.out.println(Ebean.find(ClientAppLog.class, clientUUID).getAppToEmotion().values());
//			
//			for (Date key : Ebean.find(ClientAppLog.class, clientUUID).getAppToEmotion().keySet()){
//				System.out.println(key + " " + Ebean.find(ClientAppLog.class, clientUUID).getAppToEmotion().get(key).getEmotions());
//			}
			
			break;
		
		case date:
			JSONController.dateInReturn(clientApp, oN, jModel, "All");
			break;

		case noValue:
			oN.put("Error", "No value to be returned");
			break;
		}
	}
	
	public static ReceiveAndPostValue checkReceiveAndPost(JSONModel a){
		if (a.getJSON().findValues("Emotions") == null || a.getJSON().findValues("Emotions").isEmpty()){
			if (a.getJSON().findValues("DateBegin") == null || a.getJSON().findValues("DateBegin").isEmpty()
					|| a.getJSON().findValues("DateEnd") == null || a.getJSON().findValues("DateEnd").isEmpty()){
				if (a.getJSON().findValues("Post") == null || a.getJSON().findValues("Post").isEmpty()){
					System.out.println("No value found for any action to take place...");
					return ReceiveAndPostValue.noValue;
				}
				else{
					System.out.println("Posted");
					return ReceiveAndPostValue.post;
				}
			}
			else{
				System.out.println("Date");
				return ReceiveAndPostValue.date;
			}
		}
		else{
			if (a.getJSON().findValues("DateBegin") == null || a.getJSON().findValues("DateBegin").isEmpty()
					|| a.getJSON().findValues("DateEnd") == null || a.getJSON().findValues("DateEnd").isEmpty()){
				System.out.println("Emotions");
				return ReceiveAndPostValue.emotions;
			}
			else{
				System.out.println("EmoAndDate found");
				return ReceiveAndPostValue.emoAndDate;
			}
		}
	}
	
	@Transactional
	public static CliUUIDStatus checkStats(JSONModel t, UserApp ap, String email, String Uuid){
		//Get client stuff		
		short count = 0;
		
		CliUUIDStatus s;
		
		List<JsonNode> tempCliUUIDList = t.getJSON().findValues("clientUUID");
		if (tempCliUUIDList == null || tempCliUUIDList.isEmpty()){
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
			System.out.println("Found: " + User.search(email).getUserAppHash().get(Uuid).getClientLogItems().get(x));
			s = CliUUIDStatus.isExisting;
			System.out.println("The count of the hashmap is: " + count);
			System.out.println("ClientAppLog exists.");
			return s;
		}
		else{
			s = CliUUIDStatus.isNew;			
			System.out.println(count);
			return s;
		}
	}

	public static void setJsonMod(JSONModel jsonMod) {
		JSONController.jsonMod = jsonMod;
	}
	
	@Transactional
	public static ObjectNode processJSON(JsonNode j, ObjectNode objNode){
		if (j != null){
			objNode.put("containsJSON", "true");
			JSONController.jsonMod.setJson(j);
			String UUID = JSONController.jsonMod.getUUIDFromJSON();
			String email = JSONController.jsonMod.getEmailFromJSON();						
			if (UUID == null || UUID == "" || email == null || email == ""){
				objNode.put("appUUID", "null");
				objNode.put("email","null");
				return objNode;
			}
			else{
				objNode.put("email", email);
				objNode.put("appUUID", UUID);
				if(Ebean.find(User.class, email).getUserAppHash().containsKey(UUID)){
//					//Need to access through User account (UserApps stored in UserId table)
//					UserApp tempUserApp = Ebean.find(User.class, email).getUserAppHash().get(UUID);
					UserApp tempUserApp = User.search(email).getUserAppHash().get(UUID);
					System.out.println(tempUserApp);
					System.out.println("The app hash to " + tempUserApp + " is " + tempUserApp.getClientLogItems());
					ObjectNode cliS = JSONController.processApp(tempUserApp, JSONController.getJsonMod(), objNode, email, UUID);
					return cliS;
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
	
	public static Result index(){
		return badRequest("{Error : 404} (This page is meant for POST data. It should not be used as a GET page...)");
	}
}
