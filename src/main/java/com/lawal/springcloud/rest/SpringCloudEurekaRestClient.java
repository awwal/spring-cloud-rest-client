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
import com.lawal.springcloud.eureka.model.xml.Applications.Application.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 *  * A Java wrapper for the spring-cloud-config server that is registered to an eureka instance.
 *  Combines the <tt>EurekaRestClient</tt> and <tt>SpringCloudRestClient</tt> to provide the target the
 *  configuration file has a Java <tt>Properties</tt> object
 *
 *
 * @author  Lawal Olufowobi
 */
public class SpringCloudEurekaRestClient {



    private static Logger LOG = LoggerFactory.getLogger(SpringCloudEurekaRestClient.class);
    private final String eurekaUrl;
    private final String configserverId;


    public SpringCloudEurekaRestClient(String eurekaUrl, String configserverId) {
        this.eurekaUrl = eurekaUrl;
        this.configserverId = configserverId;
    }



    /**
     * @param instances List of potential instance of the configuration server.
     * @param application Name of the application
     * @param profile E.g dev, prod
     * @param gitLabel The branch name or commitId
     * @return a <tt>Properties</tt> or null
     */

    private Properties getApplicationProperties(Collection<Instance> instances, String application, String profile, String gitLabel) {



        for (Instance instance : instances) {

            String ipAddr = instance.getIpAddr();
            int port;
            String protocol;
            if (instance.getSecurePort().isEnabled()) {
                port = instance.getSecurePort().getValue();
                protocol = "https://";

            } else {

                port = instance.getPort().getValue();
                protocol = "http://";
            }

            Properties prop = getPropertiesFromInstance(protocol + ipAddr + ":" + port, application, profile, gitLabel);
            if (prop != null) {
                return prop;
            }


            LOG.debug("Not found in instance "+instance.getHomePageUrl());
        }
        return null;
    }

    private Properties getPropertiesFromInstance(String instanceUrl, String application, String profile, String gitLabel) {


        SpringCloudRestClient cloudRestClient = new SpringCloudRestClient(instanceUrl);
        return cloudRestClient.getProperties(application, profile, gitLabel);


    }


    /**
     * @param application Name of the application (i.e the target properties file)
     * @param profile E.g dev, prod
     * @param gitLabel The branch name or commitId
     * @return a <tt>Properties</tt> or null
     */
    public Properties getApplicationProperties(String application, String profile, String gitLabel) {

        EurekaRestClient eurekaClient =  new EurekaRestClient(eurekaUrl, configserverId);

        Applications applications = eurekaClient.getApplication();

        List<Application> appList = applications.getApplication();
        if (appList.isEmpty()) return null;

        Application app = appList.iterator().next();
        List<Instance> appInstanceList = app.getInstance();
        return getApplicationProperties(appInstanceList, application, profile, gitLabel);


    }

}
