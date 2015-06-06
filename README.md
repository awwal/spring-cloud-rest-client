# SpringCloudRestClient
A Java wrapper for the spring cloud rest api. Discovery through the spring-netflix eureka also available. 
This library does not have any spring dependencies. 

Example usage. 

 ```
SpringCloudEurekaRestClient cloudEurekaClient = new SpringCloudEurekaRestClient("http://localhost:8761","CONFIGSERVER");
Properties props = cloudEurekaClient.getApplicationProperties("foo", "dev", "Master");
 ```
