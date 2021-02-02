package io.javabrains.moviecatalogservice.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.javabrains.moviecatalogservice.models.Rating;
import io.javabrains.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class UserRatingInfo {

    private static final String RATING_SERVICE = "ratingService";

    @Autowired
    RestTemplate restTemplate;

    @CircuitBreaker(name=RATING_SERVICE , fallbackMethod = "getFallbackUserRating")
    public UserRating getUserRating(@PathVariable("userId") String userId) {
        return restTemplate.getForObject("http://ratings-data-service/ratingsdata/user/" + userId, UserRating.class);
    }

    public UserRating getFallbackUserRating(@PathVariable("userId") String userId, Throwable t) {
        System.out.println("*******User Rating Fallback Activated*******");
        UserRating userRating = new UserRating();
        userRating.setUserId(userId);
        userRating.setRatings(Arrays.asList(
                new Rating("100",0)
        ));
        return userRating;
    }
}

