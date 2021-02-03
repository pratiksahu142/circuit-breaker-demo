package io.javabrains.moviecatalogservice.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.javabrains.moviecatalogservice.models.CatalogItem;
import io.javabrains.moviecatalogservice.models.Movie;
import io.javabrains.moviecatalogservice.models.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class MovieInfo {

    private static final String INFO_SERVICE = "movieInfoService";

    @Autowired
    private RestTemplate restTemplate;



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



}
