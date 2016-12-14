package de.dkt.eservices.esst.ontology;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class SST {

    protected static final String uri = "http://persistence.dfki.de/ontologies/sst-core#";

    /**
     * returns the URI for this schema
     * 
     * @return the URI for this schema
     */
    public static String getURI() {
        return uri;
    }

    protected static final Resource resource(String local) {
        return ResourceFactory.createResource(uri + local);
    }

    protected static final Property property(String local) {
        return ResourceFactory.createProperty(uri, local);
    }

    public static final Resource Story = resource("Story");
    public static final Resource Storyline = resource("Storyline");
    public static final Resource Event = resource("Event");
    public static final Resource Entity = resource("Entity");
    public static final Resource Phrase = resource("Phrase");
    public static final Resource RFC5147String = resource("RFC5147String");
    public static final Resource OffsetString = resource("OffsetBasedString");

    public static final Resource ContextCollection = resource("ContextCollection");
    public static final Property hasContext = property("hasContext");

    public static final Property storyType = property("storyType");

    public static final Property anchorOf = property("anchorOf");
    public static final Property belongsToStory = property("belongsToStory");
    public static final Property belongsToStoryLine = property("belongsToStoryLine");
    public static final Property eventSubject = property("eventSubject");
    public static final Property eventPredicate = property("eventPredicate");
    public static final Property eventObject = property("eventObject");
    public static final Property timestamp = property("timestamp");
    public static final Property eventRelevance = property("eventRelevance");

    public static final Property entityUrl = property("entityUrl");
    public static final Property entityType = property("entityType");
    public static final Property beginTS = property("beginTS");
    public static final Property endTS = property("endTS");
    public static final Property mainCharacter = property("mainCharacter");

}
