package com.oktested.helpSupport.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class HelpModel implements Serializable {

	@SerializedName("answer")
	private String answer;

	@SerializedName("question")
	private String question;

	@SerializedName("id")
	private String id;

	@SerializedName("status")
	private int status;

	@SerializedName("timestamp")
	private String timestamp;

	public void setAnswer(String answer){
		this.answer = answer;
	}

	public String getAnswer(){
		return answer;
	}

	public void setQuestion(String question){
		this.question = question;
	}

	public String getQuestion(){
		return question;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}

	public void setTimestamp(String timestamp){
		this.timestamp = timestamp;
	}

	public String getTimestamp(){
		return timestamp;
	}
}