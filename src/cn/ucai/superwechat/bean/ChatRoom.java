package cn.ucai.superwechat.bean;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class ChatRoom {
	/*public static void main(String[] args) {
		ChatRoom cr = new ChatRoom();
		String str = "abc,def";
		cr.setMembers(new String[]{str});
		cr.setName("zhangsan");
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println(mapper.writeValueAsString(cr));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}*/
	private String name;
	private String description;
	private String owner;
	private int maxusers;
	private String[] members;
	public ChatRoom() {
		super();
	}
	public ChatRoom(String name, String description, String owner, int maxusers, String[] members) {
		super();
		this.name = name;
		this.description = description;
		this.owner = owner;
		this.maxusers = maxusers;
		this.members = members;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public int getMaxusers() {
		return maxusers;
	}
	public void setMaxusers(int maxusers) {
		this.maxusers = maxusers;
	}
	public String[] getMembers() {
		return members;
	}
	public void setMembers(String[] members) {
		this.members = members;
	}
	
}
