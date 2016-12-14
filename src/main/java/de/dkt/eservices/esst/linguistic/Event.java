package de.dkt.eservices.esst.linguistic;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

public class Event {

	Entity subject;
	Entity predicate;
	Entity object;
	Date timestamp;
	double relevance;
	
	public Event() {
		subject = new Entity();
		predicate = new Entity();
		object = new Entity();
		timestamp = new Date();
		relevance = 0;
	}

	public Event(Entity subject, Entity predicate, Entity object, Date timestamp, double relevance) {
		super();
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
		this.timestamp = timestamp;
		this.relevance = relevance;
	}

	public Event(JSONObject obj) {
		super();
		this.subject = new Entity(obj.getJSONObject("subject"));
		this.predicate = new Entity(obj.getJSONObject("predicate"));
		this.object = new Entity(obj.getJSONObject("object"));
		DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
		try{
			this.timestamp = df.parse(obj.getString("timestamp"));
		}
		catch(Exception e){
			this.timestamp = new Date();
		}
		this.relevance = obj.getDouble("relevance");
	}
	
	public JSONObject getJSONObject(){
		JSONObject obj = new JSONObject();
		obj.put("subject", subject.getJSONObject());
		obj.put("predicate", predicate.getJSONObject());
		obj.put("object", object.getJSONObject());
		DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
		try{
			obj.put("timestamp", df.format(timestamp));
		}
		catch(Exception e){
			obj.put("timestamp", new Date());
		}
		obj.put("relevance", relevance);
		return obj;
	}

	public JSONObject getEmptyTemplateJSONObject(){
		JSONObject obj = new JSONObject();
		obj.put("subject", subject.getType().toString());
		obj.put("predicate", predicate.getText().toString());
		obj.put("object", object.getType().toString());
		obj.put("relevance", relevance);
		return obj;
	}

	public Entity getSubject() {
		return subject;
	}

	public void setSubject(Entity subject) {
		this.subject = subject;
	}

	public Entity getPredicate() {
		return predicate;
	}

	public void setPredicate(Entity predicate) {
		this.predicate = predicate;
	}

	public Entity getObject() {
		return object;
	}

	public void setObject(Entity object) {
		this.object = object;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public double getRelevance() {
		return relevance;
	}

	public void setRelevance(double relevance) {
		this.relevance = relevance;
	}

	public boolean entityIsInvolved(Entity e) {
		if(subject.equals(e) || object.equals(e)){
			return true;
		}
		else{
			return false;
		}
	}

	public boolean entityTypeIsInvolved(Entity e) {
		if(subject.getType()==e.getType() || object.getType()==e.getType()){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Event){
			Event e = (Event) obj;
//			if(e.getSubject().equals(subject) && e.getPredicate().equals(predicate) 
////					&& e.getObject().equals(object) && e.getTimestamp().equals(timestamp)
//					&& e.getObject().equals(object) 
//					){
//					//&& e.getRelevance()==relevance){
//			System.out.println(getSubject().getType()+"("+getPredicate().getText()+")"+getObject().getType());
//			System.out.println("----->"+e.getSubject().getType()+"("+e.getPredicate().getText()+")"+e.getObject().getType());
			if(e.getSubject().getType()==subject.getType() && e.getPredicate().getText().equals(predicate.getText()) 
					&& e.getObject().getType()==object.getType() 
					){
					//&& e.getRelevance()==relevance){
				return true;
			}
			else{
				return false;
			}
		}
		return false;
	}
}
