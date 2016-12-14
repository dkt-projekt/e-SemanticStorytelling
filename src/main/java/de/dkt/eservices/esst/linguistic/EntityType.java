package de.dkt.eservices.esst.linguistic;

public enum EntityType {
	LOCATION("location"),
	PERSON("person"),
	ORGANIZATION("organization"),
	ACTION("action"),
	OTHER("other"),
	UNKNOWN("unknown");
	private final String contentType;

	EntityType(String contentType) {
		this.contentType = contentType;
	}

	public String contentType() {
		return contentType;
	}

	/**
	 * Given a textual content type, return its RDFSerialization object.
	 */
	public static EntityType fromValue(final String contentType) {
		if(contentType==null){
			return EntityType.UNKNOWN;
		}
		String normalizedContentType = contentType.toLowerCase();
//		System.out.println(normalizedContentType);
		for (EntityType type : EntityType.values()) {
//			if (type.contentType().equals(normalizedContentType)) {
			if (normalizedContentType.contains(type.contentType())) {
				return type;
			}
		}
		return EntityType.UNKNOWN;
	}
}
