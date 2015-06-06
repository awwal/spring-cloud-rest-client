import com.lawal.springcloud.eureka.model.xml.Applications;
import com.lawal.springcloud.eureka.model.xml.Applications.Application;
import com.lawal.springcloud.eureka.model.xml.Applications.Application.Instance;
import com.lawal.springcloud.rest.EurekaFinder;

import java.util.List;

/**
 * Created by Lawal on 06/06/2015.
 */
public class EurekaFinderTest {


    public static void main(String[] args) {


        EurekaFinder ef = new EurekaFinder("http://192.168.1.180:8761/","ConfigServer");


        Applications applications = ef.getApplication();

        List<Application> appList = applications.getApplication();
        for (Application app : appList) {
            System.out.println(app.getName());
            List<Instance> appInstanceList = app.getInstance();
            for (Instance instance : appInstanceList) {
                System.out.println(instance.getIpAddr()+ " "+instance.getPort().getValue());
            }
        }

    }
}
