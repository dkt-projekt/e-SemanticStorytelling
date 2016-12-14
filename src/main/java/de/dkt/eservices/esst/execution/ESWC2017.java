package de.dkt.eservices.esst.execution;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.ITSRDF;
import de.dkt.common.niftools.NIF;
import de.dkt.common.niftools.NIFManagement;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.eservices.esst.ESemanticStorytellingService;
import de.dkt.eservices.esst.filters.Filter;
import de.dkt.eservices.esst.linguistic.Entity;
import de.dkt.eservices.esst.linguistic.Event;
import de.dkt.eservices.esst.templates.GenericStoryTemplateV1;
import de.dkt.eservices.esst.templates.GenericStoryTemplateV2;
import de.dkt.eservices.esst.templates.StoryTemplate;
import de.dkt.eservices.esst.templates.evaluation.TemplateEvaluation;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.exception.BadRequestException;

public class ESWC2017 {

	ESemanticStorytellingService service = new ESemanticStorytellingService();

	public StoryTemplate trainTemplate(List<Model> documents, String storyType, String version, double threshold) throws Exception{
		return service.trainTemplate(documents, storyType, version, threshold);
	}

	public double evaluateStory(){

		// TODO
		
		return 0;
	}

	public StoryTemplate generateStory(List<Model> documents, String storyType, String version,List<Filter> filters) throws Exception {
		Model collectionModel = NIFManagement.createCollectionFromDocuments(null, documents);
		return service.getStoryFromNIFCollection(collectionModel, storyType, version, filters);
	}

	public StoryTemplate generateStory(List<Model> documents, StoryTemplate template, String version,List<Filter> filters) throws Exception {
		Model collectionModel = NIFManagement.createCollectionFromDocuments(null, documents);
		return service.regenerateStoryFromNIF(collectionModel, template, filters);
	}

