package com.benz.dhanyaassignment.orchestrator;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.benz.dhanyaassignment.constants.OrchestratorConstants;
import com.benz.dhanyaassignment.facade.ChargingStationFacade;
import com.benz.dhanyaassignment.facade.ParkingSpotFacade;
import com.benz.dhanyaassignment.facade.RestaurantFacade;
import com.benz.dhanyaassignment.models.ErrorMapping;
import com.benz.dhanyaassignment.models.GeoCodeResponse;
import com.benz.dhanyaassignment.models.GeoCodeResponse.Items.Position;
import com.benz.dhanyaassignment.models.chargingstations.ChargingStationResponse;
import com.benz.dhanyaassignment.models.parkingspots.ParkingSpotResponse;
import com.benz.dhanyaassignment.models.restaurants.RestaurantsResponse;
import com.benz.dhanyaassignment.util.LocationConverter;
import com.benz.dhanyaassignment.util.SortByDistanceChargingStations;
import com.benz.dhanyaassignment.util.SortByDistanceParkingSpots;
import com.benz.dhanyaassignment.util.SortByDistanceRestaurants;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

@Service
@Component
public class BenzOrchestrator {

	@Autowired
	LocationConverter locationConverter;

	@Autowired
	RestaurantFacade restaurantFacade;

	@Autowired
	ParkingSpotFacade parkingSpotFacade;

	@Autowired
	ChargingStationFacade chargingStationFacade;

	@Autowired
	SortByDistanceRestaurants sortByDistance;

	@Cacheable("GeoCodeResponse")
	public Position findCoordinates(String cityName) {
		ResponseEntity<String> locationResponse = locationConverter.fetchGeoCodeData(cityName);

		Gson gson = Converters.registerOffsetDateTime(new GsonBuilder()).create();

		if (locationResponse.getStatusCodeValue() == 200) {
			GeoCodeResponse getGeoCodeResponse = gson.fromJson(locationResponse.getBody(), GeoCodeResponse.class);

			return getGeoCodeResponse.getItems().get(0).getPosition();

		}

		return null;

	}

	@SuppressWarnings("rawtypes")
	@Cacheable("RestaurantsResponse")
	public ResponseEntity restaurantsResponse(String location) {

		Position pos = findCoordinates(location);
		if (pos == null) {
			ErrorMapping errorGeo = new ErrorMapping();
			errorGeo.setCode(HttpStatus.BAD_REQUEST);
			errorGeo.setUserMessage(OrchestratorConstants.INVALID_LOCATION);
			errorGeo.setSystemMessage(OrchestratorConstants.GEO_COORDINATES_EXCEPTION);

			return new ResponseEntity<>(errorGeo, HttpStatus.BAD_REQUEST);
		}

		CompletableFuture<ResponseEntity<String>> restaurantsResponse = restaurantFacade.getRestaurants(pos);
		Gson gson = Converters.registerOffsetDateTime(new GsonBuilder()).create();

		try {
			if (restaurantsResponse.get().getStatusCodeValue() == 200) {
				RestaurantsResponse getRestaurantResponse = gson.fromJson(restaurantsResponse.get().getBody(),
						RestaurantsResponse.class);
				if (!getRestaurantResponse.getResults().getItems().isEmpty()) {
					return new ResponseEntity<>(SortByDistanceRestaurants
							.sortItemsandReturnResult(getRestaurantResponse.getResults().getItems()), HttpStatus.OK);

				} else {
					ErrorMapping error = new ErrorMapping();
					error.setCode(HttpStatus.NOT_FOUND);
					error.setUserMessage("No Restaurants nearby!");
					error.setSystemMessage(restaurantsResponse.get().getStatusCodeValue() + ":"
							+ OrchestratorConstants.RESTAURANT_EXCEPTION);
					return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);

				}
			} else {
				ErrorMapping error = new ErrorMapping();
				error.setCode(HttpStatus.BAD_REQUEST);
				error.setUserMessage(OrchestratorConstants.INVALID_RESPONSE_FROM_PARTNER);
				error.setSystemMessage(restaurantsResponse.get().getStatusCodeValue() + ":"
						+ OrchestratorConstants.RESTAURANT_EXCEPTION);

				return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
			}
		} catch (JsonSyntaxException | InterruptedException | ExecutionException e) {

			e.printStackTrace();
		}

