package cn.ucai.superwechat.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.biz.ISuperWeChatBiz;
import cn.ucai.superwechat.biz.SuperWeChatBizImpl;
import cn.ucai.superwechat.utils.I;
import cn.ucai.superwechat.utils.JsonUtil;

@WebServlet("/updateGroupName")
public class UpdateGroupNameServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private ISuperWeChatBiz  biz = new SuperWeChatBizImpl();
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String groupId = request.getParameter(I.Group.GROUP_ID);
		String groupNewName = request.getParameter(I.Group.NAME);
		Result result = biz.updateGroupNameByGroupId(groupId,groupNewName);
		JsonUtil.writeJsonToClient(result, response);
	}
}
