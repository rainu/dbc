package de.raysha.lib.dbc.meta;

public class TableMetadata {
	private Long id;
	private String name;
	private String version;
	private String metadata;
	
	private final MetadataManager manager;
	
	TableMetadata(MetadataManager manager){
		this.manager = manager;
	}
	
	public Long getId() {
		return id;
	}
	 void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	void setVersion(String version) {
		this.version = version;
	}
	public String getMetadata() {
		if(metadata == null){
			metadata = manager.getMetadata(getId());
		}
		
		return metadata;
	}
}
