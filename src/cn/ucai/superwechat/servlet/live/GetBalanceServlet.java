package cn.ucai.superwechat.servlet.live;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.dao.ILiveDao;
import cn.ucai.superwechat.dao.LiveDaoImpl;
import cn.ucai.superwechat.pojo.Wallet;
import cn.ucai.superwechat.utils.I;
import cn.ucai.superwechat.utils.JsonUtil;

@WebServlet("/live/getBalance")
public class GetBalanceServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private ILiveDao  dao = new LiveDaoImpl();
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uname = request.getParameter("uname");
		Wallet wallet = dao.getBalanceByName(uname);
		Result result = new Result();
		if(wallet==null){
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GET_BALANCE_FAIL);
		}else{
			result.setRetCode(I.MSG_SUCCESS);
			result.setRetMsg(true);
			result.setRetData(wallet);
		}
		JsonUtil.writeJsonToClient(result, response);
	}
}
