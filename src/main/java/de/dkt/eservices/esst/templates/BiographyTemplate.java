package de.dkt.eservices.esst.templates;

import java.util.List;

import org.json.JSONObject;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import de.dkt.eservices.esst.informationextraction.InformationExtraction;
import de.dkt.eservices.esst.linguistic.Entity;
import de.dkt.eservices.esst.linguistic.EntityType;
import de.dkt.eservices.esst.linguistic.Event;
import de.dkt.eservices.esst.ontology.SST;

public class BiographyTemplate extends GenericStoryTemplate implements StoryTemplate {

	public BiographyTemplate(JSONObject obj) {
		super(obj);
		// TODO Auto-generated constructor stub
	}
	
	public BiographyTemplate(Model m) {
		super(m);
		
	}

	public BiographyTemplate(String name, List<Storyline> storylines, List<Event> events) {
		super(name, storylines, events);
	}

	/**
	 * In the case of a biography, the additional information is the main character NAME.
	 */
	private Entity mainEntity;
	
	public BiographyTemplate() {
		super();
	}
	
	public BiographyTemplate(String entity) {
		super();
		mainEntity = new Entity(entity, null, EntityType.PERSON);
	}
	
	public BiographyTemplate(String entity,String url) {
		super();
		mainEntity = new Entity(entity, url, EntityType.PERSON);
	}
	
	public BiographyTemplate(Entity entity) {
		super();
		mainEntity = entity;
	}
	
	public Entity getMainEntity() {
		return mainEntity;
	}

	public void setMainEntity(Entity mainEntity) {
		this.mainEntity = mainEntity;
	}

	public boolean generateFields(){
		
		//TODO
		
		return true;
	}
	
	public Model getModel(String prefix) {
		Model m = super.getModel(prefix);
		ResIterator it = m.listResourcesWithProperty(RDF.type, SST.Story);
		Resource storyResource = it.next();
		m.add(storyResource, SST.storyType, m.createTypedLiteral("Biography", XSDDatatype.XSDstring));
		return m;
	}
	
	public boolean fillTemplate(InformationExtraction iem){
		try {
			
			//TODO
			
			
			
			
			
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
