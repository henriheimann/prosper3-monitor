package org.urbanenvironmentmonitor.device.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@Table("devices")
public class Device
{
	@Id
	private Long id;

	private String name;

	private String ttnId;
}
