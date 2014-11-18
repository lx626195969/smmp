package com.ddk.smmp.adapter.http.action;

import java.util.List;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ddk.smmp.adapter.dao.UserMode;
import com.ddk.smmp.adapter.http.entity.DeliverRequest;
import com.ddk.smmp.adapter.http.entity.DeliverResponse;
import com.ddk.smmp.adapter.http.entity.helper.Body;
import com.ddk.smmp.adapter.http.entity.helper.EMethod;
import com.ddk.smmp.adapter.http.entity.helper.EMsgType;
import com.ddk.smmp.adapter.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.adapter.model.Deliver;
import com.ddk.smmp.adapter.service.DbService;
import com.ddk.smmp.adapter.utils.Tuple2;
import com.ddk.smmp.util.Base64;

/**
 * @author leeson 2014年7月9日 下午6:11:49 li_mr_ceo@163.com <br>
 * 
 */
public class DeliverAciton extends BaseAction {
	private static final Logger logger = LoggerFactory.getLogger(DeliverAciton.class);
	
	@SuppressWarnings("rawtypes")
	@Get
	public String GET_Deliver() throws Exception {
		Tuple2<UserMode, Body> tuple2 = getBody(EMsgType.DELIVER, EMethod.GET, null);
		if(null != tuple2 && null != tuple2.e2){
			DeliverRequest request = (DeliverRequest)tuple2.e2;
			logger.info("UID [" + tuple2.e1.getUserName() + "] " + request.toString());
			
			if(tuple2.e1.getPwd().equalsIgnoreCase(Base64.encrypt(request.getPassWord(), Base64.KEY))){
				DatabaseTransaction trans = new DatabaseTransaction(true);
				try {
					DbService dbService = new DbService(trans);
					List<Deliver> delivers = dbService.getDeliverList(tuple2.e1.getUserName());
					trans.commit();
					
					Deliver[] deliverArray = new Deliver[delivers.size()];
					for(int i = 0; i < deliverArray.length; i++){
						deliverArray[i] = delivers.get(i);
					}
					DeliverResponse deliverResponse = new DeliverResponse(deliverArray.length, deliverArray);
					return deliverResponse.toJson(tuple2.e1.getKey());
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
	public Representation POST_Deliver(Representation representation) throws Exception {
		Tuple2<UserMode, Body> tuple2 = getBody(EMsgType.DELIVER, EMethod.POST, representation);
		if(null != tuple2 && null != tuple2.e2){
			DeliverRequest request = (DeliverRequest)tuple2.e2;
			logger.info("UID [" + tuple2.e1.getUserName() + "] " + request.toString());
			
			if(tuple2.e1.getPwd().equalsIgnoreCase(Base64.encrypt(request.getPassWord(), Base64.KEY))){
				DatabaseTransaction trans = new DatabaseTransaction(true);
				try {
					DbService dbService = new DbService(trans);
					List<Deliver> delivers = dbService.getDeliverList(tuple2.e1.getUserName());
					trans.commit();
					
					Deliver[] deliverArray = new Deliver[delivers.size()];
					for(int i = 0; i < deliverArray.length; i++){
						deliverArray[i] = delivers.get(i);
					}
					DeliverResponse deliverResponse = new DeliverResponse(deliverArray.length, deliverArray);
					return new StringRepresentation(deliverResponse.toJson(tuple2.e1.getKey()));
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