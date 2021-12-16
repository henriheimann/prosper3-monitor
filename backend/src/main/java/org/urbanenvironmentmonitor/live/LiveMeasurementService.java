package org.urbanenvironmentmonitor.live;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Sinks;

@Service
public class LiveMeasurementService
{
	private final Sinks.Many<String> measurements = Sinks.many().multicast().onBackpressureBuffer();

	public void publishMeasurement(String measurement)
	{
		measurements.tryEmitNext(measurement).orThrow();
	}

	public Flux<String> getMeasurements()
	{
		return measurements.asFlux()
				.filter(measurement -> measurement.startsWith(""));
	}
}
