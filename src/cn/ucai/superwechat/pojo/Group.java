package cn.ucai.superwechat.pojo;

import java.io.Serializable;

public class Group implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer mgroupId;
	private String mgroupHxid;
	private String mgroupName;
	private String mgroupDescription;
	private String mgroupOwner;
	private String mgroupLastModifiedTime;
	private Integer mgroupMaxUsers;
	private Integer mgroupAffiliationsCount;
	private Boolean mgroupIsPublic;
	private Boolean mgroupAllowInvites;
	public Group(){
		super();
	}
	public Group(String mgroupHxid,String mgroupName,String mgroupDescription,String mgroupOwner,String mgroupLastModifiedTime,Integer mgroupMaxUsers,Integer mgroupAffiliationsCount,Boolean mgroupIsPublic,Boolean mgroupAllowInvites){
		this.mgroupHxid = mgroupHxid;
		this.mgroupName = mgroupName;
		this.mgroupDescription = mgroupDescription;
		this.mgroupOwner = mgroupOwner;
		this.mgroupLastModifiedTime = mgroupLastModifiedTime;
		this.mgroupMaxUsers = mgroupMaxUsers;
		this.mgroupAffiliationsCount = mgroupAffiliationsCount;
		this.mgroupIsPublic = mgroupIsPublic;
		this.mgroupAllowInvites = mgroupAllowInvites;
	}
	public Group(Integer mgroupId,String mgroupHxid,String mgroupName,String mgroupDescription,String mgroupOwner,String mgroupLastModifiedTime,Integer mgroupMaxUsers,Integer mgroupAffiliationsCount,Boolean mgroupIsPublic,Boolean mgroupAllowInvites){
		this.mgroupId = mgroupId;
		this.mgroupHxid = mgroupHxid;
		this.mgroupName = mgroupName;
		this.mgroupDescription = mgroupDescription;
		this.mgroupOwner = mgroupOwner;
		this.mgroupLastModifiedTime = mgroupLastModifiedTime;
		this.mgroupMaxUsers = mgroupMaxUsers;
		this.mgroupAffiliationsCount = mgroupAffiliationsCount;
		this.mgroupIsPublic = mgroupIsPublic;
		this.mgroupAllowInvites = mgroupAllowInvites;
	}
	
	public Integer getMGroupId() {
 		return this.mgroupId;
 	}
 	
	public void setMGroupId(Integer mgroupId){
		this.mgroupId = mgroupId;
	}
 	
 	public String getMGroupHxid() {
 		return this.mgroupHxid;
 	}
 	
	public void setMGroupHxid(String mgroupHxid){
		this.mgroupHxid = mgroupHxid;
	}
 	
 	public String getMGroupName() {
 		return this.mgroupName;
 	}
 	
	public void setMGroupName(String mgroupName){
		this.mgroupName = mgroupName;
	}
 	
 	public String getMGroupDescription() {
 		return this.mgroupDescription;
 	}
 	
	public void setMGroupDescription(String mgroupDescription){
		this.mgroupDescription = mgroupDescription;
	}
 	
 	public String getMGroupOwner() {
 		return this.mgroupOwner;
 	}
 	
	public void setMGroupOwner(String mgroupOwner){
		this.mgroupOwner = mgroupOwner;
	}
 	
 	public String getMGroupLastModifiedTime() {
 		return this.mgroupLastModifiedTime;
 	}
 	
	public void setMGroupLastModifiedTime(String mgroupLastModifiedTime){
		this.mgroupLastModifiedTime = mgroupLastModifiedTime;
	}
 	
 	public Integer getMGroupMaxUsers() {
 		return this.mgroupMaxUsers;
 	}
 	
	public void setMGroupMaxUsers(Integer mgroupMaxUsers){
		this.mgroupMaxUsers = mgroupMaxUsers;
	}
 	
 	public Integer getMGroupAffiliationsCount() {
 		return this.mgroupAffiliationsCount;
 	}
 	
	public void setMGroupAffiliationsCount(Integer mgroupAffiliationsCount){
		this.mgroupAffiliationsCount = mgroupAffiliationsCount;
	}
 	
 	public Boolean getMGroupIsPublic() {
 		return this.mgroupIsPublic;
 	}
 	
	public void setMGroupIsPublic(Boolean mgroupIsPublic){
		this.mgroupIsPublic = mgroupIsPublic;
	}
 	
 	public Boolean getMGroupAllowInvites() {
 		return this.mgroupAllowInvites;
 	}
 	
	public void setMGroupAllowInvites(Boolean mgroupAllowInvites){
		this.mgroupAllowInvites = mgroupAllowInvites;
	}
 	
 	 	@Override
 	public String toString() {
 		return "Group ["
 	 	+ "this.mgroupId=" + mgroupId
	 	+ "this.mgroupHxid=" + mgroupHxid
	 	+ "this.mgroupName=" + mgroupName
	 	+ "this.mgroupDescription=" + mgroupDescription
	 	+ "this.mgroupOwner=" + mgroupOwner
	 	+ "this.mgroupLastModifiedTime=" + mgroupLastModifiedTime
	 	+ "this.mgroupMaxUsers=" + mgroupMaxUsers
	 	+ "this.mgroupAffiliationsCount=" + mgroupAffiliationsCount
	 	+ "this.mgroupIsPublic=" + mgroupIsPublic
	 	+ "this.mgroupAllowInvites=" + mgroupAllowInvites
		;
 	}
 
}