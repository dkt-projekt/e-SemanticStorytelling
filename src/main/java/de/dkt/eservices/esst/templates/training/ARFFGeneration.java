package de.dkt.eservices.esst.templates.training;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.NIFReader;
import de.dkt.eservices.esst.linguistic.Event;
import de.dkt.eservices.esst.ontology.NIFManagement;

public class ARFFGeneration {

	public static String generateARFFEventsVSDocuments(Map<String, Event> eventsMap, List<Model> documents){
		Map<String,String> listData = new HashMap<String,String>();
		Set<String> keys = eventsMap.keySet();

		for (String k : keys) {
			Event e = eventsMap.get(k);
			String newKey = e.getSubject().getType()+"("+e.getPredicate().getText()+")"+e.getObject().getType()+"["+e.getRelevance()+"]";
			listData.put(newKey, "");
		}
		
		//Call the clustering endpoint with some infomration.
		String input3 = "@RELATION clusteringStorylines\n";
		input3 = input3 + "@ATTRIBUTE EventName String\n";
		for (Model m : documents) {
			String uri = NIFReader.extractDocumentURI(m);
			input3 = input3 + "@ATTRIBUTE "+uri+" NUMERIC\n";
			Map<String,Event> partEvents = NIFManagement.extractEventsExtended(m);

			if(partEvents!=null){
				Map<String,Integer> partEventsRanker = new HashMap<String, Integer>();
				Set<String> ekeys = partEvents.keySet();
				for (String k : ekeys) {
					Event e = partEvents.get(k);
					String newKey = e.getSubject().getType()+"("+e.getPredicate().getText()+")"+e.getObject().getType()+"["+e.getRelevance()+"]";
					//Filter the events that are not related to the main entity.
					if(partEventsRanker.containsKey(newKey)){
						partEventsRanker.put(newKey, partEventsRanker.get(newKey)+1);
					}
					else{
						partEventsRanker.put(newKey, 1);
					}
				}				
				
				Set<String> keySet2 = listData.keySet();
				for (String k : keySet2) {
					if(partEventsRanker.containsKey(k)){
						listData.put(k, listData.get(k)+","+partEventsRanker.get(k));
					}
					else{
						listData.put(k, listData.get(k)+","+0);
					}
				}
			}
			
		}

		input3 = input3 + "@DATA\n";

		Set<String> keySet3 = listData.keySet();
		for (String k : keySet3) {
			input3 = input3 + k.replace(' ', '_').replace("<span>", "").replace("</span>", "").replace(",","") + listData.get(k) + "\n";
		}
		
		return input3;
	}

	public static String generateARFFDocumentsVSEvents(Map<String, Event> eventsMap, List<Model> documents){
		Map<String,String> listData = new HashMap<String,String>();
		Set<String> keys = eventsMap.keySet();

		for (String k : keys) {
			Event e = eventsMap.get(k);
			String newKey = e.getSubject().getType()+"("+e.getPredicate().getText()+")"+e.getObject().getType();
			listData.put(newKey, "");
		}
		
		//Call the clustering endpoint with some infomration.
		String input3 = "@RELATION clusteringStorylines\n";
		input3 = input3 + "@ATTRIBUTE DocumentName String\n";
		
		Set<String> listDataKeys = listData.keySet();
		for (String k : listDataKeys) {
			input3 = input3 + "@ATTRIBUTE "+k.replace(' ', '_')+" NUMERIC\n";
		}
		
		input3 = input3 + "@DATA\n";

		for (Model m : documents) {
			String uri = NIFReader.extractDocumentURI(m);
			input3 = input3 + uri;
			Map<String,Event> partEvents = NIFManagement.extractEventsExtended(m);

			if(partEvents!=null){
				Map<String,Integer> partEventsRanker = new HashMap<String, Integer>();
				Set<String> ekeys = partEvents.keySet();
				for (String k : ekeys) {
					Event e = partEvents.get(k);
					String newKey = e.getSubject().getType()+"("+e.getPredicate().getText()+")"+e.getObject().getType();
					//Filter the events that are not related to the main entity.
					if(partEventsRanker.containsKey(newKey)){
						partEventsRanker.put(newKey, partEventsRanker.get(newKey)+1);
					}
					else{
						partEventsRanker.put(newKey, 1);
					}
				}				
				
				Set<String> keySet2 = listData.keySet();
				for (String k : keySet2) {
					if(partEventsRanker.containsKey(k)){
						input3 = input3 + "," + partEventsRanker.get(k);
					}
					else{
						input3 = input3 + ",0";
					}
				}
			}
			
			input3 = input3 + "\n";
		}
		return input3;
	}

}
