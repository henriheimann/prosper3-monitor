package de.p3monitor.device;

import de.p3monitor.device.dtos.MeasurementsRequest;
import de.p3monitor.device.dtos.MeasurementsResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

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
}
