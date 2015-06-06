/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lawal.springcloud.rest;

import com.lawal.springcloud.eureka.model.xml.Applications;
import com.lawal.springcloud.eureka.model.xml.Applications.Application;
import com.lawal.springcloud.eureka.model.xml.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;

/**
 * Connects to an spring-eureka instance and fetches the application descriptor object provided by the eureka api.
 *
 * @author Lawal Olufowobi
 */
public class EurekaRestClient {

	private final static String ApplicationResourcePath = "eureka/apps/{appId}";

	private static final String DEFAULT_CONFIG_SERVER_ID = "CONFIGSERVER";
	private static Logger LOG = LoggerFactory.getLogger(EurekaRestClient.class);
	private final WebTarget baseTarget;
	private String configServerAppId;

	public EurekaRestClient(String eurekaUrl) {
		this(eurekaUrl, DEFAULT_CONFIG_SERVER_ID);
	}

	/**
	 *
	 * @param eurekaUrl The url to the eureka server
	 * @param configServerAppId The application Id of the config server
	 */
	public EurekaRestClient(String eurekaUrl, String configServerAppId) {
		this.configServerAppId = configServerAppId;
		Client client = ClientBuilder.newClient();
		baseTarget = client.target(eurekaUrl);
	}

	public Applications getApplication() {

		LOG.info("Connecting to eureka server " + baseTarget.getUri() + " seeking application with id="
				+ configServerAppId);

		WebTarget target = baseTarget.path(ApplicationResourcePath).resolveTemplate("appId", configServerAppId);
		// WebTarget target = baseTarget.path("eureka/apps/");
		Response res = target.request().accept(MediaType.APPLICATION_XML_TYPE).get();
		if (res.getStatusInfo().getFamily() != Response.Status.OK.getFamily()) {
			return null;
		}
		String responseString = res.readEntity(String.class);

		LOG.trace(responseString);
		try {
			JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			ByteArrayInputStream bais = new ByteArrayInputStream(responseString.getBytes());

			Object unmarshalled = unmarshaller.unmarshal(bais);
			if (unmarshalled instanceof Application) {
				Application application = (Application) unmarshalled;
				Applications applications = new Applications();
				applications.getApplication().add(application);
				return applications;
			}

			if (unmarshalled instanceof Applications) {
				Applications applications = (Applications) unmarshalled;
				return applications;
			}

		}
		catch (JAXBException e) {
			e.printStackTrace();
		}

		return null;
	}

}
