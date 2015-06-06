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
 * Created by Lawal on 06/06/2015.
 */
public class EurekaRestClient {


    private final static String ApplicationResourcePath = "eureka/apps/{appId}";

    private static final String DEFAULT_CONFIG_SERVER_ID = "CONFIGSERVER";
    private final WebTarget baseTarget;
    private String configServerId;
    private static Logger LOG = LoggerFactory.getLogger(EurekaRestClient.class);


    public EurekaRestClient(String eurekaUrl) {
        this(eurekaUrl, DEFAULT_CONFIG_SERVER_ID);
    }


    public EurekaRestClient(String eurekaUrl, String configServerId) {
        this.configServerId = configServerId;
        Client client = ClientBuilder.newClient();
        baseTarget = client.target(eurekaUrl);
    }


    public Applications getApplication() {

        LOG.info("Connecting to eureka server "+baseTarget.getUri()+ " seeking application with id "+ configServerId);

        WebTarget target = baseTarget.path(ApplicationResourcePath).resolveTemplate("appId", configServerId);
//        WebTarget target = baseTarget.path("eureka/apps/");
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



        } catch (JAXBException e) {
            e.printStackTrace();
        }


        return null;
    }


}
