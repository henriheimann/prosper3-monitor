package org.urbanenvironmentmonitor.ttn.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.urbanenvironmentmonitor.ttn.api.dtos.*;
import reactor.core.publisher.Mono;

@Headers({ "Accept: application/json" })
public interface TtnApiApplicationClient
{
	@RequestLine("GET /api/v3/applications/{applicationId}?field_mask=dev_eui_counter")
	Mono<ApplicationResponse> getApplication(@Param("applicationId") String applicationId);

	@RequestLine("POST /api/v3/applications/{applicationId}/dev-eui")
	Mono<ApplicationDeviceEuiResponse> generateDeviceEui(@Param("applicationId") String applicationId);
}
