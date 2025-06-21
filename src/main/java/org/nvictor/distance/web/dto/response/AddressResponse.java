package org.nvictor.distance.web.dto.response;

import lombok.Data;

@Data
public class AddressResponse {
	private double yandexLatitude;
	private double yandexLongitude;
	private double dadataLatitude;
	private double dadataLongitude;
	private double distanceInMeters;
	private String message;
}