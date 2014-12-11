package com.ddk.smmp.adapter.http.action;

import java.util.List;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ddk.smmp.adapter.dao.UserMode;
import com.ddk.smmp.adapter.http.entity.BalanceRequest;
import com.ddk.smmp.adapter.http.entity.BalanceResponse;
import com.ddk.smmp.adapter.http.entity.helper.Body;
import com.ddk.smmp.adapter.http.entity.helper.EMethod;
import com.ddk.smmp.adapter.http.entity.helper.EMsgType;
import com.ddk.smmp.adapter.http.entity.helper.ProductBalance;
import com.ddk.smmp.adapter.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.adapter.service.DbService;
import com.ddk.smmp.adapter.utils.Tuple2;
import com.ddk.smmp.util.Base64;

/**
 * 
 * @author leeson 2014年7月10日 下午1:41:29 li_mr_ceo@163.com <br>
 *
 */
public class BalanceAciton extends BaseAction {
	private static final Logger logger = LoggerFactory.getLogger((BalanceAciton.class).getSimpleName());
	
	@SuppressWarnings("rawtypes")
	@Get
	public String GET_Balance() throws Exception {
		Tuple2<UserMode, Body> tuple2 = getBody(EMsgType.BALANCE, EMethod.GET, null);
		if(null != tuple2 && null != tuple2.e2){
			BalanceRequest request = (BalanceRequest)tuple2.e2;
			logger.info("UID [" + tuple2.e1.getUserName() + "] " + request.toString());
			
			if(tuple2.e1.getPwd().equalsIgnoreCase(Base64.encrypt(request.getPassWord(), Base64.KEY))){
				DatabaseTransaction trans = new DatabaseTransaction(true);
				try {
					DbService dbService = new DbService(trans);
					int availableBalance = dbService.getAvailableBalance(tuple2.e1.getUserName());
					List<String> productBalanceList = dbService.getProductBalance(tuple2.e1.getUserName());
					trans.commit();
					
					ProductBalance[] productBalanceArray = new ProductBalance[productBalanceList.size()];
					for(int i = 0; i < productBalanceArray.length;i++){
						String[] strArray = productBalanceList.get(i).split("#");
						productBalanceArray[i] = new ProductBalance(Integer.parseInt(strArray[0]), Integer.parseInt(strArray[1]));
					}
					
					BalanceResponse balanceResponse = new BalanceResponse(availableBalance, productBalanceArray);
					return balanceResponse.toJson(tuple2.e1.getKey());
				} catch (Exception ex) {
					trans.rollback();
				} finally {
					trans.close();
				}
			}
		}
		
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Post
	public Representation POST_Balance(Representation representation) throws Exception {
		Tuple2<UserMode, Body> tuple2 = getBody(EMsgType.BALANCE, EMethod.POST, representation);
		if(null != tuple2 && null != tuple2.e2){
			BalanceRequest request = (BalanceRequest)tuple2.e2;
			logger.info("UID [" + tuple2.e1.getUserName() + "] " + request.toString());
			
			if(tuple2.e1.getPwd().equalsIgnoreCase(Base64.encrypt(request.getPassWord(), Base64.KEY))){
				DatabaseTransaction trans = new DatabaseTransaction(true);
				try {
					DbService dbService = new DbService(trans);
					int availableBalance = dbService.getAvailableBalance(tuple2.e1.getUserName());
					List<String> productBalanceList = dbService.getProductBalance(tuple2.e1.getUserName());
					trans.commit();
					
					ProductBalance[] productBalanceArray = new ProductBalance[productBalanceList.size()];
					for(int i = 0; i < productBalanceArray.length;i++){
						String[] strArray = productBalanceList.get(i).split("#");
						productBalanceArray[i] = new ProductBalance(Integer.parseInt(strArray[0]), Integer.parseInt(strArray[1]));
					}
					
					BalanceResponse balanceResponse = new BalanceResponse(availableBalance, productBalanceArray);
					return new StringRepresentation(balanceResponse.toJson(tuple2.e1.getKey()));
				} catch (Exception ex) {
					trans.rollback();
				} finally {
					trans.close();
				}
			}
		}
		
		return null;
	}
}