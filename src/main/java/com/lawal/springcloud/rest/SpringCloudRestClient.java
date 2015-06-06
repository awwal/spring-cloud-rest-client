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
 * Created by Lawal on 06/06/2015.
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

        if (profile == null) profile = defaultProfile;
        if (gitLabel == null) gitLabel = defaultGitLabel;


        WebTarget target = baseTarget.path(resourcePath)
                .resolveTemplate("label", gitLabel)
                .resolveTemplate("application", application)
                .resolveTemplate("profile", profile);

        LOG.info("Connecting to spring cloud config server " + target.getUri() + " for application=" + application + " profile=" + profile + " gitLabel=" + gitLabel);


        Response res = target.request().get();
        if (res.getStatusInfo().getFamily() != Response.Status.OK.getFamily()) {
            return null;
        }
        String responseString = res.readEntity(String.class);

        try {
            Properties p = new Properties();
            p.load(new StringReader(responseString));
            return p;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
