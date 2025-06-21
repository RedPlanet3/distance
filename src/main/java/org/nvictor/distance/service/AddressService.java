package org.nvictor.distance.service;

import lombok.RequiredArgsConstructor;
import org.nvictor.distance.model.AddressResult;
import org.nvictor.distance.repository.AddressResultRepository;
import org.nvictor.distance.utils.DistanceCalculator;
import org.nvictor.distance.web.dto.response.AddressResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AddressService {
	private final YandexService yandexService;
	private final DadataService dadataService;
	private final AddressResultRepository repository;

	public Mono<AddressResponse> compareAddressCoordinates(String address) {
		Mono<double[]> yandexCoords = yandexService.getCoordinates(address)
				.onErrorResume(e -> Mono.just(new double[]{0, 0}));

		Mono<double[]> dadataCoords = dadataService.getCoordinates(address)
				.onErrorResume(e -> Mono.just(new double[]{0, 0}));

		return Mono.zip(yandexCoords, dadataCoords)
				.flatMap(tuple -> {
					double[] yandex = tuple.getT1();
					double[] dadata = tuple.getT2();

					double distance = DistanceCalculator.calculateDistance(
							yandex[0], yandex[1], dadata[0], dadata[1]);

					AddressResult result = new AddressResult();
					result.setAddress(address);
					result.setYandexGeoLat(yandex[0]);
					result.setYandexGeoLon(yandex[1]);
					result.setDadataGeoLat(dadata[0]);
					result.setDadataGeoLon(dadata[1]);
					result.setDistanceInMeters(distance);

					return Mono.fromCallable(() -> saveResultTransactional(result))
							.thenReturn(buildResponse(yandex, dadata, distance));
				});
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected AddressResult saveResultTransactional(AddressResult result) {
		return repository.save(result);
	}

	private AddressResponse buildResponse(double[] yandex, double[] dadata, double distance) {
		AddressResponse response = new AddressResponse();
		response.setYandexLatitude(yandex[0]);
		response.setYandexLongitude(yandex[1]);
		response.setDadataLatitude(dadata[0]);
		response.setDadataLongitude(dadata[1]);
		response.setDistanceInMeters(distance);

		String message;
		if (yandex[0] == 0 && yandex[1] == 0 && dadata[0] == 0 && dadata[1] == 0) {
			message = "Both APIs failed to geocode the address";
		} else if (yandex[0] == 0 && yandex[1] == 0) {
			message = "Yandex API failed to geocode the address";
		} else if (dadata[0] == 0 && dadata[1] == 0) {
			message = "Dadata API failed to geocode the address";
		} else {
			message = String.format("Distance between Yandex and Dadata coordinates: %.2f meters", distance);
		}
		response.setMessage(message);
		return response;
	}
}