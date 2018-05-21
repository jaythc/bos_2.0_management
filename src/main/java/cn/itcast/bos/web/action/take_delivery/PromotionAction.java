package cn.itcast.bos.web.action.take_delivery;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.take_delivery.Promotion;
import cn.itcast.bos.service.take_delivery.PromotionService;
import cn.itcast.bos.web.action.common.BaseAction;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class PromotionAction extends BaseAction<Promotion> {

	@Autowired
	private PromotionService promotionService;
	
	//属性驱动, 获取宣传图片
	private File titleImgFile;
	// 获取图片名 
	private String titleImgFileFileName;
	
	public void setTitleImgFile(File titleImgFile) {
		this.titleImgFile = titleImgFile;
	}

	public void setTitleImgFileFileName(String titleImgFileFileName) {
		this.titleImgFileFileName = titleImgFileFileName;
	}

	@Action(value="promotion_save",results={
			@Result(name="success",location="pages/take_delivery/promotion.html",type="redirect")
	})
	public String savePromotion() throws IOException{
		
		String savePath = ServletActionContext.getServletContext().getRealPath("/upload/");
		System.err.println("savePath   "+savePath );
		// 获取项目根路径下的upload的路径
		String saveUrl = ServletActionContext.getRequest().getContextPath()+"/upload/";
		System.err.println(saveUrl);  // 打印结果:    /bos_management/upload/
		// 生成随机的图片名
		UUID uuid = UUID.randomUUID();
		// substring  获取到文件的后缀名
		// 经测试是从文件的. 开始截取, 保留点 
		String extName = titleImgFileFileName.substring(titleImgFileFileName.lastIndexOf("."));
		// 文件名的索引    10
		System.err.println("文件名的索引    "+ titleImgFileFileName.lastIndexOf("."));
		// 拼接文件名  
		String randomFileName = uuid+ extName ;
		// 保存图片     注意不要忘了斜杠 
		FileUtils.copyFile(titleImgFile, new File(savePath + "/" +randomFileName));
		
		// 将图片 的访问路径设置在model中 
		String titleImgURLPath =saveUrl+randomFileName;
		model.setTitleImg(titleImgURLPath);
		
		// 调用业务层, 封装保存数据  . promotion的其他属性由模型驱动来封装
		promotionService.save(model);
		
		return SUCCESS ;
	}
	
	
	//promotion_pageQuery
	@Action(value="promotion_pageQuery",results={
			@Result(name="success",type="json")
	})
	public String pageQuery() {
		Pageable pageable  = new PageRequest(page-1, rows);
		
		Page<Promotion> pageData = promotionService.findPageData(pageable);
		
		this.pushPageDataValueStack(pageData);
		
		return SUCCESS ;
	}
	
}

