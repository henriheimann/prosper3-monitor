package de.p3monitor.ttn.api;

import de.p3monitor.ttn.api.dtos.ApplicationDeviceEuiResponse;
import de.p3monitor.ttn.api.dtos.ApplicationResponse;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import reactor.core.publisher.Mono;

@Headers({ "Accept: application/json" })
public interface TtnApiApplicationClient
{
	@RequestLine("GET /api/v3/applications/{applicationId}?field_mask=dev_eui_counter")
	Mono<ApplicationResponse> getApplication(@Param("applicationId") String applicationId);

	@RequestLine("POST /api/v3/applications/{applicationId}/dev-eui")
	Mono<ApplicationDeviceEuiResponse> generateDeviceEui(@Param("applicationId") String applicationId);
}
