package org.urbanenvironmentmonitor.shared.exceptions;

public class ResourceNotFoundException extends RuntimeException
{
	public ResourceNotFoundException(String message)
	{
		super(message);
	}
}