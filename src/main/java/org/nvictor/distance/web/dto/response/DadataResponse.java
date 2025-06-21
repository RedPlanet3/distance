package org.nvictor.distance.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class DadataResponse {

	String source;
	String result;

	@JsonProperty("geo_lat")
	Double geoLat;
	@JsonProperty("geo_lon")
	Double geoLon;
	@JsonProperty("qc_geo")
	Integer qcGeo;

}
