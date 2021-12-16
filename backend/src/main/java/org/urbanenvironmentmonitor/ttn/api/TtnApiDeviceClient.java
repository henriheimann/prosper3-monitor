package org.urbanenvironmentmonitor.ttn.api;

import feign.Headers;
import org.reactivestreams.Publisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.urbanenvironmentmonitor.ttn.api.config.TtnApiClientConfig;
import org.urbanenvironmentmonitor.ttn.api.dtos.CreateEndDeviceRequest;
import org.urbanenvironmentmonitor.ttn.api.dtos.EndDevice;
import org.urbanenvironmentmonitor.ttn.api.dtos.ListEndDevicesRequest;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "ttnDevices", url = "${ttn.api.url}", configuration = TtnApiClientConfig.class)
@Headers({ "Accept: application/json" })
public interface TtnApiDeviceClient
{
	@Headers("Content-Type: application/json")
	@PostMapping("/api/v3/applications/{applicationId}/devices")
	Mono<EndDevice> createEndDevice(@PathVariable("applicationId") int applicationId,
	                                Publisher<CreateEndDeviceRequest> createEndDeviceRequest);

	@GetMapping("/api/v3/applications/{applicationId}/devices")
	Mono<EndDevice> getEndDevicesList(@PathVariable("applicationId") int applicationId,
	                                Publisher<ListEndDevicesRequest> listEndDevicesRequest);
}
