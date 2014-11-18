package com.ddk.smmp.channel.maiyuan_http.handler;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ddk.smmp.utils.JaxbUtils;

/**
 * @author leeson 2014年10月22日 下午12:02:25 li_mr_ceo@163.com <br>
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "returnsms")
@XmlType
public class ReportRsp {
	@XmlElement(name = "statusbox")
	private List<Report> reports = new LinkedList<Report>();
	@XmlElement(name = "callbox")
	private List<Deliver> delivers = new LinkedList<Deliver>();
	@XmlElement(name = "errorstatus")
	private ReportErr reportErr;
	
	public List<Deliver> getDelivers() {
		return delivers;
	}

	public void setDelivers(List<Deliver> delivers) {
		this.delivers = delivers;
	}

	public List<Report> getReports() {
		return reports;
	}

	public void setReports(List<Report> reports) {
		this.reports = reports;
	}

	public ReportErr getReportErr() {
		return reportErr;
	}

	public void setReportErr(ReportErr reportErr) {
		this.reportErr = reportErr;
	}

	@Override
	public String toString() {
		return "ReportRsp [reports=" + reports + ", delivers=" + delivers + ", reportErr=" + reportErr + "]";
	}

	public static void main(String[] args) {
		ReportRsp reportRsp = new ReportRsp();
		
		Report report1 = new Report();
		report1.setMobile("15023239810");
		report1.setTaskid("1212");
		report1.setStatus("10");
		report1.setReceivetime("2011-12-02 22:12:11");
		report1.setErrorcode("DELIVRD");
		report1.setExtno("01");
		
		Report report2 = new Report();
		report2.setMobile("15023239810");
		report2.setTaskid("1212");
		report2.setStatus("10");
		report2.setReceivetime("2011-12-02 22:12:11");
		report2.setErrorcode("DELIVRD");
		report2.setExtno("01");
		
		ReportErr reportErr = new ReportErr();
		reportErr.setError("1");
		reportErr.setRemark("用户名或密码不能为空");
		
		reportRsp.reports.add(report1);
		reportRsp.reports.add(report2);
		
		reportRsp.setReportErr(reportErr);
		
		System.out.println(JaxbUtils.convertToXml(reportRsp));
		
		System.out.println(JaxbUtils.converyToJavaBean(JaxbUtils.convertToXml(reportRsp), ReportRsp.class));
	}
}