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

@WebServlet("/downloadGroupMembersPagesByGroupId")
public class DownloadGroupMembersPagesByGroupIdServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private ISuperWeChatBiz  biz = new SuperWeChatBizImpl();
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String groupId = request.getParameter(I.Member.GROUP_ID);
		String pageId = request.getParameter(I.PAGE_ID);
		String pageSize = request.getParameter(I.PAGE_SIZE);
		// 分页下载群组成员
		Result result = biz.downloadGroupMembersPagesByGroupId(groupId,pageId,pageSize);
		JsonUtil.writeJsonToClient(result, response);	
	}

}
