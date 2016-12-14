package de.dkt.eservices.esst.templates;

import java.util.List;

import org.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.eservices.esst.linguistic.Event;

public class NarrativeTemplate extends GenericStoryTemplate {

	public NarrativeTemplate() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NarrativeTemplate(JSONObject obj) {
		super(obj);
		// TODO Auto-generated constructor stub
	}

	public NarrativeTemplate(Model m) {
		super(m);
		// TODO Auto-generated constructor stub
	}

	public NarrativeTemplate(String name, List<Storyline> storylines, List<Event> events) {
		super(name, storylines, events);
		// TODO Auto-generated constructor stub
	}



}
