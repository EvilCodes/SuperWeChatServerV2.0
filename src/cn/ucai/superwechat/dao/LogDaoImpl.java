package cn.ucai.superwechat.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.ucai.superwechat.bean.Log;
import cn.ucai.superwechat.utils.DBUtils;

public class LogDaoImpl implements ILogDao{

	@Override
	public List<Log> getLogs(int sum) {
		Connection conn = DBUtils.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select * from t_logs order by id desc limit 0,?";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, sum);
			rs = ps.executeQuery();
			List<Log> list = new ArrayList<Log>();
			while(rs.next()){
				Log log = new Log();
				log.setId(rs.getInt("id"));
				log.setCreateDate(rs.getString("createDate"));
				log.setPriority(rs.getString("priority"));
				log.setCategory(rs.getString("category"));
				log.setMessage(rs.getString("message"));
				list.add(log);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}

}
