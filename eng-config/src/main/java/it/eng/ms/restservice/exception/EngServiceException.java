package it.eng.ms.restservice.exception;

import org.springframework.http.HttpStatus;

public abstract class EngServiceException  extends RuntimeException {
	public abstract HttpStatus getStatus();
	public EngServiceException(String message){
    	super(message);
    }
}
