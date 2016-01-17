package com.may.ple.android.activity.criteria;

import java.util.List;


public class OrderSaveCriteriaReq {	
	public Long menuId;
	public String tableName;
	public String ref;
	public Integer amount;
	public String comment;
	public Boolean isTakeHome;
	public List<SubMenu> subMenus;
}
