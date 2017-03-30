package cn.ucai.superwechat.dao;

import java.util.List;

import cn.ucai.superwechat.bean.GiftCount;
import cn.ucai.superwechat.bean.GiftStatementsBean;
import cn.ucai.superwechat.pojo.Gift;
import cn.ucai.superwechat.pojo.RechargeStatements;
import cn.ucai.superwechat.pojo.Wallet;

public interface ILiveDao {
	/**
	 * 获取全部礼物
	 * @return
	 */
	List<Gift> getAllGifts();

	/**
	 * 分页获取充值流水
	 * @param pageId
	 * @param pageSize
	 * @param uname
	 * @return
	 */
	List<RechargeStatements> getRechargeStatementsByPage(int pageId, int pageSize, String uname);

	/**
	 * 根据用户名获取余额
	 * @param uname
	 * @return
	 */
	Wallet getBalanceByName(String uname);

	/**
	 * 统计主播收到的全部礼物信息
	 * @param anchor
	 * @return
	 */
	List<GiftCount> getGiftStatementsByAnchor(String anchor);
	/**
	 * 根据Id获取某个Gift
	 * @param giftId
	 * @return
	 */
	Gift getGiftById(Integer giftId);
	
	/**
	 * 赠送礼物
	 * @param uname 用户名
	 * @param anchor 主播名
	 * @param giftId 礼物Id
 	 * @param giftNum 礼物数量
	 * @param balance 
	 * @param giftPrice 
	 * @return 是否赠送成功
	 */
	boolean givingGifts(String uname, String anchor, Integer giftId, Integer giftNum, Integer balance, Integer giftPrice);

	/**
	 * 充值
	 * @param uname
	 * @param balance
	 * @return
	 */
	boolean recharge(RechargeStatements rss,int balance);

	/**
	 * 获取token
	 * @return
	 */
	String getAuthorization();

	/**
	 * 更新token
	 * @param token
	 * @return
	 */
	boolean updateAuthorization(String token);
	/**
	 * 获取送礼流水
	 * @param pageId
	 * @param pageSize
	 * @param uname
	 * @return
	 */
	List<GiftStatementsBean> getGivingGiftStatementsByPage(int pageId, int pageSize, String uname);
	/**
	 * 收礼物流水
	 * @param pageId
	 * @param pageSize
	 * @param anchor
	 * @return
	 */
	List<GiftStatementsBean> getReceivingGiftStatementsByPage(int pageId, int pageSize, String anchor);
}
