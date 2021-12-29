package org.urbanenvironmentmonitor.ttn;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.urbanenvironmentmonitor.ttn.api.TtnApiApplicationClient;
import org.urbanenvironmentmonitor.ttn.api.TtnApiEndDeviceClient;
import org.urbanenvironmentmonitor.ttn.api.TtnApiNetworkServerClient;
import org.urbanenvironmentmonitor.ttn.api.dtos.CreateEndDeviceRequest;
import org.urbanenvironmentmonitor.ttn.api.dtos.DeviceAddressResponse;
import org.urbanenvironmentmonitor.ttn.api.dtos.EndDevice;
import org.urbanenvironmentmonitor.ttn.api.dtos.EndDevicesResponse;
import org.urbanenvironmentmonitor.ttn.config.TtnProperties;
import org.urbanenvironmentmonitor.ttn.exception.TtnApiNotFoundException;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.security.SecureRandom;
import java.util.List;

@Service
public class TtnService
{
	private final TtnApiEndDeviceClient ttnApiEndDeviceClient;
	private final TtnApiApplicationClient ttnApiApplicationClient;
	private final TtnApiNetworkServerClient ttnApiNetworkServerClient;
	private final TtnProperties ttnProperties;

	public TtnService(TtnApiEndDeviceClient ttnApiEndDeviceClient, TtnApiApplicationClient ttnApiApplicationClient,
	                  TtnApiNetworkServerClient ttnApiNetworkServerClient, TtnProperties ttnProperties)
	{
		this.ttnApiEndDeviceClient = ttnApiEndDeviceClient;
		this.ttnApiApplicationClient = ttnApiApplicationClient;
		this.ttnApiNetworkServerClient = ttnApiNetworkServerClient;
		this.ttnProperties = ttnProperties;
	}

	private static EndDevice.Session.Keys.Key generateRandomKey()
	{
		StringBuilder stringBuilder = new StringBuilder();

		SecureRandom secureRandom = new SecureRandom();
		secureRandom.ints(0, 16)
				.limit(32)
				.mapToObj("0123456789ABCDEF"::charAt)
				.forEach(stringBuilder::append);

		return new EndDevice.Session.Keys.Key(stringBuilder.toString());
	}

	private EndDevice createEndDeviceEntity(String deviceEui, String deviceAddress)
	{
		var endDevice = new EndDevice();
		endDevice.setIds(new EndDevice.Identifiers("eui-" + deviceEui.toLowerCase(), deviceEui));
		endDevice.setNetworkServerAddress("eu1.cloud.thethings.network");
		endDevice.setApplicationServerAddress("eu1.cloud.thethings.network");
		endDevice.setFrequencyPlanId("EU_863_870_TTN");
		endDevice.setLorawanPhyVersion("PHY_V1_0_2_REV_B");
		endDevice.setMulticast(false);
		endDevice.setSupportsJoin(false);
		endDevice.setLorawanVersion("MAC_V1_0_2");
		endDevice.setSupportsClassB(false);
		endDevice.setSupportsClassC(false);

		var keys = new EndDevice.Session.Keys();
		keys.setNetworkSessionKey(generateRandomKey());
		keys.setAppSessionKey(generateRandomKey());

		var session = new EndDevice.Session();
		session.setDeviceAddress(deviceAddress);
		session.setKeys(keys);
		endDevice.setSession(session);

		var macSettings = new EndDevice.MacSettings();
		macSettings.setRx2DataRateIndex(0);
		macSettings.setRx2Frequency(869525000);
		macSettings.setRx1Delay(1);
		macSettings.setRx1DataRateOffset(0);
		macSettings.setResetsFCnt(false);
		endDevice.setMacSettings(macSettings);

		return endDevice;
	}

	public Mono<EndDevice> createEndDevice()
	{
		return Mono.zip(
						Mono.just("70B3D57ED004A4B6"),
						//ttnApiApplicationClient.generateDeviceEui(ttnProperties.getApplicationId())
						//		.map(ApplicationDeviceEuiResponse::getDevEui),
						ttnApiNetworkServerClient.generateDeviceAddress()
								.map(DeviceAddressResponse::getDeviceAddress)
				)
				.map(tuple -> createEndDeviceEntity(tuple.getT1(), tuple.getT2()))
				.flatMap(endDevice -> Mono.zip(
						ttnApiEndDeviceClient.createEndDevice(ttnProperties.getApplicationId(),
								CreateEndDeviceRequest.registryRequest(endDevice)),
						ttnApiEndDeviceClient.createEndDeviceOnNetworkServer(ttnProperties.getApplicationId(),
								CreateEndDeviceRequest.networkServerRequest(endDevice)),
						ttnApiEndDeviceClient.creatEndDeviceOnApplicationServer(ttnProperties.getApplicationId(),
								CreateEndDeviceRequest.applicationServerRequest(endDevice))
				))
				.map(Tuple2::getT1);
	}

	public Mono<EndDevice> getEndDevice(String endDeviceId)
	{
		return Mono.zip(ttnApiEndDeviceClient.getEndDevice(ttnProperties.getApplicationId(), endDeviceId),
				ttnApiEndDeviceClient.getEndDeviceFromNetworkServer(ttnProperties.getApplicationId(), endDeviceId),
				ttnApiEndDeviceClient.getEndDeviceFromApplicationServer(ttnProperties.getApplicationId(), endDeviceId))
				.map(Tuple2::getT1)
				.onErrorMap(WebClientResponseException.class, e -> {
					if (e.getRawStatusCode() == 404) {
						return new TtnApiNotFoundException(e);
					} else {
						return e;
					}
				});
	}

	public Mono<List<EndDevice>> getEndDevices()
	{
		return ttnApiEndDeviceClient.getEndDevicesList(ttnProperties.getApplicationId())
				.map(EndDevicesResponse::getEndDevices);
	}
}
