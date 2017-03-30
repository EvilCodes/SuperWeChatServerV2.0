package cn.ucai.superwechat.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import cn.ucai.superwechat.bean.GroupAvatar;
import cn.ucai.superwechat.bean.LocationUserAvatar;
import cn.ucai.superwechat.bean.MemberUserAvatar;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.pojo.Group;
import cn.ucai.superwechat.pojo.Location;
import cn.ucai.superwechat.pojo.Member;
import cn.ucai.superwechat.pojo.User;
import cn.ucai.superwechat.utils.DBUtils;
import cn.ucai.superwechat.utils.I;
import cn.ucai.superwechat.utils.Utils;

public class SuperWeChatDaoImpl implements ISuperWeChatDao {
	private Logger logger = Logger.getLogger(this.getClass());
	/**
	 * 根据用户名查找用户
	 */
	@Override
	public User findUserByUsername(String mUserName) {
		Connection conn = DBUtils.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select * from "+I.User.TABLE_NAME +" where "+I.User.USER_NAME+" = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, mUserName);
			rs = ps.executeQuery();
			if(rs.next()){
				User user = new User();
				user.setMUserName(rs.getString(I.User.USER_NAME));
				user.setMUserPassword(rs.getString(I.User.PASSWORD));
				user.setMUserNick(rs.getString(I.User.NICK));
				return user;
			}
		} catch (SQLException e) {
			logger.error("注册时查找用户头像信息失败："+e);
		} finally{
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}

	/**
	 * 插入数据到用户表
	 * 插入数据到头像表
	 * 事务操作!
	 */
	@Override
	public boolean addUserAndAvatar(User user,String suffix) {
		Connection conn = DBUtils.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			String sql = "insert into "+I.User.TABLE_NAME+" values (?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, user.getMUserName());
			ps.setString(2, user.getMUserPassword());
			ps.setString(3, user.getMUserNick());
			ps.executeUpdate();
			ps.close();
			// insert into avatar(username,path,type,time) values
			sql = "insert into "+I.Avatar.TABLE_NAME+"("
					+I.Avatar.USER_NAME+","+I.Avatar.AVATAR_PATH+","+I.Avatar.AVATAR_SUFFIX+","
					+I.Avatar.AVATAR_TYPE+","+I.Avatar.UPDATE_TIME+")"
					+ "values (?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, user.getMUserName());
			ps.setString(2, I.AVATAR_TYPE_USER_PATH);
			ps.setString(3, suffix);
			ps.setInt(4, I.AVATAR_TYPE_USER);
			ps.setString(5,System.currentTimeMillis()+"");
			ps.executeUpdate();
			ps.close();
			
			// 添加钱包信息
			ps = conn.prepareStatement("insert into wallet(uname,balance) values(?,?)");
			ps.setString(1, user.getMUserName());
			ps.setInt(2, 0);
			ps.executeUpdate();
			ps.close();
			conn.commit();
			return true;
		} catch (Exception e) {
			logger.error("注册用户失败："+e);
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally{
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}

	@Override
	public boolean updateNick(String username, String nick) {
		Connection conn = DBUtils.getConnection();
		PreparedStatement ps = null;
		try {
			String sql = "update "+ I.User.TABLE_NAME 
					+ " set "+I.User.NICK +" = ? where "
					+I.User.USER_NAME +" = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, nick);
			ps.setString(2, username);
			int count = ps.executeUpdate();
			return count==1;
		} catch (Exception e) {
			logger.error("更新昵称失败："+e);
		} finally{
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}

	@Override
	public UserAvatar getUserAvatarByUsername(String username) {
		Connection conn = DBUtils.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select * from "+I.User.TABLE_NAME+","+I.Avatar.TABLE_NAME
					+ " where "+I.User.USER_NAME +" = ?"
					+ " and " + I.User.USER_NAME +"="+I.Avatar.USER_NAME
					+ " and " + I.Avatar.AVATAR_TYPE + "=" + I.AVATAR_TYPE_USER;
			System.out.println("getUserAvatarByUsername:"+sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			rs = ps.executeQuery();
			if(rs.next()){
				UserAvatar ua = new UserAvatar();
				initUserAvatar(rs, ua);
				return ua;
			}
		} catch (Exception e) {
			logger.error("查找用户头像信息失败："+e);
		} finally{
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}

	@Override
	public boolean updatePassword(String username, String password) {
		Connection conn = DBUtils.getConnection();
		PreparedStatement ps = null;
		try {
			String sql = "update "+ I.User.TABLE_NAME 
					+ " set "+I.User.PASSWORD +" = ? where "
					+I.User.USER_NAME +" = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, password);
			ps.setString(2, username);
			int count = ps.executeUpdate();
			return count==1;
		} catch (Exception e) {
			logger.error("更新密码失败："+e);
		} finally{
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}

	@Override
	public boolean updateAvatar(String nameOrHxid, String avatarType,String suffix) {
		Connection conn = DBUtils.getConnection();
		PreparedStatement ps = null;
		try {
			String sql = "update "+ I.Avatar.TABLE_NAME 
					+ " set "+I.Avatar.UPDATE_TIME +" = ?,"+I.Avatar.AVATAR_SUFFIX+" = ?"+" where "
					+I.Avatar.USER_NAME +" = ? and "+I.Avatar.AVATAR_PATH + " = ? ";
			ps = conn.prepareStatement(sql);
			System.out.println("sql:"+sql);
			ps.setString(1, System.currentTimeMillis()+"");
			// 除了更新时间外，图片后缀名也需要更改，如将.png改为.jpg
			ps.setString(2, suffix);
			ps.setString(3, nameOrHxid);
			ps.setString(4, avatarType);
			int count = ps.executeUpdate();
			return count==1;
		} catch (Exception e) {
			logger.error("更新头像失败："+e);
		} finally{
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}

	@Override
	public List<UserAvatar> findContactPagesByUserName(String userName, String pageId, String pageSize) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = DBUtils.getConnection();
		String sql = "select * from " + I.Contact.TABLE_NAME + ","+ I.User.TABLE_NAME + ","+ I.Avatar.TABLE_NAME 
				+ " where "	+ I.Contact.USER_NAME + "=?" 
				+ " and " + I.User.USER_NAME + "=" + I.Contact.CU_NAME + " " 
				+ " and " + I.User.USER_NAME + "=" + I.Avatar.USER_NAME + " "
				+ " limit ?,?";
		System.out.println("connection=" + conn + ",sql=" + sql);
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, userName);
			Integer niPageId = Integer.parseInt(pageId);
			Integer niPageSize = Integer.parseInt(pageSize);
			// 0 5   (1-1)*5
			// 5 5   2 
			// 10 5  3
			ps.setInt(2, (niPageId-1)*niPageSize);
			ps.setInt(3, niPageSize);
			rs = ps.executeQuery();
			List<UserAvatar> listUserAvatar = new ArrayList<UserAvatar>();
			while (rs.next()) {
				UserAvatar ua = new UserAvatar();
				/*ua.setMUserName(rs.getString(I.User.USER_NAME));
				ua.setMUserNick(rs.getString(I.User.NICK));
				ua.setMAvatarId(rs.getInt(I.Avatar.AVATAR_ID));
				ua.setMAvatarType(rs.getInt(I.Avatar.AVATAR_TYPE));
				ua.setMAvatarPath(rs.getString(I.Avatar.AVATAR_PATH));
				ua.setMAvatarLastUpdateTime(rs.getString(I.Avatar.UPDATE_TIME));*/
				initUserAvatar(rs, ua);
				listUserAvatar.add(ua);
			}
			return listUserAvatar;
		} catch (SQLException e) {
			logger.error("分页查找用户好友失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}

	@Override
	public List<UserAvatar> findContactAllByUserName(String userName) {
		Connection conn = DBUtils.getConnection();
		String sql = "select * from " + I.Contact.TABLE_NAME + ","+ I.User.TABLE_NAME + ","+ I.Avatar.TABLE_NAME 
				+ " where "	+ I.Contact.USER_NAME + "=?" 
				+ " and " + I.User.USER_NAME + "=" + I.Contact.CU_NAME + " " 
				+ " and " + I.User.USER_NAME + "=" + I.Avatar.USER_NAME + " ";
		System.out.println("connection=" + conn + ",sql=" + sql);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, userName);
			rs = ps.executeQuery();
			List<UserAvatar> listUserAvatar = new ArrayList<UserAvatar>();
			while (rs.next()) {
				UserAvatar ua = new UserAvatar();
				initUserAvatar(rs, ua);
				listUserAvatar.add(ua);
			}
			return listUserAvatar;
		} catch (SQLException e) {
			logger.error("根据用户名查找全部好友失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}

	@Override
	public List<UserAvatar> findUsersForSearch(String userName, String userNick, String pageId, String pageSize) {
		Connection conn = DBUtils.getConnection();
		String sql = "select * from " + I.User.TABLE_NAME +","+ I.Avatar.TABLE_NAME + 
				" where " + I.User.USER_NAME + "=" + I.Avatar.USER_NAME;
		if(userName!=null){
			sql += " and "+I.User.USER_NAME +" like ?";
		}
		if(userNick!=null){
			sql += " and "+I.User.NICK +" like ?";
		}
		sql += "limit ?,?";
		System.out.println("connection=" + conn + ",sql=" + sql);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			if(userName!=null){
				ps.setString(1, "%"+userName+"%");
				if(userNick!=null){
					ps.setString(2, "%"+userNick+"%");
					ps.setInt(3, (Integer.parseInt(pageId)-1)*Integer.parseInt(pageSize));
					ps.setInt(4, Integer.parseInt(pageSize));
				}else{
					ps.setInt(2, (Integer.parseInt(pageId)-1)*Integer.parseInt(pageSize));
					ps.setInt(3, Integer.parseInt(pageSize));
				}
			}else{
				if(userNick!=null){
					ps.setString(1, "%"+userNick+"%");
					ps.setInt(2, (Integer.parseInt(pageId)-1)*Integer.parseInt(pageSize));
					ps.setInt(3, Integer.parseInt(pageSize));
				}else{
					ps.setInt(1, (Integer.parseInt(pageId)-1)*Integer.parseInt(pageSize));
					ps.setInt(2, Integer.parseInt(pageSize));
				}
			}
			rs = ps.executeQuery();
			List<UserAvatar> uaList = new ArrayList<UserAvatar>();
			while (rs.next()) {
				UserAvatar userAvatar = new UserAvatar();
				initUserAvatar(rs,userAvatar);
				uaList.add(userAvatar);
			}
			return uaList;
		} catch (SQLException e) {
			logger.error("根据用户名或昵称模糊查询用户信息失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}

	private void initUserAvatar(ResultSet rs,UserAvatar userAvatar) throws SQLException {
		userAvatar.setMUserName(rs.getString(I.User.USER_NAME));
		userAvatar.setMUserNick(rs.getString(I.User.NICK));
		userAvatar.setMAvatarId(rs.getInt(I.Avatar.AVATAR_ID));
		userAvatar.setMAvatarPath(rs.getString(I.Avatar.AVATAR_PATH));
		userAvatar.setMAvatarSuffix(rs.getString(I.Avatar.AVATAR_SUFFIX));
		userAvatar.setMAvatarType(rs.getInt(I.Avatar.AVATAR_TYPE));
		userAvatar.setMAvatarLastUpdateTime(rs.getString(I.Avatar.UPDATE_TIME));
	}
	
	private void initMemberUserAvatar(ResultSet rs, MemberUserAvatar memberUserAvatar) throws SQLException {
		initUserAvatar(rs, memberUserAvatar);
		memberUserAvatar.setMMemberId(rs.getInt(I.Member.MEMBER_ID));
		memberUserAvatar.setMMemberGroupId(rs.getInt(I.Member.GROUP_ID));
		memberUserAvatar.setMMemberGroupHxid(rs.getString(I.Member.GROUP_HX_ID));
		memberUserAvatar.setMMemberPermission(rs.getInt(I.Member.PERMISSION));
	}

	/**
	 * 根据群组id，下载群组成员
	 */
	@Override
	public List<MemberUserAvatar> downloadGroupMembersByGroupId(String groupId) {
		List<MemberUserAvatar> list = new ArrayList<MemberUserAvatar>();
		Connection conn = DBUtils.getConnection();
		String sql = "select * from " + I.Member.TABLE_NAME +","+ I.Avatar.TABLE_NAME +","+ I.User.TABLE_NAME +  
				" where " + I.Member.GROUP_ID + "=? "
				+ " and " + I.Member.USER_NAME + "=" + I.User.USER_NAME
				+ " and " + I.Avatar.USER_NAME + "=" + I.User.USER_NAME;
		System.out.println("sql:"+sql);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, groupId);
			rs = ps.executeQuery();
			while (rs.next()) {
				MemberUserAvatar memberUserAvatar = new MemberUserAvatar();
				initMemberUserAvatar(rs,memberUserAvatar);
				list.add(memberUserAvatar);
			}
			return list;
		} catch (SQLException e) {
			logger.error("根据群组ID下载群组成员失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}
	/**
	 * 根据群组id，分页下载群组成员
	 */
	@Override
	public List<MemberUserAvatar> downloadGroupMembersPagesByGroupId(String groupId, String pageId, String pageSize) {
		List<MemberUserAvatar> list = new ArrayList<MemberUserAvatar>();
		Connection conn = DBUtils.getConnection();
		String sql = "select * from " + I.Member.TABLE_NAME +","+ I.Avatar.TABLE_NAME +","+ I.User.TABLE_NAME +  
				" where " + I.Member.GROUP_ID + "=? "
				+ " and " + I.Member.USER_NAME + "=" + I.User.USER_NAME
				+ " and " + I.Avatar.USER_NAME + "=" + I.User.USER_NAME
				+ " limit ?,?";
		System.out.println("sql:"+sql);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, groupId);
			Integer niPageId = Integer.parseInt(pageId);
			Integer niPageSize = Integer.parseInt(pageSize);
			ps.setInt(2, (niPageId-1)*niPageSize);
			ps.setInt(3, niPageSize);
			rs = ps.executeQuery();
			while (rs.next()) {
				MemberUserAvatar memberUserAvatar = new MemberUserAvatar();
				initMemberUserAvatar(rs,memberUserAvatar);
				list.add(memberUserAvatar);
			}
			return list;
		} catch (SQLException e) {
			logger.error("根据群组ID分页下载群组成员失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}

	/**
	 * 根据环信id，下载群组成员
	 */
	@Override
	public List<MemberUserAvatar> downloadGroupMembersByHxId(String hxId) {
		List<MemberUserAvatar> list = new ArrayList<MemberUserAvatar>();
		Connection conn = DBUtils.getConnection();
		String sql = "select * from " + I.Member.TABLE_NAME +","+ I.Avatar.TABLE_NAME +","+ I.User.TABLE_NAME +  
				" where " + I.Member.GROUP_HX_ID + "=? "
				+ " and " + I.Member.USER_NAME + "=" + I.User.USER_NAME
				+ " and " + I.Avatar.USER_NAME + "=" + I.User.USER_NAME;
		System.out.println("sql:"+sql);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, hxId);
			rs = ps.executeQuery();
			while (rs.next()) {
				MemberUserAvatar memberUserAvatar = new MemberUserAvatar();
				initMemberUserAvatar(rs,memberUserAvatar);
				list.add(memberUserAvatar);
			}
			return list;
		} catch (SQLException e) {
			logger.error("根据环信ID下载群组成员失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}
	/**
	 * 根据环信id，下载群组成员，如果有pageId和pageSize，则分页下载
	 */
	@Override
	public List<MemberUserAvatar> downloadGroupMembersPagesByHxId(String hxId, String pageId, String pageSize) {
		List<MemberUserAvatar> list = new ArrayList<MemberUserAvatar>();
		Connection conn = DBUtils.getConnection();
		String sql = "select * from " + I.Member.TABLE_NAME +","+ I.Avatar.TABLE_NAME +","+ I.User.TABLE_NAME +  
				" where " + I.Member.GROUP_HX_ID + "=? "
				+ " and " + I.Member.USER_NAME + "=" + I.User.USER_NAME
				+ " and " + I.Avatar.USER_NAME + "=" + I.User.USER_NAME
				+ " limit ?,?";
		System.out.println("sql:"+sql);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, hxId);
			Integer niPageId = Integer.parseInt(pageId);
			Integer niPageSize = Integer.parseInt(pageSize);
			ps.setInt(2, (niPageId-1)*niPageSize);
			ps.setInt(3, niPageSize);
			rs = ps.executeQuery();
			while (rs.next()) {
				MemberUserAvatar memberUserAvatar = new MemberUserAvatar();
				initMemberUserAvatar(rs,memberUserAvatar);
				list.add(memberUserAvatar);
			}
			return list;
		} catch (SQLException e) {
			logger.error("根据环信ID分页下载群组成员失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}

	@Override
	public boolean deleteUser(String userName) {
		Connection conn = DBUtils.getConnection();
		PreparedStatement ps = null;
		try {
			// 关闭事务的自动提交
			conn.setAutoCommit(false);
			String sql = "delete from " + I.User.TABLE_NAME + " where " + I.User.USER_NAME + "=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, userName);
			int countUser = ps.executeUpdate();
			ps.close();
			
			sql = "delete from "+ I.Avatar.TABLE_NAME + " where " + I.Avatar.USER_NAME + "=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, userName);
			int countAvatar = ps.executeUpdate();
			// 提交事务
			if(countUser > 0 && countAvatar > 0){
				conn.commit();
				return true;
			}
		} catch (SQLException e) {
			logger.error("删除用户失败："+e);
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}

	@Override
	public Group findGroupByHxid(String mGroupHxid) {
		Connection conn = DBUtils.getConnection();
		String sql = "select * from " + I.Group.TABLE_NAME
				+ " where " + I.Group.HX_ID + "=?";
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, mGroupHxid);
			rs = ps.executeQuery();
			if (rs.next()) {
				Group group = new Group();
				initGroup(rs, group);
				return group;
			}
		} catch (SQLException e) {
			logger.error("根据环信ID查找群组失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}
	
	private void initGroup(ResultSet rs, Group group) throws SQLException {
		group.setMGroupId(rs.getInt(I.Group.GROUP_ID));
		group.setMGroupHxid(rs.getString(I.Group.HX_ID));
		group.setMGroupName(rs.getString(I.Group.NAME));
		group.setMGroupDescription(rs.getString(I.Group.DESCRIPTION));
		group.setMGroupOwner(rs.getString(I.Group.OWNER));
		group.setMGroupLastModifiedTime(rs.getString(I.Group.MODIFIED_TIME));
		group.setMGroupMaxUsers(rs.getInt(I.Group.MAX_USERS));
		group.setMGroupAffiliationsCount(rs.getInt(I.Group.AFFILIATIONS_COUNT));
		group.setMGroupIsPublic(Utils.int2boolean(rs.getInt(I.Group.IS_PUBLIC)));
		group.setMGroupAllowInvites(Utils.int2boolean(rs.getInt(I.Group.ALLOW_INVITES)));
	}

	@Override
	public boolean addGroupAndGroupOwnerMember(Group group,String suffix) {
		Connection conn = DBUtils.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// 关闭事务的自动提交
			conn.setAutoCommit(false);
			String sql = "insert into " + I.Group.TABLE_NAME + "(" 
					+ I.Group.HX_ID + "," + I.Group.NAME + ","
					+ I.Group.DESCRIPTION + "," + I.Group.OWNER + "," 
					+ I.Group.MODIFIED_TIME + "," + I.Group.MAX_USERS+ "," 
					+ I.Group.AFFILIATIONS_COUNT + "," + I.Group.IS_PUBLIC + "," 
					+ I.Group.ALLOW_INVITES + ")values(?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, group.getMGroupHxid());
			ps.setString(2, group.getMGroupName());
			ps.setString(3, group.getMGroupDescription());
			ps.setString(4, group.getMGroupOwner());
			ps.setString(5, group.getMGroupLastModifiedTime());
			ps.setInt(6, group.getMGroupMaxUsers());
			ps.setInt(7, group.getMGroupAffiliationsCount());
			ps.setInt(8, Utils.boolean2int(group.getMGroupIsPublic()));
			ps.setInt(9, Utils.boolean2int(group.getMGroupAllowInvites()));
			ps.executeUpdate();
			
			int gourpId = -1;
			rs = ps.getGeneratedKeys();
			if (rs != null && rs.next()) {
				// 得到新插入数据的id
				int id = rs.getInt(1);
				System.out.println("dao.createGroup,id=" + id);
				System.out.println("dao.createGroup,group=" + group);
				gourpId =  id;
			}
			ps.close();
			
			sql = "insert into " + I.Avatar.TABLE_NAME + "(" + I.Avatar.USER_NAME + "," + I.Avatar.AVATAR_PATH + ","
					+I.Avatar.AVATAR_SUFFIX+","+ I.Avatar.AVATAR_TYPE + ","+I.Avatar.UPDATE_TIME+")values(?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, group.getMGroupHxid());
			ps.setString(2, I.AVATAR_TYPE_GROUP_PATH);
			ps.setString(3, suffix);
			ps.setInt(4, I.AVATAR_TYPE_GROUP);
			ps.setString(5,System.currentTimeMillis()+"");
			ps.executeUpdate();
			ps.close();
			
			sql = "insert into " + I.Member.TABLE_NAME + 
					"(" + I.Member.USER_NAME + "," + I.Member.GROUP_ID + ","
					+ I.Member.GROUP_HX_ID + ","+I.Member.PERMISSION+")values(?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, group.getMGroupOwner());
			ps.setInt(2, gourpId);
			ps.setString(3, group.getMGroupHxid());
			ps.setInt(4,I.PERMISSION_OWNER);
			ps.executeUpdate();
			// 提交事务
			conn.commit();
			return true;
		} catch (SQLException e) {
			logger.error("创建群失败："+e);
			// 回滚事务
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return false;
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
	}

	@Override
	public GroupAvatar findGroupAvatarByHxId(String hxId) {
		Connection conn = DBUtils.getConnection();
		String sql = "select * from " + I.Group.TABLE_NAME +","+ I.Avatar.TABLE_NAME + 
				" where " + I.Group.HX_ID + "=? "
				+ " and " + I.Group.HX_ID + "=" + I.Avatar.USER_NAME
				+ " and "+I.Avatar.AVATAR_TYPE + " = 1";
		System.out.println("connection=" + conn + ",sql=" + sql);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, hxId);
			rs = ps.executeQuery();
			if (rs.next()) {
				GroupAvatar groupAvatar = new GroupAvatar();
				initGroupAvatar(rs,groupAvatar);
				return groupAvatar;
			}
		} catch (SQLException e) {
			logger.error("根据环信ID查找群组及群组头像信息失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}
	
	private void initGroupAvatar(ResultSet rs, GroupAvatar groupAvatar) throws SQLException {
		groupAvatar.setMGroupId(rs.getInt(I.Group.GROUP_ID));
		groupAvatar.setMGroupHxid(rs.getString(I.Group.HX_ID));
		groupAvatar.setMGroupName(rs.getString(I.Group.NAME));
		groupAvatar.setMGroupDescription(rs.getString(I.Group.DESCRIPTION));
		groupAvatar.setMGroupOwner(rs.getString(I.Group.OWNER));
		groupAvatar.setMGroupLastModifiedTime(rs.getString(I.Group.MODIFIED_TIME));
		groupAvatar.setMGroupMaxUsers(rs.getInt(I.Group.MAX_USERS));
		groupAvatar.setMGroupAffiliationsCount(rs.getInt(I.Group.AFFILIATIONS_COUNT));
		groupAvatar.setMGroupIsPublic(Utils.int2boolean(rs.getInt(I.Group.IS_PUBLIC)));
		groupAvatar.setMGroupAllowInvites(Utils.int2boolean(rs.getInt(I.Group.ALLOW_INVITES)));
		groupAvatar.setMAvatarId(rs.getInt(I.Avatar.AVATAR_ID));
		groupAvatar.setMAvatarUserName(rs.getString(I.Avatar.USER_NAME));
		groupAvatar.setMAvatarPath(rs.getString(I.Avatar.AVATAR_PATH));
		groupAvatar.setMAvatarSuffix(rs.getString(I.Avatar.AVATAR_SUFFIX));
		groupAvatar.setMAvatarType(rs.getInt(I.Avatar.AVATAR_TYPE));
		groupAvatar.setMAvatarLastUpdateTime(rs.getString(I.Avatar.UPDATE_TIME));
	}
	
	@Override
	public GroupAvatar findGroupAvatarByGroupId(String groupId) {
		Connection conn = DBUtils.getConnection();
		String sql = "select * from " + I.Group.TABLE_NAME +","+ I.Avatar.TABLE_NAME + 
				" where " + I.Group.GROUP_ID + "=? "
				+ " and " + I.Group.HX_ID + "=" + I.Avatar.USER_NAME;
		System.out.println("connection=" + conn + ",sql=" + sql);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, groupId);
			rs = ps.executeQuery();
			if (rs.next()) {
				GroupAvatar groupAvatar = new GroupAvatar();
				initGroupAvatar(rs,groupAvatar);
				return groupAvatar;
			}
		} catch (SQLException e) {
			logger.error("根据群组ID查找群组及头像信息失败"+e);
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}
	
	/**
	 * 添加群组成员信息
	 */
	@Override
	public boolean addGroupMemberAndUpdateGroupAffiliationsCount(String userName,GroupAvatar groupAvatar) {
		Connection conn = DBUtils.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			Member member = new Member(userName,groupAvatar.getMGroupId(),groupAvatar.getMGroupHxid(),I.PERMISSION_NORMAL);
			String sql = "insert into " + I.Member.TABLE_NAME + "(" 
					+ I.Member.USER_NAME + "," + I.Member.GROUP_ID + "," 
					+ I.Member.GROUP_HX_ID + "," + I.Member.PERMISSION 
					+ ")values(?,?,?,?)";
			System.out.println("connection=" + conn + ",sql=" + sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, member.getMMemberUserName());
			ps.setInt(2, member.getMMemberGroupId());
			ps.setString(3, member.getMMemberGroupHxid());
			ps.setInt(4, member.getMMemberPermission());
			int count1 = ps.executeUpdate();
			ps.close();
			sql = "update " + I.Group.TABLE_NAME + " set " + I.Group.AFFILIATIONS_COUNT + "=?,"
					+ I.Group.MODIFIED_TIME + "=?" + " where " + I.Group.GROUP_ID + "=?";
			System.out.println("sql"+sql);
			ps = conn.prepareStatement(sql);
			// 1、实体类同步更新
			groupAvatar.setMGroupAffiliationsCount(groupAvatar.getMGroupAffiliationsCount()+1);
			ps.setInt(1, groupAvatar.getMGroupAffiliationsCount());
			groupAvatar.setMAvatarLastUpdateTime(System.currentTimeMillis()+"");
			ps.setString(2, groupAvatar.getMAvatarLastUpdateTime());
			ps.setInt(3, groupAvatar.getMGroupId());
			int count2 = ps.executeUpdate();
			conn.commit();
			return count1 > 0 && count2>0;
		} catch (SQLException e) {
			logger.error("添加群成员失败："+e);
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}
	
	@Override
	public boolean addGroupMembersAndUpdateGroupAffiliationsCount(String userNameArr,GroupAvatar groupAvatar) {
		Connection conn = DBUtils.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			String[] userNames = userNameArr.split(",");
			Member[] memberArr = new Member[userNames.length];
			for(int i=0;i<userNames.length;i++){
				memberArr[i] = new Member(userNames[i],groupAvatar.getMGroupId(),groupAvatar.getMGroupHxid(),I.PERMISSION_NORMAL);
			}
			String sql = "insert into " + I.Member.TABLE_NAME + "(" 
					+ I.Member.USER_NAME + "," + I.Member.GROUP_ID + "," 
					+ I.Member.GROUP_HX_ID + "," + I.Member.PERMISSION 
					+ ")values(?,?,?,?)";
			System.out.println("connection=" + conn + ",sql=" + sql);
			ps = conn.prepareStatement(sql);
			for(int i=0;i<memberArr.length;i++){
				ps.setString(1, memberArr[i].getMMemberUserName());
				ps.setInt(2, memberArr[i].getMMemberGroupId());
				ps.setString(3, memberArr[i].getMMemberGroupHxid());
				ps.setInt(4, memberArr[i].getMMemberPermission());
				ps.addBatch();
			}
			
			int[] countArr = ps.executeBatch();
			System.out.println(Arrays.toString(countArr));
			System.out.println(countArr.length);
			ps.close();
			sql = "update " + I.Group.TABLE_NAME + " set " + I.Group.AFFILIATIONS_COUNT + "=?,"
					+ I.Group.MODIFIED_TIME + "=?" + " where " + I.Group.GROUP_ID + "=?";
			System.out.println("sql"+sql);
			ps = conn.prepareStatement(sql);
			groupAvatar.setMGroupAffiliationsCount(groupAvatar.getMGroupAffiliationsCount()+memberArr.length);
			ps.setInt(1, groupAvatar.getMGroupAffiliationsCount());
			groupAvatar.setMAvatarLastUpdateTime(System.currentTimeMillis()+"");
			ps.setString(2, groupAvatar.getMAvatarLastUpdateTime());
			ps.setInt(3, groupAvatar.getMGroupId());
			int count2 = ps.executeUpdate();
			conn.commit();
			return countArr.length > 0 && count2>0;
		} catch (SQLException e) {
			logger.error("批量添加群成员失败："+e);
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}
	
	/**
	 * 删除指定群成员
	 */
	@Override
	public boolean delGroupMemberAndUpdateGroupAffiliationsCount(String userName, GroupAvatar groupAvatar) {
		Connection conn = DBUtils.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			String sql = "delete from " + I.Member.TABLE_NAME + " where " + I.Member.USER_NAME + "=?" + " and "
					+ I.Member.GROUP_ID + " =?";
			System.out.println("connection=" + conn + ",sql=" + sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, userName);
			ps.setInt(2, groupAvatar.getMGroupId());
			int count1 = ps.executeUpdate();
			ps.close();
			
			sql = "update " + I.Group.TABLE_NAME + " set " + I.Group.AFFILIATIONS_COUNT + "=?,"
					+ I.Group.MODIFIED_TIME + "=?" + " where " + I.Group.GROUP_ID + "=?";
			System.out.println("sql:"+sql);
			ps = conn.prepareStatement(sql);
			ps.setInt(1, groupAvatar.getMGroupAffiliationsCount()-1);
			ps.setString(2, System.currentTimeMillis()+"");
			ps.setInt(3, groupAvatar.getMGroupId());
			int count2 = ps.executeUpdate();
			conn.commit();
			return count1 > 0 && count2 > 0;
		} catch (SQLException e) {
			logger.error("删除指定群成员失败："+e);
			try {
				conn.rollback();
				return false;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}

	@Override
	public boolean delGroupMembersAndUpdateGroupAffiliationsCount(String userNames, GroupAvatar groupAvatar) {
		Connection conn = DBUtils.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			String sql = "delete from " + I.Member.TABLE_NAME + " where " + I.Member.USER_NAME + " in ("+"?) " + " and "
					+ I.Member.GROUP_ID + " =?";
			System.out.println("connection=" + conn + ",sql=" + sql);
			ps = conn.prepareStatement(sql);
			String[] userNameArr = userNames.split(",");
			for(String userName:userNameArr){
				ps.setString(1, userName);
				ps.setInt(2, groupAvatar.getMGroupId());
				ps.addBatch();
			}
			int[] count1 = ps.executeBatch();
			ps.close();
			sql = "update " + I.Group.TABLE_NAME + " set " + I.Group.AFFILIATIONS_COUNT + "=?,"
					+ I.Group.MODIFIED_TIME + "=?" + " where " + I.Group.GROUP_ID + "=?";
			System.out.println("sql"+sql);
			ps = conn.prepareStatement(sql);
			ps.setInt(1, groupAvatar.getMGroupAffiliationsCount()-userNames.split(",").length);
			ps.setString(2, System.currentTimeMillis()+"");
			ps.setInt(3, groupAvatar.getMGroupId());
			int count2 = ps.executeUpdate();
			conn.commit();
			return count1.length > 0 && count2 > 0;
		} catch (SQLException e) {
			logger.error("批量删除群成员失败"+e);
			try {
				conn.rollback();
				return false;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}

	/**
	 * 注意三者的删除顺序问题
	 * @param groupId
	 * @return
	 */
	@Override
	public boolean deleteGroupAndMembers(String groupId) {
		Connection conn = DBUtils.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			// 删除群成员
			String sql = "delete from " + I.Member.TABLE_NAME + " where " + I.Member.GROUP_ID + "=?";
			System.out.println("connection=" + conn + ",sql=" + sql);
			ps = conn.prepareStatement(sql);
			ps.setInt(1, Integer.parseInt(groupId));
			ps.executeUpdate();
			ps.close();
			// 删除群组头像
			sql = "delete from " + I.Avatar.TABLE_NAME + " where " + I.Avatar.USER_NAME 
					+ "=(select "+I.Group.HX_ID+" from "+I.Group.TABLE_NAME+" where "+I.Group.GROUP_ID+"=?)";
			System.out.println("connection=" + conn + ",sql=" + sql);
			ps = conn.prepareStatement(sql);
			ps.setInt(1, Integer.parseInt(groupId));
			ps.executeUpdate();
			ps.close();
			// 删除群组
			sql = "delete from " + I.Group.TABLE_NAME + " where " + I.Group.GROUP_ID + "=?";
			System.out.println("connection=" + conn + ",sql=" + sql);
			ps = conn.prepareStatement(sql);
			ps.setInt(1, Integer.parseInt(groupId));
			ps.executeUpdate();
			conn.commit();
			return true;
		} catch (SQLException e) {
			logger.error("解散群组失败："+e);
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}
	
	/**
	 * 查找cname是否是name的好友
	 */
	@Override
	public boolean findContact(String name, String cname) {
		Connection conn = DBUtils.getConnection();
		String sql = "select * from " + I.Contact.TABLE_NAME
				+ " where "	+ I.Contact.USER_NAME + "=?" 
				+ " and " + I.Contact.CU_NAME + "=?";
		System.out.println("connection=" + conn + ",sql=" + sql);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, cname);
			rs = ps.executeQuery();
			if(rs.next()){
				return true;
			}
		} catch (SQLException e) {
			logger.error("查找好友关系失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return false;
	}

	/**
	 * 创建好友关系
	 */
	@Override
	public boolean addContact(String name, String cname) {
		Connection conn = DBUtils.getConnection();
		PreparedStatement ps = null;
		try {
			String sql = "insert into " + I.Contact.TABLE_NAME 
					+ "(" + I.Contact.USER_NAME + "," + I.Contact.CU_NAME + ")values(?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, cname);
			int count = ps.executeUpdate();
			return count>0;
		} catch (SQLException e) {
			logger.error("添加好友关系失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}

	/**
	 * 删除好友关系
	 */
	@Override
	public boolean delContact(String name, String cname) {
		Connection conn = DBUtils.getConnection();
		PreparedStatement ps = null;
		try {
			String sql = "delete from " + I.Contact.TABLE_NAME + " where "
					+ " (" + I.Contact.USER_NAME + "=?" + "and "+I.Contact.CU_NAME + "=? ) or "
					+ " (" + I.Contact.USER_NAME + "=?" + "and "+I.Contact.CU_NAME + "=?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, cname);
			ps.setString(3, cname);
			ps.setString(4, name);
			int count = ps.executeUpdate();
			return count==2;
		} catch (SQLException e) {
			logger.error("删除好友关系失败："+e);
			return false;
		} finally {
			DBUtils.closeAll(conn, ps, null);
		}
	}
	
	@Override
	public GroupAvatar findPublicGroupAvatarByHxId(String hxId) {
		Connection conn = DBUtils.getConnection();
		String sql = "select * from " + I.Group.TABLE_NAME +","+ I.Avatar.TABLE_NAME + 
				" where " + I.Group.HX_ID + "=? "
				+ " and " + I.Group.HX_ID + "=" + I.Avatar.USER_NAME
				+ " and " + I.Group.IS_PUBLIC + "=" + I.GROUP_PUBLIC;
		System.out.println("connection=" + conn + ",sql=" + sql);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, hxId);
			rs = ps.executeQuery();
			if (rs.next()) {
				GroupAvatar groupAvatar = new GroupAvatar();
				initGroupAvatar(rs,groupAvatar);
				return groupAvatar;
			}
		} catch (SQLException e) {
			logger.error("根据环信ID查找公开群组及头像信息失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}
	/**
	 * 查找某一指定用户所在的所有群
	 */
	@Override
	public List<GroupAvatar> findAllGroupByUserName(String userName) {
		Connection conn = DBUtils.getConnection();
		String sql = "select * from " + I.Member.TABLE_NAME + ","+ I.Group.TABLE_NAME + ","+ I.Avatar.TABLE_NAME + " where "
				+ I.Member.USER_NAME + "=?" + 
				" and " + I.Member.GROUP_ID + "=" + I.Group.GROUP_ID 
				+ " and " + I.Avatar.AVATAR_TYPE + "=1 "
				+ " and " + I.Group.HX_ID + "=" + I.Avatar.USER_NAME + " ";
		System.out.println("connection=" + conn + ",sql=" + sql);
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<GroupAvatar> listGroupAvatar = new ArrayList<GroupAvatar>();
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, userName);
			rs = ps.executeQuery();
			while (rs.next()) {
				GroupAvatar ga = new GroupAvatar();
				initGroupAvatar(rs, ga);
				listGroupAvatar.add(ga);
			}
			return listGroupAvatar;
		} catch (SQLException e) {
			logger.error("查找某一指定用户所在的所有群失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}

	/**
	 * 查找所有的公开群，不包括当前用户已经所在的群
	 */
	@Override
	public List<GroupAvatar> findPublicGroups(String userName, int pageId, int pageSize) {
		List<GroupAvatar> listGroupAvatar = new ArrayList<GroupAvatar>();
		Connection conn = DBUtils.getConnection();
		String sql = "select * from " + I.Group.TABLE_NAME + ","+ I.Avatar.TABLE_NAME 
				+ " where " + I.Group.IS_PUBLIC + "=?"
				+ " and " + I.Group.HX_ID + "=" + I.Avatar.USER_NAME
				+ " and " + I.Avatar.AVATAR_TYPE + "=1 "
				+ " and " + I.Group.GROUP_ID + " not in ("
				+ "select distinct " + I.Member.GROUP_ID + " from " + I.Member.TABLE_NAME + " where " + I.Member.USER_NAME + "=?"
				+ ") limit ?,?";
		System.out.println("connection=" + conn + ",sql=" + sql);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, I.GROUP_PUBLIC);
			ps.setString(2, userName);
			ps.setInt(3, (pageId-1)*pageSize);
			ps.setInt(4, pageSize);
			rs = ps.executeQuery();
			while (rs.next()) {
				GroupAvatar groupAvatar = new GroupAvatar();
				initGroupAvatar(rs, groupAvatar);
				listGroupAvatar.add(groupAvatar);
			}
			return listGroupAvatar;
		} catch (SQLException e) {
			logger.error("查找所有的公开群，不包括当前用户已经所在的群失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}

	@Override
	public List<GroupAvatar> findGroupByGroupName(String groupName) {
		List<GroupAvatar> listGroupAvatar = new ArrayList<GroupAvatar>();
		Connection conn = DBUtils.getConnection();
		String sql = "select * from " + I.Group.TABLE_NAME + ","+ I.Avatar.TABLE_NAME 
				+ " where " + I.Group.NAME + " like ?"
				+ " and " + I.Group.HX_ID + "=" + I.Avatar.USER_NAME
				+ " and " + I.Avatar.AVATAR_TYPE + "=1 ";
		System.out.println("connection=" + conn + ",sql=" + sql);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, "%"+groupName+"%");
			rs = ps.executeQuery();
			while (rs.next()) {
				GroupAvatar groupAvatar = new GroupAvatar();
				initGroupAvatar(rs, groupAvatar);
				listGroupAvatar.add(groupAvatar);
			}
			return listGroupAvatar;
		} catch (SQLException e) {
			logger.error("根据群组名称模糊查询群组信息失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}

	@Override
	public boolean uploadUserLocation(Location location) {
		Connection conn = DBUtils.getConnection();
		String sql = "insert into " + I.Location.TABLE_NAME + 
				"(" + I.Location.USER_NAME + "," + I.Location.LATITUDE + "," + I.Location.LONGITUDE + "," + I.Location.IS_SEARCHED + ","
				+ I.Location.UPDATE_TIME + ")values(?,?,?,?,?)";
		System.out.println("connection=" + conn + ",sql=" + sql);
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, location.getMLocationUserName());
			ps.setDouble(2, location.getMLocationLatitude());
			ps.setDouble(3, location.getMLocationLongitude());
			ps.setInt(4, Utils.boolean2int(location.getMLocationIsSearched()));
			ps.setString(5, location.getMLocationLastUpdateTime());
			int count = ps.executeUpdate();
			if(count>0){
				return true;
			}
		} catch (SQLException e) {
			logger.error("添加用户位置失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}

	@Override
	public boolean updateUserLocation(Location location) {
		Connection conn = DBUtils.getConnection();
		String sql = "update " + I.Location.TABLE_NAME + " set " + I.Location.LATITUDE + "=?," + I.Location.LONGITUDE
				+ "=?," + I.Location.IS_SEARCHED + "=?," + I.Location.UPDATE_TIME + "=?" + " where "
				+ I.Location.USER_NAME + "=?";
		System.out.println("connection=" + conn + ",sql=" + sql);
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setDouble(1, location.getMLocationLatitude());
			ps.setDouble(2, location.getMLocationLongitude());
			ps.setInt(3, Utils.boolean2int(location.getMLocationIsSearched()));
			ps.setString(4, location.getMLocationLastUpdateTime());
			ps.setString(5, location.getMLocationUserName());
			int count = ps.executeUpdate();
			return count == 1;
		} catch (SQLException e) {
			logger.error("更新用户位置失败："+e);
		} finally {
			DBUtils.closeAll(conn,ps,null);
		}
		return false;
	}

	public Location getLocationByUserName(String userName){
		Connection conn = DBUtils.getConnection();
		String sql = "select * from " + I.Location.TABLE_NAME+" where "+I.Location.USER_NAME + "=?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		Location location = new Location();
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, userName);
			rs = ps.executeQuery();
			if(rs.next()){
				location.setMLocationLatitude(rs.getDouble(I.Location.LATITUDE));
				location.setMLocationLongitude(rs.getDouble(I.Location.LONGITUDE));
			}
			return location;
		} catch (SQLException e) {
			logger.error("获取用户位置失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}
	
	@Override
	public List<LocationUserAvatar> downloadLocation(String userName, String pageId, String pageSize) {
		Connection conn = DBUtils.getConnection();
		Location location = getLocationByUserName(userName);
		String sql = "SELECT *,LEFT ((2 * ASIN (SQRT (POW (SIN ((RADIANS ("+location.getMLocationLatitude()+") - RADIANS ("+I.Location.LATITUDE+")) / 2),"
						+"2) + COS (RADIANS("+location.getMLocationLatitude()+")) * COS (RADIANS("+I.Location.LATITUDE+")) * POW ("
						+"SIN ((RADIANS ("+location.getMLocationLongitude()+") - RADIANS ("+I.Location.LONGITUDE+")) / 2),2))) * 6378.137),4)"
						+ " AS distance"
						+ " FROM "+I.User.TABLE_NAME+","+I.Avatar.TABLE_NAME+","+I.Location.TABLE_NAME
						+" where "+I.User.USER_NAME+" != ? and "+I.User.USER_NAME+" = "+I.Location.USER_NAME
						+" and "+I.User.USER_NAME +" = " +I.Avatar.USER_NAME +" and "+ I.Avatar.AVATAR_TYPE + "= 0"
						+" HAVING distance <= "+I.DEFAULT_DISTANCE+" ORDER BY distance ASC limit ?,?";
		System.out.println("connection=" + conn + ",sql=" + sql);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, userName);
			Integer niPageId = Integer.parseInt(pageId);
			Integer niPageSize = Integer.parseInt(pageSize);
			ps.setInt(2, (niPageId-1)*niPageSize);
			ps.setInt(3, niPageSize);
			rs = ps.executeQuery();
			List<LocationUserAvatar> listLocationUserAvatar = new ArrayList<LocationUserAvatar>();
			while (rs.next()) {
				LocationUserAvatar lua = new LocationUserAvatar();
				initUserAvatar(rs,lua);
				initLocation(rs,lua);
				lua.setDistance(rs.getDouble("distance"));
				listLocationUserAvatar.add(lua);
			}
			return listLocationUserAvatar;
		} catch (SQLException e) {
			logger.error("下载附近的人失败："+e);
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return null;
	}
	private void initLocation(ResultSet rs,LocationUserAvatar locationUserAvatar) throws SQLException {
		locationUserAvatar.setMLocationId(rs.getInt(I.Location.LOCATION_ID));
		locationUserAvatar.setMLocationLatitude(rs.getDouble(I.Location.LATITUDE));
		locationUserAvatar.setMLocationLongitude(rs.getDouble(I.Location.LONGITUDE));
		locationUserAvatar.setMLocationIsSearched(Utils.int2boolean(rs.getInt(I.Location.IS_SEARCHED)));
		locationUserAvatar.setMLocationLastUpdateTime(rs.getString(I.Location.UPDATE_TIME));
	}
	
	@Override
	public boolean updateGroupNameByGroupId(String groupId, String groupNewName) {
		Connection conn = DBUtils.getConnection();
		String sql = "update " + I.Group.TABLE_NAME + " set " + I.Group.NAME + "=?" + " where " + I.Group.GROUP_ID + "=?";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, groupNewName);
			ps.setString(2, groupId);
			int count = ps.executeUpdate();
			return count == 1;
		} catch (SQLException e) {
			logger.error("根据群组ID更新群组名称："+e);
		} finally {
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}

	@Override
	public boolean deleteGroupAndMembersByHxid(String hxid) {
		Connection conn = DBUtils.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			// 删除群成员
			String sql = "delete from " + I.Member.TABLE_NAME + " where " + I.Member.GROUP_HX_ID + "=?";
			System.out.println("connection=" + conn + ",sql=" + sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, hxid);
			ps.executeUpdate();
			ps.close();
			// 删除群组头像
			sql = "delete from " + I.Avatar.TABLE_NAME + " where " + I.Avatar.USER_NAME 
					+ "=(select "+I.Group.HX_ID+" from "+I.Group.TABLE_NAME+" where "+I.Group.HX_ID+"=?)";
			System.out.println("connection=" + conn + ",sql=" + sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1,hxid);
			ps.executeUpdate();
			ps.close();
			// 删除群组
			sql = "delete from " + I.Group.TABLE_NAME + " where " + I.Group.HX_ID + "=?";
			System.out.println("connection=" + conn + ",sql=" + sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, hxid);
			ps.executeUpdate();
			conn.commit();
			return true;
		} catch (SQLException e) {
			logger.error("根据群组ID解散群："+e);
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}

	@Override
	public boolean updateGroupNameByHxId(String hxId, String groupNewName) {
		Connection conn = DBUtils.getConnection();
		String sql = "update " + I.Group.TABLE_NAME + " set " + I.Group.NAME + "=?" + " where " + I.Group.HX_ID + "=?";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, groupNewName);
			ps.setString(2, hxId);
			int count = ps.executeUpdate();
			return count == 1;
		} catch (SQLException e) {
			logger.error("根据群组ID更新群组名称："+e);
		} finally {
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}
}
