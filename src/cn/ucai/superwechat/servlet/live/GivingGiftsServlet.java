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
import cn.ucai.superwechat.pojo.Gift;
import cn.ucai.superwechat.pojo.Wallet;
import cn.ucai.superwechat.utils.I;
import cn.ucai.superwechat.utils.JsonUtil;

@WebServlet("/live/givingGifts")
public class GivingGiftsServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private ILiveDao  dao = new LiveDaoImpl();
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uname = request.getParameter("uname");
		String anchor = request.getParameter("anchor");
		Integer giftId = Integer.parseInt(request.getParameter("giftId"));
		Integer giftNum = Integer.parseInt(request.getParameter("giftNum"));
		Wallet currentWallet = dao.getBalanceByName(uname);
		Gift gift = dao.getGiftById(giftId);
		Result result = new Result();
		if(currentWallet.getBalance()<gift.getGprice()*giftNum){
			result.setRetMsg(false);
			result.setRetData("余额不足");
			result.setRetCode(I.MSG_NOT_SUFFICIENT_FOUND);
		}else{
			boolean flag = dao.givingGifts(uname,anchor,giftId,giftNum,currentWallet.getBalance(),gift.getGprice());
			if(flag){
				result.setRetCode(I.MSG_SUCCESS);
				result.setRetMsg(true);
				Wallet w = dao.getBalanceByName(uname);
				result.setRetData(w);
			}else{
				result.setRetMsg(false);
				result.setRetCode(I.MSG_GET_GIFTS_FAIL);
			}
		}
		JsonUtil.writeJsonToClient(result, response);
	}
}
