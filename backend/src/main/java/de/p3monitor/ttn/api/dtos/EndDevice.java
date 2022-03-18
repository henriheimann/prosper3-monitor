package de.p3monitor.ttn.api.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EndDevice
{
	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Identifiers
	{
		private String deviceId;

		@JsonProperty(value = "dev_eui")
		private String deviceEui;
	}

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Session
	{
		@Getter
		@Setter
		@ToString
		@NoArgsConstructor
		@AllArgsConstructor
		@JsonIgnoreProperties(ignoreUnknown = true)
		public static class Keys
		{
			@Getter
			@Setter
			@ToString
			@NoArgsConstructor
			@AllArgsConstructor
			public static class Key
			{
				private String key;
			}

			@JsonProperty(value = "app_s_key")
			private Key appSessionKey;

			@JsonProperty(value = "f_nwk_s_int_key")
			private Key networkSessionKey;
		}

		private Keys keys;

		@JsonProperty(value = "dev_addr")
		private String deviceAddress;
	}

	@Getter
	@Setter
	@ToString
	public static class MacSettings
	{
		private int rx2DataRateIndex;
		private int rx2Frequency;
		private int rx1Delay;
		private int rx1DataRateOffset;
		private boolean resetsFCnt;
		private boolean useAdr;
	}

	private Identifiers ids;
	private String networkServerAddress;
	private String applicationServerAddress;
	private Session session;
	private String frequencyPlanId;
	private String lorawanPhyVersion;
	private Boolean multicast;
	private Boolean supportsJoin;
	private String lorawanVersion;
	private Boolean supportsClassC;
	private Boolean supportsClassB;
	private MacSettings macSettings;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
