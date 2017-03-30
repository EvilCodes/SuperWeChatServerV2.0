package cn.ucai.superwechat.pojo;

import java.io.Serializable;

public class Member implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer mmemberId;
	private String mmemberUserName;
	private Integer mmemberGroupId;
	private String mmemberGroupHxid;
	private Integer mmemberPermission;
	public Member(){
		super();
	}
	public Member(String mmemberUserName,Integer mmemberGroupId,String mmemberGroupHxid,Integer mmemberPermission){
		this.mmemberUserName = mmemberUserName;
		this.mmemberGroupId = mmemberGroupId;
		this.mmemberGroupHxid = mmemberGroupHxid;
		this.mmemberPermission = mmemberPermission;
	}
	public Member(Integer mmemberId,String mmemberUserName,Integer mmemberGroupId,String mmemberGroupHxid,Integer mmemberPermission){
		this.mmemberId = mmemberId;
		this.mmemberUserName = mmemberUserName;
		this.mmemberGroupId = mmemberGroupId;
		this.mmemberGroupHxid = mmemberGroupHxid;
		this.mmemberPermission = mmemberPermission;
	}
	
	public Integer getMMemberId() {
 		return this.mmemberId;
 	}
 	
	public void setMMemberId(Integer mmemberId){
		this.mmemberId = mmemberId;
	}
 	
 	public String getMMemberUserName() {
 		return this.mmemberUserName;
 	}
 	
	public void setMMemberUserName(String mmemberUserName){
		this.mmemberUserName = mmemberUserName;
	}
 	
 	public Integer getMMemberGroupId() {
 		return this.mmemberGroupId;
 	}
 	
	public void setMMemberGroupId(Integer mmemberGroupId){
		this.mmemberGroupId = mmemberGroupId;
	}
 	
 	public String getMMemberGroupHxid() {
 		return this.mmemberGroupHxid;
 	}
 	
	public void setMMemberGroupHxid(String mmemberGroupHxid){
		this.mmemberGroupHxid = mmemberGroupHxid;
	}
 	
 	public Integer getMMemberPermission() {
 		return this.mmemberPermission;
 	}
 	
	public void setMMemberPermission(Integer mmemberPermission){
		this.mmemberPermission = mmemberPermission;
	}
 	
 	 	@Override
 	public String toString() {
 		return "Member ["
 	 	+ "this.mmemberId=" + mmemberId
	 	+ "this.mmemberUserName=" + mmemberUserName
	 	+ "this.mmemberGroupId=" + mmemberGroupId
	 	+ "this.mmemberGroupHxid=" + mmemberGroupHxid
	 	+ "this.mmemberPermission=" + mmemberPermission
		;
 	}
 
}