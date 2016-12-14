package de.dkt.eservices.esst.templates;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;

import de.dkt.common.niftools.ITSRDF;
import de.dkt.common.niftools.NIF;
import de.dkt.common.niftools.RDFS;
import de.dkt.eservices.esst.filters.Filter;
import de.dkt.eservices.esst.informationextraction.InformationExtraction;
import de.dkt.eservices.esst.linguistic.Entity;
import de.dkt.eservices.esst.linguistic.EntityType;
import de.dkt.eservices.esst.linguistic.Event;
import de.dkt.eservices.esst.ontology.SST;
import eu.freme.common.exception.ExternalServiceFailedException;

public class GenericStoryTemplateV2 implements StoryTemplate {

	String name;
	Entity mainEntity;
	
	List<Storyline> storylines;

	List<Event> events;
	
	public GenericStoryTemplateV2() {
		super();
	}
	
	public GenericStoryTemplateV2(String name, Entity main,List<Storyline> storylines,List<Event> events) {
		super();
		this.name = name;
		mainEntity = main;
		this.storylines = storylines;
		this.events = events;
	}

	public boolean fillTemplate(InformationExtraction iem){
		try {
			for (Storyline sl: storylines) {
				iem.fillStoryline(sl);
			}
			for (Event e: events) {
				iem.fillEvent(e);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean filterTemplate(List<Filter> filters){
		try {
			for (Filter f : filters) {
				for (Storyline sl: storylines) {
					f.filterStoryline(sl);
				}
				for (Event e: events) {
					f.filterEvent(e);
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean refillTemplate(InformationExtraction iem,List<Filter> filters){
		try {
			if(fillTemplate(iem)){
				throw new ExternalServiceFailedException("Error at filling template in the refillTemplate method.");
			}
			if(filterTemplate(filters)){
				throw new ExternalServiceFailedException("Error at filtering template in the refillTemplate method.");
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public GenericStoryTemplateV2(Model m) {
		super();
		Resource storyResource;
		ResIterator it = m.listResourcesWithProperty(RDF.type, SST.Story);
		Resource r = it.next();
		storyResource = r;
		String storyUrl = r.getURI();
		this.name = storyUrl.substring(storyUrl.lastIndexOf('/')+1);
		
		String storyType;
		NodeIterator nodeIt = m.listObjectsOfProperty(storyResource, SST.storyType);
		while(nodeIt.hasNext()){
			storyType = nodeIt.next().asLiteral().getString();
		}

		//TODO Add the extraction and inclusion of the mainEntity
		
		events = getEventsFromModel(m,storyResource,false);
		//Add events associated to story
		
		storylines = new LinkedList<Storyline>();
		ResIterator resIt = m.listSubjectsWithProperty(SST.belongsToStory, storyResource);
		int counter=1;
		while (resIt.hasNext()) {
			Resource slResource = resIt.next();

			//TODO maybe we should not consider in this part the events that are part of the story directly.

			NodeIterator nodeIt1 = m.listObjectsOfProperty(slResource, SST.beginTS);
			Date dateInitial = (Date) nodeIt1.next().asLiteral().getValue();
			
			NodeIterator nodeIt2 = m.listObjectsOfProperty(slResource, SST.endTS);
			Date dateFinal = (Date) nodeIt2.next().asLiteral().getValue();
			
			NodeIterator nodeIt3 = m.listObjectsOfProperty(slResource, SST.mainCharacter);
			Resource objResource = nodeIt3.next().asResource();
			Statement st3_1 = m.getProperty(objResource, SST.anchorOf);
			Statement st3_2 = m.getProperty(objResource, SST.entityUrl);
			Statement st3_3 = m.getProperty(objResource, SST.entityType);
			Entity mainCharacter = new Entity(st3_1.getObject().asLiteral().getString(), st3_2.getObject().asResource().getURI(), st3_3.getObject().asLiteral().getString());

			List<Event> events = getEventsFromModel(m,slResource,true);
			Storyline sl = new Storyline("storyline"+counter, dateInitial, dateFinal, mainCharacter, events);
			counter++;
			storylines.add(sl);
		}

	}

	private List<Event> getEventsFromModel(Model m, Resource storyResource, boolean belongsToStoryline) {
		
		List<Event> events = new LinkedList<Event>();
		Property p;
		if(belongsToStoryline){
			p = SST.belongsToStoryLine;
		}
		else{
			p = SST.belongsToStory;
		}
		ResIterator resIt = m.listSubjectsWithProperty(p, storyResource);
		while (resIt.hasNext()) {
			Resource eventResource = resIt.next();
			//String eventUri = eventResource.getURI();
			
			NodeIterator nodeIt1 = m.listObjectsOfProperty(eventResource, SST.eventSubject);
			Resource subjResource = nodeIt1.next().asResource();
			
			Statement st1_1 = m.getProperty(subjResource, SST.anchorOf);
			Statement st1_2 = m.getProperty(subjResource, SST.entityUrl);
			Statement st1_3 = m.getProperty(subjResource, SST.entityType);
			Entity subject = new Entity(st1_1.getObject().asLiteral().getString(), st1_2.getObject().asResource().getURI(), st1_3.getObject().asLiteral().getString());

			NodeIterator nodeIt2 = m.listObjectsOfProperty(eventResource, SST.eventPredicate);
			Resource predResource = nodeIt2.next().asResource();
			Statement st2_1 = m.getProperty(predResource, SST.anchorOf);
			Statement st2_2 = m.getProperty(predResource, SST.entityUrl);
			Statement st2_3 = m.getProperty(predResource, SST.entityType);
			Entity predicate = new Entity(st2_1.getObject().asLiteral().getString(), st2_2.getObject().asResource().getURI(), st2_3.getObject().asLiteral().getString());
			
			NodeIterator nodeIt3 = m.listObjectsOfProperty(eventResource, SST.eventObject);
			Resource objResource = nodeIt3.next().asResource();
			Statement st3_1 = m.getProperty(objResource, SST.anchorOf);
			Statement st3_2 = m.getProperty(objResource, SST.entityUrl);
			Statement st3_3 = m.getProperty(objResource, SST.entityType);
			Entity object = new Entity(st3_1.getObject().asLiteral().getString(), st3_2.getObject().asResource().getURI(), st3_3.getObject().asLiteral().getString());
			
			NodeIterator nodeIt4 = m.listObjectsOfProperty(eventResource, SST.timestamp);
			Date timestamp = (Date) nodeIt4.next().asLiteral().getValue();
			
			NodeIterator nodeIt5 = m.listObjectsOfProperty(eventResource, SST.eventRelevance);
			double relevance = nodeIt5.next().asLiteral().getDouble();

			Event ev = new Event(subject, predicate, object, timestamp, relevance);
			events.add(ev);
		}
		return events;
	}

	public GenericStoryTemplateV2(JSONObject obj) {
		super();
		this.storylines = new LinkedList<Storyline>();
		JSONArray storyLinesArray = obj.getJSONArray("storylines");
		for (int i = 0; i < storyLinesArray.length(); i++) {
			storylines.add(new Storyline(storyLinesArray.getJSONObject(i)));
		}
		this.events = new LinkedList<Event>();
		JSONArray eventsArray = obj.getJSONArray("events");
		for (int i = 0; i < eventsArray.length(); i++) {
			events.add(new Event(eventsArray.getJSONObject(i)));
		}
	}

	public GenericStoryTemplateV2(String templateContent) {
		super();
		JSONObject obj = new JSONObject(templateContent);
		this.events = new LinkedList<Event>();
		if(obj.has("mainEntity")){
			String mainEntityType = obj.getString("mainEntity");
			mainEntity = new Entity(null, null, mainEntityType);
		}
		this.name = obj.getString("name");
		JSONArray eventsArray = obj.getJSONArray("events");
		for (int i = 0; i < eventsArray.length(); i++) {
			JSONObject obj2 = eventsArray.getJSONObject(i);
			String subj = obj2.getString("subject");
			String pred = obj2.getString("predicate");
			String obje = obj2.getString("object");
			String rele = obj2.getString("relevance");
			
			Event e = new Event(new Entity(null, null, EntityType.fromValue(subj)),
					new Entity(pred, null, EntityType.ACTION),
					new Entity(null, null, EntityType.fromValue(obje)),
					null,
					Double.parseDouble(rele));
			
			events.add(e);
		}
	}

	public JSONObject getEmptyTemplateJSONObject(){
		JSONObject obj = new JSONObject();
		obj.put("name", name);
		obj.put("mainEntity", mainEntity.getType().toString());
		JSONArray storylinesArray = new JSONArray();
		for (Storyline sl : storylines) {
			storylinesArray.put(sl.getEmptyTemplateJSONObject());
		}
		obj.put("storylines", storylinesArray);
		JSONArray eventsArray = new JSONArray();
		for (Event event : events) {
			eventsArray.put(event.getEmptyTemplateJSONObject());
		}
		obj.put("events", eventsArray);
		return obj;
	}
	
	public String getSimpleScreenString(){
		String s = "";
		s += name + "\n";
		s += "\t" + mainEntity.getType() + "\n";
		s += "\tEvents:\n";
		for (Event e : events) {
			s += "\t\t" + e.getSubject().getType() + "(" + e.getPredicate().getText() + ")" + e.getObject().getType() + "\n";
		}
		s += "\tStorylines:\n";
		for (Storyline sl : storylines) {
			s+="\t\t"+sl.name+"\n";
			for (Event e : sl.events) {
				s += "\t\t\t" + e.getSubject().getType() + "(" + e.getPredicate().getText() + ")" + e.getObject().getType() + "\n";
			}
		}
		return s;
	}

	public JSONObject getJSONObject(){
		JSONObject obj = new JSONObject();
		obj.put("name", name);
		obj.put("mainEntity", mainEntity.getJSONObject());
		JSONArray storylinesArray = new JSONArray();
		for (Storyline st : storylines) {
			storylinesArray.put(st.getJSONObject());
		}
		obj.put("storylines", storylinesArray);
		JSONArray eventsArray = new JSONArray();
		for (Event event : events) {
			eventsArray.put(event.getJSONObject());
		}
		obj.put("events", eventsArray);
		return obj;
	}
	
	
	public void addEvent(Event e) {
		for (Storyline sl : storylines) {
			if(sl.fits(e)){
				sl.addEvent(e);
				return;
			}
		}
		events.add(e);
	}

	public Model initializeModel(){
		Model m = ModelFactory.createDefaultModel();
		
		m.setNsPrefix("rdf", RDF.getURI());
		m.setNsPrefix("xsd", XSD.getURI());
		m.setNsPrefix("itsrdf", ITSRDF.getURI());
		m.setNsPrefix("nif", NIF.getURI());
		m.setNsPrefix("rdfs", RDFS.getURI());
		m.setNsPrefix("sst", SST.getURI());
		return m;
	}

	@Override
	public Model getModel(String prefix) {
		Model m = initializeModel();

		//Add story information
		String storyUri;
		if(prefix==null){
			storyUri = "http://dkt.dfki.de/sst/"+name;
		}
		else{
			storyUri = (prefix.endsWith(File.separator)) ? prefix + name : prefix+File.separator+name;
		}
		Resource storyAsResource = m.createResource(storyUri);
		m.add(storyAsResource, RDF.type, SST.Story);
		m.add(storyAsResource, RDF.type, NIF.RFC5147String);
//		m.add(spanAsResource, SST.beginTS, m.createTypedLiteral(, XSDDatatype.XSDdateTime));
//		m.add(spanAsResource, SST.endTS, m.createTypedLiteral(, XSDDatatype.XSDdateTime));
		m.add(storyAsResource, SST.storyType, m.createTypedLiteral("", XSDDatatype.XSDstring));

		//Add events associated to story
		for (Event e : events) {
			addEventToModel(m, e, storyAsResource, false);
		}

		//Add storylines associated with stories
		for (Storyline sl : storylines) {
			String storylineUri = storyAsResource.getURI() + "/storyline101";
			Resource storylineAsResource = m.createResource(storylineUri);
			m.add(storylineAsResource, RDF.type, SST.Storyline);
			m.add(storylineAsResource, RDF.type, NIF.RFC5147String);
			m.add(storylineAsResource, SST.belongsToStory, storyAsResource);
			m.add(storylineAsResource, SST.beginTS, m.createTypedLiteral(sl.getDateInitial(), XSDDatatype.XSDdateTime));
			m.add(storylineAsResource, SST.endTS, m.createTypedLiteral(sl.getDateFinal(), XSDDatatype.XSDdateTime));

			Resource mainCharacterAsResource = m.createResource(storylineUri+"/mainCharacter");
	       	m.add(mainCharacterAsResource, RDF.type, SST.Entity);
	       	m.add(mainCharacterAsResource, RDF.type, NIF.RFC5147String);
	       	m.add(mainCharacterAsResource, SST.anchorOf, m.createTypedLiteral(sl.getMainCharacter().getText(), XSDDatatype.XSDstring));
	       	m.add(mainCharacterAsResource, SST.entityUrl, m.createResource(sl.getMainCharacter().getUrl()));

	       	m.add(storylineAsResource, SST.mainCharacter, mainCharacterAsResource);

			for (Event e : sl.getEvents()) {
				addEventToModel(m, e, storylineAsResource, true);
			}
		}
		return m;
	}

	public void addEventToModel(Model m, Event e, Resource storyAsResource, boolean belongsToStoryline){
		String eventUri = storyAsResource.getURI() + "/event101";
		Resource eventAsResource = m.createResource(eventUri);
		m.add(eventAsResource, RDF.type, SST.Event);
		m.add(eventAsResource, RDF.type, NIF.RFC5147String);
		if(belongsToStoryline){
			m.add(eventAsResource, SST.belongsToStoryLine, storyAsResource);
		}
		else{
			m.add(eventAsResource, SST.belongsToStory, storyAsResource);
		}
		
		//Add entities associated to event
		Resource eventSubjectAsResource = m.createResource(eventUri+"/subj");
       	m.add(eventSubjectAsResource, RDF.type, SST.Entity);
       	m.add(eventSubjectAsResource, RDF.type, NIF.RFC5147String);
       	m.add(eventSubjectAsResource, SST.anchorOf, m.createTypedLiteral(e.getSubject().getText(), XSDDatatype.XSDstring));
       	m.add(eventSubjectAsResource, SST.entityUrl, m.createResource(e.getSubject().getUrl()));
		
		Resource eventPredicateAsResource = m.createResource(eventUri+"/pred");
       	m.add(eventPredicateAsResource, RDF.type, SST.Entity);
       	m.add(eventPredicateAsResource, RDF.type, NIF.RFC5147String);
       	m.add(eventPredicateAsResource, SST.anchorOf, m.createTypedLiteral(e.getPredicate().getText(), XSDDatatype.XSDstring));
       	m.add(eventPredicateAsResource, SST.entityUrl, m.createResource(e.getPredicate().getUrl()));

		Resource eventObjectAsResource = m.createResource(eventUri+"/obj");
       	m.add(eventObjectAsResource, RDF.type, SST.Entity);
       	m.add(eventObjectAsResource, RDF.type, NIF.RFC5147String);
       	m.add(eventObjectAsResource, SST.anchorOf, m.createTypedLiteral(e.getObject().getText(), XSDDatatype.XSDstring));
       	m.add(eventObjectAsResource, SST.entityUrl, m.createResource(e.getObject().getUrl()));

       	m.add(eventAsResource, SST.eventSubject, eventSubjectAsResource);
    	m.add(eventAsResource, SST.eventPredicate, eventPredicateAsResource);
    	m.add(eventAsResource, SST.eventObject, eventObjectAsResource);
    	m.add(eventAsResource, SST.timestamp, m.createTypedLiteral(e.getTimestamp(), XSDDatatype.XSDdateTime));
	}
	
	public void addEntityToModel(Model m, String uri, Entity e){
		Resource eventObjectAsResource = m.createResource(uri);
       	m.add(eventObjectAsResource, RDF.type, SST.Entity);
       	m.add(eventObjectAsResource, RDF.type, NIF.RFC5147String);
       	m.add(eventObjectAsResource, SST.anchorOf, m.createTypedLiteral(e.getText(), XSDDatatype.XSDstring));
       	m.add(eventObjectAsResource, SST.entityUrl, m.createResource(e.getUrl()));
	}

	public static void main(String[] args) {
		
		String inputJson = "			{\n" + 
"				  \"name\": \"TestTemplate\",\n" + 
"				  \"storylines\": [{\n" + 
"				    \"mainCharacter\": {\n" + 
"				      \"text\": \"Hillary\",\n" + 
"				      \"type\": \"EMPTY\",\n" + 
"				      \"url\": \"\"\n" + 
"				    },\n" + 
"				    \"dateInitial\": \"9. Oktober 2016\",\n" + 
"				    \"dateFinal\": \"10. November 2016\",\n" + 
"				    \"events\": []\n" + 
"				  }],\n" + 
"				  \"events\": [{\n" + 
"				    \"predicate\": {\n" + 
"				      \"text\": \"EMPTY\",\n" + 
"				      \"type\": \"EMPTY\",\n" + 
"				      \"url\": \"EMPTY\"\n" + 
"				    },\n" + 
"				    \"subject\": {\n" + 
"				      \"text\": \"EMPTY\",\n" + 
"				      \"type\": \"EMPTY\",\n" + 
"				      \"url\": \"EMPTY\"\n" + 
"				    },\n" + 
"				    \"object\": {\n" + 
"				      \"text\": \"EMPTY\",\n" + 
"				      \"type\": \"EMPTY\",\n" + 
"				      \"url\": \"EMPTY\"\n" + 
"				    },\n" + 
"				    \"timestamp\": \"23. November 2016\"\n" + 
"				  }]\n" + 
"				}\n";
		List<Event> events = new LinkedList<Event>();
		events.add(new Event(new Entity(), new Entity(), new Entity(), new Date(), 0));
//		GenericStoryTemplate gst = new GenericStoryTemplate("TestTemplate", events);
		GenericStoryTemplateV2 gst = new GenericStoryTemplateV2(new JSONObject(inputJson));
		System.out.println(gst.getJSONObject().toString(2));
	}
	
}
