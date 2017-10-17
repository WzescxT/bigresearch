package com.monetware.controller;

import com.monetware.model.analysis.TextLibrary;
import com.monetware.model.common.RtInfo;
import com.monetware.service.analyze.TextLibraryService;
import com.monetware.util.AuthUtil;
import com.monetware.util.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** 
 *@author  venbillyu 
 *@date 创建时间：2016年12月27日 下午3:17:53 
 *@describle 
 */
@RequestMapping("/analysis")
@Controller
public class AnalysisTextLibraryController {
	@Autowired
	private TextLibraryService textLibraryService;
	//获取当前用户文本库
	@RequestMapping("/getTextLibraries")
	@ResponseBody
	public RtInfo getTextLibraries(@RequestBody HashMap<String, Object> queryMap,@RequestHeader HttpHeaders headers) {
		String token = headers.getFirst("Authorization");
		int userId=AuthUtil.parseToken(token);
		long pageNow=Long.decode(queryMap.get("pageNow").toString());
		long pageSize=Long.decode(queryMap.get("pageSize").toString());
		String name = queryMap.get("name").toString();
		RtInfo rtInfo=new RtInfo();
		Map<String, Object>infoMap=new HashMap<String, Object>();
		
		List<TextLibrary> textLibraries=textLibraryService.getUserTextLibraries(name,userId, pageNow, pageSize);
		
		infoMap.put("textLibraries", textLibraries);
		
		long bigTotalItems=textLibraryService.getUserTextLibrariesNo(userId);
		long thisMonthItems=textLibraryService.getMonthTextLibraryNo(userId,name);
		
		infoMap.put("bigTotalItems",bigTotalItems );
		infoMap.put("thisMonthItems",thisMonthItems );

    	rtInfo.setRt_info(infoMap);
		return rtInfo;
		
	}
	//获取当前用户所有文本库
	@RequestMapping("/getUserAllTextLibraries")
	@ResponseBody
	public RtInfo getUserAllTextLibraries(@RequestHeader HttpHeaders headers) {
		RtInfo rtInfo=new RtInfo();
		try {
			String token = headers.getFirst("Authorization");
			int userId=AuthUtil.parseToken(token);
			List<TextLibrary> textLibraries=textLibraryService.getUserAllTextLibraries(userId);
			rtInfo.setRt_info(textLibraries);
		} catch (Exception e) {
			rtInfo.setError_code(1);
			rtInfo.setError_msg("您还没有文本库可供选用");
			e.printStackTrace();
		}
		return rtInfo;
	}
	//创建文本库
	@RequestMapping("/createTextLibrary")
	@ResponseBody
	public RtInfo createTextLibrary(@RequestBody TextLibrary textLibrary,@RequestHeader HttpHeaders headers) {
		RtInfo rtInfo= new RtInfo();
		try {
			String token = headers.getFirst("Authorization");
			int userId=AuthUtil.parseToken(token);
			textLibrary.setCreateUser(userId);
			Map<String, Object>infoMap=new HashMap<String, Object>();
			textLibraryService.createTextLibrary(textLibrary);
			rtInfo.setRt_msg("文本库创建成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rtInfo.setError_code(1);
			rtInfo.setError_msg("处理失败");
		}
		return rtInfo;
		
	}
	
/*
	//上传文件到文本库	(文本保存)
	@RequestMapping(value = "/uploadTextLibrary", method = RequestMethod.POST)
	@ResponseBody
	public RtInfo uploadTextLibrary(HttpServletRequest request, HttpServletResponse res) {
		System.out.println("=======>in");
		RtInfo rtInfo=new RtInfo();
		rtInfo.setRt_msg("文本上传成功");
		// 接收参数
		try {
			long textLibraryId = Long.decode(request.getParameter("textLibraryId"));
			String fileType=request.getParameter("fileType");
			String columnNames=request.getParameter("columnNames");
			//更改文本库状态
			TextLibrary tl=new TextLibrary();
			tl.setId(textLibraryId);
			tl.setImportStatus(1);
			textLibraryService.updateTextLibrary(tl);
			
			String goalFilePath = "D://bigresearch/textlibrary/"+textLibraryId+".txt";
			File goalFile=new File(goalFilePath);
			//创建文本
			if (!goalFile.exists()) {
				if (!goalFile.getParentFile().exists()) {
					// 如果目标文件所在的目录不存在，则创建父目录
					goalFile.getParentFile().mkdirs();
				}
				goalFile.createNewFile();
			}
			System.out.println("========>"+textLibraryId);
			System.out.println("========>"+fileType);
			System.out.println("========>"+columnNames);
			// 解析器解析request的上下文
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
					request.getSession().getServletContext());
			// 先判断request中是否包涵multipart类型的数据，
			if (multipartResolver.isMultipart(request)) {
				
				// 再将request中的数据转化成multipart类型的数据
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				Iterator<String> iter = multiRequest.getFileNames();
				while (iter.hasNext()) {
					// 这里的name为fileItem的alias属性值，相当于form表单中name
					String name = iter.next();
					// 根据name值拿取文件
					MultipartFile file = multiRequest.getFile(name);
					InputStream is=file.getInputStream();
					if (file != null) {
						if (fileType.contains("txt")) {
							FileOpearte.writeJsonLine(is, goalFilePath);
							
						}else if (fileType.contains("xls")) {
							textLibraryService.importExcel(is, goalFilePath, columnNames);
						}else {
							
						}
						String fileName = file.getOriginalFilename();
						System.out.println("=========>"+fileName);
					}
				}
				long lineNo=FileOpearte.getLineNo(goalFilePath);
				TextLibrary tl2=new TextLibrary();
				tl.setId(textLibraryId);
				tl.setLineNo(lineNo);
				tl.setImportStatus(2);
				textLibraryService.updateTextLibrary(tl);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return rtInfo;
	}
	
	*/
	
	
	
	
	//上传文件到文本库
	@RequestMapping(value = "/uploadTextLibrary", method = RequestMethod.POST)
	@ResponseBody
	public RtInfo uploadTextLibrary(HttpServletRequest request, HttpServletResponse res) {
		System.out.println("=======>in");
		RtInfo rtInfo=new RtInfo();
		rtInfo.setRt_msg("文本上传成功");
		// 接收参数
		try {
			long textLibraryId = Long.decode(request.getParameter("textLibraryId"));
			System.out.println("文本库id=======>"+textLibraryId);
			String columnNames=request.getParameter("columnNames");
			//更改文本库状态
			TextLibrary tl=new TextLibrary();
			tl.setId(textLibraryId);
			tl.setImportStatus(1);
			textLibraryService.updateTextLibrary(tl);
			//判断是否建表，未见表则创建文本
			System.out.println("========>"+textLibraryId);
			System.out.println("========>"+columnNames);
			// 解析器解析request的上下文
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
					request.getSession().getServletContext());
			// 先判断request中是否包涵multipart类型的数据，
			if (multipartResolver.isMultipart(request)) {
				// 再将request中的数据转化成multipart类型的数据
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				Iterator<String> iter = multiRequest.getFileNames();
				while (iter.hasNext()) {
					// 这里的name为fileItem的alias属性值，相当于form表单中name
					String name = iter.next();
					// 根据name值拿取文件
					MultipartFile file = multiRequest.getFile(name);
					if (file != null&&!file.isEmpty()) {
						//上传前需要删除已经分词数据，重新整理分析数据
						
						
						
						
						
						
						InputStream is=file.getInputStream();
						ExcelUtil excelUtil=new ExcelUtil(is);
						String header[]=textLibraryService.getColumns(excelUtil, columnNames);
						
						
						
						
						
						String sql="";
						for (int i = 0; i < header.length; i++) {
							sql+="`"+header[i]+"` longtext,";
						}
						System.out.println("=====>"+sql);
						textLibraryService.createTextLibraryInfoTable(textLibraryId,sql);
						textLibraryService.importExcel(excelUtil, textLibraryId, columnNames);
						
						
						
						
						
						
						
						
						
					}
				}
				long lineNo=textLibraryService.getTextLibraryInfoNo(textLibraryId);
				TextLibrary tl2=new TextLibrary();
				tl2.setId(textLibraryId);
				tl2.setLineNo(lineNo);
				tl2.setImportStatus(2);
				textLibraryService.updateTextLibrary(tl2);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return rtInfo;
	}

	
	//删除文本
	@RequestMapping(value = "/deleteText", method = RequestMethod.POST)
	@ResponseBody
	public RtInfo deleteText(@RequestBody HashMap<String, Long> paramMap) {
		RtInfo rtInfo=new RtInfo();
		rtInfo.setRt_msg("文本删除成功");
		long textLibraryId=paramMap.get("textLibraryId");
		textLibraryService.deleteText(textLibraryId);
		return rtInfo;
	}
	
	
	//删除文本库
	@RequestMapping(value = "/deleteTextLibrary", method = RequestMethod.POST)
	@ResponseBody
	public RtInfo deleteTextLibrary(@RequestBody HashMap<String, Long> paramMap) {
		RtInfo rtInfo=new RtInfo();
		rtInfo.setRt_msg("文本库删除成功");
		long textLibraryId=paramMap.get("textLibraryId");
		textLibraryService.deleteTextLibrary(textLibraryId);
		return rtInfo;
	}

	//删除文本库
	@RequestMapping(value = "/getTextlibrary", method = RequestMethod.POST)
	@ResponseBody
	public RtInfo getTextlibrary(@RequestBody HashMap<String, Long> paramMap) {
		RtInfo rtInfo=new RtInfo();
		long textLibraryId=paramMap.get("textLibraryId");
		TextLibrary library=textLibraryService.getTextLibrary(textLibraryId);
		rtInfo.setRt_info(library);
		return rtInfo;
	}
}
