package cn.ucai.superwechat.bean;

public class Log {
	private int id;
	private String createDate;
	private String priority;
	private String category;
	private String message;

	public Log() {
		super();
	}

	public Log(int id,String createDate, String priority, String category, String message) {
		super();
		this.id = id;
		this.createDate = createDate;
		this.priority = priority;
		this.category = category;
		this.message = message;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Log ["+id+"\t"+ createDate +"\t"+ priority +"\t"+ category +"\t"+ message + "]";
	}
}
