package de.dkt.eservices.esst;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.feedback.InteractionManagement;
import de.dkt.common.niftools.NIFReader;
import de.dkt.eservices.esst.filters.Filter;
import de.dkt.eservices.esst.filters.FilterFactory;
import de.dkt.eservices.esst.templates.StoryTemplate;
import de.dkt.eservices.esst.templates.StoryTemplateFactory;
import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.conversion.rdf.RDFConversionService;
import eu.freme.common.rest.BaseRestController;
import eu.freme.common.rest.NIFParameterSet;

@RestController
public class ESemanticStorytellingRestController extends BaseRestController{

	Logger logger = Logger.getLogger(ESemanticStorytellingRestController.class);
	
	@Autowired
	ESemanticStorytellingService sstService;

	@Autowired
	RDFConversionService rdfConversionService;

	@RequestMapping(value = "/e-sst/testURL", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> testURL(
			@RequestParam(value = "preffix", required = false) String preffix,
            @RequestBody(required = false) String postBody) throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
    	responseHeaders.add("Content-Type", "text/plain");
    	ResponseEntity<String> response = new ResponseEntity<String>("The restcontroller is working properly", responseHeaders, HttpStatus.OK);
    	return response;
	}

	@RequestMapping(value = "/e-sst/generateStoryFromSparql", method = { RequestMethod.GET })
	public ResponseEntity<String> generateStoryFromSparql(
			HttpServletRequest request,
			@RequestParam(value = "outformat", required = false) String outformat,
			@RequestParam(value = "prefix", required = false) String prefix,
			@RequestParam(value = "language", required = false) String language,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
			@RequestHeader(value = "collectionName", required = false) String collectionName,
			@RequestParam(value = "storyType", required = false) String storyType,
			@RequestParam(value = "version", required = false) String version,
			@RequestParam(value = "filters", required = false) String sFilters,
			@RequestBody(required = false) String postBody) throws Exception {
		try {
			List<Filter> filters = FilterFactory.extractFiltersFromString(sFilters, "json");
			Model outputModel = sstService.processDocumentsCollectionSparql(collectionName, storyType, version, filters);
			String nifOutput = NIFReader.model2String(outputModel, RDFSerialization.fromValue(outformat));
			InteractionManagement.sendInteraction("dkt-usage@"+request.getHeader("X-FORWARDED-FOR"), "usage", "e-sst/generateStoryFromSparql", "Success", "", "", "", "");
			return new ResponseEntity<String>(nifOutput, new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			InteractionManagement.sendInteraction("dkt-usage@"+request.getHeader("X-FORWARDED-FOR"), "error", "e-sst/generateStoryFromSparql", e.getMessage(), "", "Exception", e.getMessage(), "");
			throw e;
		}
	}


	@RequestMapping(value = "/e-sst/generateStoryFromCollection", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> generateStory(
			HttpServletRequest request,
			@RequestParam(value = "input", required = false) String input,
			@RequestParam(value = "i", required = false) String i,
			@RequestParam(value = "informat", required = false) String informat,
			@RequestParam(value = "f", required = false) String f,
			@RequestParam(value = "outformat", required = false) String outformat,
			@RequestParam(value = "o", required = false) String o,
			@RequestParam(value = "prefix", required = false) String prefix,
			@RequestParam(value = "p", required = false) String p,
			@RequestParam(value = "language", required = false) String language,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
			@RequestParam(value = "storyType", required = false) String storyType,
			@RequestParam(value = "version", required = false) String version,
			@RequestParam(value = "filters", required = false) String sFilters,
			@RequestBody(required = false) String postBody) throws Exception {
		if (input == null) {
			input = i;
		}
		if (informat == null) {
			informat = f;
		}
		if (outformat == null) {
			outformat = o;
		}
		if (prefix == null) {
			prefix = p;
		}
//		ParameterChecker.checkNotNullOrEmpty(inputDataFormat, "input data type", logger);
        NIFParameterSet nifParameters = this.normalizeNif(input, informat, outformat, postBody, acceptHeader, contentTypeHeader, prefix);
        Model inModel = null;
        if (nifParameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
			rdfConversionService.plaintextToRDF(inModel, nifParameters.getInput(),language, nifParameters.getPrefix());
        } else {
            inModel = rdfConversionService.unserializeRDF(nifParameters.getInput(), nifParameters.getInformat());
        }
		try {
			List<Filter> filters = FilterFactory.extractFiltersFromString(sFilters, "json");
			Model outputModel = sstService.processDocumentsCollectionNIF(inModel, storyType, version, filters);
			String nifOutput = NIFReader.model2String(outputModel, nifParameters.getOutformat());
			InteractionManagement.sendInteraction("dkt-usage@"+request.getHeader("X-FORWARDED-FOR"), "usage", "e-sst/generateStoryFromCollection", "Success", "", "", "", "");
			return new ResponseEntity<String>(nifOutput, new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			InteractionManagement.sendInteraction("dkt-usage@"+request.getHeader("X-FORWARDED-FOR"), "error", "e-sst/generateStoryFromCollection", e.getMessage(), "", "Exception", e.getMessage(), "");
			throw e;
		}
	}

	@RequestMapping(value = "/e-sst/regenerateStoryFromSparql", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> regenerateStorySparql(
			HttpServletRequest request,
			@RequestParam(value = "input", required = false) String input,
			@RequestParam(value = "language", required = false) String language,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
			@RequestParam(value = "collectionName", required = false) String collectionName,
			@RequestParam(value = "filters", required = false) String sFilters,
			@RequestBody(required = false) String postBody) throws Exception {
		if (input == null) {
			input = postBody;
		}
		if (input == null) {
			throw new BadRequestException("There is no input found. Input parameter and body are empty.");
		}
		try {
			StoryTemplate story = StoryTemplateFactory.extractTemplateFromString(input,"turtle");
			List<Filter> filters = FilterFactory.extractFiltersFromString(sFilters, "json");
			Model outputModel = sstService.regenerateStoryFromSparql(collectionName, story, filters);
			String nifOutput = NIFReader.model2String(outputModel, RDFSerialization.TURTLE);
			InteractionManagement.sendInteraction("dkt-usage@"+request.getHeader("X-FORWARDED-FOR"), "usage", "e-sst/regenerateStorySparql", "Success", "", "", "", "");
			return new ResponseEntity<String>(nifOutput, new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			InteractionManagement.sendInteraction("dkt-usage@"+request.getHeader("X-FORWARDED-FOR"), "error", "e-sst/regenerateStorySparql", e.getMessage(), "", "Exception", e.getMessage(), "");
			throw e;
		}
	}
	@RequestMapping(value = "/e-sst/regenerateStoryFromNIFCollection", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> regenerateStory(
			HttpServletRequest request,
			@RequestParam(value = "input", required = false) String input,
			@RequestParam(value = "i", required = false) String i,
			@RequestParam(value = "informat", required = false) String informat,
			@RequestParam(value = "f", required = false) String f,
			@RequestParam(value = "outformat", required = false) String outformat,
			@RequestParam(value = "o", required = false) String o,
			@RequestParam(value = "prefix", required = false) String prefix,
			@RequestParam(value = "p", required = false) String p,
			@RequestParam(value = "language", required = false) String language,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
			@RequestParam(value = "storyType", required = false) String storyType,
			@RequestParam(value = "informationSource", required = false) String informationSource,
			@RequestParam(value = "filters", required = false) String sFilters,
			@RequestParam(value = "story", required = false) String sStory,
			@RequestBody(required = false) String postBody) throws Exception {

		if (input == null) {
			input = i;
		}
		if (informat == null) {
			informat = f;
		}
		if (outformat == null) {
			outformat = o;
		}
		if (prefix == null) {
			prefix = p;
		}
        NIFParameterSet nifParameters = this.normalizeNif(input, informat, outformat, postBody, acceptHeader, contentTypeHeader, prefix);
        Model inModel = null;
        if (nifParameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
			rdfConversionService.plaintextToRDF(inModel, nifParameters.getInput(),language, nifParameters.getPrefix());
        } else {
            inModel = rdfConversionService.unserializeRDF(nifParameters.getInput(), nifParameters.getInformat());
        }
		try {
			StoryTemplate story = StoryTemplateFactory.extractTemplateFromString(sStory,"turtle");
			List<Filter> filters = FilterFactory.extractFiltersFromString(sFilters, "json");
			Model outputModel = sstService.regenerateStoryFromNIF(inModel, story, filters);
			String nifOutput = NIFReader.model2String(outputModel, nifParameters.getOutformat());
			InteractionManagement.sendInteraction("dkt-usage@"+request.getHeader("X-FORWARDED-FOR"), "usage", "e-sst/regenerateStory", "Success", "", "", "", "");
			return new ResponseEntity<String>(nifOutput, new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			InteractionManagement.sendInteraction("dkt-usage@"+request.getHeader("X-FORWARDED-FOR"), "error", "e-sst/regenerateStory", e.getMessage(), "", "Exception", e.getMessage(), "");
			throw e;
		}
	}

	@RequestMapping(value = "/e-sst/trainStoryTemplate", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> trainTemplate(
			HttpServletRequest request,
			@RequestParam(value = "input", required = false) String input,
			@RequestParam(value = "i", required = false) String i,
			@RequestParam(value = "informat", required = false) String informat,
			@RequestParam(value = "f", required = false) String f,
			@RequestParam(value = "outformat", required = false) String outformat,
			@RequestParam(value = "o", required = false) String o,
			@RequestParam(value = "prefix", required = false) String prefix,
			@RequestParam(value = "p", required = false) String p,
			@RequestParam(value = "language", required = false) String language,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
			@RequestParam(value = "storyType", required = false) String storyType,
			@RequestParam(value = "filters", required = false) String sFilters,
			@RequestBody(required = false) String postBody) throws Exception {

		if (input == null) {
			input = i;
		}
		if (informat == null) {
			informat = f;
		}
		if (outformat == null) {
			outformat = o;
		}
		if (prefix == null) {
			prefix = p;
		}
        NIFParameterSet nifParameters = this.normalizeNif(input, informat, outformat, postBody, acceptHeader, contentTypeHeader, prefix);
        Model inModel = null;
        if (nifParameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
			rdfConversionService.plaintextToRDF(inModel, nifParameters.getInput(),language, nifParameters.getPrefix());
        } else {
            inModel = rdfConversionService.unserializeRDF(nifParameters.getInput(), nifParameters.getInformat());
        }
		try {
			boolean correctlyTrained = sstService.trainTemplate(inModel, storyType);
			if(correctlyTrained){
				InteractionManagement.sendInteraction("dkt-usage@"+request.getHeader("X-FORWARDED-FOR"), "usage", "e-sst/trainTemplate", "Success", "", "", "", "");
				return new ResponseEntity<String>("Template () correctly trained.", new HttpHeaders(), HttpStatus.OK);
			}
			else{
				InteractionManagement.sendInteraction("dkt-usage@"+request.getHeader("X-FORWARDED-FOR"), "usage", "e-sst/trainTemplate", "Error", "", "Failure", "The training process returned FALSE value. Check the logs.", "");
				return new ResponseEntity<String>("Template () correctly trained.", new HttpHeaders(), HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			InteractionManagement.sendInteraction("dkt-usage@"+request.getHeader("X-FORWARDED-FOR"), "usage", "e-sst/trainTemplate", e.getMessage(), "", "Exception", e.getMessage(), "");
			throw e;
		}
	}
	
}
