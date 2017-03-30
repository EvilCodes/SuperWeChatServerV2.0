package cn.ucai.superwechat.servlet.live;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.dao.ILiveDao;
import cn.ucai.superwechat.dao.LiveDaoImpl;
import cn.ucai.superwechat.pojo.RechargeStatements;
import cn.ucai.superwechat.pojo.Wallet;
import cn.ucai.superwechat.utils.I;
import cn.ucai.superwechat.utils.JsonUtil;
import cn.ucai.superwechat.utils.PropertiesUtils;

@WebServlet("/live/recharge")
public class RechargeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ILiveDao dao = new LiveDaoImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String uname = request.getParameter("uname");
		String strRmb = request.getParameter("rmb");
		Result result = new Result();
		if (!strRmb.matches("\\d+")) {
			result.setRetMsg(false);
			result.setRetCode(I.MSG_RECHARGE_FAIL);
			result.setRetData("额度非法！");
			JsonUtil.writeJsonToClient(result, response);
			return;
		}
		int rcount = Integer.parseInt(strRmb)
				* Integer.parseInt(PropertiesUtils.getValue("exchangerate", "exchangerate.properties"));
		int rmb = Integer.parseInt(request.getParameter("rmb"));
//		String rdate = request.getParameter("rdate");
		RechargeStatements rs = new RechargeStatements();
		rs.setRcount(rcount);
		rs.setRdate(System.currentTimeMillis()+"");
		rs.setRmb(rmb);
		rs.setUname(uname);

		Wallet wallet = dao.getBalanceByName(uname);
		boolean flag = dao.recharge(rs, wallet.getBalance());
		if (!flag) {
			result.setRetMsg(false);
			result.setRetCode(I.MSG_RECHARGE_FAIL);
			result.setRetData("充值失败！");
		} else {
			result.setRetCode(I.MSG_SUCCESS);
			result.setRetMsg(true);
			wallet.setBalance(wallet.getBalance() + rs.getRcount());
			result.setRetData(wallet);
		}
		JsonUtil.writeJsonToClient(result, response);
	}

}
