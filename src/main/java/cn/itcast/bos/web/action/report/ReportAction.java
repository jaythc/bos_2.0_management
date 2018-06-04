package cn.itcast.bos.web.action.report;

import cn.itcast.bos.domain.take_delivery.WayBill;
import cn.itcast.bos.service.take_delivery.WayBillService;
import cn.itcast.bos.utils.FileUtils;
import cn.itcast.bos.web.action.common.BaseAction;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.management.Query;
import javax.servlet.ServletOutputStream;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Controller
@Scope("prototype")
@ParentPackage("json-default")
@Namespace("/")
public class ReportAction extends BaseAction<WayBill> {

    private final Logger logger = LoggerFactory.getLogger(ReportAction.class);

    @Autowired
    private WayBillService wayBillService ;

    @Action("report_exportXls")
    public String exportxls() throws IOException {
        // 查询出满足条件的数据,导出到xls中
       List<WayBill> wayBills = wayBillService.findWayBills(model);

       //生成excle文件
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet sheet = hssfWorkbook.createSheet("运单数据");

        //表头
        HSSFRow headRow = sheet.createRow(0);
        headRow.createCell(0).setCellValue("运单号");
        headRow.createCell(1).setCellValue("寄件人");
        headRow.createCell(2).setCellValue("寄件人电话");
        headRow.createCell(3).setCellValue("寄件人地址");
        headRow.createCell(4).setCellValue("收件人");
        headRow.createCell(5).setCellValue("收件人电话");
        headRow.createCell(6).setCellValue("收件人地址");

        // 遍历封装表格数据
        for (WayBill wayBill : wayBills) {
            /**
             * 获取表单中最后一行的编号。 由于excel文件格式的特质，
             * 如果调用此方法的结果为零，则无法判断这意味着表单中是否有零行，或者位置为零。
             * 对于这种情况，另外调用getPhysicalNumberOfRows（）来判断零位是否有一行。
             */
            HSSFRow dataRow = sheet.createRow(sheet.getLastRowNum() + 1);
            logger.info("xls表单的最后一行+1的值:  " + dataRow);
            dataRow.createCell(0).setCellValue(wayBill.getWayBillNum());
            dataRow.createCell(1).setCellValue(wayBill.getSendName());
            dataRow.createCell(2).setCellValue(wayBill.getSendMobile());
            dataRow.createCell(3).setCellValue(wayBill.getSendAddress());
            dataRow.createCell(4).setCellValue(wayBill.getRecName());
            dataRow.createCell(5).setCellValue(wayBill.getRecMobile());
            dataRow.createCell(6).setCellValue(wayBill.getRecAddress());
        }
        // 下载文件,设置两个头,一个流

        // 下载导出,设置头信息
        ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
        // 文件名
        String filename = "运单数据.xls";
        //浏览器的名称
        String agent = ServletActionContext.getRequest().getHeader("user-agent");
        filename = FileUtils.encodeDownloadFilename(filename, agent);
        ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment;filename=" + filename);
        ServletOutputStream outputStream = ServletActionContext.getResponse().getOutputStream();
        hssfWorkbook.write(outputStream);

        //关闭
        hssfWorkbook.close();
        return  NONE;
    }

    @Autowired
    private DataSource dataSource;
    //report_exportJasperPdf
    @Action("report_exportJasperPdf")
    public String exportJasperPdf() throws IOException, JRException, SQLException {
        // 查询出满足当前条件的结果数据
        List<WayBill> wayBills = wayBillService.findWayBills(model);

        //下载导出设置头信息
        ServletActionContext.getResponse().setContentType("application/pdf");
        String filename = "运单数据.pdf";
        String agent = ServletActionContext.getRequest().getHeader("user-agent");
        filename = FileUtils.encodeDownloadFilename(filename, agent);
        ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment;filename=" + filename);

        //根据目标, 生成pdf
        // 读取模板
        String jrxml=ServletActionContext.getServletContext().getRealPath("/WEB-INF/jasper/waybill.jrxml");
        //创建 JasperReport对象
        JasperReport report = JasperCompileManager.compileReport(jrxml);


        //设置模板数据
        // 一种是手动设置的变量, 一种是数据库中的变量
        HashMap<String, Object> parameters = new HashMap<>();
        // 设置Parameter的变量
        parameters.put("company", "顺丰速运");

        //设置Field变量

        // 生成数据库中数据的生成pdf报表的写法
        //JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters,dataSource.getConnection());

        // 把查询的数据,生成报表的写法
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters,new JRBeanCollectionDataSource(wayBills));

        // 生成pdf客户端
        //下载的pdf不能打开, 程序执行到这一步就不往下走了 原因是没有加入itext的jar包
        JRPdfExporter exporter = new JRPdfExporter();

        logger.info("执行pdf输出流.............");
        //设置打印对象
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        //设置输出流
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, ServletActionContext.getResponse().getOutputStream());
        exporter.exportReport(); //导出
        //关闭连接
        ServletActionContext.getResponse().getOutputStream().close();
        return NONE;
    }

}
