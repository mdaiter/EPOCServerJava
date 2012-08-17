package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import javax.persistence.*;


@Entity
public class UserAppLogItem extends Model {
	
	@Required
	@Temporal(TemporalType.DATE)
	@Column(name="date")
	@Id
	private Date dateOfRecording;
	
	private String appName;
	
	@Required
	private Integer timeHasRun;
	
	@Required
	private String nameOfUserAppLog;
	
	@ManyToOne
	private ClientAppLog cAppLog = new ClientAppLog();
	
//	@ElementCollection(fetch = FetchType.LAZY, targetClass = Byte.class)
////	@ManyToMany(cascade = CascadeType.ALL)
//	@MapKeyClass(String.class)
////	@JoinColumn(name="emoDate", referencedColumnName="date")
//	@CollectionTable(name="emotions", joinColumns=@JoinColumn(name="date_of_recording"))
	@OneToMany(mappedBy = "logItem", cascade = CascadeType.ALL, targetEntity=ByteExtend.class)
	@MapKey(name = "id")
	private Map <Long, ByteExtend> emotions = new HashMap<Long, ByteExtend>();

//	@ManyToMany(cascade = CascadeType.ALL)
//	@JoinColumn(name="immDate", referencedColumnName="date")
//	@ElementCollection(fetch = FetchType.LAZY, targetClass = Boolean.class)
//	@MapKeyClass(String.class)
//    @CollectionTable(name="immediateFacialValues", joinColumns=@JoinColumn(name="date_of_recording"))
	@OneToMany(mappedBy = "logItem", cascade = CascadeType.ALL, targetEntity=BooleanExtend.class)
	@MapKey(name = "id")
	private Map <Long, BooleanExtend> immediateFacialValues = new HashMap<Long, BooleanExtend>();
		
	public UserAppLogItem(){
		setDateOfRecording(new Date());
		timeHasRun = 0;
	}
	
	public UserAppLogItem(ClientAppLog c){
		this.cAppLog = c;
		dateOfRecording = new Date();
		timeHasRun = 0;
	}
	
	public UserAppLogItem(Map<Long, ByteExtend> emo, Map<Long, BooleanExtend> face, ClientAppLog c){
		this.cAppLog = c;
		dateOfRecording = new Date();
		timeHasRun = 0;
		emotions = new HashMap<Long, ByteExtend>(emo);
		System.out.println("Just created emotable: " + emotions + " for userapplogitem: " + this);
		immediateFacialValues = new HashMap<Long, BooleanExtend>(face);
	}
	
	public UserAppLogItem(ClientAppLog c, Date d){
		dateOfRecording = d;
		this.cAppLog = c;
		timeHasRun = 0;
		emotions = new HashMap<Long, ByteExtend>();
		System.out.println("Just created emotable: " + emotions + " for userapplogitem: " + this);
		immediateFacialValues = new HashMap<Long, BooleanExtend>();
	}
	
	public void updateTime(Integer time){
		timeHasRun+=time;
	}
	
//	@ElementCollection(fetch = FetchType.LAZY, targetClass = Byte.class)
//	@MapKeyClass(String.class)
//    @CollectionTable(name="emotions", joinColumns=@JoinColumn(name="date_of_recording"))
//	@ManyToMany(cascade = CascadeType.ALL)
//	@JoinColumn(name="emoDate", referencedColumnName="date")
	@OneToMany(mappedBy = "logItem", cascade = CascadeType.ALL, targetEntity=ByteExtend.class)
	@MapKey(name = "Id")
	public Map<Long, ByteExtend> getEmotions(){
		return emotions;
	}
//	@ElementCollection(fetch = FetchType.LAZY, targetClass = Byte.class)
//	@MapKeyClass(String.class)
//    @CollectionTable(name="emotions", joinColumns=@JoinColumn(name="date_of_recording"))
//	@ManyToMany(cascade = CascadeType.ALL)
//	@JoinColumn(name="immDate", referencedColumnName="date")
	public Map<Long, BooleanExtend> getImmediateFacialValues(){
		return immediateFacialValues;
	}
	
	//Used to update boolean arraylist + immediate array
//	public void updateValuesFacial(Boolean newBool, String emo){
//		//Put in short term list
//		immediateFacialValues.put(emo, new BooleanExtend(newBool));
		
//		//Put in long term list
//		ArrayList<Boolean> temp = longTermFacVal.get(emo);
//		temp.add(newBool);
//		longTermFacVal.put(emo, temp);
	//}
	
	//Once we get new emotions in, update the current averages and add it to the emtionsArr
//	public void updateValuesEmotions(Byte newVal, String emo){
//		//Update average values
//		Byte currAv = emotions.get(emo);
//		Long average = (long) (currAv*(timeHasRun-1));
//		average+=newVal;
//		average = average/timeHasRun;
//
//		Byte x = new Byte(average.byteValue());
//
//		emotions.put(emo, x);
//		
//		//Then add it to the end of the ArrayList
////		ArrayList<Byte> arr = emotionsArr.get(emo);
////		arr.add(newVal);
////		emotionsArr.put(emo, arr);
//	}

	public ClientAppLog getcAppLog() {
		return cAppLog;
	}

	public void setcAppLog(ClientAppLog cAppLog) {
		this.cAppLog = cAppLog;
	}

	public void setEmotions(Map<Long, ByteExtend> x){
		emotions = x;
	}
	
	public void setImmediateFacialValues(Map<Long, BooleanExtend> x){
		immediateFacialValues = x;
	}
	
	public Date getDateOfRecording() {
		return dateOfRecording;
	}

	public void setDateOfRecording(Date dateOfRecording) {
		this.dateOfRecording = dateOfRecording;
	}

	public String getNameOfUserAppLog() {
		return nameOfUserAppLog;
	}

	public void setNameOfUserAppLog(String nameOfUserAppLog) {
		this.nameOfUserAppLog = nameOfUserAppLog;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}	
}
