package cn.ucai.superwechat.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.ucai.superwechat.bean.GiftCount;
import cn.ucai.superwechat.bean.GiftStatementsBean;
import cn.ucai.superwechat.pojo.Gift;
import cn.ucai.superwechat.pojo.RechargeStatements;
import cn.ucai.superwechat.pojo.Wallet;
import cn.ucai.superwechat.utils.DBUtils;
import cn.ucai.superwechat.utils.PropertiesUtils;

public class LiveDaoImpl implements ILiveDao {
	private Logger logger = Logger.getLogger(this.getClass());
	@Override
	public List<Gift> getAllGifts() {
		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = DBUtils.getConnection();
		String sql = "select * from gift order by gprice,id";
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			List<Gift> list = new ArrayList<Gift>();
			while (rs.next()) {
				Gift gift = new Gift();
				gift.setId(rs.getInt("id"));
				gift.setGname(rs.getString("gname"));
				gift.setGurl(PropertiesUtils.getValue("gift_path","exchangerate.properties")+rs.getString("gurl"));
				gift.setGprice(rs.getInt("gprice"));
				list.add(gift);
			}
			return list;
		} catch (SQLException e) {
			logger.error("查询全部礼物失败："+e);
			return null;
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
	}
	@Override
	public List<RechargeStatements> getRechargeStatementsByPage(int pageId, int pageSize, String uname) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = DBUtils.getConnection();
		String sql = "select * from recharge_statements where uname = ? limit ?,? ";
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, uname);
			ps.setInt(2, (pageId-1)*pageSize);
			ps.setInt(3, pageSize);
			rs = ps.executeQuery();
			List<RechargeStatements> list = new ArrayList<RechargeStatements>();
			while (rs.next()) {
				RechargeStatements rss = new RechargeStatements();
				rss.setId(rs.getInt("id"));
				rss.setUname(uname);
				rss.setRcount(rs.getInt("rcount"));
				rss.setRmb(rs.getInt("rmb"));
//				String strRdate = rs.getString("rdate");
//				System.out.println(strRdate);
//				rss.setRdate(strRdate.substring(0, strRdate.lastIndexOf(".")));
				rss.setRdate(rs.getString("rdate"));
				list.add(rss);
			}
			return list;
		} catch (SQLException e) {
			logger.error("分页查询充值记录失败："+e);
			return null;
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
	}
	@Override
	public Wallet getBalanceByName(String uname) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = DBUtils.getConnection();
		String sql = "select * from wallet where uname = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, uname);
			rs = ps.executeQuery();
			if (rs.next()) {
				Wallet wallet = new Wallet();
				wallet.setId(rs.getInt("id"));
				wallet.setUname(uname);
				wallet.setBalance(rs.getInt("balance"));
				return wallet;
			}
		} catch (SQLException e) {
			logger.error("根据用户获取余额失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}
	@Override
	public List<GiftCount> getGiftStatementsByAnchor(String anchor) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = DBUtils.getConnection();
		String sql = "SELECT giftId,count(giftId),sum(giftNum) FROM	gift_statements GROUP BY anchor,giftId HAVING anchor = ?";
		List<GiftCount> list = new ArrayList<>();
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, anchor);
			rs = ps.executeQuery();
			while (rs.next()) {
				GiftCount gc = new GiftCount();
				gc.setGiftId(rs.getInt(1));
				gc.setGcount(rs.getInt(2));
				gc.setSum(rs.getInt(3));
				list.add(gc);
			}
			ps.close();
			
			for(int i=0;i<list.size();i++){
				ps = conn.prepareStatement("select * from gift where id = ?");
				ps.setInt(1, list.get(i).getGiftId());
				ResultSet rs2 = ps.executeQuery();
				if(rs2.next()){
					list.get(i).setId(rs2.getInt("id"));
					list.get(i).setGname(rs2.getString("gname"));
					list.get(i).setGurl(rs2.getString("gurl"));
					list.get(i).setGprice(rs2.getInt("gprice"));
				}
				ps.close();
				rs2.close();
			}
			return list;
		} catch (SQLException e) {
			logger.error("根据用户获取余额失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}
	@Override
	public Gift getGiftById(Integer giftId) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = DBUtils.getConnection();
		String sql = "select * from gift where id = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, giftId);
			rs = ps.executeQuery();
			if (rs.next()) {
				Gift gift = new Gift();
				gift.setId(rs.getInt("id"));
				gift.setGname(rs.getString("gname"));
				gift.setGurl(rs.getString("gurl"));
				gift.setGprice(rs.getInt("gprice"));
				return gift;
			}
		} catch (SQLException e) {
			logger.error("根据ID查询礼物失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}
	@Override
	public boolean givingGifts(String uname, String anchor, Integer giftId, Integer giftNum,Integer balance,Integer giftPrice) {
		PreparedStatement ps = null;
		Connection conn = DBUtils.getConnection();
		String sql = "INSERT into gift_statements(uname,anchor,giftId,giftNum,gdate) values(?,?,?,?,?);";
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			ps.setString(1, uname);
			ps.setString(2, anchor);
			ps.setInt(3, giftId);
			ps.setInt(4, giftNum);
			ps.setString(5, System.currentTimeMillis()+"");
			ps.executeUpdate();
			ps.close();
			
			ps = conn.prepareStatement("update wallet set balance = ? where uname = ?");
			ps.setInt(1, balance-giftPrice*giftNum);
			ps.setString(2, uname);
			ps.executeUpdate();
			conn.commit();
			return true;
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			logger.error("根据ID查询礼物失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}
	@Override
	public boolean recharge(RechargeStatements rss,int balance) {
		PreparedStatement ps = null;
		Connection conn = DBUtils.getConnection();
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("update wallet set balance = ? where uname = ?");
			ps.setInt(1, rss.getRcount()+balance);
			ps.setString(2, rss.getUname());
			ps.executeUpdate();
			ps.close();
			
			ps = conn.prepareStatement("insert into recharge_statements(uname,rcount,rmb,rdate) values(?,?,?,?)");
			ps.setString(1, rss.getUname());
			ps.setInt(2, rss.getRcount());
			ps.setInt(3, rss.getRmb());
			ps.setString(4, rss.getRdate());
			ps.executeUpdate();
			ps.close();
			conn.commit();
			return true;
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			logger.error("充值失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}
	@Override
	public String getAuthorization() {
		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = DBUtils.getConnection();
		String sql = "select token from t_token";
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("token");
			}
		} catch (SQLException e) {
			logger.error("数据库中获取token失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}
	@Override
	public boolean updateAuthorization(String token) {
		PreparedStatement ps = null;
		Connection conn = DBUtils.getConnection();
		try {
			ps = conn.prepareStatement("update t_token set token = ?");
			ps.setString(1,token);
			return ps.executeUpdate()==1;
		} catch (SQLException e) {
			logger.error("更新token失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}
	@Override
	public List<GiftStatementsBean> getGivingGiftStatementsByPage(int pageId, int pageSize, String uname) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = DBUtils.getConnection();
		String sql = "select gs.id,uname,anchor,giftId,giftNum,gdate,gname,gurl,gprice "
				+ " from gift g,gift_statements gs where g.id = gs.giftId "
				+ " and uname = ? limit ?,?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, uname);
			ps.setInt(2, (pageId-1)*pageSize);
			ps.setInt(3, pageSize);
			rs = ps.executeQuery();
			List<GiftStatementsBean> list = new ArrayList<GiftStatementsBean>();
			while (rs.next()) {
				GiftStatementsBean gsb = new GiftStatementsBean();
				gsb.setId(rs.getInt("id"));
				gsb.setUname(uname);
				gsb.setAnchor(rs.getString("anchor"));
				gsb.setGiftid(rs.getInt("giftId"));
				gsb.setGiftnum(rs.getInt("giftNum"));
				gsb.setGdate(rs.getString("gdate"));
				gsb.setGname(rs.getString("gname"));
				gsb.setGurl(rs.getString("gurl"));
				gsb.setGprice(rs.getInt("gprice"));
				list.add(gsb);
			}
			return list;
		} catch (SQLException e) {
			logger.error("分页查询送礼记录失败："+e);
			return null;
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
	}
	@Override
	public List<GiftStatementsBean> getReceivingGiftStatementsByPage(int pageId, int pageSize, String anchor) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = DBUtils.getConnection();
		String sql = "select gs.id,uname,anchor,giftId,giftNum,gdate,gname,gurl,gprice "
				+ " from gift g,gift_statements gs where g.id = gs.giftId "
				+ " and anchor = ? limit ?,?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, anchor);
			ps.setInt(2, (pageId-1)*pageSize);
			ps.setInt(3, pageSize);
			rs = ps.executeQuery();
			List<GiftStatementsBean> list = new ArrayList<GiftStatementsBean>();
			while (rs.next()) {
				GiftStatementsBean gsb = new GiftStatementsBean();
				gsb.setId(rs.getInt("id"));
				gsb.setUname(rs.getString("uname"));
				gsb.setAnchor(rs.getString("anchor"));
				gsb.setGiftid(rs.getInt("giftId"));
				gsb.setGiftnum(rs.getInt("giftNum"));
				gsb.setGdate(rs.getString("gdate"));
				gsb.setGname(rs.getString("gname"));
				gsb.setGurl(rs.getString("gurl"));
				gsb.setGprice(rs.getInt("gprice"));
				list.add(gsb);
			}
			return list;
		} catch (SQLException e) {
			logger.error("分页查询收礼记录失败："+e);
			return null;
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
	}
}
