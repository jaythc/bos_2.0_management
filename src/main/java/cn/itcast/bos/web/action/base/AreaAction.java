package cn.itcast.bos.web.action.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.service.base.AreaService;
import cn.itcast.bos.utils.PinYin4jUtils;
import cn.itcast.bos.web.action.common.BaseAction;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class AreaAction extends BaseAction<Area> {

	@Autowired
	private AreaService areaService;
	
	//接收上传的文件
	private File file ;

	public void setFile(File file) {
		this.file = file;
	}
	
	//分页查询
	@Action(value="area_pageQuery",results={@Result(name="success",type="json")})
	public String pageQuery(){
		Pageable pageable = new PageRequest(page-1, rows);
		Specification<Area> specification = new Specification<Area>() {

			@Override
			public Predicate toPredicate(Root<Area> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				//定义一个集合存放条件
				ArrayList<Predicate> list = new ArrayList<Predicate>();
				
				if (StringUtils.isNoneBlank(model.getProvince())) {
					Predicate p1 = cb.like(root.get("province").as(String.class), "%"+model.getProvince()+"%");
					list.add(p1);
				}
				if (StringUtils.isNoneBlank(model.getCity())) {
					Predicate p2 = cb.like(root.get("city").as(String.class), "%"+model.getCity()+"%");
					list.add(p2);
				}
				if (StringUtils.isNoneBlank(model.getDistrict())) {
					Predicate p3 = cb.like(root.get("district").as(String.class), "%"+model.getDistrict()+"%");
					list.add(p3);
				}
				//用CriteriaBuilder 的and方法, 拼接所有的条件 . 
				//由于里面传递的参数是Predicate的可变参数, 本质为一个数组, 
				//因此需要把list集合转为Predicate数组, 数组的长度最好为list集合的长度. 
				return cb.and(list.toArray(new Predicate[list.size()]));
			}
		};
		//调用业务层, 完成条件分页查询 
		Page<Area> pageData = areaService.findPageData(specification,pageable);
		this.pushPageDataValueStack(pageData);
		return SUCCESS;
	}
	
	
	@Action(value="area_batchImport")
	public String batchImport() throws IOException{
		//定义一个集合,用于保存xls中, 每一行的数据
		ArrayList<Area> areas = new ArrayList<Area>();
		
		//HSSF用于解析xls格式的文件 
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(file));
		
		HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
		
		for (Row row : sheet) {
			//跳过第一行
			if (row.getRowNum()==0) {
				continue;
			}
			//跳过最后一行
			if (row.getCell(0)==null||StringUtils.isBlank(row.getCell(0).getStringCellValue())) {
				break; //到达最后一行,结束方法
			}
			
			//遍历xls表格中每一行数据,  给Area对象, 封装参数  
			Area area = new Area();
			//getCell(a) 其中a代表 ,xls表格中的第几列, 从0 开始, 把列中,对应的数据,封装到area对象中
			area.setId(row.getCell(0).getStringCellValue());
			area.setProvince(row.getCell(1).getStringCellValue());
			area.setCity(row.getCell(2).getStringCellValue());
			area.setDistrict(row.getCell(3).getStringCellValue());
			area.setPostcode(row.getCell(4).getStringCellValue());
			
			//pinyin4j生成城市的编码和简码
			
			//获取省市区
			String province = area.getProvince();
			String city = area.getCity();
			String district = area.getDistrict();
			
			//减一 是为了  去掉省市区 
			province= province.substring(0, province.length()-1);
			city = city.substring(0, city.length()-1);
			district=district.substring(0, district.length()-1);
			
			//获取省市区的简码 的数组
			String[] headArray = PinYin4jUtils.getHeadByString(province+city+district);
			
			//将数组遍历并拼接
			StringBuffer buffer = new StringBuffer();
			for (String headStr : headArray) {
				buffer.append(headStr);
			}
			//StringBuffer转为String
			String shortCode= buffer.toString();
			area.setShortcode(shortCode);
			
			//获取城市编码
			String cityCode = PinYin4jUtils.hanziToPinyin(city);
			area.setCitycode(cityCode);
			
			//将这一行的数据,添加到集合中
			areas.add(area);
		}
		
		areaService.saveBatch(areas);
		hssfWorkbook.close();
		return NONE;//返回none是因为,保存区域信息之后, 不需要任何的操作
	}
	
	
}
