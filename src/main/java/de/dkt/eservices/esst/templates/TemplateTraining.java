package de.dkt.eservices.esst.templates;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import de.dkt.common.niftools.ITSRDF;
import de.dkt.common.niftools.NIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.eservices.esst.linguistic.Entity;
import de.dkt.eservices.esst.linguistic.EntityType;
import de.dkt.eservices.esst.linguistic.Event;
import de.dkt.eservices.esst.ontology.NIFManagement;
import de.dkt.eservices.esst.templates.training.ARFFGeneration;
import eu.freme.common.exception.BadRequestException;

public class TemplateTraining {

	public StoryTemplate trainTemplate(List<Model> documents,String version, double threshold){
//		for (Model m : documents) {
//			Map<String,Map<String,String>> entitiesMap = NIFReader.extractEntitiesExtended(m);
//			System.out.println(entitiesMap.size());
//			Map<String,Event> eventsMap = NIFManagement.extractEventsExtended(m);
//			System.out.println(eventsMap.size());
//		}
		Model collection = de.dkt.common.niftools.NIFManagement.createCollectionFromDocuments("", documents);
//		Map<String,Map<String,String>> entitiesMap = NIFReader.extractEntitiesExtended(collection);
//		System.out.println(entitiesMap.size());
//		Map<String,Event> eventsMap = NIFManagement.extractEventsExtended(collection);
//		System.out.println(eventsMap.size());
		return trainTemplate(collection, documents, version, threshold);
	}
	
	public StoryTemplate trainTemplate(Model collection,List<Model> documents,String version, double threshold){
		if(version.equalsIgnoreCase("v1")){
			return trainTemplateV1(collection,threshold);
		}
		else if(version.equalsIgnoreCase("v2")){
			return trainTemplateV2(collection,documents,threshold);
		}
		else if(version.equalsIgnoreCase("v3")){
			return trainTemplateV3(collection,threshold);
		}
		else{
			throw new BadRequestException("Unsupported Story Version");
		}
	}
	
	public StoryTemplate trainTemplateV1(Model collection, double threshold){
		Entity main = extractMostFrequentEntity(collection);
		
		//Extract all the events of the model.
		Map<String,Event> eventsMap = NIFManagement.extractEventsExtended(collection);

		if(eventsMap==null){
			throw new BadRequestException("There are no events in the input fail. The training process needs events. Provide proper files.");
		}
		
		//Order them by frequency or defined score.
		Map<Event,Integer> eventsRanker = new HashMap<Event, Integer>();
		Set<String> ekeys = eventsMap.keySet();
		for (String k : ekeys) {
			Event e = eventsMap.get(k);
			String time = (e.getTimestamp()==null)?null:e.getTimestamp().toString();
//			System.out.println(e.getSubject().getType()+"("+e.getPredicate().getText()+")"+e.getObject().getType()+"("+time+")"+"-->"+e.getRelevance());

			//Filter the events that are not related to the main entity.
			if( e.entityTypeIsInvolved(main) ){
//				System.out.println(e.getJSONObject().toString(2));
				Set<Event> ekeys2 = eventsRanker.keySet();
				boolean done=false;
				for (Event ev2 : ekeys2) {
					if(ev2.equals(e)){
						done=true;
						eventsRanker.put(ev2, eventsRanker.get(ev2)+1);
						break;
					}
				}
				if(!done){
					eventsRanker.put(e, 1);
				}
			}
		}
		
		List<Event> events = new LinkedList<Event>();
		while(!eventsRanker.isEmpty()){
			Set<Event> ekeys2 = eventsRanker.keySet();
			int threshold2 = 0;
			Event ev = null;
			for (Event k : ekeys2) {
//				System.out.println(k.getSubject().getText()+"("+k.getPredicate().getText()+")"+k.getObject().getText());
//				System.out.println(eventsRanker.get(k)+">"+entityThreshold);
				if(eventsRanker.get(k)>threshold2){
					threshold2 = eventsRanker.get(k);
					ev = k;
				}
			}
//			System.out.println("------------");
			eventsRanker.remove(ev);
			ev.setRelevance(threshold2);
			events.add(ev);
		}
//		for (Event k : events) {
//			String time = (k.getTimestamp()==null)?null:k.getTimestamp().toString();
//			System.out.println(k.getSubject().getType()+"("+k.getPredicate().getText()+")"+k.getObject().getType()+"("+time+")"+"-->"+k.getRelevance());
//		}
		
		List<Event> cleanedEvents = new LinkedList<Event>();
		for (Event event : events) {
			if(event.getRelevance()>threshold){
				cleanedEvents.add(event);
			}
		}
		GenericStoryTemplateV1 gstv1 = new GenericStoryTemplateV1("automaticTempalte1", main, cleanedEvents);
		return gstv1;
	}
	
