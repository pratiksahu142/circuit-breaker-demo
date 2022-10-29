# Movie Catalog Application 
## User can maintain list of movies and rate each of them, which will be called a Catalog for that user.

NOTE : Most of the response have been hardcoded in order to focus on circuit breaker POC only

Steps to deploy all services in this architecture :

1. Simply deploy discovery-server first
2. Deploy movie-info & ratings data service
3. Deploy movie-catalog service in the end

For example, making a get call to http://localhost:8081/catalog/${userId},movie-catalog service will
receive this call, fetch the list of movies that user has rated by making a call to ratings-data
service, then call movie-info service to get details of each of these movies and overall response
will contain this list of movie details along with the rating for that particular user

________________
CIRCUIT BREAKER :
----------------

In simpler terms, circuit breaker pattern came into picture to have fault tolerant & resilient
systems in the microservices world. It works in a similar way as an electrical MCB works, which is
whenever load is beyond tolerable, it stops the flow in order to have a faster recovery. In our view
of implementing microservices, we can have a circuit breaker, which if encounters a failure or
timeout from a http request, will stop taking traffic and save our application from getting further
load from requests giving it time to recover. There are other features as well that come with
Resilience4j.

Here, in our example we have implemented this circuit breaker in movie-catalog service while it
makes a call to other two services to get the responses
Demo can be simulated once all of these services are deployed, by executing
MovieCatalogServiceApplicationTests->loadContexts() Test in movie-catalog service.

Further details can be found in the presentation in same location as is README.md