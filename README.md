# Wikipedia

Spring Boot "WIKIPEDIA" Project

Assignment - Simple RESTful software service

How to Run

This application is packaged as a war which has Tomcat 8 embedded. No Tomcat or JBoss installation is necessary. You run it using the java -jar command.

Clone this repository
Make sure you are using JDK 1.8 and Maven 3.x
You can build the project and run the tests by running mvn clean package
Once successfully built, you can run the service by one of these two methods:

mvn spring-boot:run -Drun.arguments="spring.profiles.active=test"

Check the stdout or wikipedia_offer.log file to make sure no exceptions are thrown

About the Service

The service is just a simple hotel review REST service. It uses an in-memory database (H2) to store the data. 
You can also do with a relational database like MySQL or PostgreSQL. 
If your database connection properties work, you can call some REST endpoints defined in com.wp.offer.Wikipedia.api.rest.WikiOfferController on port 8090. (see below)

With PostMan you can run tests.

URLS to access:

GET ALL- http://localhost:8090/wiki/v1/offers/
GET with Id- http://localhost:8090/wiki/v1/offers/cancel/109
CREATE - http://localhost:8090/wiki/v1/offers
   - For create required following script to be added under 'Pre-request Script'
        var current_timestamp = new Date();
		postman.setEnvironmentVariable("current_timestamp", current_timestamp.toISOString());
   - under Body
        {
			"id": 114,
			"name": "Wiki offer 114",
			"description": "Wiki offer 114 Description",
			"currency": "GBP",
			"offerStatus": "Active",
			"offerStartDate": "10/07/2018",
			"daysValid": 1
		}
Cancel with PUT - http://localhost:8090/wiki/v1/offers/cancel/113 



