package de.dkt.eservices.esst.filters;

import java.text.ParseException;

import org.json.JSONObject;

import de.dkt.eservices.esst.templates.StoryTemplate;

public class GeographicalFilter implements Filter {

	private FilterType filterType;
	private String filterName;

	double longitude;
	double latitude;

	double distance;
	
	public GeographicalFilter(JSONObject obj) throws ParseException {
		this.filterType = FilterType.getFilterType(obj.getString("filterType"));
		this.filterName = obj.getString("filterName");
		this.longitude = Double.parseDouble(obj.getString("longitude"));
		this.latitude = Double.parseDouble(obj.getString("latitude"));
		this.distance = Double.parseDouble(obj.getString("distance"));
	}
	
	@Override
	public JSONObject getJSONObject() {
		JSONObject obj = new JSONObject();
		obj.put("filterType", filterType.toString());
		obj.put("filterName", filterName);
		obj.put("latitude", latitude);
		obj.put("longitude", longitude);
		obj.put("distance", distance);
		return obj;
	}

	@Override
	public StoryTemplate filterStory(StoryTemplate story) {
		// TODO Auto-generated method stub
		return null;
	}

}
