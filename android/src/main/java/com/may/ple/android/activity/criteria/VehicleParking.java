package com.may.ple.android.activity.criteria;

import java.util.Date;
import java.util.Map;

public class VehicleParking {
	public Long id;
	public Date inDateTime;
	public Date outDateTime;
	public Integer price;
	public Integer status;
	public String licenseNo;
	public String deviceId;
	public String gateName;
	public Map<String, Long> dateTimeDiffMap;
}
