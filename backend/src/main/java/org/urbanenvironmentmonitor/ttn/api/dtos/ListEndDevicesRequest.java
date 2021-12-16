package org.urbanenvironmentmonitor.ttn.api.dtos;

import java.util.List;

public class ListEndDevicesRequest
{
	private List<String> applicationIds;
	private List<String> fieldMask;
	private String order;
	private int page;
	private int limit;
}
