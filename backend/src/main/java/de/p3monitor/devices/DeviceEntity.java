package de.p3monitor.devices;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode
@Table("devices")
public class DeviceEntity
{
	@Id
	private Long id;

	private String name;

	private String ttnId;

	private String ttnDeviceAddress;

	private String ttnNetworkSessionKey;

	private String ttnApplicationSessionKey;

	private double latitude;

	private double longitude;
}