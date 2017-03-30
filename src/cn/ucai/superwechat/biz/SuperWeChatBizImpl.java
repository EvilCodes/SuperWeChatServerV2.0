package cn.ucai.superwechat.biz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.ucai.superwechat.bean.GroupAvatar;
import cn.ucai.superwechat.bean.LocationUserAvatar;
import cn.ucai.superwechat.bean.MemberUserAvatar;
import cn.ucai.superwechat.bean.Pager;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.dao.ISuperWeChatDao;
import cn.ucai.superwechat.dao.SuperWeChatDaoImpl;
import cn.ucai.superwechat.pojo.Group;
import cn.ucai.superwechat.pojo.Location;
import cn.ucai.superwechat.pojo.User;
import cn.ucai.superwechat.utils.I;
import cn.ucai.superwechat.utils.ImageUtil;
import cn.ucai.superwechat.utils.PropertiesUtils;

public class SuperWeChatBizImpl implements ISuperWeChatBiz{
	private ISuperWeChatDao dao;
	public SuperWeChatBizImpl() {
		dao = new SuperWeChatDaoImpl();
	}
	@Override
	public Result register(User user, HttpServletRequest request) {
		Result result = new Result();
		// 完成注册业务逻辑
		// 查找数据库中有没有重名的用户
		User u = dao.findUserByUsername(user.getMUserName());
		if(u==null){// 没有
			// 获得头像的后缀名
			String suffix = uploadAvatar(user.getMUserName(),I.AVATAR_TYPE_USER_PATH,request);
			// if(suffix!=null){// 头像上传成功
				if(dao.addUserAndAvatar(user,suffix)){// 注册成功
					result.setRetMsg(true);
					result.setRetCode(I.MSG_SUCCESS);
				}else{
					// 删除头像
					deleteAvatar(PropertiesUtils.getValue("avatar_path","path.properties")+I.AVATAR_TYPE_USER_PATH+"/",user.getMUserName()+suffix);
					result.setRetMsg(false);
					result.setRetCode(I.MSG_REGISTER_FAIL);
				}
			/*}else{// 头像上传失败
				result.setRetMsg(false);
				result.setRetCode(I.MSG_REGISTER_UPLOAD_AVATAR_FAIL);
			}*/
		}else{// 已存在
			result.setRetMsg(false);
			result.setRetCode(I.MSG_REGISTER_USERNAME_EXISTS);
		}
		return result;
	}
	
	/**
	 * 删除头像
	 * @param path
	 * @param name
	 */
	private void deleteAvatar(String path,String imageName) {
		File file = new File(path,imageName);
		if(file.exists()){
			file.delete();
		}
	}
	
