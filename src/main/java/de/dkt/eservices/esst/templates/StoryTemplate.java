package de.dkt.eservices.esst.templates;

import java.util.List;

import org.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.eservices.esst.filters.Filter;
import de.dkt.eservices.esst.informationextraction.InformationExtraction;
import de.dkt.eservices.esst.informationextraction.SparqlInformationExtraction;
import de.dkt.eservices.esst.linguistic.Entity;
import de.dkt.eservices.esst.linguistic.Event;

public interface StoryTemplate {

	public Model getModel(String prefix);
	
	public void addEvent(Event e);

	public boolean fillTemplate(InformationExtraction iem);

	public boolean filterTemplate(List<Filter> filters);

	public boolean refillTemplate(InformationExtraction iem, List<Filter> filters);

	public JSONObject getJSONObject();

	public JSONObject getEmptyTemplateJSONObject();

	public String getSimpleScreenString();
	
	public Entity getMainEntity();

	public List<Event> getEvents();
	
//	public Object getField(String field);
//	
//	public void fillField(String key, Field value);
//	
//	public List<String> requiredFieldsNames();
//
//	public List<Field> listRequiredFields();
//
//	public boolean hasRequiredFieldLeft();
//	
//	public Field nextRequiredField();
//
//	public List<Field> listNonRequiredFields();
//
//	public String toScreenString();
//
//	public boolean addAdditionalInformation(String ai);
//
//	public String getAdditionalInformation();
}
