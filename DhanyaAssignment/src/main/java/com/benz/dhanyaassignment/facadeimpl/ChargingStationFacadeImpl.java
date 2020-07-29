package com.benz.dhanyaassignment.facadeimpl;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.benz.dhanyaassignment.facade.ChargingStationFacade;
import com.benz.dhanyaassignment.models.GeoCodeResponse.Items.Position;

@Component
@Service
public class ChargingStationFacadeImpl implements ChargingStationFacade {
	
	@Autowired
	RestTemplate restTemplate;

	@Value("${explorePlaces.uri}")
	String uri;

	@Value("${apiKey}")
	String apiKey;

	@Async
	@Override
	public CompletableFuture<ResponseEntity<String>> getChargingStations(Position coordinates) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.set("at", coordinates.getLat() + "," + coordinates.getLng());
		params.set("cat", "ev-charging-station");
		params.set("apiKey", apiKey);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri).queryParams(params);

		HttpEntity<String> httpEntity = new HttpEntity<>(headers);

		return CompletableFuture.completedFuture(restTemplate.exchange(builder.toUriString(), HttpMethod.GET, httpEntity, String.class));

	}

	

}
