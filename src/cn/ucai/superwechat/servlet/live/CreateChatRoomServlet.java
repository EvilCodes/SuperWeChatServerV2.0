package cn.ucai.superwechat.servlet.live;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import cn.ucai.superwechat.bean.ChatRoom;
import cn.ucai.superwechat.dao.ILiveDao;
import cn.ucai.superwechat.dao.LiveDaoImpl;
import cn.ucai.superwechat.utils.HTTPUtil;
import cn.ucai.superwechat.utils.I;
import cn.ucai.superwechat.utils.JsonUtil;
import cn.ucai.superwechat.utils.PropertiesUtils;

@WebServlet("/live/createChatRoom")
public class CreateChatRoomServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private ILiveDao  dao = new LiveDaoImpl();
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String auth = request.getParameter("auth");
		if(!auth.equals(PropertiesUtils.getValue("auth", "token.properties"))){
			JsonUtil.writeJsonToClient("{\"msg\":\"口令失败\"}", response);
			return;
		}
		String name = request.getParameter("name");
		String description = request.getParameter("description");
		String owner = request.getParameter("owner");
		int maxusers = Integer.parseInt(request.getParameter("maxusers"));
		String strMembers = request.getParameter("members");
		ChatRoom cr = new ChatRoom();
		cr.setName(name);
		cr.setDescription(description);
		cr.setMaxusers(maxusers);
		cr.setOwner(owner);
		if(strMembers!=null && strMembers.length()!=0){
			cr.setMembers(strMembers.split(","));
		}else{
			cr.setMembers(new String[]{});
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(cr);
			String authorization = dao.getAuthorization();
			String result = HTTPUtil.requestPost(I.REQUEST_BASE_URL_CHATROOM, json, authorization);
			System.out.println("result:"+result);
			JSONObject resultJson = new JSONObject(result);
			try{
				resultJson.getString("error");
			}catch(JSONException e){// 有异常说明正确获取了数据（正确获取数据中没有error字段）
				JsonUtil.writeJsonToClient(result, response);
				return;// 正常就不往下走了。
			}
			// token错误、过期或其他原因没有获取正确的数据往下走
			// 根据client_id和client_secret重新获取token
			String token = HTTPUtil.getToken();
			if(dao.updateAuthorization(token)){//更新数据表中的token
				// 重新发送创建聊天室的请求
				result = HTTPUtil.requestPost(I.REQUEST_BASE_URL_CHATROOM, json, token);
				JsonUtil.writeJsonToClient(result, response);
			}
		} catch (Exception e) {
			JsonUtil.writeJsonToClient(e.getMessage(), response);
			e.printStackTrace();
		}
	}
}
