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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

/**
 * @author Lawal Olufowobi
 *
 * A Java wrapper for the spring-cloud-config server to fetch configuration files as <tt>Properties</tt> object that can
 * be used in non-spring application
 *
 */
public class SpringCloudRestClient {

	public static final String resourcePath = "/{label}/{application}-{profile}.properties";

	public static final String defaultGitLabel = "Master";

	public static final String defaultProfile = "default";

	private static Logger LOG = LoggerFactory.getLogger(SpringCloudRestClient.class);

	private WebTarget baseTarget;

	public SpringCloudRestClient(String springcloudUrl) {
		Client client = ClientBuilder.newClient();
		baseTarget = client.target(springcloudUrl);
	}

	public Properties getProperties(String application, String profile, String gitLabel) {

		if (profile == null)
			profile = defaultProfile;
		if (gitLabel == null)
			gitLabel = defaultGitLabel;

		WebTarget target = baseTarget.path(resourcePath).resolveTemplate("label", gitLabel)
				.resolveTemplate("application", application).resolveTemplate("profile", profile);

		LOG.info("Connecting to spring cloud config server " + target.getUri() + " for application=" + application
				+ " profile=" + profile + " gitLabel=" + gitLabel);

		Response res = target.request().get();
		if (res.getStatusInfo().getFamily() != Response.Status.OK.getFamily()) {
			return null;
		}
		String responseString = res.readEntity(String.class);

		try {
			Properties p = new Properties();
			p.load(new StringReader(responseString));
			return p;
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
