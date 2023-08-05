package com.bx.reggie.controller;

import com.bx.reggie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/2 21:32
 */
@Slf4j
@RestController
@SuppressWarnings({"all"})
@RequestMapping("/common")
public class UploadFileController {
	//绑定配置文件中的路径
	@Value("${reggie.path}")
	private String basePath;
	
	//文件上传到服务器
	@PostMapping("/upload")
	public R<String> upload(MultipartFile file) throws IOException {
		//获取文件原始名
		String originalFilename = file.getOriginalFilename();
		//获取文件后缀名
		originalFilename = originalFilename.substring(originalFilename.lastIndexOf("."));
		//给文件重新命名
		String fileName = UUID.randomUUID().toString() + originalFilename;
		//判断路径是否存在，不存在则需创建
		File filePath = new File(basePath);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
		//将上传的文件保存到磁盘
		file.transferTo(new File(basePath + fileName));
		//将文件名字返回给前端以用来下载展示
		return R.success(fileName);
	}
	
	
	//文件下载
	@GetMapping("/download")
	//前端需要下载的文件名和需要返回给前端的文件内容 对象
	public void download(String name, HttpServletResponse response) {
		try {
			//创建文件输入流来读取前端需要从磁盘下载的文件
			FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
			//创建文件输出流将文件写回给前端
			ServletOutputStream outputStream = response.getOutputStream();
			
			int lenth = 0;
			//将读取的文件内容放到字节数组里
			byte[] bytes = new byte[1024];
			//多次循环读取，直到文件内容被读完
			while ((lenth = fileInputStream.read(bytes)) != -1) {
				//将每次读取到的文件写回给前端
				outputStream.write(bytes, 0, lenth);
				//刷新一下
				outputStream.flush();
			}
			//关闭资源
			fileInputStream.close();
			outputStream.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
