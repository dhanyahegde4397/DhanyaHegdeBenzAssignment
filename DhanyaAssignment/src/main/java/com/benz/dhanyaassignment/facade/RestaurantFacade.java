package com.benz.dhanyaassignment.facade;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;

import com.benz.dhanyaassignment.models.GeoCodeResponse.Items.Position;


public interface RestaurantFacade {

	public CompletableFuture<ResponseEntity<String>> getRestaurants(Position coordinates);

}
