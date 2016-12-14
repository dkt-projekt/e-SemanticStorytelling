package de.dkt.eservices.esst.templates;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import de.dkt.common.filemanagement.FileFactory;
import de.dkt.common.niftools.NIFReader;
import de.dkt.eservices.esst.ontology.SST;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.exception.ExternalServiceFailedException;

public class StoryTemplateFactory {

	static Logger logger = Logger.getLogger(StoryTemplateFactory.class);

	public static StoryTemplate getTemplate(String storyType,String version) throws Exception {
		File templateFile = FileFactory.generateFileInstance("templates/"+version+"/"+storyType + "_template.json"); 
		InputStream in = new FileInputStream(templateFile);
		String content = IOUtils.toString(in);
		return new GenericStoryTemplateV1(content);
/*		if(storyType.equalsIgnoreCase("biography")){
			return new BiographyTemplate();
		}
//		else if(storyType.equalsIgnoreCase("feature")){
//			return new FeatureTemplate();
//		}
		else if(storyType.equalsIgnoreCase("news")){
			return new NewsTemplate();
		}
		else if(storyType.equalsIgnoreCase("narrative")){
			return new NarrativeTemplate();
		}
//		else if(storyType.equalsIgnoreCase("instruction")){
//			return new InstructionTemplate();
//		}
//		else if(storyType.equalsIgnoreCase("productreview")){
//			return new ProductReviewTemplate();
//		}
//		else if(storyType.equalsIgnoreCase("troubleshooting")){
//			return new TroubleShootingTemplate();
//		}
 
		throw new Exception("Unsupported storyType");*/
	}

	public static StoryTemplate extractTemplateFromString(String sStory, String format) throws Exception {
		if(format.equalsIgnoreCase("json") || format.equalsIgnoreCase("application/json")){
			JSONObject obj = new JSONObject(sStory);
			String storyType = obj.getString("storyType");
			if(storyType.equalsIgnoreCase("biography")){
				return new BiographyTemplate(obj);
			}
			else if(storyType.equalsIgnoreCase("news")){
				return new NewsTemplate(obj);
			}
			else if(storyType.equalsIgnoreCase("narrative")){
				return new NarrativeTemplate(obj);
			}
			throw new Exception("Unsupported storyType");
		}
		if(format.equalsIgnoreCase("turtle") || format.equalsIgnoreCase("text/turtle")){
			Model m = NIFReader.extractModelFromFormatString(sStory, RDFSerialization.TURTLE);
			ResIterator it = m.listResourcesWithProperty(RDF.type, SST.Story);
			Resource storyResource = it.next();
//			this.name = storyUrl.substring(storyUrl.lastIndexOf('/')+1);
			NodeIterator nodeIt = m.listObjectsOfProperty(storyResource, SST.storyType);
			String storyType = nodeIt.next().asLiteral().getString();

			if(storyType.equalsIgnoreCase("biography")){
				return new BiographyTemplate(m);
			}
			else if(storyType.equalsIgnoreCase("news")){
				return new NewsTemplate(m);
			}
			else if(storyType.equalsIgnoreCase("narrative")){
				return new NarrativeTemplate(m);
			}
			throw new Exception("Unsupported storyType");
		}
		else{
			String msg = "Unsupported inFormat for filters.";
			logger.error(msg);
			throw new ExternalServiceFailedException(msg);
		}
	}

}
