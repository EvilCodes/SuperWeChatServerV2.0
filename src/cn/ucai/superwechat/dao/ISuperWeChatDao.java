package cn.ucai.superwechat.dao;

import java.util.List;

import cn.ucai.superwechat.bean.GroupAvatar;
import cn.ucai.superwechat.bean.LocationUserAvatar;
import cn.ucai.superwechat.bean.MemberUserAvatar;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.pojo.Group;
import cn.ucai.superwechat.pojo.Location;
import cn.ucai.superwechat.pojo.User;

public interface ISuperWeChatDao {
	/**
	 * 根据用户名查找用户是否存在
	 * @param mUserName
	 * @return
	 */
	User findUserByUsername(String mUserName);
	/**
	 * 添加用户和用户头像信息
	 * @param user
	 * @return
	 */
	boolean addUserAndAvatar(User user,String suffix);

	/**
	 * 根据用户名更新昵称
	 * @param username
	 * @param nick
	 * @return
	 */
	boolean updateNick(String username, String nick);

	/**
	 * 根据用户名查找用户和用户头像的信息
	 * @param username
	 * @return
	 */
	UserAvatar getUserAvatarByUsername(String username);
	
	/**
	 * 根据用户名更新密码
	 * @param username
	 * @param nick
	 * @return
	 */
	boolean updatePassword(String username, String password);

	/**
	 * 根据用户名和类型更新头像最后更新时间
	 * @param nameOrHxid
	 * @param avatarType
	 * @return
	 */
	boolean updateAvatar(String nameOrHxid, String avatarType,String suffix);
	/**
	 * 根据用户名删除用户
	 * @param userName
	 * @return
	 */
	public boolean deleteUser(String userName);
	/**
	 * 分页查询好友信息
	 * @param userName
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	List<UserAvatar> findContactPagesByUserName(String userName, String pageId, String pageSize);

	/**
	 * 查询好友全部信息
	 * @param userName
	 * @return
	 */
	List<UserAvatar> findContactAllByUserName(String userName);
	/**
	 * 根据用户名或密码模糊查询用户信息
	 * @param userName
	 * @param userNick
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	public List<UserAvatar> findUsersForSearch(String userName, String userNick, String pageId, String pageSize);
	/**
	 * 根据群组id，下载群组成员
	 * @param groupId
	 * @return
	 */
	public List<MemberUserAvatar> downloadGroupMembersByGroupId(String groupId);
	/**
	 * 根据群组id，分页下载群组成员
	 * @param groupId
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	public List<MemberUserAvatar> downloadGroupMembersPagesByGroupId(String groupId, String pageId, String pageSize);
	/**
	 * 根据环信id，下载全部群组成员
	 * @param groupId
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	public List<MemberUserAvatar> downloadGroupMembersByHxId(String hxId);
	/**
	 * 根据环信id，分页下载群组成员
	 * @param groupId
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	public List<MemberUserAvatar> downloadGroupMembersPagesByHxId(String hxId, String pageId, String pageSize);
	/**
	 * 根据群组id查找群组是否存在
	 * @param groupId
	 * @return
	 */
	public GroupAvatar findGroupAvatarByGroupId(String groupId);
	/**
	 * 根据群组环信id查找群组信息
	 * @param mGroupHxid
	 * @return
	 */
	Group findGroupByHxid(String mGroupHxid);
	/**
	 * 创建群组和群主作为成员
	 * @param group
	 * @return
	 */
	boolean addGroupAndGroupOwnerMember(Group group,String suffix);
	/**
	 * 根据环信id查找群组和头像信息
	 * @param mGroupHxid
	 * @return
	 */
	GroupAvatar findGroupAvatarByHxId(String mGroupHxid);
	/**
	 * 添加群组成员,并更新群当前总人数
	 * @return
	 */
	public boolean addGroupMemberAndUpdateGroupAffiliationsCount(String userName,GroupAvatar groupAvatar);
	/**
	 * 添加多个群组成员
	 * @param memberArr
	 * @return
	 */
	public boolean addGroupMembersAndUpdateGroupAffiliationsCount(String userNameArr,GroupAvatar groupAvatar);
	/**
	 * 在群组中删除某指定姓名的用户
	 * @param userName
	 * @param groupAvatar
	 * @return
	 */
	public boolean delGroupMemberAndUpdateGroupAffiliationsCount(String userName, GroupAvatar groupAvatar);
	/**
	 * 删除多个群组成员
	 * @param userNames
	 * @param groupAvatar
	 * @return
	 */
	public boolean delGroupMembersAndUpdateGroupAffiliationsCount(String userName, GroupAvatar groupAvatar);
	/**
	 * 根据群组ID，删除群组，同时删除成员表中的该群组的所有成员
	 * @param groupId
	 * @return
	 */
	public boolean deleteGroupAndMembers(String groupId);
	/**
	 * 根据群组环信ID，删除群组，同时删除成员表中的该群组的所有成员
	 * @param groupId
	 * @return
	 */
	boolean deleteGroupAndMembersByHxid(String hxid);
	/**
	 * 查找cname是否已经是name的好友
	 * @param name
	 * @param cname
	 * @return
	 */
	public boolean findContact(String name, String cname);
	/**
	 * 创建好友关系
	 * @param name
	 * @param cname
	 * @return
	 */
	public boolean addContact(String name, String cname);
	/**
	 * 删除好友关系
	 * @param name
	 * @param cname
	 * @return
	 */
	public boolean delContact(String name, String cname);
	/**
	 * 根据环信id查找群组是否存在
	 * @param groupId
	 * @return
	 */
	public GroupAvatar findPublicGroupAvatarByHxId(String hxId);
	/**
	 * 查询某个用户所在的所有群
	 * @param userName
	 * @return
	 */
	public List<GroupAvatar> findAllGroupByUserName(String userName);
	/**
	 * 查找所有的公开群，不包括当前用户已经所在的群
	 * @param userName
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	public List<GroupAvatar> findPublicGroups(String userName, int pageId, int pageSize);
	/**
	 * 根据群组名称，模糊查询所有匹配的群组
	 * @param groupName
	 * @return
	 */
	public List<GroupAvatar> findGroupByGroupName(String groupName);
	/**
	 * 上传用户地理位置信息
	 * @param location
	 * @return
	 */
	public boolean uploadUserLocation(Location location);
	/**
	 * 更新用户的地理位置信息
	 * @param location
	 * @return
	 */
	public boolean updateUserLocation(Location location);
	/**
	 * 查询距离最近的人
	 * @param userName
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	public List<LocationUserAvatar> downloadLocation(String userName, String pageId, String pageSize);
	/**
	 * 更新群组名称
	 * @param groupId
	 * @param groupNewName
	 * @return
	 */
	boolean updateGroupNameByGroupId(String groupId, String groupNewName);
	/**
	 * 根据环信Id更新群组名称
	 * @param hxId
	 * @param groupNewName
	 * @return
	 */
	boolean updateGroupNameByHxId(String hxId, String groupNewName);

}
