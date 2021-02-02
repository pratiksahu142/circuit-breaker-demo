package io.javabrains.moviecatalogservice.services;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
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
public class MovieInfo {

    private static final String INFO_SERVICE = "movieInfoService";

    @Autowired
    RestTemplate restTemplate;



    @RateLimiter(name = INFO_SERVICE)
    @Retry(name = INFO_SERVICE, fallbackMethod = "getFallbackCatalogItem")
    @CircuitBreaker(name=INFO_SERVICE , fallbackMethod = "getFallbackCatalogItem")
    public CatalogItem getCatalogItem(Rating rating) {
        Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
        return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating());
    }

    @CircuitBreaker(name=INFO_SERVICE , fallbackMethod = "getFallbackCatalogItem")
    public CatalogItem getSlowCatalogItem(Rating rating) {
        Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId() + "/slow", Movie.class);
        return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating());
    }

    public CatalogItem getFallbackCatalogItem(Rating rating, Throwable t) {
        System.out.println("*******Catalog Item Fallback Activated*******");
        return new CatalogItem("No Movie", "", rating.getRating());
    }








//    FUNCTIONAL APPROACH
//
//    private final Bulkhead bulkhead;
//
//    public MovieInfo() {
//        bulkhead = createBulkhead();
//    }
//
//    private Bulkhead createBulkhead(){
//        BulkheadConfig bulkheadConfig = BulkheadConfig.custom()
//                .maxConcurrentCalls(10)
//                .maxWaitDuration(Duration.ofMillis(500))
//                .build();
//        Bulkhead bulkhead = Bulkhead.of("custom-bulkhead", bulkheadConfig);
//        bulkhead.getEventPublisher()
//                .onCallPermitted(event -> System.out.println("Call Permitted"))
//                .onCallRejected(event -> System.out.println("Call Rejected"));
//        return bulkhead;
//    }
//
//    public String bulkhead(){
//        CheckedFunction0<String> infoServiceCall = Bulkhead.decorateCheckedSupplier(bulkhead,
//                () -> "The movie info is" + restTemplate.getForObject("http://movie-info-service/movies/110", String.class));
//
//        Try<String> result = Try.of(infoServiceCall)
//                .recover((throwable -> "This is bulkhead fallback"));
//
//        return result.get();
//    }
}
