package org.magnum.dataup;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = org.springframework.http.HttpStatus.NOT_FOUND)
public final class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
}