package it.eng.ms.restservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ForbiddenException extends EngServiceException {

	public HttpStatus getStatus() {
		return HttpStatus.FORBIDDEN;
	}

	public ForbiddenException(String message){
    	super(message);
    }
}
