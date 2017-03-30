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
import cn.ucai.superwechat.pojo.User;
import cn.ucai.superwechat.utils.I;
import cn.ucai.superwechat.utils.JsonUtil;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ISuperWeChatBiz  biz = new SuperWeChatBizImpl();
    public RegisterServlet() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 1、接收用户传来的参数
		String username = request.getParameter(I.User.USER_NAME);
		String password = request.getParameter(I.User.PASSWORD);
		String nick = request.getParameter(I.User.NICK);
		System.out.println(username+":"+password+":"+nick);
		// 2、注册（用户表插入、头像表插入、接收用户上传的图片）
		// 封装接收参数
		User user = new User(username,password,nick);
		Result result = biz.register(user,request);
		// 3、将注册结果发送给客户端
		JsonUtil.writeJsonToClient(result, response);
	}

}
