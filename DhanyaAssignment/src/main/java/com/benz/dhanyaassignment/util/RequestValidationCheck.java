package com.benz.dhanyaassignment.util;

public class RequestValidationCheck {

	private RequestValidationCheck() {
		super();

	}

	public static boolean validationCheck(String cityName) {
		
		 return ((!cityName.equals("")) 
	            && (cityName != null) 
	            && (cityName.matches("^[a-zA-Z]*$"))); 
	}
}
