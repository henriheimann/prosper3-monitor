package org.urbanenvironmentmonitor.ttn.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.reactivestreams.Publisher;
import org.urbanenvironmentmonitor.ttn.api.dtos.CreateEndDeviceRequest;
import org.urbanenvironmentmonitor.ttn.api.dtos.EndDevice;
import org.urbanenvironmentmonitor.ttn.api.dtos.EndDevicesResponse;
import reactor.core.publisher.Mono;

@Headers({ "Accept: application/json" })
public interface TtnApiEndDeviceClient
{
	@Headers("Content-Type: application/json")
	@RequestLine("POST /api/v3/applications/{applicationId}/devices")
	Mono<EndDevice> createEndDevice(@Param("applicationId") String applicationId,
	                                CreateEndDeviceRequest createEndDeviceRequest);

	@Headers("Content-Type: application/json")
	@RequestLine("POST /api/v3/ns/applications/{applicationId}/devices")
	Mono<EndDevice> createEndDeviceOnNetworkServer(@Param("applicationId") String applicationId,
	                                               CreateEndDeviceRequest createEndDeviceRequest);

	@Headers("Content-Type: application/json")
	@RequestLine("POST /api/v3/as/applications/{applicationId}/devices")
	Mono<EndDevice> creatEndDeviceOnApplicationServer(@Param("applicationId") String applicationId,
	                                                  CreateEndDeviceRequest createEndDeviceRequest);

	@RequestLine("GET /api/v3/applications/{applicationId}/devices/{endDeviceId}")
	Mono<EndDevice> getEndDevice(@Param("applicationId") String applicationId,
	                             @Param("endDeviceId") String endDeviceId);

	@RequestLine("GET /api/v3/ns/applications/{applicationId}/devices/{endDeviceId}")
	Mono<EndDevice> getEndDeviceFromNetworkServer(@Param("applicationId") String applicationId,
	                                              @Param("endDeviceId") String endDeviceId);

	@RequestLine("GET /api/v3/as/applications/{applicationId}/devices/{endDeviceId}")
	Mono<EndDevice> getEndDeviceFromApplicationServer(@Param("applicationId") String applicationId,
	                                                  @Param("endDeviceId") String endDeviceId);

	@RequestLine("GET /api/v3/applications/{applicationId}/devices?field_mask=name")
	Mono<EndDevicesResponse> getEndDevicesList(@Param("applicationId") String applicationId);
}
