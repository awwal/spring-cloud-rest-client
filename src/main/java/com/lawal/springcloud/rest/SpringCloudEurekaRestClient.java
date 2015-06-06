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
 * Created by Lawal on 06/06/2015.
 */
public class SpringCloudEurekaRestClient {


    public static final String defaultGitLabel = "Master";
    public static final String defaultProfile = "default";
    private static Logger LOG = LoggerFactory.getLogger(SpringCloudEurekaRestClient.class);
    private final String eurekaUrl;
    private final String configserverId;


    public SpringCloudEurekaRestClient(String eurekaUrl, String configserverId) {
        this.eurekaUrl = eurekaUrl;
        this.configserverId = configserverId;
    }


    private Properties getApplicationProperties(Collection<Instance> instances, String application, String profile, String gitLabel) {

        if (profile == null) profile = defaultProfile;
        if (gitLabel == null) gitLabel = defaultGitLabel;

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


        }
        return null;
    }

    private Properties getPropertiesFromInstance(String instanceUrl, String application, String profile, String gitLabel) {


        SpringCloudRestClient cloudRestClient = new SpringCloudRestClient(instanceUrl);
        return cloudRestClient.getProperties(application, profile, gitLabel);


    }

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
