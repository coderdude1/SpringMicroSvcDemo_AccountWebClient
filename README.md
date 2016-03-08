# Overview
This project will setup and run a spring boot client facing webapp for account services.  It will register
to a registration service, and will request an account microservice.  This is the web client piece of a 
multiproject demo.  At a minimum this will need to have the registration service project and the 
AccountService_Demo to demonstrate this usage of the microservice
 
This is an evolution of a springblog post on microservices with spring.  The original source is
[here](https://spring.io/blog/2015/07/14/microservices-with-spring) and provides a great overview of these
pieces and how they work.

I ended up splitting up the original project that had three tiers (registration server, account micro service, and
the client webservice) into three separate projects.   This allowed me to inject the spring config service.
This requires the use of the bootstrap.yml to set the properties, and each app needs to be set separately.

# Account Web Client
# Configuration
Spring boot by default will use local configs are located in /resources.  The first place is 
boostrap.[yml, properties].  This is a place to 'bootstrap' the spring context before it gets fired up.
The other is by convention (app-name).[yml, properties].  Springboot is configured to use this file
via a -D property via an environment variable, or in the springboot app file(look in WebServer.java).
The property name is 'spring.config.name', and in this app the value is 'web-server'. It can be a .yml 
or a .properties file.
 
This app is set up to use either a local config or using 2 different spring config server as a demo.  Details
follow.
 
## Local config
Make sure everything in the /resources/bootstrap.yml are commented out.  Make sure that 
everything in /resources/web-server.yml are uncommented.  An interesting note is the local 
config is configured to host this app on port 3333.  The 2 spring configs loaded via the spring config
server specify different ports to show that the app is loading it's config remotely.
 
## Spring Config Server
Make sure everything in /resources/bootstrap.yml is uncommented, and everything in /resourcs/web-server.yml
are commented out.  Note the port in use will now be spring 3334.  You will need to make sure you are running the 
SpringConfigServer_Micro project, and have a gitrepo that contains the various config files (TBD)

There are two options for config when using the spring config server.  The first demo's the default, ie no profile.
The second shows the use of a profile (called 'alt').  In the local bootstrap.yml
there is a property called profile with a value called 'alt'.  When this is uncommented, it will ask for a different
config, which has a default port of 3335.

### Spring config client options
#### Fail if we can't talk to the config server
spring.cloud.config.failFast=true  Set this to false if we want to keep trying to find it, will
noise up logs.  Default is 'false'.  I was getting errors in the netflix server, and this made that error
go away but another one pop up
 
#### Retry config if we can't talk to the config server
spring.cloud.config.failFast=true and we need to add the following to our maven deps
 
1. spring-retry
2. spring-boot-starter-aop

This will give us a default of 6 times for attempting to retry then die.  The default behaviour 
is to retry 6 times with an initial backoff interval of 1000ms and an exponential multiplier 
of 1.1 for subsequent backoffs. You can configure these properties (and others) using 
spring.cloud.config.retry.* configuration properties.

#### Specifying what config to ask for.
The Config Service serves property sources from /{name}/{profile}/{label}, where the default 
bindings in the client app are

    "name" = ${spring.application.name}
    "profile" = ${spring.profiles.active} (actually Environment.getActiveProfiles())
    "label" = "master" - looks like a branch to use

All of them can be overridden by setting spring.cloud.config.* (where * is "name", "profile" 
or "label"). The "label" is useful for rolling back to previous versions of configuration; 
with the default Config Server implementation it can be a git label, branch name or commit 
id. Label can also be provided as a comma-separated list, in which case the items in the list are tried on-by-one until one succeeds. This can be useful when working on a feature branch, for instance, when you might want to align the config label with your branch, but make it optional (e.g. spring.cloud.config.label=myfeature,develop).
 
I have an web-server-alt.yml option in the config server, with http port 3335 to show specifying
a profile.

You can specify more than one profile, via comma sepearted list

## How a service is wired into the client
The rest template that is used to access the Account microservice has some cool stuff going on.  The @EnableDiscoveryClient
will recognize the REstEmplate being autowired and injects the microservice connection instead (ie it talks to the DisvoeryServer
and gets the url (not sure if it does it fore each request, or caches it.)

[This is the url to see the webserver stuff](http://localhost:3333/) it includes links for demoing the microservice calls
and the beans that provide various metrics and such

## Spring Boot Web Default URL's
Spring boot will provide a set of urls for various metrics and status checks (typically in JSON format).  These can be disabled, and it
looks like for at least some of these can have a custom handler to provide app specific info.  go to
[url list](http://localhost:2222).  [this is a list](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html)
of all of them (there are a lot, including these

1. /beans - list all the spring beans in the context
2. /env - list the environment values
3. /health - list the health of the app.  there is a callback interface you can override to add custome behavior
4. /metrics - various JVM stats
5. /trace - Displays trace information (by default the last few HTTP requests).
6. /info - Displays arbitrary application info.  I think this can be overridden
7. /flyway - list flyway migrations (doesn't work, not sure why, probably needs a config or impl)
8. /autoconfig - Displays an auto-configuration report showing all auto-configuration candidates and the reason why they ‘were’ or ‘were not’ applied.
9. /actuator - Provides a hypermedia-based “discovery page” for the other endpoints. Requires Spring HATEOAS to be on the classpath.

 
# Interesting URLS

1.  Orignal source [Microservices with spring](https://spring.io/blog/2015/07/14/microservices-with-spring)
1.  [Ngnix article](https://www.nginx.com/blog/introduction-to-microservices/)
2.  [Configuring It All Out" or "12-Factor App-Style Configuration with Spring"](https://spring.io/blog/2015/01/13/configuring-it-all-out-or-12-factor-app-style-configuration-with-spring)
2.  [Spring Cloud Netflix](http://cloud.spring.io/spring-cloud-netflix/spring-cloud-netflix.html)
3.  [Microservices With Spring](https://spring.io/blog/2015/07/14/microservices-with-spring)
4.  [Microservice Registration and Discovery with Spring Cloud and Netflix's Eureka](https://spring.io/blog/2015/01/20/microservice-registration-and-discovery-with-spring-cloud-and-netflix-s-eureka)
5.  [Spring cloud samples git repo](https://github.com/spring-cloud-samples/)
6.  [Configuring it all git repo](https://github.com/joshlong/configuring-it-all-out/blob/master/cloud-client/pom.xml)
