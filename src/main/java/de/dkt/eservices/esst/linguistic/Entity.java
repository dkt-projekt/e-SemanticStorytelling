package de.dkt.eservices.esst.linguistic;

import org.json.JSONObject;

public class Entity {

	private String text;
	private String url;
	private EntityType type;
	
	public Entity() {
		super();
	}
	
	public Entity(String text, String url, EntityType type) {
		super();
		this.text = text;
		this.url = url;
		this.type = type;
	}

	public Entity(String text, String url, String type) {
		super();
		this.text = text;
		this.url = url;
		this.type = EntityType.fromValue(type);
	}

	public Entity(JSONObject obj) {
		super();
		this.text = obj.getString("text");
		if(this.text.equalsIgnoreCase("EMPTY")){
			this.text=null;
		}
		this.url = obj.getString("url");
		if(this.url.equalsIgnoreCase("EMPTY")){
			this.url=null;
		}
		String type = obj.getString("type");
		if(type.equalsIgnoreCase("EMPTY")){
			this.type=EntityType.UNKNOWN;
		}
		else{
			this.type = EntityType.fromValue(type);
		}
	}
	
	public JSONObject getJSONObject(){
		JSONObject obj = new JSONObject();
		if(text!=null){
			obj.put("text", text);
		}
		else{
			obj.put("text", "EMPTY");
		}
		if(url!=null){
			obj.put("url", url);
		}
		else{
			obj.put("url", "EMPTY");
		}
		if(type!=null){
			obj.put("type", type.toString());
		}
		else{
			obj.put("type", "EMPTY");
		}
		return obj;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public EntityType getType() {
		return type;
	}

	public void setType(EntityType type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Entity){
			Entity e = (Entity)obj;
			if(!e.getText().equalsIgnoreCase(text)){
				return false;
			}
//			else if(e.getUrl()==null && url==null ){
//				return false;
//			}
//			else if(!e.getUrl().equalsIgnoreCase(url)){
//				return false;
//			}
			else if(e.getType()!=type ){
				return false;
			}
			else{
				return true;
			}
//			if(e.getType()!=type ){
//				return false;
//			}
//			else{
//				return true;
//			}
		}
		return false;
	}
	
}
