package org.nvictor.distance.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class YandexResponse {
	private Response response;

	@Data
	public static class Response {
		@JsonProperty("GeoObjectCollection")
		private GeoObjectCollection geoObjectCollection;
	}

	@Data
	public static class GeoObjectCollection {
		@JsonProperty("featureMember")
		private List<FeatureMember> featureMember;
	}

	@Data
	public static class FeatureMember {
		@JsonProperty("GeoObject")
		private GeoObject geoObject;
	}

	@Data
	public static class GeoObject {
		@JsonProperty("Point")
		private Point point;
	}

	@Data
	public static class Point {
		private String pos;
	}
}
