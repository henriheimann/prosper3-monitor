package de.p3monitor.device.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SensorValuesResponse
{
	private final Double bat;
	private final Double mst;
	private final Double tmp;
	private final Double hum;
	private final Double irt;
	private final Double bgh;
}
