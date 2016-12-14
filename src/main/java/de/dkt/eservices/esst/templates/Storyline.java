package de.dkt.eservices.esst.templates;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import de.dkt.eservices.esst.linguistic.Entity;
import de.dkt.eservices.esst.linguistic.Event;

public class Storyline {

	Date dateInitial;
	Date dateFinal;
	String name;
	Entity mainCharacter;
	List<Event> events;

	public Storyline() {
		events = new LinkedList<Event>();
	}

	public Storyline(JSONObject obj) {
		DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
		try{
			this.dateInitial = df.parse(obj.getString("dateInitial"));
		}
		catch(Exception e){
			this.dateInitial = null;
		}
		try{
			this.dateFinal = df.parse(obj.getString("dateFinal"));
		}
		catch(Exception e){
			this.dateFinal = null;
		}
		
		mainCharacter = new Entity(obj.getJSONObject("mainCharacter"));
		name = obj.getString("name");
		events = new LinkedList<Event>();
		JSONArray eventsArray = obj.getJSONArray("events");
		for (int i = 0; i < eventsArray.length(); i++) {
			events.add(new Event(eventsArray.getJSONObject(i)));
		}
	}

	public Storyline(String name, Date dateInitial, Date dateFinal, Entity mainCharacter, List<Event> events) {
		super();
		this.name = name;
		this.dateInitial = dateInitial;
		this.dateFinal = dateFinal;
		this.mainCharacter = mainCharacter;
		if(events==null){
			this.events = new LinkedList<Event>();
		}
		else{
			this.events = events;
		}
	}

	public JSONObject getJSONObject(){
		DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
		JSONObject obj = new JSONObject();
		if(dateInitial!=null){
			obj.put("dateInitial", df.format(dateInitial));
		}
		else{
			obj.put("dateInitial", "EMPTY");
		}
		if(dateFinal!=null){
			obj.put("dateFinal", df.format(dateFinal));
		}
		else{
			obj.put("dateFinal", "EMPTY");
		}
		obj.put("mainCharacter", mainCharacter.getJSONObject());
		JSONArray events = new JSONArray();
		for (Event event : this.events) {
			events.put(event.getJSONObject());
		}
		obj.put("events", events);
		return obj;
	}

	public JSONObject getEmptyTemplateJSONObject(){
		JSONObject obj = new JSONObject();
		if(mainCharacter==null){			
			obj.put("mainCharacter", "null");
		}
		else{
			obj.put("mainCharacter", mainCharacter.getType().toString());
		}
		JSONArray events = new JSONArray();
		for (Event event : this.events) {
			events.put(event.getEmptyTemplateJSONObject());
		}
		obj.put("events", events);
		return obj;
	}

	public boolean fits(Event e) {
		
		
		// TODO Auto-generated method stub
		
		
		return false;
	}

	public void addEvent(Event e) {
		events.add(e);
	}

	public Date getDateInitial() {
		return dateInitial;
	}

	public void setDateInitial(Date dateInitial) {
		this.dateInitial = dateInitial;
	}

	public Date getDateFinal() {
		return dateFinal;
	}

	public void setDateFinal(Date dateFinal) {
		this.dateFinal = dateFinal;
	}

	public Entity getMainCharacter() {
		return mainCharacter;
	}

	public void setMainCharacter(Entity mainCharacter) {
		this.mainCharacter = mainCharacter;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}
	
}
