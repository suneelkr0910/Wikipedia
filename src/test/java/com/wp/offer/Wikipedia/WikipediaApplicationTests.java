package com.wp.offer.Wikipedia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wp.offer.Wikipedia.domain.WikiOffer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class WikipediaApplicationTests {

	private static final String RESOURCE_LOCATION_PATTERN = "http://localhost/wiki/v1/offers";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	@Autowired
	private TestRestTemplate restTemplate;

	@Value("${local.server.port}")
	private int port;

	@Test
	public void createRetrieveAndDeleteOffer() throws ParseException{
		//System.setProperty("proxyPort", "8090");
		//create an offer
		WikiOffer r1 = new WikiOffer(100, "Summer_Offer","offer_100_desc",1300.00,"GBP",DATE_FORMAT.parse("11/07/2018"),3,"ACTIVE");
		//restTemplate.put("http://localhost:" + port + "/wiki/v1/offers",r1);
		restTemplate.postForEntity("http://localhost:" + port +"/wiki/v1/offers", r1, WikiOffer.class);

		//retrieve the created offer
		ResponseEntity<WikiOffer> response = restTemplate.getForEntity("http://localhost:" + port + "/wiki/v1/offers/" + r1.getId(), WikiOffer.class, "");
		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assert.assertEquals(r1.getName(),response.getBody().getName());

		String url = "http://localhost:" + port + "/wiki/v1/offers/{id}";
		long id = r1.getId();
		// delete

		//delete the created Offer
		ResponseEntity<WikiOffer> delResponse = restTemplate.exchange(url,
				HttpMethod.DELETE,
				HttpEntity.EMPTY,
				WikiOffer.class,
				id);

		Assert.assertEquals(delResponse.getBody().getId(), 0);
		Assert.assertEquals(delResponse.getBody().getName(),null);

	}

	@Test
	public void testCreateViolationWithDuplicateOfferName() throws ParseException{
		//create offer
		//public WikiOffer(long id, String name, String description, double price, String currency, Date offerStartDate, int daysValid, String offerStatus) {
		WikiOffer offer1 = new WikiOffer(103, "Summer_Offer","offer_100_desc",1300.00,"GBP",DATE_FORMAT.parse("11/07/2018"),3,"ACTIVE");
		restTemplate.postForEntity("http://localhost:" + port +"/wiki/v1/offers", offer1, WikiOffer.class);
		ResponseEntity<WikiOffer> response = restTemplate.getForEntity("http://localhost:" + port + "/wiki/v1/offers/" + offer1.getId(), WikiOffer.class, "");
		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assert.assertEquals(offer1.getName(),response.getBody().getName());
		//now create offer with samename
		WikiOffer offer2 = new WikiOffer(103, "Summer_Offer","offer_101_desc",1400.00,"GBP",DATE_FORMAT.parse("10/07/2018"),2,"ACTIVE");
		restTemplate.postForEntity("http://localhost:" + port +"/wiki/v1/offers", offer2, WikiOffer.class);
		ResponseEntity<WikiOffer> response1 = restTemplate.getForEntity("http://localhost:" + port + "/wiki/v1/offers/" + offer2.getId(), WikiOffer.class, "");
		//offer should not be available while accessing
		Assert.assertEquals(response1.getStatusCode(),HttpStatus.NOT_FOUND);
	}

	@Test
	public void testCreateOfferAndCancel() throws ParseException{
		//create offer with validdate and daysofValidity
		WikiOffer offer1 = new WikiOffer(101, "Summer_Offer","offer_100_desc",1300.00,"GBP",DATE_FORMAT.parse("10/07/2018"),3,"ACTIVE");
		restTemplate.postForEntity("http://localhost:" + port +"/wiki/v1/offers", offer1, WikiOffer.class);
		ResponseEntity<WikiOffer> response = restTemplate.getForEntity("http://localhost:" + port + "/wiki/v1/offers/" + offer1.getId(), WikiOffer.class, "");
		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assert.assertEquals("ACTIVE",response.getBody().getOfferStatus());
		restTemplate.put("http://localhost:" + port +"/wiki/v1/offers/cancel/"+ response.getBody().getId(), offer1, WikiOffer.class);
		//restTemplate.pu.postForEntity("http://localhost:" + port +"/wiki/v1/offers/cancel/"+ response.getBody().getId(), offer1, WikiOffer.class);
		ResponseEntity<WikiOffer> canResponse = restTemplate.getForEntity("http://localhost:" + port + "/wiki/v1/offers/" + response.getBody().getId(), WikiOffer.class, "");
		Assert.assertEquals("CANCELLED",canResponse.getBody().getOfferStatus());

	}

	@Test
	public void testCreateOfferAndValidateExpired() throws ParseException{
		//create offer
		WikiOffer offer1 = new WikiOffer(102, "Summer_Offer","offer_100_desc",1300.00,"GBP",DATE_FORMAT.parse("08/07/2018"),2,"ACTIVE");
		restTemplate.postForEntity("http://localhost:" + port +"/wiki/v1/offers", offer1, WikiOffer.class);
		ResponseEntity<WikiOffer> response = restTemplate.getForEntity("http://localhost:" + port + "/wiki/v1/offers/" + offer1.getId(), WikiOffer.class, "");
		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assert.assertEquals("EXPIRED",response.getBody().getOfferStatus());

	}

	private long getResourceIdFromUrl(String locationUrl) {
		String[] parts = locationUrl.split("/");
		return Long.valueOf(parts[parts.length - 1]);
	}

	private byte[] toJson(Object r) throws Exception {
		ObjectMapper map = new ObjectMapper();
		return map.writeValueAsString(r).getBytes();
	}





}
