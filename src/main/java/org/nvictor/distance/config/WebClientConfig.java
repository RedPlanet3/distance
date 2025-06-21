package org.nvictor.distance.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Value("${dadata.base-url}")
	private String dadataBaseUrl;

	@Value("${yandex.base-url}")
	private String yandexBaseUrl;

	@Bean
	public WebClient yandexWebClient() {
		return WebClient.builder()
				.baseUrl(yandexBaseUrl)
				.build();
	}

	@Bean
	public WebClient dadataWebClient() {
		return WebClient.builder()
				.baseUrl(dadataBaseUrl)
				.build();
	}
}