	private String uploadAvatar(String name,String avatarType,HttpServletRequest request){
		String path = null;
		if (avatarType.equals(I.AVATAR_TYPE_USER_PATH)) {// 用户上传头像
			path = PropertiesUtils.getValue("avatar_path", "path.properties") + I.AVATAR_TYPE_USER_PATH + "/";
		} else if (avatarType.equals(I.AVATAR_TYPE_GROUP_PATH)) {// 群组上传
			path = PropertiesUtils.getValue("avatar_path", "path.properties") + I.AVATAR_TYPE_GROUP_PATH + "/";
		}
		try {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(4096); // 设置缓冲区大小，这里是4kb
			// 设置临时文件目录
			factory.setRepository(new File(PropertiesUtils.getValue("temp_path", "path.properties")));// 设置缓冲区目录
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setSizeMax(4194304); // 设置最大文件尺寸，这里是4MB
			List<FileItem> items = upload.parseRequest(request);// 得到所有的文件
			Iterator<FileItem> i = items.iterator();
			String fileName = null;
			while (i.hasNext()) {
				FileItem fi = (FileItem) i.next();
				fileName = fi.getName();
				if (fileName != null) {
					File savedFile = null;
					if(name.indexOf(".")!=-1){// 更新头像操作
						// 如果是更新图片，传过来的是shangpeng.jpg,，需要修改为shangpeng.png,上传则不必
						savedFile = new File(path, name.substring(0,name.lastIndexOf(".")) + fileName.substring(fileName.lastIndexOf(".")));
					}else{// 上传头像操作
						savedFile = new File(path, name + fileName.substring(fileName.lastIndexOf(".")));
					}
					fi.write(savedFile);
				}
			}
			return fileName.substring(fileName.lastIndexOf("."));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Result unRegister(String userName) {
		Result result = new Result();
		boolean isDelete = dao.deleteUser(userName);
		if(isDelete){
			String path = PropertiesUtils.getValue("avatar_path","path.properties") + I.AVATAR_TYPE_USER_PATH 
					+ "/" + userName + I.AVATAR_SUFFIX_JPG;
			File file = new File(path);
			if (file.exists()){
				file.delete();
				result.setRetMsg(true);
				result.setRetCode(I.MSG_SUCCESS);
			}
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_UNREGISTER_FAIL);
		}
		return result;
	}
	
	@Override
	public Result login(User user) {
		Result result = new Result();
		User u = dao.findUserByUsername(user.getMUserName());
		if(u!=null){
			if(u.getMUserPassword().equals(user.getMUserPassword())){
				result.setRetMsg(true);
				result.setRetCode(I.MSG_SUCCESS);
				UserAvatar ua = dao.getUserAvatarByUsername(user.getMUserName());
				result.setRetData(ua);
			}else{
				result.setRetMsg(false);
				result.setRetCode(I.MSG_LOGIN_ERROR_PASSWORD);
			}
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_LOGIN_UNKNOW_USER);
		}
		return result;
	}

	
	/**
	 * 根据用户名更新昵称
	 */
	@Override
	public Result updateNick(String username, String nick) {
		Result result = new Result();
		if(dao.updateNick(username,nick)){// 更新成功
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
			UserAvatar ua = dao.getUserAvatarByUsername(username);
			result.setRetData(ua);
		}else{// 更新失败
			result.setRetMsg(false);
			result.setRetCode(I.MSG_USER_UPDATE_NICK_FAIL);
		}
		return result;
	}
	@Override
	public Result updatePassword(String username, String password) {
		Result result = new Result();
		if(dao.updatePassword(username,password)){// 更新成功
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
			UserAvatar ua = dao.getUserAvatarByUsername(username);
			result.setRetData(ua);
		}else{// 更新失败
			result.setRetMsg(false);
			result.setRetCode(I.MSG_USER_UPDATE_PASSWORD_FAIL);
		}
		return result;
	}
	@Override
	public void downAvatar(String nameOrHxid,String avatarSuffix ,String avatarType, HttpServletResponse response) {
		// 1、从文件中读
		// 2、将读到的内容写入到客户端
		response.setContentType("image/jpeg"); // MIME
		File file = new File(PropertiesUtils.getValue("avatar_path","path.properties")+avatarType+"/",nameOrHxid+avatarSuffix);
		try {
			ImageUtil.zoom(file.getPath(), response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 更新用户或群组的头像
	 */
	@Override
	public Result updateAvatar(String nameOrHxid, String avatarType, HttpServletRequest request) {
		Result result = new Result();
		// 先上传新图片覆盖旧图片
		String suffix = uploadAvatar(nameOrHxid,avatarType,request);
		if(suffix!=null){
			if(dao.updateAvatar(nameOrHxid,avatarType,suffix)){// 更新头像表的最后更新时间
				if (avatarType.equals(I.AVATAR_TYPE_USER_PATH)) {// 用户
					UserAvatar ua = dao.getUserAvatarByUsername(nameOrHxid);
					result.setRetData(ua);
				} else if (avatarType.equals(I.AVATAR_TYPE_GROUP_PATH)) {// 群组
					GroupAvatar ga = dao.findGroupAvatarByHxId(nameOrHxid);
					result.setRetData(ga);
				}
				result.setRetMsg(true);
				result.setRetCode(I.MSG_SUCCESS);
			}else{
				result.setRetMsg(false);
				result.setRetCode(I.MSG_UPLOAD_AVATAR_FAIL);
			}
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_UPLOAD_AVATAR_FAIL);
		}
		return result;
	}

	@Override
	public Result findContactPagesByUserName(String userName, String pageId, String pageSize) {
		Result result = new Result();
		List<UserAvatar> listUserAvatar = dao.findContactPagesByUserName(userName, pageId, pageSize);
		if(listUserAvatar!=null){
			Pager pager = getPager(pageId, listUserAvatar);
			result.setRetData(pager);
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GET_CONTACT_PAGES_FAIL);
		}
		return result;
	}
	
	/**
	 * 将分页查询得到的内容封装为Pager类
	 * @param pageId
	 * @param list
	 * @param maxRecord
	 * @return
	 */
	public Pager getPager(String pageId,List<?> list){
		Pager pager = new Pager();
		pager.setCurrentPage(Integer.parseInt(pageId));
		pager.setMaxRecord(list.size());
		pager.setPageData(list);
		return pager;
	}
	
	@Override
	public Result findContactAllByUserName(String userName) {
		Result result = new Result();
		List<UserAvatar> listUserAvatar = dao.findContactAllByUserName(userName);
		if(listUserAvatar!=null){
			result.setRetData(listUserAvatar);
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GET_CONTACT_ALL_FAIL);
		}
		return result;
	}

