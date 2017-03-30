package cn.ucai.superwechat.servlet.live;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.utils.I;
import cn.ucai.superwechat.utils.JsonUtil;
import cn.ucai.superwechat.utils.PropertiesUtils;

@WebServlet("/live/uploadChatRoomAvatar")
public class UploadChatRoomAvatarServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String chatRoomId = request.getParameter("chatRoomId");
		String fileName = uploadAvatar(chatRoomId,request);
		Result result = new Result();
		if(fileName==null){
			result.setRetMsg(false);
			result.setRetCode(I.MSG_REGISTER_UPLOAD_CHATROOM_AVATAR_FAIL);
		}else{
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
			result.setRetData(fileName);
		}
		JsonUtil.writeJsonToClient(result, response);
	}
	
	private String uploadAvatar(String name,HttpServletRequest request){
		String path = null;
		path = PropertiesUtils.getValue("avatar_path", "path.properties") + I.AVATAR_TYPE_CHATROOM_PATH + "/";
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
					File savedFile = new File(path, name + fileName.substring(fileName.lastIndexOf(".")));
					fi.write(savedFile);
				}
			}
//			return fileName.substring(fileName.lastIndexOf("."));
			return fileName;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
