package de.dkt.eservices.esst.filters;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.freme.common.exception.ExternalServiceFailedException;

public class FilterFactory {

	static Logger logger = Logger.getLogger(FilterFactory.class);
	
	public static Filter generateFilterFromJSONObject(JSONObject obj) {
		try{
			String type = obj.getString("filterType");
			if(type.equalsIgnoreCase("temporalfilter")){
				return new TemporalFilter(obj);
			}
			else if(type.equalsIgnoreCase("geographicalfilter")){
				return new GeographicalFilter(obj);
			}
		}
		catch(Exception e){
			logger.error(e.getMessage(),e);
			throw new ExternalServiceFailedException(e.getMessage());
		}
		String msg = "Unsupported filter type";
		logger.error(msg);
		throw new ExternalServiceFailedException(msg);
	}
	
	public static List<Filter> extractFiltersFromString(String sFilters, String format){
		if(format.equalsIgnoreCase("json") || format.equalsIgnoreCase("application/json")){
			List<Filter> filtersList = new LinkedList<Filter>();
			JSONObject obj = new JSONObject(sFilters);
			JSONArray filtersArray = obj.getJSONArray("filters");
			for (int i = 0; i < filtersArray.length(); i++) {
				Filter f = generateFilterFromJSONObject(filtersArray.getJSONObject(i));
				filtersList.add(f);
			}
			return filtersList;
		}
		else{
			String msg = "Unsupported inFormat for filters.";
			logger.error(msg);
			throw new ExternalServiceFailedException(msg);
		}
	}
	
	public static String filters2String(List<Filter> filtersList, String format){
		if(format.equalsIgnoreCase("json") || format.equalsIgnoreCase("application/json")){
			JSONArray filtersArray = new JSONArray();
			for (Filter filter : filtersList) {
				filtersArray.put(filter.getJSONObject());
			}
			JSONObject obj = new JSONObject();
			obj.put("filters", filtersArray);
			return obj.toString();
		}
		else{
			String msg = "Unsupported outFormat for filters.";
			logger.error(msg);
			throw new ExternalServiceFailedException(msg);
		}
	}
	
}
