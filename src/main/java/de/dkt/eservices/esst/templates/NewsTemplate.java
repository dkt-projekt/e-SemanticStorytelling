package de.dkt.eservices.esst.templates;

import java.util.List;

import org.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.eservices.esst.linguistic.Event;

public class NewsTemplate extends GenericStoryTemplate{

	public NewsTemplate() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NewsTemplate(JSONObject obj) {
		super(obj);
		// TODO Auto-generated constructor stub
	}

	public NewsTemplate(Model m) {
		super(m);
		// TODO Auto-generated constructor stub
	}

	public NewsTemplate(String name, List<Storyline> storylines, List<Event> events) {
		super(name, storylines, events);
		// TODO Auto-generated constructor stub
	}

}
