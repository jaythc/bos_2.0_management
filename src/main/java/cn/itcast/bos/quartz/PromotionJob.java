package cn.itcast.bos.quartz;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import cn.itcast.bos.service.take_delivery.PromotionService;

public class PromotionJob implements Job {

	@Autowired
	private PromotionService promotionService;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("活动过期处理程序   ");
		
		// 设置每分钟执行一次 ,  如果当前时间大于promotion 数据表的截止时间endDate那么就把status设置为2
		promotionService.updateStatus(new Date());
		
	}

}
