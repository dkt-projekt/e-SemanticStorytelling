package de.dkt.eservices.esst.filters;

import org.json.JSONObject;

import de.dkt.eservices.esst.linguistic.Event;
import de.dkt.eservices.esst.templates.StoryTemplate;
import de.dkt.eservices.esst.templates.Storyline;

public interface Filter {

	public JSONObject getJSONObject();
	
	public StoryTemplate filterStory(StoryTemplate story);

	public void filterEvent(Event event);

	public void filterStoryline(Storyline sl);

}
