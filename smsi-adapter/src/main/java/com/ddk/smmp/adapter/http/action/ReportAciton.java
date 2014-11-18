package com.ddk.smmp.adapter.http.action;

import java.util.List;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ddk.smmp.adapter.dao.UserMode;
import com.ddk.smmp.adapter.http.entity.ReportRequest;
import com.ddk.smmp.adapter.http.entity.ReportResponse;
import com.ddk.smmp.adapter.http.entity.helper.Body;
import com.ddk.smmp.adapter.http.entity.helper.EMethod;
import com.ddk.smmp.adapter.http.entity.helper.EMsgType;
import com.ddk.smmp.adapter.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.adapter.model.Report;
import com.ddk.smmp.adapter.service.DbService;
import com.ddk.smmp.adapter.utils.Tuple2;
import com.ddk.smmp.util.Base64;

/**
 * @author leeson 2014年7月9日 下午6:11:49 li_mr_ceo@163.com <br>
 * 
 */
public class ReportAciton extends BaseAction {
	private static final Logger logger = LoggerFactory.getLogger(ReportAciton.class);
	
	@SuppressWarnings("rawtypes")
	@Get
	public String GET_Report() throws Exception {
		Tuple2<UserMode, Body> tuple2 = getBody(EMsgType.REPORT, EMethod.GET, null);
		if(null != tuple2 && null != tuple2.e2){
			ReportRequest request = (ReportRequest)tuple2.e2;
			logger.info("UID [" + tuple2.e1.getUserName() + "] " + request.toString());
			
			if(tuple2.e1.getPwd().equalsIgnoreCase(Base64.encrypt(request.getPassWord(), Base64.KEY))){
				DatabaseTransaction trans = new DatabaseTransaction(true);
				try {
					DbService dbService = new DbService(trans);
					List<Report> reports = dbService.getReportList(tuple2.e1.getUserName());
					trans.commit();
					
					Report[] reportArray = new Report[reports.size()];
					for(int i = 0; i < reportArray.length; i++){
						reportArray[i] = reports.get(i);
					}
					ReportResponse reportResponse = new ReportResponse(reportArray.length, reportArray);
					return reportResponse.toJson(tuple2.e1.getKey());
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
	public Representation POST_Report(Representation representation) throws Exception {
		Tuple2<UserMode, Body> tuple2 = getBody(EMsgType.REPORT, EMethod.POST, representation);
		if(null != tuple2 && null != tuple2.e2){
			ReportRequest request = (ReportRequest)tuple2.e2;
			logger.info("UID [" + tuple2.e1.getUserName() + "] " + request.toString());
			
			if(tuple2.e1.getPwd().equalsIgnoreCase(Base64.encrypt(request.getPassWord(), Base64.KEY))){
				DatabaseTransaction trans = new DatabaseTransaction(true);
				try {
					DbService dbService = new DbService(trans);
					List<Report> reports = dbService.getReportList(tuple2.e1.getUserName());
					trans.commit();
					
					Report[] reportArray = new Report[reports.size()];
					for(int i = 0; i < reportArray.length; i++){
						reportArray[i] = reports.get(i);
					}
					ReportResponse reportResponse = new ReportResponse(reportArray.length, reportArray);
					return new StringRepresentation(reportResponse.toJson(tuple2.e1.getKey()));
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