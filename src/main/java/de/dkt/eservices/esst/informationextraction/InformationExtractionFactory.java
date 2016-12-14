package de.dkt.eservices.esst.informationextraction;

import org.apache.log4j.Logger;

public class InformationExtractionFactory {

	static Logger logger = Logger.getLogger(InformationExtractionFactory.class);

	public static InformationExtraction generateInformationExtraction(String ieType) throws Exception {
		if(ieType.equalsIgnoreCase("sparql")){
			return new SparqlInformationExtraction();
		}
		else if(ieType.equalsIgnoreCase("nif")){
			return new NIFInformationExtraction();
		}
		throw new Exception("Unsupported storyType");
	}

}
