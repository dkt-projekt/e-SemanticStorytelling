package de.dkt.eservices.esst;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.NIFManagement;
import de.dkt.eservices.esst.filters.Filter;
import de.dkt.eservices.esst.informationextraction.InformationExtraction;
import de.dkt.eservices.esst.informationextraction.InformationExtractionFactory;
import de.dkt.eservices.esst.informationextraction.NIFInformationExtraction;
import de.dkt.eservices.esst.informationextraction.SparqlInformationExtraction;
import de.dkt.eservices.esst.templates.GenericStoryTemplateV1;
import de.dkt.eservices.esst.templates.StoryTemplate;
import de.dkt.eservices.esst.templates.StoryTemplateFactory;
import de.dkt.eservices.esst.templates.TemplateTraining;
import eu.freme.common.conversion.rdf.RDFConversionService;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 *
 *
 */
@Component
public class ESemanticStorytellingService {

	Logger logger = Logger.getLogger(ESemanticStorytellingService.class);

	String sstsDirectory = "ssts/";
	
	@Autowired
	RDFConversionService rdfConversion;
    
	public Model processDocumentsCollectionSparql(String collectionName,String storyType,String version,List<Filter> filters) throws Exception {
		try {
			/**
			 * Decide which template is going to be used for the required type of story
			 */
			StoryTemplate template = StoryTemplateFactory.getTemplate(storyType,version);
			
			/**
			 * Filling the template with the appropriate information
			 */
			SparqlInformationExtraction iem = new SparqlInformationExtraction(collectionName);
			if(!template.fillTemplate(iem)){
				String msg = "Error at filling the story template";
				logger.error(msg);
	            throw new ExternalServiceFailedException(msg);
			}
			if(filters!=null){
				if(!template.filterTemplate(filters)){
					String msg = "Error at filtering the story template";
					logger.error(msg);
		            throw new ExternalServiceFailedException(msg);
				}
			}
			return template.getModel(null);
		} catch (BadRequestException e) {
			logger.error(e.getMessage());
            throw e;
		}
	}

	public Model processDocumentsCollectionNIF(Model collectionModel,String storyType,String version, List<Filter> filters) throws Exception {
		try {
//			List<Model> documents = NIFManagement.extractDocumentsModels(collectionModel);

			/**
			 * Decide which template is going to be used for the required type of story
			 */
			StoryTemplate template = StoryTemplateFactory.getTemplate(storyType,version);
			
			/**
			 * Filling the template with the appropriate information
			 */
			NIFInformationExtraction iem = new NIFInformationExtraction(collectionModel);
			if(!template.fillTemplate(iem)){
				String msg = "Error at filling the story template";
				logger.error(msg);
	            throw new ExternalServiceFailedException(msg);
			}
			if(filters!=null){
				if(!template.filterTemplate(filters)){
					String msg = "Error at filtering the story template";
					logger.error(msg);
		            throw new ExternalServiceFailedException(msg);
				}
			}
			return template.getModel(null);
		} catch (BadRequestException e) {
			logger.error(e.getMessage());
            throw e;
		}
	}

	public StoryTemplate getStoryFromNIFCollection(Model collectionModel,String storyType,String version, List<Filter> filters) throws Exception {
		try {
//			List<Model> documents = NIFManagement.extractDocumentsModels(collectionModel);
			/**
			 * Decide which template is going to be used for the required type of story
			 */
			StoryTemplate template = StoryTemplateFactory.getTemplate(storyType,version);
			
			/**
			 * Filling the template with the appropriate information
			 */
			
			System.out.println(template.getJSONObject().toString(2));
			
			NIFInformationExtraction iem = new NIFInformationExtraction(collectionModel);
			if(!template.fillTemplate(iem)){
				String msg = "Error at filling the story template";
				logger.error(msg);
	            throw new ExternalServiceFailedException(msg);
			}

			System.out.println(template.getJSONObject().toString(2));

			if(filters!=null){
				if(!template.filterTemplate(filters)){
					String msg = "Error at filtering the story template";
					logger.error(msg);
		            throw new ExternalServiceFailedException(msg);
				}
			}
			return template;
		} catch (BadRequestException e) {
			logger.error(e.getMessage());
            throw e;
		}
	}

	public StoryTemplate regenerateStoryFromNIF(Model collectionModel,StoryTemplate template,List<Filter> filters) throws Exception {
		try {
//			List<Model> documents = NIFManagement.extractDocumentsModels(collectionModel);
			NIFInformationExtraction iem = new NIFInformationExtraction(collectionModel);
			if(!template.fillTemplate(iem)){
				String msg = "Error at refilling the story template";
				logger.error(msg);
	            throw new ExternalServiceFailedException(msg);
			}
//			System.out.println(template.getJSONObject().toString(2));
			if(filters!=null){
				if(!template.filterTemplate(filters)){
					String msg = "Error at filtering the story template";
					logger.error(msg);
		            throw new ExternalServiceFailedException(msg);
				}
			}
			return template;
		} catch (BadRequestException e) {
			logger.error(e.getMessage());
            throw e;
		}
	}

	public Model regenerateStoryFromSparql(String collectionName,StoryTemplate story,List<Filter> filters) throws Exception {
		try {
			InformationExtraction iem = InformationExtractionFactory.generateInformationExtraction("sparql");
			iem.setInformationSource(collectionName);
			if(!story.refillTemplate(iem,filters)){
				String msg = "Error at filling the story template";
				logger.error(msg);
	            throw new ExternalServiceFailedException(msg);
			}
			return story.getModel(null);
		} catch (BadRequestException e) {
			logger.error(e.getMessage());
            throw e;
		}
	}

	public StoryTemplate trainTemplate(Model inModel, String storyType, String version, double threshold) throws Exception{
		//Extract documents from collection.
		List<Model> documents = NIFManagement.extractDocumentsModels(inModel);
		if(documents==null || documents.isEmpty()){
			throw new ExternalServiceFailedException("There are no documents at the input.");
		}
		TemplateTraining tt = new TemplateTraining();
		return tt.trainTemplate(documents, version, threshold);
	}

	public StoryTemplate trainTemplate(List<Model> documents, String storyType, String version, double threshold) throws Exception{
		if(documents==null || documents.isEmpty()){
			throw new ExternalServiceFailedException("There are no documents at the input.");
		}
		TemplateTraining tt = new TemplateTraining();
		return tt.trainTemplate(documents, version, threshold);
	}

}
