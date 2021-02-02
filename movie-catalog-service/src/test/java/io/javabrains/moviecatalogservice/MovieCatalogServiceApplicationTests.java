package io.javabrains.moviecatalogservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.apache.http.HttpHeaders.USER_AGENT;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MovieCatalogServiceApplicationTests {

    private static final String GET_URL = "http://localhost:8081/catalog/foo";

    @Test
	public void contextLoads() throws IOException {
        testCircuitBreaker(10,GET_URL);
        //testCircuitBreaker(10,GET_URL+"/slow");
	}

	private static void testCircuitBreaker(int times, String url) throws IOException {
        for(int i=0;i<5;i++)
            System.out.println();
        for(int i=0;i<times;i++)
            sendGET(url);
        for(int i=0;i<5;i++)
            System.out.println();
    }

    private static void sendGET(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("GET request not worked");
        }

    }

}

