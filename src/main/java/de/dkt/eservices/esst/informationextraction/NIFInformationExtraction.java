package de.dkt.eservices.esst.informationextraction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import de.dkt.common.niftools.ITSRDF;
import de.dkt.common.niftools.NIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.TIME;
import de.dkt.eservices.esst.linguistic.Entity;
import de.dkt.eservices.esst.linguistic.EntityType;
import de.dkt.eservices.esst.linguistic.Event;
import de.dkt.eservices.esst.ontology.SST;
import de.dkt.eservices.esst.templates.StoryTemplate;
import de.dkt.eservices.esst.templates.Storyline;

public class NIFInformationExtraction extends InformationExtraction {

//	List<Model> documents;
	Model collection;
	
	public NIFInformationExtraction() {
	}
	
//	public NIFInformationExtraction(List<Model> documents) {
//		this.documents = documents;
//	}
//	
	public NIFInformationExtraction(Model collection) {
		this.collection = collection;
	}
	
	public StoryTemplate fillTemplate(StoryTemplate template) {

		
		
		
		return template;
	}

	public List<Event> fillEvent(Event e) {
//		System.out.println(NIFReader.model2String(collection, RDFSerialization.TURTLE));
		List<Event> validEvents = new LinkedList<Event>();
		if(e.getPredicate()==null){
			return validEvents;
		}
		
		ResIterator resIt = collection.listResourcesWithProperty(RDF.type, SST.Event);
		
		while(resIt.hasNext()){
			Resource evRes = resIt.next();
			Resource predRes = evRes.getProperty(SST.eventPredicate).getObject().asResource();

			Entity eSubj = e.getSubject();
			Entity ePred = e.getPredicate();
			Entity eObj = e.getObject();

//			System.out.println("----------------------");
//			System.out.println(predRes.hasProperty(ITSRDF.taClassRef, DBO.action) + "--" + 
//					predRes.hasLiteral(NIF.anchorOf, e.getPredicate().getText()));

//			System.out.println(ePred.getText()+"--"+predRes.getProperty(NIF.anchorOf).getObject().asLiteral().getString());

			if(evRes.hasProperty(SST.eventSubject)
					&& evRes.hasProperty(SST.eventObject)
					&& predRes.hasLiteral(NIF.anchorOf, e.getPredicate().getText())){
				
//				System.out.println("ENTERING--------------------------");
				Resource subjRes = evRes.getProperty(SST.eventSubject).getObject().asResource();
				String subjType = subjRes.getProperty(ITSRDF.taClassRef).getObject().asResource().getURI().toLowerCase();

//				System.out.println(e.getSubject().getType().toString() + "--" + subjType);
				if(e.getSubject()!=null && subjType.contains(e.getSubject().getType().toString().toLowerCase())){
					
					if(evRes.hasProperty(SST.eventObject)){
//						System.out.println("ENTERING--------------------------21");
						Resource objRes = evRes.getProperty(SST.eventObject).getObject().asResource();
						String objType = (objRes.hasProperty(ITSRDF.taClassRef))?objRes.getProperty(ITSRDF.taClassRef).getObject().asResource().getURI():null;
	
//						System.out.println("ENTERING--------------------------22");
						if(e.getObject()!=null && objType!=null && objType.contains(e.getObject().getType().toString().toLowerCase())){
							//Set values to the story elements.
	
//							System.out.println("ENTERING--------------------------33");
	
							String subjText = subjRes.getProperty(NIF.anchorOf).getObject().asLiteral().getString();
							String subjUrl = (subjRes.hasProperty(ITSRDF.taIdentRef))?subjRes.getProperty(ITSRDF.taIdentRef).getObject().asResource().getURI():null;
							
							eSubj.setText(subjText);
							eSubj.setUrl(subjUrl);
							
							String objText = objRes.getProperty(NIF.anchorOf).getObject().asLiteral().getString();
							String objUrl = (objRes.hasProperty(ITSRDF.taIdentRef))?objRes.getProperty(ITSRDF.taIdentRef).getObject().asResource().getURI():null;
							eObj.setText(objText);
							eObj.setUrl(objUrl);
							
							double relevance = (evRes.hasProperty(SST.eventRelevance))?evRes.getProperty(SST.eventRelevance).getObject().asLiteral().getDouble():0;

							Date timestamp = null;
							if(evRes.hasProperty(SST.timestamp)){
								Resource timeRes = evRes.getProperty(SST.timestamp).getObject().asResource();
								if(timeRes.hasProperty(TIME.intervalStarts)){
				            		SimpleDateFormat parser=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
									try{
										timestamp = parser.parse(timeRes.getProperty(TIME.intervalStarts).getObject().asLiteral().getString());
									}
									catch(Exception ex){
										
									}
								}
							}
							
							Event auxE = new Event(new Entity(subjText, subjUrl, e.getSubject().getType()),
									new Entity(e.getPredicate().getText(), e.getPredicate().getUrl(), e.getPredicate().getType()),
									new Entity(objText, objUrl, e.getObject().getType()),
									timestamp,
									relevance);
//							e.setSubject(eSubj);
//							e.setPredicate(ePred);
//							e.setObject(eObj);
							validEvents.add(auxE);
						}
					}
				}

			}
		}
		return validEvents;
	}

