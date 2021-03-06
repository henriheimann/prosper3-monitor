package de.p3monitor.testhelper.data;

import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@UtilityClass
public class TestWebClientResponseException
{
	public static WebClientResponseException createWithStatus(int status)
	{
		return new WebClientResponseException(status, "", null, null, null);
	}
}
