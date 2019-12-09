package it.eng.ms.restservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends EngServiceException {

	public HttpStatus getStatus() {
		return HttpStatus.NOT_FOUND;
	}

	public NotFoundException(String message){
    	super(message);
    }
}
