package de.dkt.eservices.esst;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import de.dkt.eservices.esst.templates.StoryTemplate;
import de.dkt.eservices.esst.templates.StoryTemplateFactory;
import eu.freme.bservices.testhelper.TestHelper;
import eu.freme.bservices.testhelper.ValidationHelper;
import eu.freme.bservices.testhelper.api.IntegrationTestSetup;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ESSTTest {

	TestHelper testHelper;
	ValidationHelper validationHelper;

	@Before
	public void setup() {
		ApplicationContext context = IntegrationTestSetup
				.getContext(TestConstants.pathToPackage);
		testHelper = context.getBean(TestHelper.class);
		validationHelper = context.getBean(ValidationHelper.class);
	}

	private HttpRequestWithBody baseRequest(String path) {
		String url = testHelper.getAPIBaseUrl() + "/e-sst"+path;
		return Unirest.post(url);
	}

	private HttpRequestWithBody trainModelRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-mallet/trainModel";
		return Unirest.post(url);
	}

	@Test
	public void test0_SanityCheck() throws UnirestException, IOException,
	Exception {
		HttpResponse<String> response = baseRequest("/testURL")
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle").asString();
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);
	}

	@Test
	public void test1_CheckTemplateV1Generation() throws UnirestException, IOException,Exception {
		StoryTemplate st = StoryTemplateFactory.getTemplate("biography", "v1");
		System.out.println(st.getJSONObject().toString(1));
	}

//	@Test
//	public void testM_3_AnalyzeNIFTextDocumentClassification() throws UnirestException, IOException,Exception {
//		HttpResponse<String> response = documentClassificationRequest()
//				.queryString("informat", "turtle")
//				.queryString("input", TestConstants.inputText)
//				.queryString("outformat", "turtle")
//				.queryString("modelName", "condat_types")
////				.queryString("modelPath", "recursos/")
//				.queryString("language", "de")
//				.asString();
//		Assert.assertEquals(response.getStatus(),200);
//		assertTrue(response.getBody().length() > 0);
//		Assert.assertEquals(TestConstants.expectedResponseClassification2, response.getBody());
//	}

}
