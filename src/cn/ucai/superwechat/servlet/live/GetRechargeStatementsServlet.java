package cn.ucai.superwechat.servlet.live;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.dao.ILiveDao;
import cn.ucai.superwechat.dao.LiveDaoImpl;
import cn.ucai.superwechat.pojo.Gift;
import cn.ucai.superwechat.pojo.RechargeStatements;
import cn.ucai.superwechat.utils.I;
import cn.ucai.superwechat.utils.JsonUtil;

@WebServlet("/live/getRechargeStatements")
public class GetRechargeStatementsServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private ILiveDao  dao = new LiveDaoImpl();
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int pageId = Integer.parseInt(request.getParameter("pageId"));
		int pageSize = Integer.parseInt(request.getParameter("pageSize"));
		String uname = request.getParameter("uname");
		List<RechargeStatements> list = dao.getRechargeStatementsByPage(pageId,pageSize,uname);
		Result result = new Result();
		if(list==null){
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GET_RECHARGESTATEMENTS_FAIL);
		}else{
			result.setRetCode(I.MSG_SUCCESS);
			result.setRetMsg(true);
			result.setRetData(list);
		}
		JsonUtil.writeJsonToClient(result, response);
	}

}
