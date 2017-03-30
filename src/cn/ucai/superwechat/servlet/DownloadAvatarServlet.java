package cn.ucai.superwechat.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.ucai.superwechat.biz.ISuperWeChatBiz;
import cn.ucai.superwechat.biz.SuperWeChatBizImpl;
import cn.ucai.superwechat.utils.I;

@WebServlet("/downloadAvatar")
public class DownloadAvatarServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private ISuperWeChatBiz  biz = new SuperWeChatBizImpl();
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String nameOrHxid = request.getParameter(I.NAME_OR_HXID);
		String avatarSuffix = request.getParameter(I.Avatar.AVATAR_SUFFIX);
		String avatarType = request.getParameter(I.AVATAR_TYPE);
		biz.downAvatar(nameOrHxid,avatarSuffix,avatarType,response);
	}
}
