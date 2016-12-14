package de.dkt.eservices.esst.ontology;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import de.dkt.common.niftools.ITSRDF;
import de.dkt.common.niftools.NIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.TIME;
import de.dkt.eservices.esst.linguistic.Entity;
import de.dkt.eservices.esst.linguistic.Event;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;

public class NIFManagement {

	public static Map<String,Event> extractEventsExtended(Model nifModel){
		Map<String,Event> list = new HashMap<String,Event>();

		//ResIterator iterEntities = nifModel.listSubjectsWithProperty(NIF.entity);
//		ResIterator iterEntities = nifModel.listSubjectsWithProperty(RDF.type, DKTNIF.Event);
//		System.out.println(NIFReader.model2String(nifModel, RDFSerialization.TURTLE));

		ResIterator iterEntities = nifModel.listSubjectsWithProperty(RDF.type, SST.Event);
        while (iterEntities.hasNext()) {
//        	System.out.println("entramos para algo");
    		Map<String,String> map = new HashMap<String,String>();
            Resource r = iterEntities.nextResource();

            String entityURI = r.getURI();
//            System.out.println(entityURI);
            
            StmtIterator it5 = nifModel.listStatements(r, (Property)null, (RDFNode)null);
//            while(it5.hasNext()){
//            	Statement st5 = it5.next();
//            	System.out.println("\t--------------------------------");
//            	System.out.println("\t"+st5.getSubject().getURI());
//            	System.out.println("\t"+st5.getPredicate().getURI());
//            	System.out.println("\t"+st5.getObject().toString());
//            }
            Resource resSubj = r.getPropertyResourceValue(SST.eventSubject);
            Resource resPred = r.getPropertyResourceValue(SST.eventPredicate);
            Resource resObj = r.getPropertyResourceValue(SST.eventObject);
            Resource resTime = r.getPropertyResourceValue(SST.timestamp);
            Statement resRel = r.getProperty(SST.eventRelevance);
            double relevance = 1;
            if(resRel!=null){
            	relevance = resRel.getLiteral().getDouble();
            }

            Entity entSubj =null;
            if(resSubj!=null){
            	String subjText = (resSubj.hasProperty(NIF.anchorOf))?resSubj.getProperty(NIF.anchorOf).getString():null;
            	String subjURL = (resSubj.hasProperty(ITSRDF.taIdentRef))?resSubj.getProperty(ITSRDF.taIdentRef).getResource().getURI():null;
            	String subjType = (resSubj.hasProperty(ITSRDF.taClassRef))?resSubj.getProperty(ITSRDF.taClassRef).getResource().getURI():null;
            	entSubj = new Entity(subjText, subjURL, subjType);
            }
            else{
//            	System.out.println("ERROR in subject");
            }

            Entity entPred =null;
            if(resPred!=null){
            	String predText = (resPred.hasProperty(NIF.anchorOf))?resPred.getProperty(NIF.anchorOf).getString():null;
            	String predURL = (resPred.hasProperty(ITSRDF.taIdentRef))?resPred.getProperty(ITSRDF.taIdentRef).getResource().getURI():null;
            	String predType = (resPred.hasProperty(ITSRDF.taClassRef))?resPred.getProperty(ITSRDF.taClassRef).getResource().getURI():null;
            	entPred = new Entity(predText, predURL, predType);
            }
            else{
//            	System.out.println("ERROR in predicate");
            }

            Entity entObj =null;
            if(resObj!=null){
            	String objText = (resObj.hasProperty(NIF.anchorOf))?resObj.getProperty(NIF.anchorOf).getString():null;
            	String objURL = (resObj.hasProperty(ITSRDF.taIdentRef))?resObj.getProperty(ITSRDF.taIdentRef).getResource().getURI():null;
            	String objType = (resObj.hasProperty(ITSRDF.taClassRef))?resObj.getProperty(ITSRDF.taClassRef).getResource().getURI():null;
            	entObj = new Entity(objText, objURL, objType);
            }
            else{
//            	System.out.println("ERROR in object");
            }

            Date timestamp = null;
            if(resTime!=null){
            	String time = null;
            	try{
            		SimpleDateFormat parser=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            		time = resTime.getProperty(TIME.intervalStarts).getString();
                	timestamp = parser.parse(time);
            	}
            	catch(Exception e){
//            		System.out.println("no interval or fail");
                	timestamp = null;
//                	StmtIterator stit = resTime.listProperties();
//                	while(stit.hasNext()){
//                		Statement st = stit.next();
//                		System.out.println("\t"+st.getSubject().getURI().toString());
//                		System.out.println("\t"+st.getPredicate().getURI().toString());
//                		if(st.getObject().isLiteral()){
//                    		System.out.println("\t"+st.getObject().asLiteral().getString());
//                		}
//                		else{
//                    		System.out.println("\t"+st.getObject().asResource().getURI().toString());            			
//                		}
//                	}
            	}
//            	System.out.println("--"+time);
//            	timestamp = new Date(time);
            }

            if(entSubj!=null && entPred!=null && entObj!=null){
	            Event e = new Event(entSubj, entPred, entObj, timestamp, relevance);
	            list.put(entityURI, e);
            }
        }
        if(list.isEmpty()){
        	return null;
        }
		return list;
	}


}
