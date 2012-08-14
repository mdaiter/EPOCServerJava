package models;

import java.util.List;

import org.codehaus.jackson.JsonNode;

public class JSONModel {
	
	private JsonNode json;
	
	//Only need it to allocate memory to the class
	public JSONModel(){}
	
	public void setJson(JsonNode jsonTemp){
		json = jsonTemp;
	}
	
	public JsonNode getJSON(){
		return json;
	}
	
	public String getUUIDFromJSON(){
		//Show JSON sent
		List<JsonNode> tempList = json.findValues("appUUID");
		String x = new String();
		for (JsonNode j : tempList){
			x += j.getTextValue();
		}
		return x;
	}
	public String getEmailFromJSON(){
		//Show JSON sent
		List<JsonNode> tempList = json.findValues("email");
		String x = new String();
		for (JsonNode j : tempList){
			x += j.getTextValue();
		}
		return x;
	}
	
}
