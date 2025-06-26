package org.nvictor.distance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nvictor.distance.exception.GeocodingException;
import org.nvictor.distance.web.dto.response.YandexResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class YandexService {
	private final WebClient yandexWebClient;

	@Value("${YANDEX_API_KEY}")
	private String yandexApiKey;

	public Mono<double[]> getCoordinates(String address) {

		return yandexWebClient.get()
				.uri(uriBuilder -> uriBuilder
						.queryParam("geocode", address)
						.queryParam("apikey", yandexApiKey)
						.queryParam("format", "json")
						.build())
				.retrieve()
				.bodyToMono(YandexResponse.class)
				.doOnError(error -> log.error("Yandex API error for address {}: {}", address, error.getMessage()))
				.flatMap(response -> {
					try {
						List<YandexResponse.FeatureMember> featureMembers = response.getResponse()
								.getGeoObjectCollection()
								.getFeatureMember();
						if (featureMembers.size() != 1) {
							return Mono.error(new GeocodingException("Yandex API error for address " + address + ". Too many results"));
						}
						String pos = featureMembers
								.get(0)
								.getGeoObject()
								.getPoint()
								.getPos();
						String[] coords = pos.split(" ");
						double longitude = Double.parseDouble(coords[0]);
						double latitude = Double.parseDouble(coords[1]);

						log.info("Yandex coordinates for address {}: lat={}, lon={}", address, latitude, longitude);
						return Mono.just(new double[]{latitude, longitude});
					} catch (Exception e) {
						log.warn("No coordinates found in Yandex API response for address: {}", address);
						return Mono.error(new GeocodingException("No coordinates found for address in Yandex API"));
					}
				});
	}
}