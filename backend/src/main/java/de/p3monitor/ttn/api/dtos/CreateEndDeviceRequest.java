package de.p3monitor.ttn.api.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateEndDeviceRequest
{
	private EndDevice endDevice;
	private FieldMask fieldMask;

	private CreateEndDeviceRequest(EndDevice endDevice, FieldMask fieldMask)
	{
		this.endDevice = endDevice;
		this.fieldMask = fieldMask;
	}

	public static CreateEndDeviceRequest registryRequest(EndDevice reference)
	{
		var endDevice = new EndDevice();
		endDevice.setIds(reference.getIds());
		endDevice.setNetworkServerAddress(reference.getNetworkServerAddress());
		endDevice.setApplicationServerAddress(reference.getApplicationServerAddress());

		var fieldMask = new FieldMask("network_server_address", "application_server_address");

		return new CreateEndDeviceRequest(endDevice, fieldMask);
	}

	public static CreateEndDeviceRequest networkServerRequest(EndDevice reference)
	{
		var keys = new EndDevice.Session.Keys();
		keys.setNetworkSessionKey(reference.getSession().getKeys().getNetworkSessionKey());

		var session = new EndDevice.Session();
		session.setKeys(keys);
		session.setDeviceAddress(reference.getSession().getDeviceAddress());

		var endDevice = new EndDevice();
		endDevice.setIds(reference.getIds());
		endDevice.setFrequencyPlanId(reference.getFrequencyPlanId());
		endDevice.setLorawanPhyVersion(reference.getLorawanPhyVersion());
		endDevice.setMulticast(reference.getMulticast());
		endDevice.setSupportsJoin(reference.getSupportsJoin());
		endDevice.setLorawanVersion(reference.getLorawanVersion());
		endDevice.setSession(session);
		endDevice.setSupportsClassC(reference.getSupportsClassC());
		endDevice.setSupportsClassB(reference.getSupportsClassB());
		endDevice.setMacSettings(reference.getMacSettings());

		var fieldMask = new FieldMask("frequency_plan_id", "lorawan_phy_version", "multicast", "supports_join",
				"lorawan_version", "ids.dev_eui", "ids.device_id", "session.keys.f_nwk_s_int_key.key",
				"session.dev_addr", "supports_class_c", "supports_class_b", "mac_settings.rx2_data_rate_index",
				"mac_settings.rx2_frequency", "mac_settings.rx1_delay", "mac_settings.rx1_data_rate_offset",
				"mac_settings.resets_f_cnt", "mac_settings.use_adr");

		return new CreateEndDeviceRequest(endDevice, fieldMask);
	}

	public static CreateEndDeviceRequest applicationServerRequest(EndDevice reference)
	{
		var keys = new EndDevice.Session.Keys();
		keys.setAppSessionKey(reference.getSession().getKeys().getAppSessionKey());

		var session = new EndDevice.Session();
		session.setKeys(keys);
		session.setDeviceAddress(reference.getSession().getDeviceAddress());

		var endDevice = new EndDevice();
		endDevice.setIds(reference.getIds());
		endDevice.setSession(session);

		var fieldMask = new FieldMask("ids.dev_eui", "ids.device_id", "session.keys.app_s_key.key", "session.dev_addr");

		return new CreateEndDeviceRequest(endDevice, fieldMask);
	}
}
