package cn.ucai.superwechat.dao;

import java.util.List;

import cn.ucai.superwechat.bean.Log;

public interface ILogDao {
	public abstract List<Log> getLogs(int sum);
}
