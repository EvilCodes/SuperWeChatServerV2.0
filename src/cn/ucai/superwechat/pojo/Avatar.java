package cn.ucai.superwechat.pojo;

import java.io.Serializable;

public class Avatar implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer mavatarId;
	private String mavatarUserName;
	private String mavatarPath;
	private Integer mavatarType;
	private String mavatarLastUpdateTime;
	public Avatar(){
		super();
	}
	public Avatar(Integer mavatarId,String mavatarUserName,String mavatarPath,Integer mavatarType,String mavatarLastUpdateTime){
		this.mavatarId = mavatarId;
		this.mavatarUserName = mavatarUserName;
		this.mavatarPath = mavatarPath;
		this.mavatarType = mavatarType;
		this.mavatarLastUpdateTime = mavatarLastUpdateTime;
	}
	
	public Integer getMAvatarId() {
 		return this.mavatarId;
 	}
 	
	public void setMAvatarId(Integer mavatarId){
		this.mavatarId = mavatarId;
	}
 	
 	public String getMAvatarUserName() {
 		return this.mavatarUserName;
 	}
 	
	public void setMAvatarUserName(String mavatarUserName){
		this.mavatarUserName = mavatarUserName;
	}
 	
 	public String getMAvatarPath() {
 		return this.mavatarPath;
 	}
 	
	public void setMAvatarPath(String mavatarPath){
		this.mavatarPath = mavatarPath;
	}
 	
 	public Integer getMAvatarType() {
 		return this.mavatarType;
 	}
 	
	public void setMAvatarType(Integer mavatarType){
		this.mavatarType = mavatarType;
	}
 	
 	public String getMAvatarLastUpdateTime() {
 		return this.mavatarLastUpdateTime;
 	}
 	
	public void setMAvatarLastUpdateTime(String mavatarLastUpdateTime){
		this.mavatarLastUpdateTime = mavatarLastUpdateTime;
	}
 	
 	 	@Override
 	public String toString() {
 		return "Avatar ["
 	 	+ "this.mavatarId=" + mavatarId
	 	+ "this.mavatarUserName=" + mavatarUserName
	 	+ "this.mavatarPath=" + mavatarPath
	 	+ "this.mavatarType=" + mavatarType
	 	+ "this.mavatarLastUpdateTime=" + mavatarLastUpdateTime
		;
 	}
 
}