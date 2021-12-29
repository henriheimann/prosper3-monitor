package org.urbanenvironmentmonitor.ttn.api;

import feign.Headers;
import feign.RequestLine;
import org.urbanenvironmentmonitor.ttn.api.dtos.DeviceAddressResponse;
import reactor.core.publisher.Mono;

@Headers({ "Accept: application/json" })
public interface TtnApiNetworkServerClient
{
	@RequestLine("GET /api/v3/ns/dev_addr")
	Mono<DeviceAddressResponse> generateDeviceAddress();
}
