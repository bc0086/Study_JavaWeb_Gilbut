package com.javaweb.bcjin.common.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FileDownloadController {
	private static String ARTICLE_IMAGE_REPO = "c:\\03Workspace\\file_repo";
	
	@RequestMapping("/download")
	// 다운로드할 이미지 파일이름을 전달
	public void download(@RequestParam("imageFileName") String imageFileName,
							@RequestParam("articleNO") String articleNO,
							HttpServletResponse response) throws Exception {
		OutputStream out = response.getOutputStream();
		String downFile = ARTICLE_IMAGE_REPO + "\\" + articleNO + "\\" + imageFileName;
		File file = new File(downFile); // 다운로드할 파일 객체를 생성함
		
		response.setHeader("Cache-Control", "no-cache");
		response.addHeader("Content-dispotition", "attachment; fileName=" + imageFileName);
			// 헤더의 파일이름을 설정함
		
		FileInputStream in = new FileInputStream(file);
		byte[] buffer = new byte[1024*8];
		while(true) {
			int count = in.read(buffer);
			if(count == -1) break;
			out.write(buffer,0, count);
		} // 버퍼를 이용해 한번에 8Kbyte씩 브라우저로 전송함
		in.close();
		out.close();
	}
}
