package de.dkt.eservices.esst.templates.evaluation;

import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.eservices.esst.linguistic.Entity;
import de.dkt.eservices.esst.linguistic.Event;
import de.dkt.eservices.esst.templates.StoryTemplate;
import de.dkt.eservices.esst.templates.TemplateTraining;
import eu.freme.common.exception.BadRequestException;

public class TemplateEvaluation {

	TemplateTraining training = new TemplateTraining();
	public double[] evaluateTemplate(StoryTemplate story, Model model, String version){
		if(version.equalsIgnoreCase("v1")){
			return evaluateTemplateV1(story, model);
		}
		else if(version.equalsIgnoreCase("v2")){
			return evaluateTemplateV2(story, model);
		}
		throw new BadRequestException("Unsupported version.");
	}

	private double[] evaluateTemplateV1(StoryTemplate story, Model model) {
		double mainEntity=0,precision=0,recall=0,fmeasure=0,combined=0;
		double alpha = 0.8;
		
		Entity mainGold = training.extractMostFrequentEntity(model);

		if(mainGold.equals(story.getMainEntity())){
			mainEntity=1;
		}
		
		List<Event> eventsGold = training.extractOrderedListEvents(model);
		List<Event> events = story.getEvents();

		double cntGood = 0;
		double cntBad = 0;
		for (Event event : events) {
			if(eventsGold.contains(event)){
				cntGood++;
			}
			else{
				cntBad++;
			}
		}
		precision = cntGood/(cntGood+cntBad);
		
		double cntGood2 = 0;
		double cntBad2 = 0;
		for (Event event : eventsGold) {
			if(events.contains(event)){
				cntGood2++;
			}
			else{
				cntBad2++;
			}
		}
		recall = cntGood2/(cntGood2+cntBad2);
		
		fmeasure = 2*precision*recall/(precision+recall);		
		
		combined = (alpha*fmeasure+(1-alpha)*mainEntity)/2;
		
		double [] result = {mainEntity,precision,recall,fmeasure,combined};
		return result;
	}

	private double[] evaluateTemplateV2(StoryTemplate story, Model model) {
		// TODO Auto-generated method stub
		return null;
	}
}
