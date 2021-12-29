package org.urbanenvironmentmonitor.device.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TtnSyncResponse
{
	private final TtnSyncState state;
	private final TtnDeviceResponse deviceResponse;
}