	public StoryTemplate trainTemplateV2(Model collection, List<Model> documents, double threshold){

		Entity main = extractMostFrequentEntity(collection);

		//Extract all the events of the model.
		Map<String,Event> eventsMap = NIFManagement.extractEventsExtended(collection);
		if(eventsMap==null){
			throw new BadRequestException("There are no events in the input fail. The training process needs events. Provide proper files.");
		}	
//		//Order them by frequency or defined score.
//		Map<Event,Integer> eventsRanker = new HashMap<Event, Integer>();
//		Set<String> ekeys = eventsMap.keySet();
//		for (String k : ekeys) {
//			Event e = eventsMap.get(k);
//			//Filter the events that are not related to the main entity.
//			if( e.entityTypeIsInvolved(main) ){
////				System.out.println(e.getJSONObject().toString(2));
//				Set<Event> ekeys2 = eventsRanker.keySet();
//				boolean done=false;
//				for (Event ev2 : ekeys2) {
//					if(ev2.equals(e)){
//						done=true;
//						eventsRanker.put(ev2, eventsRanker.get(ev2)+1);
//						break;
//					}
//				}
//				if(!done){
//					eventsRanker.put(e, 1);
//				}
//					
////				if(eventsRanker.containsKey(e)){
////					eventsRanker.put(e, entitiesRanker.get(e)+1);
////				}
////				else{
////					eventsRanker.put(e, 1);
////				}
//			}
//		}
//
		List<Event> events = new LinkedList<Event>();
//		while(!eventsRanker.isEmpty()){
//			Set<Event> ekeys2 = eventsRanker.keySet();
//			int threshold2 = 0;
//			Event ev = null;
//			for (Event k : ekeys2) {
////				System.out.println(k.getSubject().getText()+"("+k.getPredicate().getText()+")"+k.getObject().getText());
////				System.out.println(eventsRanker.get(k)+">"+entityThreshold);
//				if(eventsRanker.get(k)>threshold2){
//					threshold2 = eventsRanker.get(k);
//					ev = k;
//				}
//			}
////			System.out.println("------------");
//			eventsRanker.remove(ev);
//			ev.setRelevance(threshold2);
//			events.add(ev);
//		}
//		for (Event k : events) {
//			System.out.println(k.getSubject().getType()+"("+k.getPredicate().getText()+")"+k.getObject().getType()+"-->"+k.getRelevance());
//		}
		List<Storyline> storylines = new LinkedList<Storyline>();
		try{
//			System.out.println("ÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖ");
			storylines = generateStorylinesFromEvents(eventsMap,events,collection, documents, threshold);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		GenericStoryTemplateV2 gstv2 = new GenericStoryTemplateV2("automaticTempalte2", main, storylines, events);
		return gstv2;
	}
	
	private List<Storyline> generateStorylinesFromEvents(Map<String, Event> eventsMap, List<Event> events, Model collection, List<Model> documents, double threshold) throws Exception {

		String input3 = ARFFGeneration.generateARFFEventsVSDocuments(eventsMap, documents);
//		String input3 = ARFFGeneration.generateARFFDocumentsVSEvents(eventsMap, documents);

//		System.out.println();
//		System.out.println(input3);
//		System.out.println();
		
		HttpResponse<String> response2 = Unirest.post("https://dev.digitale-kuratierung.de/api/e-clustering/generateClusters")
				.queryString("language", "en")
				.queryString("algorithm", "kmeans")
				.body(input3)
				.asString();

		if(response2.getStatus()!=200){
			System.out.println("BODY ERROR: "+response2.getBody());
			return null;
		}
		else{
			System.out.println("BODY CORRECT: " + response2.getBody());
			JSONObject obj = new JSONObject(response2.getBody());
			JSONObject obj2 = obj.getJSONObject("results");
			int numberClusters = Integer.parseInt(obj2.get("numberClusters").toString());
			Storyline [] storylines = new Storyline[numberClusters];
			JSONObject obj3 = obj2.getJSONObject("clustered");
			Iterator<String> keys = obj3.keys();
			while(keys.hasNext()){
				String k = keys.next();
				int cluster = obj3.getInt(k);
				if(storylines[cluster]==null){
					storylines[cluster] = new Storyline();
				}
				String subType = k.substring(0, k.indexOf('('));
				String obType = k.substring(k.lastIndexOf(')')+1);
				String predText = k.substring(k.indexOf('(')+1,k.lastIndexOf(')'));
				String relText = k.substring(k.indexOf('[')+1,k.lastIndexOf(']'));
				double relevance = Double.parseDouble(relText);
				Event e = new Event(new Entity(null, null, subType), new Entity(predText, null, EntityType.ACTION), new Entity(null, null, obType), null, relevance);
	//			System.out.println(e.getJSONObject().toString(2));
				
				if(relevance>threshold){
					storylines[cluster].addEvent(e);
//					System.out.println("ADDED");
				}
				else{
//					System.out.println("NOT ADDED");
				}
			}
	
			List<Storyline> storylines2 = new LinkedList<Storyline>();
			for (Storyline storyline : storylines) {
				if(storyline!=null){
					System.out.println("EVENTS SIZE in storyline:"+storyline.events.size());
					storylines2.add(storyline);
				}
			}
			// TODO Add all the remaining events to the events list.
			return storylines2;
		}
	}

	public StoryTemplate trainTemplateV3(Model collection, double threshold){
		/**
		 * TODO
		 */
		return null;
	}

	public Entity extractMostFrequentEntity(Model collection){
		/**
		 * The main entity will be the most frequent entity for the first approach.
		 */
		//Get all the entities in the collection and extract the most common.
		Map<String,Map<String,String>> entitiesMap = NIFReader.extractEntitiesExtended(collection);
		
		HashMap<String,Integer> entitiesRanker = new HashMap<String, Integer>();
		Set<String> keys = entitiesMap.keySet();
//		System.out.println("----------KEYS------------");
		for (String k : keys) {
//			System.out.println(k);				
			Set<String> keys2 = entitiesMap.get(k).keySet();
//			System.out.println("----------KEYS------------");
//			for (String k2 : keys2) {
//				System.out.println(k2 + "-->"+entitiesMap.get(k).get(k2));
////				System.out.println(ITSRDF.taClassRef.toString());
//			}
			String url = entitiesMap.get(k).get(NIF.anchorOf.toString());
//			System.out.println(url);
			if(url!=null){
				if(entitiesRanker.containsKey(url)){
					entitiesRanker.put(url, entitiesRanker.get(url)+1);
				}
				else{
					entitiesRanker.put(url, 1);
				}
			}
		}
		
		Set<String> keys2 = entitiesRanker.keySet();
		int entityThreshold = 0;
		String entity = "";
		for (String k : keys2) {
			if(entitiesRanker.get(k)>entityThreshold){
				entityThreshold = entitiesRanker.get(k);
				entity = k;
			}
		}
//		System.out.println(entityThreshold);

		Set<String> keys3 = entitiesMap.keySet();
		Entity main = null; 
		for (String k : keys3) {
			String url = entitiesMap.get(k).get(NIF.anchorOf.toString());
//			System.out.println(entity +"<-->"+url+"--");
			if(url.equalsIgnoreCase(entity)){
				main = new Entity(entity, entitiesMap.get(k).get(ITSRDF.taIdentRef.toString()), entitiesMap.get(k).get(ITSRDF.taClassRef.toString()));
//				System.out.println("MAINENTITY: "+main.getJSONObject().toString(2));
				break;
			}
		}
		return main;
	}

	public List<Event> extractOrderedListEvents(Model collection){
		//Extract all the events of the model.
		Map<String,Event> eventsMap = NIFManagement.extractEventsExtended(collection);

		if(eventsMap==null){
			throw new BadRequestException("There are no events in the input fail. The training process needs events. Provide proper files.");
		}
		
		Map<Event,Integer> eventsRanker = new HashMap<Event, Integer>();
		Set<String> ekeys = eventsMap.keySet();
		for (String k : ekeys) {
			Event e = eventsMap.get(k);
			String time = (e.getTimestamp()==null)?null:e.getTimestamp().toString();
			Set<Event> ekeys2 = eventsRanker.keySet();
			boolean done=false;
			for (Event ev2 : ekeys2) {
				if(ev2.equals(e)){
					done=true;
					eventsRanker.put(ev2, eventsRanker.get(ev2)+1);
					break;
				}
			}
			if(!done){
				eventsRanker.put(e, 1);
			}
		}
		
		List<Event> events = new LinkedList<Event>();
		while(!eventsRanker.isEmpty()){
			Set<Event> ekeys2 = eventsRanker.keySet();
			int threshold2 = 0;
			Event ev = null;
			for (Event k : ekeys2) {
				if(eventsRanker.get(k)>threshold2){
					threshold2 = eventsRanker.get(k);
					ev = k;
				}
			}
			eventsRanker.remove(ev);
			ev.setRelevance(threshold2);
			events.add(ev);
		}
		return events;
	}
}
