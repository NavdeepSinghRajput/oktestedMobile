package com.oktested.login.model;

import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;

import java.util.List;

public class SocialLoginDataResponse extends BaseResponse {
	@SerializedName("access_token")
	public String accessToken;

	@SerializedName("refresh_token")
	public String refreshToken;

	@SerializedName("last_login")
	public String lastLogin;

	@SerializedName("name")
	public String name;

	@SerializedName("display_name")
	public String display_name;

	@SerializedName("mobile")
	public String mobile;

	@SerializedName("id")
	public String id;

	@SerializedName("follow")
	public List<Object> follow;

	@SerializedName("email")
	public String email;

	@SerializedName("picture")
	public String picture;

	@SerializedName("timestamp")
	public String timestamp;

	@SerializedName("gender")
	public String gender;

	@SerializedName("dob")
	public String dob;
}