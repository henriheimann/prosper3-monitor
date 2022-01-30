package de.p3monitor.ttn.api;

import de.p3monitor.ttn.api.dtos.DeviceAddressResponse;
import feign.Headers;
import feign.RequestLine;
import reactor.core.publisher.Mono;

@Headers({ "Accept: application/json" })
public interface TtnApiNetworkServerClient
{
	@RequestLine("GET /api/v3/ns/dev_addr")
	Mono<DeviceAddressResponse> generateDeviceAddress();
}
