package com.benz.dhanyaassignment.models;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ErrorMapping {

	String userMessage;
	String systemMessage;
	HttpStatus code;
}