		return null;

	}

	@SuppressWarnings("rawtypes")
	@Cacheable("ParkingSpotResponse")
	public ResponseEntity parkingSpotsResponse(String location) {
		Position pos = findCoordinates(location);
		if (pos == null) {
			ErrorMapping errorGeo = new ErrorMapping();
			errorGeo.setCode(HttpStatus.BAD_REQUEST);
			errorGeo.setUserMessage(OrchestratorConstants.INVALID_LOCATION);
			errorGeo.setSystemMessage(OrchestratorConstants.GEO_COORDINATES_EXCEPTION);

			return new ResponseEntity<>(errorGeo, HttpStatus.BAD_REQUEST);
		}
		CompletableFuture<ResponseEntity<String>> parkingSpotResponse = parkingSpotFacade.getParkingSpots(pos);
		Gson gson = Converters.registerOffsetDateTime(new GsonBuilder()).create();

		try {
			if (parkingSpotResponse.get().getStatusCodeValue() == 200) {
				ParkingSpotResponse getParkingSpotResponse = gson.fromJson(parkingSpotResponse.get().getBody(),
						ParkingSpotResponse.class);
				if (!getParkingSpotResponse.getResults().getItems().isEmpty()) {
					return new ResponseEntity<>(SortByDistanceParkingSpots
							.sortItemsandReturnResult(getParkingSpotResponse.getResults().getItems()), HttpStatus.OK);
				} else {
					ErrorMapping error = new ErrorMapping();
					error.setCode(HttpStatus.NOT_FOUND);
					error.setUserMessage("No ParkingSpots nearby!");
					error.setSystemMessage(parkingSpotResponse.get().getStatusCodeValue() + ":"
							+ OrchestratorConstants.PARKING_SPOTS_EXCEPTION);

					return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
				}
			} else {
				ErrorMapping error = new ErrorMapping();
				error.setCode(HttpStatus.BAD_REQUEST);
				error.setUserMessage(OrchestratorConstants.INVALID_RESPONSE_FROM_PARTNER);
				error.setSystemMessage(parkingSpotResponse.get().getStatusCodeValue() + ":"
						+ OrchestratorConstants.PARKING_SPOTS_EXCEPTION);

				return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
			}
		} catch (JsonSyntaxException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		return null;

	}

	@SuppressWarnings("rawtypes")
	@Cacheable("ChargingStationResponse")
	public ResponseEntity chargingStationResponse(String location) {
		Position pos = findCoordinates(location);
		if (pos == null) {
			ErrorMapping errorGeo = new ErrorMapping();
			errorGeo.setCode(HttpStatus.BAD_REQUEST);
			errorGeo.setUserMessage(OrchestratorConstants.INVALID_LOCATION);
			errorGeo.setSystemMessage(OrchestratorConstants.GEO_COORDINATES_EXCEPTION);
			return new ResponseEntity<>(errorGeo, HttpStatus.BAD_REQUEST);

		}
		CompletableFuture<ResponseEntity<String>> chargingStationResponse = chargingStationFacade
				.getChargingStations(pos);
		Gson gson = Converters.registerOffsetDateTime(new GsonBuilder()).create();

		try {
			if (chargingStationResponse.get().getStatusCodeValue() == 200) {
				ChargingStationResponse getchargingStationResponse = gson
						.fromJson(chargingStationResponse.get().getBody(), ChargingStationResponse.class);

				if (!getchargingStationResponse.getResults().getItems().isEmpty()) {
					return new ResponseEntity<>(SortByDistanceChargingStations.sortItemsandReturnResult(
							getchargingStationResponse.getResults().getItems()), HttpStatus.OK);
				} else {
					ErrorMapping error = new ErrorMapping();
					error.setCode(HttpStatus.NOT_FOUND);
					error.setUserMessage("No ChargingStations nearby!");
					error.setSystemMessage(chargingStationResponse.get().getStatusCodeValue() + ":"
							+ OrchestratorConstants.CHARGING_STATION_EXCEPTION);

					return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
				}
			} else {
				ErrorMapping error = new ErrorMapping();
				error.setCode(HttpStatus.BAD_REQUEST);
				error.setUserMessage(OrchestratorConstants.INVALID_RESPONSE_FROM_PARTNER);
				error.setSystemMessage(chargingStationResponse.get().getStatusCodeValue() + ":"
						+ OrchestratorConstants.CHARGING_STATION_EXCEPTION);
				return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

			}
		} catch (JsonSyntaxException | InterruptedException | ExecutionException e) {

			e.printStackTrace();
		}

		return null;

	}

}
