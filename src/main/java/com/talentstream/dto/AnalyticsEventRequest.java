package com.talentstream.dto;

public class AnalyticsEventRequest {
	
    private long userId;
   
    private String feature;
    
   
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public String getFeature() {
		return feature;
	}
	public void setFeature(String feature) {
		this.feature = feature;
	}
	
    
}
