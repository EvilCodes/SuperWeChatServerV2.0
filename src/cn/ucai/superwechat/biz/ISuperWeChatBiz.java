package cn.ucai.superwechat.biz;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.pojo.Group;
import cn.ucai.superwechat.pojo.Location;
import cn.ucai.superwechat.pojo.User;

/**
 * 分层：解耦合
 * 处理项目的业务逻辑
 */
public interface ISuperWeChatBiz {
	/**
	 * 注册
	 * @param user
	 * @param request
	 * @return
	 */
	Result register(User user, HttpServletRequest request);
	/**
	 * 解除注册，主要业务包括用户表和头像表中删除数据，并删除服务器本地用户图片
	 * @param userName
	 * @return
	 */
	Result unRegister(String userName);

	/**
	 * 登录
	 * @param user
	 * @return
	 */
	Result login(User user);
	/**
	 * 根据用户名更新昵称
	 * @param username
	 * @param nick
	 * @return
	 */
	Result updateNick(String username, String nick);
	/**
	 * 根据用户名更新密码
	 * @param username
	 * @param password
	 * @return
	 */
	Result updatePassword(String username, String password);
	
	/**
	 * 提供用户或群组的头像，供客户端下载
	 * @param nameOrHxid
	 * @param avatarType
	 * @param response
	 * @param width:图片的宽度
	 * @param height:图片的高度
	 */
	void downAvatar(String nameOrHxid,String avatarSuffix, String avatarType, HttpServletResponse response);
	/**
	 * 更新头像
	 * @param nameOrHxid
	 * @param avatarType
	 * @param response
	 * @return
	 */
	Result updateAvatar(String nameOrHxid, String avatarType, HttpServletRequest request);
	
	/**
	 * 分页下载好友信息
	 * @param userName
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	Result findContactPagesByUserName(String userName, String pageId, String pageSize);
	
	/**
	 * 下载还有全部信息
	 * @param userName
	 * @return
	 */
	Result findContactAllByUserName(String userName);
	
	/**
	 * 根据用户名查找用户
	 * @param userName
	 * @return
	 */
	Result findUserByUserName(String userName);
	/**
	 * 根据用户名或昵称模糊查询用户信息
	 * @param userName
	 * @param userNick
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	Result findUsersForSearch(String userName, String userNick, String pageId, String pageSize);
	/**
	 * 根据群组id，下载群组成员
	 * @param groupId
	 * @return
	 */
	Result downloadGroupMembersByGroupId(String groupId);
	/**
	 * 根据群组id，分页下载群组成员
	 * @param groupId
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	Result downloadGroupMembersPagesByGroupId(String groupId, String pageId, String pageSize);
	/**
	 * 根据环信ID，下载全部群组成员
	 * @param hxId
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	Result downloadGroupMembersByHxId(String hxId);
	/**
	 * 根据环信ID，分页下载群组成员
	 * @param hxId
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	Result downloadGroupMembersPagesByHxId(String hxId, String pageId, String pageSize);
	/**
	 * 创建群组
	 * @param group
	 * @param request
	 * @return
	 */
	Result createGroup(Group group, HttpServletRequest request);
	/**
	 * 添加群组成员
	 * @param userName
	 * @param hxId
	 * @return
	 */
	Result addGroupMember(String userName, String hxId);
	/**
	 * 添加多个群组成员
	 * @param userNameArr
	 * @param hxId
	 * @return
	 */
	Result addGroupMembers(String userNameArr, String hxId);
	/**
	 * 删除群成员
	 * @param userName
	 * @param groupId
	 * @return
	 */
	Result deleteGroupMember(String userName, String groupId);
	/**
	 * 删除多个群成员
	 * @param userNames
	 * @param groupId
	 * @return
	 */
	Result deleteGroupMembers(String userNames, String groupId);
	/**
	 * 根据群组id删除群组
	 * @param groupId
	 * @return
	 */
	Result deleteGroup(String groupId);
	/**
	 * 根据群组环信ID删除群组
	 * @param hxid
	 * @return
	 */
	Result deleteGroupByHxid(String hxid);
	/**
	 * 添加好友关系
	 * @param name
	 * @param cname
	 * @return
	 */
	Result addContact(String name, String cname);

	/**
	 * 解除好友关系
	 * @param name
	 * @param cname
	 * @return
	 */
	Result delContact(String name, String cname);
	/**
	 * 根据群组id查找群组
	 * @param groupId
	 * @return
	 */
	Result findGroupByGroupId(String groupId);
	/**
	 * 根据环信id查找群组
	 * @param groupId
	 * @return
	 */
	Result findGroupByHxId(String hxId);
	/**
	 * 根据环信id查找公开群组
	 * @param groupId
	 * @return
	 */
	Result findPublicGroupByHxId(String hxId);
	/**
	 * 查询某个用户所在的所有群
	 * @param userName
	 * @return
	 */
	Result findAllGroupByUserName(String userName);
	/**
	 * 查找所有的公开群，不包括当前用户已经所在的群
	 * @param userName
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	Result findPublicGroups(String userName, int pageId, int pageSize);
	/**
	 * 根据群组名称，模糊查询所有匹配的群组
	 * @param groupName
	 * @return
	 */
	Result findGroupByGroupName(String groupName);
	/**
	 * 上传用户的地理位置信息
	 * @param location
	 * @return
	 */
	Result uploadUserLocation(Location location);
	/**
	 * 更新用户的地理位置信息
	 * @param location
	 * @return
	 */
	Result updateUserLocation(Location location);
	/**
	 * 分页获取附近的人 用户列表
	 * @param userName
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	Result downloadLocation(String userName, String pageId, String pageSize);
	/**
	 * 根据群组id更新群组名称
	 * @param groupId
	 * @param groupNewName
	 * @return
	 */
	Result updateGroupNameByGroupId(String groupId, String groupNewName);
	/**
	 * 根据环信Id更新群组名称
	 * @param hxId
	 * @param groupNewName
	 * @return
	 */
	Result updateGroupNameByHxId(String hxId, String groupNewName);

}




