	/**
	 * 根据用户名查找用户
	 * 1、查找到了则返回成功和查找到的用户信息
	 * 2、查找不到则返回false
	 */
	@Override
	public Result findUserByUserName(String userName) {
		Result result = new Result();
		UserAvatar ua = dao.getUserAvatarByUsername(userName);
		if(ua==null){
			result.setRetMsg(false);
			result.setRetCode(I.MSG_LOGIN_UNKNOW_USER);
		}else{
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
			result.setRetData(ua);
		}
		return result;
	}

	/**
	 * 根据用户名或昵称，模糊分页查询数据信息
	 */
	@Override
	public Result findUsersForSearch(String userName, String userNick, String pageId, String pageSize) {
		Result result = new Result();
		List<UserAvatar> uaList = dao.findUsersForSearch(userName, userNick, pageId, pageSize);
		if(uaList==null){
			result.setRetMsg(false);
			result.setRetCode(I.MSG_LOGIN_UNKNOW_USER);
		}else{
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
			Pager pager = getPager(pageId, uaList);
			result.setRetData(pager);
		}
		return result;
	}
	
	@Override
	public Result downloadGroupMembersByGroupId(String groupId) {
		Result result = new Result();
		List<MemberUserAvatar> listMemberUserAvatar = dao.downloadGroupMembersByGroupId(groupId);
		if(listMemberUserAvatar!=null){
			result.setRetData(listMemberUserAvatar);
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GROUP_GET_MEMBERS_FAIL);
		}
		return result;
	}
	
	@Override
	public Result downloadGroupMembersPagesByGroupId(String groupId, String pageId, String pageSize) {
		Result result = new Result();
		List<MemberUserAvatar> listMemberUserAvatar = dao.downloadGroupMembersPagesByGroupId(groupId, pageId, pageSize);
		if(listMemberUserAvatar!=null){
			Pager pager = getPager(pageId, listMemberUserAvatar);
			result.setRetData(pager);
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GROUP_GET_MEMBERS_FAIL);
		}
		return result;
	}
	
	@Override
	public Result downloadGroupMembersByHxId(String hxId) {
		Result result = new Result();
		List<MemberUserAvatar> listMemberUserAvatar = dao.downloadGroupMembersByHxId(hxId);
		if(listMemberUserAvatar!=null){
			result.setRetData(listMemberUserAvatar);
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GROUP_GET_MEMBERS_FAIL);
		}
		return result;
	}
	
	@Override   //downloadGroupMembersPagesByHxId
	public Result downloadGroupMembersPagesByHxId(String hxId, String pageId, String pageSize) {
		Result result = new Result();
		List<MemberUserAvatar> listMemberUserAvatar = dao.downloadGroupMembersPagesByHxId(hxId, pageId, pageSize);
		if(listMemberUserAvatar!=null){
			Pager pager = getPager(pageId, listMemberUserAvatar);
			result.setRetData(pager);
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GROUP_GET_MEMBERS_FAIL);
		}
		return result;
	}
	/**
	 * 创建群组
	 */
	@Override
	public Result createGroup(Group group, HttpServletRequest request) {
		Result result = null;
		Group groupFind = dao.findGroupByHxid(group.getMGroupHxid());
		if(groupFind==null){
			String suffix = uploadAvatar(group.getMGroupHxid(),I.AVATAR_TYPE_GROUP_PATH,request);
			// if(suffix!=null){// 头像上传成功
				if(dao.addGroupAndGroupOwnerMember(group,suffix)){// 添加群组成功
					result = new Result(true,I.MSG_SUCCESS);
					GroupAvatar gAvatar = dao.findGroupAvatarByHxId(group.getMGroupHxid());
					try {
						gAvatar.setMGroupDescription(URLEncoder.encode(gAvatar.getMGroupDescription(),"utf-8"));
						gAvatar.setMGroupName(URLEncoder.encode(gAvatar.getMGroupName(),"utf-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					result.setRetData(gAvatar);
				}else{// 添加群组失败
					result = new Result(false,I.MSG_GROUP_CREATE_FAIL);
					// 删除本地图片
					deleteAvatar(PropertiesUtils.getValue("avatar_path","path.properties")+I.AVATAR_TYPE_GROUP_PATH,group.getMGroupHxid()+suffix);
				}
			/*}else{
				result = new Result(false,I.MSG_UPLOAD_AVATAR_FAIL);
			}*/
		}else{// 群组已存在
			result = new Result(false,I.MSG_GROUP_HXID_EXISTS);
		}
		return result;
	}

	@Override
	public Result addGroupMember(String userName, String hxId) {
		Result result = new Result();
		GroupAvatar groupAvatar = dao.findGroupAvatarByHxId(hxId);
		if(groupAvatar!=null){
			if(dao.addGroupMemberAndUpdateGroupAffiliationsCount(userName,groupAvatar)){// 添加成员成功
				result.setRetMsg(true);
				UserAvatar ua = dao.getUserAvatarByUsername(userName);
//				result.setRetData(groupAvatar);
				result.setRetData(ua);
				result.setRetCode(I.MSG_SUCCESS);
				return result;
			}
		}
		result.setRetCode(I.MSG_GROUP_ADD_MEMBER_FAIL);
		result.setRetMsg(false);
		return result;
	}

	@Override
	public Result addGroupMembers(String userNameArr, String hxId) {
		Result result = new Result();
		GroupAvatar groupAvatar = dao.findGroupAvatarByHxId(hxId);
		if(groupAvatar!=null){
			if(dao.addGroupMembersAndUpdateGroupAffiliationsCount(userNameArr,groupAvatar)){// 添加成员成功
				result.setRetMsg(true);
				result.setRetData(groupAvatar);
				result.setRetCode(I.MSG_SUCCESS);
				return result;
			}
		}
		result.setRetCode(I.MSG_GROUP_ADD_MEMBER_FAIL);
		result.setRetMsg(false);
		return result;
	}
	
	@Override
	public Result deleteGroupMember(String userName, String groupId) {
		Result result = new Result();
		GroupAvatar groupAvatar = dao.findGroupAvatarByGroupId(groupId);
		if(groupAvatar!=null){
			if(dao.delGroupMemberAndUpdateGroupAffiliationsCount(userName,groupAvatar)){// 删除群成员
				result.setRetMsg(true);
				result.setRetData(groupAvatar);
				result.setRetCode(I.MSG_SUCCESS);
				return result;
			}
		}
		result.setRetCode(I.MSG_GROUP_DELETE_MEMBER_FAIL);
		result.setRetMsg(false);
		return result;
	}

	@Override
	public Result deleteGroupMembers(String userNames, String groupId) {
		GroupAvatar groupAvatar = dao.findGroupAvatarByGroupId(groupId);
		Result result = new Result();
		if(groupAvatar!=null){
			if(dao.delGroupMembersAndUpdateGroupAffiliationsCount(userNames, groupAvatar)){
				result.setRetMsg(true);
				result.setRetCode(I.MSG_SUCCESS);
				return result;
			}
		}
		result.setRetCode(I.MSG_GROUP_DELETE_MEMBERS_FAIL);
		result.setRetMsg(false);
		return result;
	}

	@Override
	public Result deleteGroup(String groupId) {
		Result result = new Result();
		GroupAvatar ga = dao.findGroupAvatarByGroupId(groupId);
		if(dao.deleteGroupAndMembers(groupId)){
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
			// 删除本地硬盘头像信息
			deleteAvatar(PropertiesUtils.getValue("avatar_path","path.properties")+I.AVATAR_TYPE_GROUP_PATH+"/",ga.getMGroupHxid()+ga.getMAvatarSuffix());
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GROUP_DELETE_FAIL);
		}
		return result;
	}
	
	/**
	 * 添加好友关系：
	 * 1、如果已经存在好友关系，则返回相关信息
	 * 2、不存在关系，则建立关系
	 * 3、建立失败，返回失败信息，建立成功，返回被添加用户的信息
	 */
	@Override
	public Result addContact(String name, String cname) {
		Result result = new Result();
		boolean isContact = dao.findContact(name,cname);
		if(isContact){
			result.setRetMsg(false);
			result.setRetCode(I.MSG_CONTACT_FIRENDED);
		}else{
			boolean addContact = dao.addContact(name,cname);
			if(addContact){
				result.setRetMsg(true);
				result.setRetCode(I.MSG_SUCCESS);
				UserAvatar ua = dao.getUserAvatarByUsername(cname);
				result.setRetData(ua);
			}else{
				result.setRetMsg(false);
				result.setRetCode(I.MSG_CONTACT_ADD_FAIL);
			}
		}
		return result;
	}

	/**
	 * 删除好友关系
	 * 1、删除成功，返回true
	 * 2、删除失败，返回false即可
	 */
	@Override
	public Result delContact(String name, String cname) {
		Result result = new Result();
		boolean delContact = dao.delContact(name,cname);
		if(delContact){
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_CONTACT_DEL_FAIL);
		}
		return result;
	}
	
	@Override
	public Result findGroupByGroupId(String groupId) {
		Result result = new Result();
		GroupAvatar ga = dao.findGroupAvatarByGroupId(groupId);
		if(ga!=null){
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
			result.setRetData(ga);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GROUP_FIND_BY_GOURP_ID_FAIL);
		}
		return result;
	}

	@Override
	public Result findGroupByHxId(String hxId) {
		Result result = new Result();
		GroupAvatar ga = dao.findGroupAvatarByHxId(hxId);
		if(ga!=null){
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
			result.setRetData(ga);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GROUP_FIND_BY_HX_ID_FAIL);
		}
		return result;
	}

	@Override
	public Result findPublicGroupByHxId(String hxId) {
		Result result = new Result();
		GroupAvatar ga = dao.findPublicGroupAvatarByHxId(hxId);
		if(ga!=null){
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
			result.setRetData(ga);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GROUP_FIND_BY_HX_ID_FAIL);
		}
		return result;
	}

	/**
	 * 查找某一指定用户的所有的群（所在的所有群）
	 */
	@Override
	public Result findAllGroupByUserName(String userName) {
		Result result = new Result();
		List<GroupAvatar> listGroupAdater = dao.findAllGroupByUserName(userName);
		if(listGroupAdater!=null){
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
			result.setRetData(listGroupAdater);
		}else{
			result.setRetCode(I.MSG_GROUP_FIND_BY_USER_NAME_FAIL);
			result.setRetMsg(false);
		}
		return result;
	}

	/**
	 * 查找所有的公开群，不包括当前用户已经所在的群
	 */
	@Override
	public Result findPublicGroups(String userName, int pageId, int pageSize) {
		Result result = new Result();
		List<GroupAvatar> listGroupAdater = dao.findPublicGroups(userName,pageId,pageSize);
		if(listGroupAdater!=null){
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
			Pager pager = getPager(pageId+"", listGroupAdater);
			result.setRetData(pager);
			
			return result;
		}else{
			result.setRetCode(I.MSG_PUBLIC_GROUP_FAIL);
			result.setRetMsg(false);
		}
		return null;
	}

	/**
	 * 根据群组名称，模糊查询所有匹配的群组
	 */
	@Override
	public Result findGroupByGroupName(String groupName) {
		Result result = new Result();
		List<GroupAvatar> listGroupAdater = dao.findGroupByGroupName(groupName);
		if(listGroupAdater!=null){
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
			result.setRetData(listGroupAdater);
		}else{
			result.setRetCode(I.MSG_GROUP_FIND_BY_GROUP_NAME_FAIL);
			result.setRetMsg(false);
		}
		return result;
	}

	@Override
	public Result uploadUserLocation(Location location) {
		Result result = new Result();
		if(dao.uploadUserLocation(location)){
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_LOCATION_UPLOAD_FAIL);
		}
		return result;
	}

	@Override
	public Result updateUserLocation(Location location) {
		Result result = new Result();
		if(dao.updateUserLocation(location)){
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_LOCATION_UPDATE_FAIL);
		}
		return result;
	}

	@Override
	public Result downloadLocation(String userName, String pageId, String pageSize) {
		Result result = new Result();
		List<LocationUserAvatar> listLocationUserAvatar = dao.downloadLocation(userName,pageId,pageSize);
		if(listLocationUserAvatar==null){
			result.setRetMsg(false);
			result.setRetCode(I.MSG_LOCATION_GET_FAIL);
			
		}else{
			result.setRetMsg(true);
			Pager pager = getPager(pageId, listLocationUserAvatar);
			result.setRetData(pager);
			result.setRetCode(I.MSG_SUCCESS);
		}
		return result;
	}
	
	@Override
	public Result updateGroupNameByGroupId(String groupId, String groupNewName) {
		Result result = new Result();
		GroupAvatar ga = dao.findGroupAvatarByGroupId(groupId);
		if(ga==null){
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GROUP_UNKONW);
		}else{
			if(dao.updateGroupNameByGroupId(groupId,groupNewName)){
				result.setRetMsg(true);
				ga.setMGroupName(groupNewName);
				result.setRetData(ga);
				result.setRetCode(I.MSG_SUCCESS);
			}else{
				result.setRetMsg(false);
				result.setRetCode(I.MSG_USER_UPDATE_NICK_FAIL);
			}
		}
		return result;
	}
	@Override
	public Result deleteGroupByHxid(String hxid) {
		Result result = new Result();
//		GroupAvatar ga = dao.findGroupAvatarByGroupId(groupId);
		GroupAvatar ga = dao.findGroupAvatarByHxId(hxid);
		if(dao.deleteGroupAndMembersByHxid(hxid)){
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
			// 删除本地硬盘头像信息
			deleteAvatar(PropertiesUtils.getValue("avatar_path","path.properties")+I.AVATAR_TYPE_GROUP_PATH+"/",ga.getMGroupHxid()+ga.getMAvatarSuffix());
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GROUP_DELETE_FAIL);
		}
		return result;
	}
	@Override
	public Result updateGroupNameByHxId(String hxId, String groupNewName) {
		Result result = new Result();
		GroupAvatar ga = dao.findGroupAvatarByHxId(hxId);
		if(ga==null){
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GROUP_UNKONW);
		}else{
			if(dao.updateGroupNameByHxId(hxId,groupNewName)){
				result.setRetMsg(true);
				ga.setMGroupName(groupNewName);
				result.setRetData(ga);
				result.setRetCode(I.MSG_SUCCESS);
			}else{
				result.setRetMsg(false);
				result.setRetCode(I.MSG_USER_UPDATE_NICK_FAIL);
			}
		}
		return result;
	}

}
