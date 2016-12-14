package de.dkt.eservices.esst.filters;

import eu.freme.common.exception.ExternalServiceFailedException;

public enum FilterType {
	TEMPORAL (1),
	GEOGRAPHICAL (2);

	private int value;
	FilterType(int value){
		this.value = value;
	}
	
	public String toString() {
		switch (value) {
		case 1:
			return "temporalFilter";
		case 2:
			return "geographicalFilter";
		default:
			throw new ExternalServiceFailedException("Invalid filtertype value");
		}
	}
	
	public static FilterType getFilterType(String s){
		if(s.equalsIgnoreCase("temporalFilter")){
			return TEMPORAL;
		}
		else if(s.equalsIgnoreCase("geographicalFilter")){
			return GEOGRAPHICAL;
		}
		throw new ExternalServiceFailedException("Invalid filtertype value");
	}

}