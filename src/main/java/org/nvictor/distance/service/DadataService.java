package org.nvictor.distance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nvictor.distance.exception.GeocodingException;
import org.nvictor.distance.web.dto.response.DadataResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DadataService {
	private final WebClient dadataWebClient;

	@Value("${DADATA_API_KEY}")
	private String DADATA_CLIENT_TOKEN;
	@Value("${DADATA_API_SECRET}")
	private String DADATA_API_SECRET;

	public Mono<double[]> getCoordinates(String address) {
		return dadataWebClient.post()
				.uri("/address")
				.header(HttpHeaders.AUTHORIZATION, "Token " + DADATA_CLIENT_TOKEN)
				.header("X-Secret", DADATA_API_SECRET)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue("[\"" + address + "\"]")
				.retrieve()
				.bodyToFlux(DadataResponse.class)
				.collectList()
				.doOnError(error -> log.error("Dadata API error for address {}: {}", address, error.getMessage()))
				.flatMap(responses -> {
					if (responses == null || responses.isEmpty()) {
						log.warn("No coordinates found in Dadata API response for address: {}", address);
						return Mono.error(new GeocodingException("No coordinates found for address in Dadata API"));
					}

					DadataResponse response = responses.get(0);

					if (response.getGeoLat() == null || response.getGeoLon() == null) {
						log.warn("No coordinates found in Dadata API response for address: {}", address);
						return Mono.error(new GeocodingException("No coordinates found for address in Dadata API"));
					}

					// Проверка качества геокодирования (qc_geo)
					if (response.getQcGeo() != null && response.getQcGeo() == 5) {
						log.warn("Low quality geocoding (qc_geo={}) for address: {}", response.getQcGeo(), response.getSource());
						return Mono.error(new GeocodingException(
								String.format("Low quality geocoding (qc_geo=%d) for address: %s",
										response.getQcGeo(), response.getSource())));
					}

					log.info("Dadata coordinates for address {}: lat={}, lon={}, qc_geo={}", 
							address, response.getGeoLat(), response.getGeoLon(), response.getQcGeo());
					
					return Mono.just(new double[]{response.getGeoLat(), response.getGeoLon()});
				});
	}
}