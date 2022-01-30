package de.p3monitor.device.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TtnDeviceResponse
{
	private final LocalDateTime createdAt;
}
