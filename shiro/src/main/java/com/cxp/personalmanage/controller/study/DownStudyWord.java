package com.cxp.personalmanage.controller.study;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.cxp.personalmanage.common.Constant;

@Controller
@RequestMapping(value = "/download")
public class DownStudyWord {

	private static final Logger logger = LoggerFactory.getLogger(DownStudyWord.class);

	@RequestMapping(value = "/springBootWord")
	public String downloadSpringBoot(HttpServletRequest request, HttpServletResponse response) {

		response.setHeader("content-type", "application/octet-stream");
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=" + Constant.SPRINGBOOT_FILENAME);
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("word/" + "SpringBoot.docx");
		if (null != inputStream) {
			byte[] buffer = new byte[1024];
			// FileInputStream fis = null;
			BufferedInputStream bis = null;
			OutputStream os = null;
			try {
				// fis = new FileInputStream(file);
				bis = new BufferedInputStream(inputStream);
				os = response.getOutputStream();
				int i = bis.read(buffer);
				while (i != -1) {
					os.write(buffer, 0, i);
					os.flush();
					i = bis.read(buffer);
				}
				System.out.println("success");
			} catch (Exception e) {
				logger.info("下载文件出错, " + e.getMessage());
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (bis != null) {
					try {
						bis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
}
