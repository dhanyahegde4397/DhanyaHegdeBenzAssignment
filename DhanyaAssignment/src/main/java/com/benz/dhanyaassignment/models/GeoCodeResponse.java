package com.benz.dhanyaassignment.models;

import java.util.List;

import lombok.Data;

@Data
public class GeoCodeResponse {

	List<Items> items;

	@Data
	public static class Items {

		private String title;
		private String id;
		private Position position;

		@Data
		public static class Position {
			private double lat;
			private double lng;

		}

	}

}
