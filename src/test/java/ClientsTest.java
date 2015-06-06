import com.lawal.springcloud.eureka.model.xml.Applications;
import com.lawal.springcloud.eureka.model.xml.Applications.Application;
import com.lawal.springcloud.eureka.model.xml.Applications.Application.Instance;
import com.lawal.springcloud.rest.EurekaRestClient;
import com.lawal.springcloud.rest.SpringCloudEurekaRestClient;
import com.lawal.springcloud.rest.SpringCloudRestClient;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;


/**
 * Created by Lawal on 06/06/2015.
 */
public class ClientsTest {

    private final static String eurekaUrl = "http://192.168.1.180:8761/";
    private final static String configServerId= "ConfigServer";
    private static final String cloudServerUrl = "http://192.168.1.180:8882/";
    private String applicationName = "phonebook";


    @Test
    public void testEurekaRestClient(){


        EurekaRestClient ef = new EurekaRestClient(eurekaUrl, configServerId);
        Applications applications = ef.getApplication();
        assertNotNull(applications);
        assertEquals(1, applications.getApplication().size());
        Application application = applications.getApplication().get(0);
        assertTrue(application.getInstance().size()>0);

        Instance instance = application.getInstance().get(0);
        assertNotNull(instance);

        System.out.println(instance.getHostName());
        System.out.println(instance.getHomePageUrl());



    }


    @Test
    public void testSpringCloudRestClient(){

        SpringCloudRestClient client = new SpringCloudRestClient(cloudServerUrl);

        Properties prop = client.getProperties(applicationName, null, null);
        assertNotNull(prop);
        System.out.println(prop);


    }


    @Test
    public void testSpringCloudWithEureka(){

        SpringCloudEurekaRestClient cloudEurekaClient = new SpringCloudEurekaRestClient(eurekaUrl,configServerId);
        Properties props = cloudEurekaClient.getApplicationProperties(applicationName, "dev", "Master");
        assertNotNull(props);
        assertFalse(props.isEmpty());
        System.out.println(props);
    }


}