	public static void main(String[] args) throws Exception {
		String executionType = "training";
		// Training properties
		String nifFolder = "src/test/resources/annotatedDocuments/";
		double threshold = 1.5;
		// Generation Properties
		String storyType = "biography";
		String trainingVersion = "v1";
		String generationVersion = "v1";
		
		File folder = new File(nifFolder);
		File [] fileList = folder.listFiles();
		List<Model> biographies = new LinkedList<Model>();
		
		String LOC="",PER="",ORG="",UNK="",ACT="",EVE="",TOTAL="";
		
		for (File f : fileList) {
			String content = FileUtils.readFileToString(f);
			Model mAux = NIFReader.extractModelFromFormatString(content, RDFSerialization.JSON_LD);
			biographies.add(mAux);

			System.out.println(NIFReader.model2String(mAux, RDFSerialization.TURTLE));
			System.exit(0);
//			int loc=0,per=0,org=0,unk=0,act=0;
//
//			Map<String,Map<String,String>> entitiesMap = NIFReader.extractEntitiesExtended(mAux);
//			
//			Set<String> keys = entitiesMap.keySet();
//			for (String k : keys) {
//				String type = entitiesMap.get(k).get(ITSRDF.taClassRef.toString());
//				
//				if(type.contains("Location")){
//					loc++;
//				}
//				else if(type.contains("Organisation")){
//					org++;
//				}
//				else if(type.contains("Person")){
//					per++;
//				}
//				else if(type.contains("Thing")){
//					unk++;
//				}
//				else if(type.contains("Action")){
//					act++;
//				}
//			}
//			
//			LOC+=" & "+loc;
//			PER+=" & "+per;
//			ORG+=" & "+org;
//			UNK+=" & "+unk;
//			ACT+=" & "+act;
//			Map<String,Event> eventsMap = de.dkt.eservices.esst.ontology.NIFManagement.extractEventsExtended(mAux);
//			EVE+=" & "+eventsMap.size();
//			TOTAL+=" & "+(loc+org+per+unk+act+eventsMap.size());
//			//			System.out.println(NIFReader.model2String(mAux, RDFSerialization.TURTLE));
		}
		
//		System.out.println(LOC);
//		System.out.println(PER);
//		System.out.println(ORG);
//		System.out.println(UNK);
//		System.out.println(ACT);
//		System.out.println(EVE);
//		System.out.println(TOTAL);

		ESWC2017 eswc = new ESWC2017();
//
//		System.out.println(biographies.size());
//		//Generate templates out of the goldsstandard. Use all of them for generating the templates.
//		StoryTemplate st1 = eswc.trainTemplate(biographies, storyType, trainingVersion, 0);
//		JSONObject storyTrainedTemplate = st1.getEmptyTemplateJSONObject();
//		System.out.println(storyTrainedTemplate.toString(2));
////		
//		System.out.println(st1.getSimpleScreenString());
////		StoryTemplate story = eswc.generateStory(biographies, storyType, generationVersion, null);
		
		eswc.crossEvaluation(6, biographies, 0, storyType, trainingVersion, generationVersion);
		System.out.println("\n");
		eswc.crossEvaluation(6, biographies, 1, storyType, trainingVersion, generationVersion);
		System.out.println("\n");
		eswc.crossEvaluation(6, biographies, threshold, storyType, trainingVersion, generationVersion);
		System.out.println("\n");
		eswc.crossEvaluation(6, biographies, 2, storyType, trainingVersion, generationVersion);
		System.out.println("\n");
		eswc.crossEvaluation(6, biographies, 5, storyType, trainingVersion, generationVersion);
		System.out.println("\n");
		eswc.crossEvaluation(6, biographies, 10, storyType, trainingVersion, generationVersion);
		System.out.println("\n");
		eswc.crossEvaluation(6, biographies, 15, storyType, trainingVersion, generationVersion);

	}
	
	
	public void crossEvaluation(int iterations, List<Model> documents, double threshold, String storyType, 
			String version, String generationVersion) throws Exception{
		ESWC2017 eswc = new ESWC2017();
		
		double totalFinal [] = new double[5];
		DecimalFormat df = new DecimalFormat("#.###");
		for (int i = 0; i < iterations; i++) {
			//Make the divisions between training and evaluation
			List<Model> training=new LinkedList<Model>();
			List<Model> test=new LinkedList<Model>();
//			System.out.println("-----------");
//			String tr = "";
//			String te = "";
			for (int j = 0; j < documents.size(); j++) {
				if( ((j+i)%(iterations))==0 ){
					test.add(documents.get(j));
//					te+=","+j;
				}
				else{
					training.add(documents.get(j));
//					tr+=","+j;
				}
			}
//			System.out.println(tr);
//			System.out.println("TRAINING SIZE: "+training.size());
//			System.out.println(te);
//			System.out.println("TEST SIZE: "+test.size());
//			System.out.println(biographies.size());
			//Generate templates out of the goldsstandard. Use all of them for generating the templates.
			StoryTemplate st1 = eswc.trainTemplate(training, storyType, version, threshold);
//			JSONObject storyTrainedTemplate = st1.getEmptyTemplateJSONObject();
//			System.out.println(storyTrainedTemplate.toString(2));
//			System.out.println(st1.getSimpleScreenString());
			String templateContent = st1.getEmptyTemplateJSONObject().toString();
//			System.out.println(templateContent+"\n\n\n\n\n");
			double [][] totalNumbers = new double[test.size()][];
			for (int j = 0; j < test.size(); j++) {
				Model model = test.get(j);
				List<Model> testDocuments = new LinkedList<Model>();
				testDocuments.add(model);
				StoryTemplate input = null;
				if(version.equalsIgnoreCase("v1")){
					input = new GenericStoryTemplateV1(templateContent);
				}
				else if(version.equalsIgnoreCase("v2")){
					input = new GenericStoryTemplateV2(templateContent);
				}
				StoryTemplate story = eswc.generateStory(testDocuments, input, generationVersion, null);
//
//				//Evaluate filled story.
				TemplateEvaluation evaluation = new TemplateEvaluation();
				totalNumbers[j] = evaluation.evaluateTemplate(story,model,version);
			}
			double [] numbers = new double[totalNumbers[0].length];
			for (int j = 0; j < totalNumbers.length; j++) {
				for (int j2 = 0; j2 < numbers.length; j2++) {
					numbers[j2] = numbers[j2] + totalNumbers[j][j2];
				}
			}
			System.out.print("Iteration ["+i+"]:");
			for (int j2 = 0; j2 < numbers.length; j2++) {
				numbers[j2] = numbers[j2] / totalNumbers.length;
				totalFinal[j2] = totalFinal[j2] + numbers[j2];
				System.out.print(" & " + df.format(numbers[j2]));
			}
			System.out.println();
		}
		for (int j2 = 0; j2 < totalFinal.length; j2++) {
			totalFinal[j2] = totalFinal[j2] / iterations;
			System.out.print(" & " + df.format(totalFinal[j2]));
		}
		System.out.println();
		System.out.print("\t\tME\tP\tR\tF\tfin");
	}
	
}
