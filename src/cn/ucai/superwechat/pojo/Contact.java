package cn.ucai.superwechat.pojo;

import java.io.Serializable;

public class Contact implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer mcontactId;
	private String mcontactUserName;
	private String mcontactCname;
	public Contact(){
		super();
	}
	public Contact(Integer mcontactId,String mcontactUserName,String mcontactCname){
		this.mcontactId = mcontactId;
		this.mcontactUserName = mcontactUserName;
		this.mcontactCname = mcontactCname;
	}
	
	public Integer getMContactId() {
 		return this.mcontactId;
 	}
 	
	public void setMContactId(Integer mcontactId){
		this.mcontactId = mcontactId;
	}
 	
 	public String getMContactUserName() {
 		return this.mcontactUserName;
 	}
 	
	public void setMContactUserName(String mcontactUserName){
		this.mcontactUserName = mcontactUserName;
	}
 	
 	public String getMContactCname() {
 		return this.mcontactCname;
 	}
 	
	public void setMContactCname(String mcontactCname){
		this.mcontactCname = mcontactCname;
	}
 	
 	 	@Override
 	public String toString() {
 		return "Contact ["
 	 	+ "this.mcontactId=" + mcontactId
	 	+ "this.mcontactUserName=" + mcontactUserName
	 	+ "this.mcontactCname=" + mcontactCname
		;
 	}
 
}