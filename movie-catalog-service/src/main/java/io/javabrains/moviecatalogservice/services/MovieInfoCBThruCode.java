package io.javabrains.moviecatalogservice.services;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.javabrains.moviecatalogservice.models.CatalogItem;
import io.javabrains.moviecatalogservice.models.Movie;
import io.javabrains.moviecatalogservice.models.Rating;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Service
public class MovieInfoCBThruCode {
//    FUNCTIONAL APPROACH

    @Autowired
    private RestTemplate restTemplate;

    private final Bulkhead bulkhead;

    private final CircuitBreaker circuitBreaker;

    public MovieInfoCBThruCode() {
        bulkhead = createBulkhead();
        circuitBreaker = createCircuitBreaker();
    }

    private CircuitBreaker createCircuitBreaker() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .slowCallRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(10000))
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                .permittedNumberOfCallsInHalfOpenState(4)
                .minimumNumberOfCalls(10)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(7)
                .build();
        CircuitBreakerRegistry circuitBreakerRegistry =
                CircuitBreakerRegistry.of(circuitBreakerConfig);
        CircuitBreaker circuitBreakerWithCustomConfig = circuitBreakerRegistry
                .circuitBreaker("customCircuitBreaker", circuitBreakerConfig);

        return circuitBreakerWithCustomConfig;
    }

    public CatalogItem getCatalogItem(Movie movie, Rating rating) {
        return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating());
    }

    public CatalogItem circuitBreaker(Rating rating){

        CheckedFunction0<CatalogItem> infoServiceCall = CircuitBreaker.decorateCheckedSupplier(circuitBreaker,
                () -> getCatalogItem(restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class), rating));

        Try<CatalogItem> result = Try.of(infoServiceCall)
                .recover((throwable -> getFallbackCatalogItem(rating, throwable)));

        return result.get();
    }

    public CatalogItem getFallbackCatalogItem(Rating rating, Throwable t) {
        System.out.println("*******Catalog Item Fallback Activated*******");
        return new CatalogItem("No Movie", "", rating.getRating());
    }

    private Bulkhead createBulkhead(){
        BulkheadConfig bulkheadConfig = BulkheadConfig.custom()
                .maxConcurrentCalls(10)
                .maxWaitDuration(Duration.ofMillis(500))
                .build();
        Bulkhead bulkhead = Bulkhead.of("custom-bulkhead", bulkheadConfig);
        bulkhead.getEventPublisher()
                .onCallPermitted(event -> System.out.println("Call Permitted"))
                .onCallRejected(event -> System.out.println("Call Rejected"));
        return bulkhead;
    }

    public String bulkhead(){
        CheckedFunction0<String> infoServiceCall = Bulkhead.decorateCheckedSupplier(bulkhead,
                () -> "The movie info is" + restTemplate.getForObject("http://movie-info-service/movies/110", String.class));

        Try<String> result = Try.of(infoServiceCall)
                .recover((throwable -> "This is bulkhead fallback"));

        return result.get();
    }


}
