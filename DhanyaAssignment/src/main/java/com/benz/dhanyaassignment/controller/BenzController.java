package com.benz.dhanyaassignment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.benz.dhanyaassignment.constants.OrchestratorConstants;
import com.benz.dhanyaassignment.models.ErrorMapping;
import com.benz.dhanyaassignment.orchestrator.BenzOrchestrator;
import com.benz.dhanyaassignment.util.RequestValidationCheck;

@RequestMapping("/")
@RestController
public class BenzController {

	@Autowired
	BenzOrchestrator orchestrator;

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/location", produces = "application/json")
	public ResponseEntity searchPlaces(@RequestBody String cityName) {

		if (RequestValidationCheck.validationCheck(cityName)) {

			ResponseEntity restaurantresponse = orchestrator.restaurantsResponse(cityName);
			ResponseEntity parkingSpotresponse = orchestrator.parkingSpotsResponse(cityName);
			ResponseEntity chargingStationresponse = orchestrator.chargingStationResponse(cityName);

			return new ResponseEntity<>(
					"\nRestaurants :\n" + restaurantresponse + "\n\n Parking Spots :\n\n" + parkingSpotresponse
							+ "\n Charging Stations :\n" + chargingStationresponse,
					(restaurantresponse.getStatusCodeValue() != 200 || parkingSpotresponse.getStatusCodeValue() != 200
							|| chargingStationresponse.getStatusCodeValue() != 200) ? HttpStatus.MULTI_STATUS
									: HttpStatus.OK);

		} else {

			ErrorMapping error = new ErrorMapping();
			error.setCode(HttpStatus.BAD_REQUEST);
			error.setUserMessage("Invalid Request");
			error.setSystemMessage(OrchestratorConstants.INVALID_LOCATION);
			return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		}

	}
}
