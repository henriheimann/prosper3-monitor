package org.urbanenvironmentmonitor.ttn.api.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class FieldMask
{
	private List<String> paths;

	public FieldMask(String... paths)
	{
		this.paths = new ArrayList<String>(Arrays.asList(paths));
	}
}
