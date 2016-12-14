package de.dkt.eservices.esst.informationextraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import de.dkt.eservices.esst.linguistic.Event;
import de.dkt.eservices.esst.templates.StoryTemplate;
import de.dkt.eservices.esst.templates.Storyline;

public class SparqlInformationExtraction extends InformationExtraction {

	String sparqlEndpoint = "http://dev.digitale-kuratierung.de:8890/sparql?default-graph-uri=http%3A%2F%2Fdigitale-kuratierung.de%2Fns%2Fgraphs%2F";
	
	public SparqlInformationExtraction(){
		
	}
	
	public SparqlInformationExtraction(String collectionName) {
		sparqlEndpoint += collectionName;
	}
	
	public QueryExecution createQuery(String queryStr) {
		Query q = QueryFactory.create(queryStr);
		return QueryExecutionFactory.sparqlService(sparqlEndpoint, q);
	}

	public StoryTemplate fillTemplate(StoryTemplate template) {

		/**
		 * TODO In order to fill a template we need to get information from a Triple storage.
		 * 
		 *  - Which is the information we should retrieve (depends on the type of story)
		 *  - How to generate the storylines.
		 *  
		 */
		return template;
	}
	
	public List<Event> fillEvent(Event e) {
		List<Event> events = new LinkedList<Event>();
		String query = "prefix nif: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#>\n"+ 
					"prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#>\n"+
					"	\n"+
					"select queryElements WHERE{\n"+ 
					" SUBJECT PREDICATE OBJECT \n"+
//					" ?s ?p ?o\n"+
					"}\n"+
					"LIMIT 500000000";
		String queryElements = " ";
		if(e.getSubject()==null){
			query = query.replace("SUBJECT", "?s");
			queryElements += "?s ";
		}
		else{
			query = query.replace("SUBJECT", e.getSubject().getUrl());
		}
		if(e.getObject()==null){
			query = query.replace("OBJECT", "?o");
			queryElements += "?o ";
		}
		else{
			query = query.replace("OBJECT", e.getObject().getUrl());
		}
		if(e.getPredicate()==null){
			query = query.replace("PREDICATE", "?p");
			queryElements += "?p ";
		}
		else{
			query = query.replace("PREDICATE", e.getPredicate().getUrl());
		}
		query = query.replace("queryElements", queryElements);
		
		QueryExecution qexec = null;
		HashMap<String,List<String>> data = new HashMap<String, List<String>>();
		try{
			qexec = createQuery("");
			ResultSet res = qexec.execSelect();
			while(res.hasNext()){
				QuerySolution qs = res.next();
				String anchor = qs.getLiteral("anchor").getString();
				String uri = qs.getResource("uri").getURI();
				List<String> list = data.get(anchor);
				if( list == null ){
					list = new ArrayList<String>();
					data.put(anchor, list);
				}
				list.add(uri);
			}
		} finally{
			if( qexec != null ){
				qexec.close();
			}
		}

		//TODO
		
		
		return events;
	}

	public boolean setInformationSource(Object obj){
		if(obj instanceof String){
			String collectionName = (String)obj;
			sparqlEndpoint += collectionName;
			return true;
		}
		return false;
	}

	@Override
	public Storyline fillStoryline(Storyline sl) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
