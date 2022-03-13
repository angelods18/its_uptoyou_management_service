package it.itsuptoyou.models;

import org.springframework.data.mongodb.core.geo.GeoJson;

import lombok.Data;

@Data
public class Location {

	private String formattedAddress;
	private String city;
	private GeoPoint geoPoint;
	
	@Data
	public class GeoPoint{
		private String type;
		private Double[] coordinates;
	}
	
}
