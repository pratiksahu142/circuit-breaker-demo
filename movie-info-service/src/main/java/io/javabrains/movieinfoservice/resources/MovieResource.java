package io.javabrains.movieinfoservice.resources;

import io.javabrains.movieinfoservice.models.Movie;
import io.javabrains.movieinfoservice.models.MovieSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/movies")
public class MovieResource {

    @Value("${api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/{movieId}")
    public Movie getMovieInfo(@PathVariable("movieId") String movieId) {
        //https://api.themoviedb.org/3/movie/550?api_key=72efa00cadaf316a942a91b9b5dca5fe
        //https://api.themoviedb.org/3/movie/0?api_key=72efa00cadaf316a942a91b9b5dca5fe
        System.out.println("*****MovieID : "+ movieId + "*******");
        String MovieAPIUrl = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" +  apiKey;
        System.out.println(MovieAPIUrl);
        MovieSummary movieSummary = restTemplate.getForObject(MovieAPIUrl, MovieSummary.class);
        return new Movie(movieId, movieSummary.getTitle(), movieSummary.getOverview());

    }

    @RequestMapping("/{movieId}/slow")
    public Movie getMovieInfoSlow(@PathVariable("movieId") String movieId) {
        //https://api.themoviedb.org/3/movie/550?api_key=72efa00cadaf316a942a91b9b5dca5fe
        //https://api.themoviedb.org/3/movie/0?api_key=72efa00cadaf316a942a91b9b5dca5fe
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("***** Slow MovieID : "+ movieId + "*******");
        String MovieAPIUrl = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" +  apiKey;
        System.out.println(MovieAPIUrl);
        MovieSummary movieSummary = restTemplate.getForObject(MovieAPIUrl, MovieSummary.class);
        return new Movie(movieId, movieSummary.getTitle(), movieSummary.getOverview());

    }


}