	public boolean setInformationSource(Object obj){
		if(obj==null){
			return false;
		}
		if(obj instanceof Model){
			this.collection = (Model) obj;
			return true;
		}
		return false;
	}

	@Override
	public Storyline fillStoryline(Storyline sl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Entity> getOrderedListEntities() {
		/**
		 * The main entity will be the most frequent entity for the first approach.
		 */
		//Get all the entities in the collection and extract the most common.
		Map<String,Map<String,String>> entitiesMap = NIFReader.extractEntitiesExtended(collection);
		
		HashMap<String,Integer> entitiesRanker = new HashMap<String, Integer>();
		HashMap<String,Entity> entitiesM = new HashMap<String,Entity>();
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
			String text = (entitiesMap.get(k).containsKey(NIF.anchorOf.toString()))?entitiesMap.get(k).get(NIF.anchorOf.toString()):null;
			String url = (entitiesMap.get(k).containsKey(ITSRDF.taIdentRef.toString()))?entitiesMap.get(k).get(ITSRDF.taIdentRef.toString()):null;
			String classType = (entitiesMap.get(k).containsKey(ITSRDF.taClassRef.toString()))?entitiesMap.get(k).get(ITSRDF.taClassRef.toString()):null;
			EntityType type = null;
			if(classType.contains("Location")){
				type=EntityType.LOCATION;
			}
			else if(classType.contains("Organisation")){
				type=EntityType.ORGANIZATION;
			}
			else if(classType.contains("Person")){
				type=EntityType.PERSON;
			}
			else if(classType.contains("Action")){
				type=EntityType.ACTION;
			}
			else if(classType.contains("Unknwon")){
				type=EntityType.UNKNOWN;
			}
			else{
				type=EntityType.UNKNOWN;
			}
//			System.out.println(url);
			Entity ent = new Entity(text,url,type);
			if(entitiesRanker.containsKey(text)){
				entitiesRanker.put(text, entitiesRanker.get(text)+1);
//				System.out.println("INCREASING: "+ent.getText());
			}
			else{
				entitiesRanker.put(text, 1);
				entitiesM.put(text, ent);
//				System.out.println("ADDING: "+ent.getText());
			}
		}

//		System.exit(0);
		List<Entity> entities= new LinkedList<Entity>();
		while(!entitiesRanker.isEmpty()){
			Set<String> ekeys2 = entitiesRanker.keySet();
			int threshold2 = 0;
			Entity ev = null;
			String sk = null;
			for (String s : ekeys2) {
				Entity k = entitiesM.get(s);
//				System.out.println(k.getSubject().getText()+"("+k.getPredicate().getText()+")"+k.getObject().getText());
//				System.out.println(eventsRanker.get(k)+">"+entityThreshold);
				
				if(entitiesRanker.get(s)>threshold2){
					threshold2 = entitiesRanker.get(s);
					ev = k;
					sk = s;
				}
			}
//			System.out.println("------------");
//			System.out.println(ev.getText() + "--" + ev.getType() + "--" + threshold2);
			entitiesRanker.remove(sk);
			entities.add(ev);
		}
		return entities;
	}

}
