package com.cxp.personalmanage.utils;

import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by ASUS on 2017/11/10.
 */
public class ResponseUtil {

    // 设置字符请求头
    public static void write(HttpServletResponse response, Object o) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.println(o.toString());
        out.flush();
        out.close();
    }

    // 下载导出设置
    public static void exportExcel(HttpServletResponse response, Workbook workbook, String fileName)
            throws Exception {
    	response.setHeader("content-type", "application/octet-stream");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + new String(fileName.getBytes("utf-8"), "iso8859-1"));
 //       response.setContentType("application/ynd.ms-excel;charset=UTF-8");
        response.setContentType("application/octet-stream;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        workbook.write(out);
        out.flush();
        workbook.close();
    }
}
