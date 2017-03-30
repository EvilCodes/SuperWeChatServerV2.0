package cn.ucai.superwechat.bean;

import cn.ucai.superwechat.pojo.GiftStatements;

public class GiftStatementsBean extends GiftStatements{
	private String gname;

    private String gurl;

    private Integer gprice;

	public String getGname() {
		return gname;
	}

	public void setGname(String gname) {
		this.gname = gname;
	}

	public String getGurl() {
		return gurl;
	}

	public void setGurl(String gurl) {
		this.gurl = gurl;
	}

	public Integer getGprice() {
		return gprice;
	}

	public void setGprice(Integer gprice) {
		this.gprice = gprice;
	}
    
}
