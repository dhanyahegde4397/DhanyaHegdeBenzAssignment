package com.benz.dhanyaassignment.models.chargingstations;

import lombok.Data;

	
	@Data
	public class Items {

		private String title;
		private double[] position;
		private int distance;
		private int averageRating;
		private OpeningHours openingHours;

		

		@Data
		public static class OpeningHours {
			private String text;
			private String label;
			private boolean isOpen;
		}

	}

	
	




