# e-SemanticStorytelling

The e-SemanticStorytelling module performs semantic stories generation. Given either a trained template, new stories can be generated for a providede NIF collection.

##Story Generation

This endpoint allows the generation of semanatic stories based on a predefined template and a collection of documents in NIF format.

###Endpoint

https://api.digitale-kuratierung.de/api/e-sst/generateStoryFromCollection

###Input

The API conforms to the general NIF API specifications. For more details, see: http://persistence.uni-leipzig.org/nlp2rdf/specification/api.html

In addition to the input, informat and outformat parameters, the following parameters have to be set to perform `Semantic Story Genration` on the input:

`storyType`: type of the story that will be generated. For now, there is only one available value: `biography`.

`filters`: this parameters has not been used for the moment but it is meant for the filtering of the information included in the generation process.

`version`: the version of the approach used for the generation of the story. There are two approaches available: `v1`, which generates a story composed by a list of events, and `v2`, which generates a more structured story where the events are organized in clusters. 

NOTE: The second approach (`v2`) is still not completely functional and has some known bugs. 

###Output

A document in NIF format or JSON containing the generated story.

Example cURL post for using the ner analysis:
TO BE INCLUDED

##Template Training 

This endpoint is available for training new templates. For the moment, only single list events biography templates can be generated.

###Endpoint

https://api.digitale-kuratierung.de/api/e-sst/trainStoryTemplate

###Input

`storyType`: type of the story that will be generated. For now, there is only one available value: `biography`.

`filters`: this parameters has not been used for the moment but it is meant for the filtering of the information included in the generation process.

`version`: the version of the approach used for the training of the template. There are two approaches available: `v1`, which generates a template composed by a list of events, and `v2`, which generates a more structured template where the events are organized in clusters. 

`threshold`: score limit for the events that will be included in the training process.

###Output

A JSON string containing the Story Template.

Examle cURL post: 
TO BE COMPLETED
