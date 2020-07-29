package com.benz.dhanyaassignment.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Component
public class LocationConverter {

	@Autowired
	RestTemplate restTemplate;

	@Value("${geocode.uri}")
	String uri;

	@Value("${apiKey}")
	String apiKey;

	public ResponseEntity<String> fetchGeoCodeData(String location) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<String> response = null;

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.set("q", location);
		params.set("apiKey", apiKey);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri).queryParams(params);

		HttpEntity<String> httpEntity = new HttpEntity<>(headers);

		response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, httpEntity, String.class);
		return response;
	}

}
