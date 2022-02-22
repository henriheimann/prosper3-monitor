package de.p3monitor.devices;

import de.p3monitor.devices.requests.AveragedMeasurementsRequest;
import de.p3monitor.devices.responses.AveragedMeasurementsResponse;
import de.p3monitor.devices.requests.MeasurementsRequest;
import de.p3monitor.devices.responses.MeasurementsResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/measurements")
@AllArgsConstructor
public class MeasurementsController
{
	private final MeasurementsService measurementsService;

	@PostMapping("")
	public Flux<MeasurementsResponse> measurements(@Valid @RequestBody MeasurementsRequest measurementsRequest)
	{
		return measurementsService.measurements(measurementsRequest);
	}

	@PostMapping("/averaged")
	public Mono<AveragedMeasurementsResponse> getClimateWidgetMeasurements(@Valid @RequestBody AveragedMeasurementsRequest averagedMeasurementsRequest)
	{
		return measurementsService.averagedMeasurements(averagedMeasurementsRequest);
	}
}
