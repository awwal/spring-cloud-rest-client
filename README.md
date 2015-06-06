# SpringCloudRestClient
A Java wrapper for the spring cloud config rest api. Discovery through the spring-eureka also available. 
This library does not have any spring dependencies. Hence it can be used in non-spring environment like JavaEE.
The configuration files are returned as Java Properties object.


Example usage. 

 ```
SpringCloudEurekaRestClient cloudEurekaClient = new SpringCloudEurekaRestClient("http://localhost:8761","CONFIGSERVER");
Properties props = cloudEurekaClient.getApplicationProperties("foo", "dev", "Master");
 ```
