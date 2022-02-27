package de.p3monitor.ttn;

import de.p3monitor.ttn.api.dtos.*;
import de.p3monitor.ttn.config.TtnProperties;
import de.p3monitor.ttn.exception.TtnApiNotFoundException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import de.p3monitor.ttn.api.TtnApiApplicationClient;
import de.p3monitor.ttn.api.TtnApiEndDeviceClient;
import de.p3monitor.ttn.api.TtnApiNetworkServerClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

import java.security.SecureRandom;
import java.util.List;

@Slf4j
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
		macSettings.setUseAdr(false);
		endDevice.setMacSettings(macSettings);

		return endDevice;
	}

	private EndDevice mergeEndDevices(Tuple3<EndDevice, EndDevice, EndDevice> tuple)
	{
		var endDevice = tuple.getT1();
		var nsEndDevice = tuple.getT2();
		var asEndDevice = tuple.getT3();

		endDevice.setSession(nsEndDevice.getSession());
		endDevice.getSession().getKeys().setAppSessionKey(asEndDevice.getSession().getKeys().getAppSessionKey());

		return endDevice;
	}

	public Mono<EndDevice> createEndDevice()
	{
		return Mono.zip(ttnApiApplicationClient.generateDeviceEui(ttnProperties.getApplicationId())
								.map(ApplicationDeviceEuiResponse::getDevEui),
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
				.map(this::mergeEndDevices);
	}

	public Mono<EndDevice> getEndDevice(String endDeviceId)
	{
		return Mono.zip(ttnApiEndDeviceClient.getEndDevice(ttnProperties.getApplicationId(), endDeviceId),
				ttnApiEndDeviceClient.getEndDeviceFromNetworkServer(ttnProperties.getApplicationId(), endDeviceId),
				ttnApiEndDeviceClient.getEndDeviceFromApplicationServer(ttnProperties.getApplicationId(), endDeviceId))
				.map(this::mergeEndDevices)
				.onErrorMap(e -> {
					if (e instanceof FeignException && ((FeignException)e).status() == 404) {
						return new TtnApiNotFoundException(e);
					} else {
						log.error("Error executing TTN request", e);
						return e;
					}
				});
	}

	public Mono<Void> deleteEndDevice(String endDeviceId)
	{
		return ttnApiEndDeviceClient.deleteEndDeviceFromNetworkServer(ttnProperties.getApplicationId(), endDeviceId)
				.onErrorResume((e) -> Mono.empty())
				.then(ttnApiEndDeviceClient.deleteEndDeviceFromApplicationServer(ttnProperties.getApplicationId(), endDeviceId))
				.onErrorResume((e) -> Mono.empty())
				.then(ttnApiEndDeviceClient.deleteEndDevice(ttnProperties.getApplicationId(), endDeviceId))
				.onErrorResume((e) -> Mono.empty());
	}

	public Mono<List<EndDevice>> getEndDevices()
	{
		return ttnApiEndDeviceClient.getEndDevicesList(ttnProperties.getApplicationId())
				.map(EndDevicesResponse::getEndDevices);
	}
}
