package de.dkt.eservices.esst.informationextraction;

import java.util.List;

import de.dkt.eservices.esst.linguistic.Entity;
import de.dkt.eservices.esst.linguistic.Event;
import de.dkt.eservices.esst.templates.StoryTemplate;
import de.dkt.eservices.esst.templates.Storyline;

public abstract class InformationExtraction {

	public abstract StoryTemplate fillTemplate(StoryTemplate template);	

	public abstract List<Event> fillEvent(Event e);	

	public abstract Storyline fillStoryline(Storyline sl);

	public abstract boolean setInformationSource(Object obj);

	public abstract List<Entity> getOrderedListEntities();
	
}
