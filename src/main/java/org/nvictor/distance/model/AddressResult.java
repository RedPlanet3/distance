package org.nvictor.distance.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "address_result")
public class AddressResult extends BaseEntity {

	private String address;
	private double yandexGeoLat;
	private double yandexGeoLon;
	private double dadataGeoLat;
	private double dadataGeoLon;
	private double distanceInMeters;

}
