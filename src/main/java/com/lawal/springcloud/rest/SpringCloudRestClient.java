package com.lawal.springcloud.rest;

import com.lawal.springcloud.eureka.model.xml.Applications.Application.Instance;

import java.util.Collection;
import java.util.Properties;

/**
 * Created by Lawal on 06/06/2015.
 */
public class SpringCloudRestClient {


    public Properties getConfigurationProperties(Collection<Instance> instances, String propertyFile, String envCtx, String git){

        for (Instance instance : instances) {

            String ipAddr = instance.getIpAddr();
            int port = instance.getPort().getValue();

            Properties prop = getProperties(ipAddr+":"+port, propertyFile, envCtx, git);
            if(prop!=null){
                return prop;
            }


        }
        return null;
    }

    private Properties getProperties(String springcloud, String propertyFile, String envCtx, String git) {


    }
}
