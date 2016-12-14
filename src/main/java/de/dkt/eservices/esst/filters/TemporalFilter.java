package de.dkt.eservices.esst.filters;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import de.dkt.eservices.esst.templates.StoryTemplate;

public class TemporalFilter implements Filter {

	private FilterType filterType;
	private String filterName;

	Date startingDate;
	Date endingDate;
	
	public TemporalFilter(JSONObject obj) throws ParseException {
		this.filterType = FilterType.getFilterType(obj.getString("filterType"));
		this.filterName = obj.getString("filterName");
		DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
		this.startingDate = df.parse(obj.getString("startingDate"));
		this.endingDate = df.parse(obj.getString("endingDate"));
	}
	
	@Override
	public JSONObject getJSONObject() {
		DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
		JSONObject obj = new JSONObject();
		obj.put("filterType", filterType.toString());
		obj.put("filterName", filterName);
		obj.put("startingDate", df.format(startingDate));
		obj.put("endingDate", df.format(endingDate));
		return obj;
	}

	@Override
	public StoryTemplate filterStory(StoryTemplate story) {
		// TODO Auto-generated method stub
		return null;
	}

}
