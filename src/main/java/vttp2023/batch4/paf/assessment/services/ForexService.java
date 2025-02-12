package vttp2023.batch4.paf.assessment.services;

import java.io.StringReader;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class ForexService {
	// Task 5 
	public float convert(String from, String to, float amount) {
		String url = UriComponentsBuilder.fromUriString("https://api.frankfurter.dev/v1/latest")
					.queryParam("base", from.toUpperCase())
					.queryParam("symbols", to.toUpperCase())
					.toUriString();

		RequestEntity<Void> req = RequestEntity
                    .get(url)
                    .accept(MediaType.APPLICATION_JSON)     
                    .build();
                    
         
		try {
            // Make REST API call
			RestTemplate template = new RestTemplate();
			ResponseEntity<String> resp = template.exchange(req, String.class);
            
			// Extract payload
            String payload = resp.getBody();

			JsonReader reader = Json.createReader(new StringReader(payload));
        	JsonObject json = reader.readObject();
			
			double toAUDrate = json.getJsonObject("rates").getJsonNumber(to.toUpperCase()).doubleValue();

			if(toAUDrate <= 0) 
				return -1000f;

            return (float) (amount * toAUDrate);
        } catch(Exception ex) {
            //ex.printStackTrace();
			
			return -1000f;
        }
	}
}
