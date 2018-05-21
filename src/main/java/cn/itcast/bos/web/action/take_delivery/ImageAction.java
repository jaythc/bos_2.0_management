package cn.itcast.bos.web.action.take_delivery;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class ImageAction extends ActionSupport {
	private static final long serialVersionUID = 7250560888184285432L;
	//处理文件上传的action
	private File imgFile ;
	private String imgFileFileName;
	private String imgFileContentType ;
	
	public void setImgFile(File imgFile) {
		this.imgFile = imgFile;
	}
	public void setImgFileFileName(String imgFileFileName) {
		this.imgFileFileName = imgFileFileName;
	}
	public void setImgFileContentType(String imgFileContentType) {
		this.imgFileContentType = imgFileContentType;
	}
	
	@Action(value="image_upload",results={@Result(name="success",type="json")})
	public String imageUpload() throws IOException{
		System.err.println("文件  "+ imgFile);
		System.err.println("文件名   "+imgFileFileName);
		System.err.println("文件类型     "+ imgFileContentType);
		// 获取upload文件夹的绝对路径, 即硬盘中的路径
		String savePath = ServletActionContext.getServletContext().getRealPath("/upload/");
		System.err.println("savePath   "+savePath );
		// 获取项目根路径下的upload的路径
		String saveUrl = ServletActionContext.getRequest().getContextPath()+"/upload/";
		System.err.println(saveUrl);  // 打印结果:    /bos_management/upload/
		// 生成随机的图片名
		UUID uuid = UUID.randomUUID();
		// substring  获取到文件的后缀名
		// 经测试是从文件的. 开始截取, 保留点 
		String extName = imgFileFileName.substring(imgFileFileName.lastIndexOf("."));
		// 文件名的索引    10
		System.err.println("文件名的索引    "+ imgFileFileName.lastIndexOf("."));
		// 拼接文件名  
		String randomFileName = uuid+ extName ;
		// 保存图片     注意不要忘了斜杠 
		FileUtils.copyFile(imgFile, new File(savePath + "/" +randomFileName));
		// 返回浏览器 文件上传成功
		HashMap<String, Object> result = new HashMap<String,Object>();
		// 如果保存图片失败, 存入0 
		result.put("error", 0);
		// 返回给浏览器 图片的地址和文件名
		result.put("url", saveUrl+ randomFileName);
		ActionContext.getContext().getValueStack().push(result);
		return SUCCESS ;
	}
	
	// image_manage
	//  图片管理  
	@Action(value="image_manage",results={
			@Result(name="success",type="json")})
	public String manage(){
		// 获取的图片保存的磁盘路径
		String rootPath = ServletActionContext.getServletContext().getRealPath("/upload/");
		System.err.println("savePath   "+rootPath );
		// 获取项目根路径下的upload的路径.   
		// 获取的访问路径
		String rootUrl = ServletActionContext.getRequest().getContextPath()+"/upload/";
		
		// ArrayList集合中的泛型为map集合 
		ArrayList<Map<String, Object>> fileList = new ArrayList<Map<String, Object>>();
		
		// 当前上传的目录
		File currentPathFile = new File(rootPath);
		// 定义数组, 规定图片的类型范围
		String[] fileTypes = {"gif", "jpg", "jpeg", "png", "bmp"};
		
		// 遍历上传的目录内的文件 
		if (currentPathFile.listFiles() != null) {
			for (File file : currentPathFile.listFiles()) {
				Map<String, Object> hash = new HashMap<String, Object>();
				String fileName = file.getName();
				if (file.isDirectory()) {
					hash.put("is_dir", true);
					hash.put("has_file", (file.listFiles() != null));
					hash.put("filesize", 0L);
					hash.put("is_photo", false);
					hash.put("filetype", "");
				} else if (file.isFile()) {
					String fileExt = fileName.substring(
							fileName.lastIndexOf(".") + 1).toLowerCase();
					hash.put("is_dir", false);
					hash.put("has_file", false);
					hash.put("filesize", file.length());
					hash.put("is_photo", Arrays.<String>asList(fileTypes)
							.contains(fileExt));
					hash.put("filetype", fileExt);
				}
				hash.put("filename", fileName);
				hash.put("datetime",
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file
								.lastModified()));
				fileList.add(hash);
			}
		}
		// 返回给 kindeditor 数据 
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("moveup_dir_path", "");
		result.put("current_dir_path", rootPath);
		result.put("current_url", rootUrl);
		result.put("total_count", fileList.size());
		result.put("file_list", fileList);
		ActionContext.getContext().getValueStack().push(result);
		
		return SUCCESS ; 
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
