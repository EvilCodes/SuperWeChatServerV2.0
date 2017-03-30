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
import cn.ucai.superwechat.pojo.Group;
import cn.ucai.superwechat.utils.I;
import cn.ucai.superwechat.utils.JsonUtil;

@WebServlet("/createGroup")
public class CreateGroupServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private ISuperWeChatBiz  biz = new SuperWeChatBizImpl();
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String hxid = request.getParameter(I.Group.HX_ID);
		String name = request.getParameter(I.Group.NAME);
		String desc = request.getParameter(I.Group.DESCRIPTION);
		String owner = request.getParameter(I.Group.OWNER);
		String isPublic = request.getParameter(I.Group.IS_PUBLIC);
		String allowInvites = request.getParameter(I.Group.ALLOW_INVITES);
		Group group = new Group(hxid, name, desc, owner, System.currentTimeMillis()+"", I.GROUP_MAX_USERS_DEFAULT, I.GROUP_AFFILIATIONS_COUNT_DEFAULT, Boolean.parseBoolean(isPublic), Boolean.parseBoolean(allowInvites));
		Result result = biz.createGroup(group,request);
		System.out.println("result="+result);
		JsonUtil.writeJsonToClient(result, response);
	}
}
