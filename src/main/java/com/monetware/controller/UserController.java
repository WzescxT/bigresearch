package com.monetware.controller;

import com.monetware.model.common.RtInfo;
import com.monetware.model.common.User;
import com.monetware.service.common.UserService;
import com.monetware.util.AuthUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
public class UserController {


    @Autowired
    private UserService userService;

    @RequestMapping("/getUserInfo")
    @ResponseBody
    public RtInfo getUserInfo(@RequestBody User user) {
    	RtInfo rtInfo=new RtInfo();
    	HashMap<String, Object> infoMap = new HashMap<String, Object>();
    	int id=user.getId();
        User realUser = userService.getUserById(id);
        if (realUser!=null) {
        	infoMap.put("user", realUser);
        	infoMap.put("token", AuthUtil.getToken(realUser.getId(), realUser.getUsername(),realUser.getRoleLevel()));
        	rtInfo.setRt_info(infoMap);
		}else {
			rtInfo.setError_code(1);
			rtInfo.setError_msg("该用户不存在");
		}
        return rtInfo;
    }
    


    //分页显示数据
    @RequestMapping("/getUserList")
    @ResponseBody
    public RtInfo getUserList( @RequestBody HashMap<String, Object> queryMap,@RequestHeader HttpHeaders headers) {
		RtInfo rtInfo=new RtInfo();
		String token = headers.getFirst("Authorization");
		int userId=AuthUtil.parseToken(token);
		queryMap.put("userId", Long.decode(String.valueOf(userId)));
		queryMap.put("pageStart",(Long.decode(String.valueOf(queryMap.get("pageNow")))-1)*Long.decode(String.valueOf(queryMap.get("pageSize"))));
		String name = (String)queryMap.get("name")==null?"":(String)queryMap.get("name");
		List<User> users = userService.getUserList(queryMap);
		long bigTotalItems=userService.getUserNo(userId,name);
		long thisMonthItems=userService.getMonthUserNo(userId,name);
		rtInfo.setRt_mapinfo("users",users);
		rtInfo.setRt_mapinfo("bigTotalItems",bigTotalItems);
		rtInfo.setRt_mapinfo("thisMonthItems",thisMonthItems);
		return rtInfo;
    }









	@RequestMapping("/updateBasic")
    @ResponseBody
    public RtInfo updateBasic(@RequestBody User user, @RequestHeader HttpHeaders headers) {
		RtInfo rtInfo=new RtInfo();
		String token = headers.getFirst("Authorization");
		int userId=AuthUtil.parseToken(token);
		User realUser = userService.getUserById(userId);
		if (!realUser.getUsername().equals(user.getUsername())&&!userService.isNameAvailable(user.getUsername())){
			//判断用户名是否重复
			rtInfo.setError_code(1);
			rtInfo.setError_msg("用户名已存在，请重新输入");
			return rtInfo;
		}
    	boolean bol = userService.updateBasic(user)>0?true:false;
        if (bol){
        	rtInfo.setRt_msg("修改成功");
        	rtInfo.setRt_info(userService.getUserById(userId));
		}else{
			rtInfo.setError_code(1);
			rtInfo.setError_msg("修改失败请重试");
		}
        return rtInfo;
    }

    @RequestMapping("/updatePassword")
    @ResponseBody
    public RtInfo updatePassword(@RequestBody Map<String,String> paranMap, @RequestHeader HttpHeaders headers) {
		RtInfo rtInfo=new RtInfo();
		String token = headers.getFirst("Authorization");
		int userId=AuthUtil.parseToken(token);
		String oldPassword = paranMap.get("oldPassword");
		String newPassword = paranMap.get("newPassword");
		boolean bol = userService.updatePassword(userId,oldPassword,newPassword)>0?true :false;
		if (bol){
			rtInfo.setRt_msg("修改成功");
		}else{
			rtInfo.setError_code(1);
			rtInfo.setError_msg("密码不正确，修改失败！");
		}

        return rtInfo;
    }



	@RequestMapping(value="/uploadAvatr", method=RequestMethod.POST  )
	@ResponseBody
	public RtInfo uploadAvatr(HttpServletRequest request) {
		RtInfo rtInfo=new RtInfo();
		String token = request.getParameter("token");
		int userId=AuthUtil.parseToken(token);

		//解析器解析request的上下文
		CommonsMultipartResolver multipartResolver =
				new CommonsMultipartResolver(request.getSession().getServletContext());
		//先判断request中是否包涵multipart类型的数据，
		if(multipartResolver.isMultipart(request)){
			//再将request中的数据转化成multipart类型的数据
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			Iterator iter = multiRequest.getFileNames();
			while(iter.hasNext()){
				//根据name值拿取文件
				System.out.println("根据文件名取文件");
				String name=(String)iter.next();
				System.out.println("name:"+name);
				//根据name值拿取文件
				MultipartFile file = multiRequest.getFile(name);
				if(file != null){
					String path = "D:/bigresearch/" + userId+"/avatr.png";
					File localFile = new File(path);
					if(!localFile.getParentFile().exists()) {
						//如果目标文件所在的目录不存在，则创建父目录
						localFile.getParentFile().mkdirs();
					}
					//写文件到本地
					try {
						file.transferTo(localFile);
						User updateUser = new User();
						updateUser.setId(userId);
						updateUser.setAvatr(path);
						userService.updateBasic(updateUser);

					} catch (IOException e) {
						rtInfo.setError_code(1);
						rtInfo.setError_msg("上传头像失败！");
						return rtInfo;
					}
				}
			}
		}else {
			rtInfo.setRt_msg("上传头像成功");
		}
		return rtInfo;
	}












	//用户管理



    @RequestMapping("/createUser")
    @ResponseBody
    public RtInfo createUser(@RequestBody User user, @RequestHeader HttpHeaders headers) {
		RtInfo rtInfo=new RtInfo();
		String token = headers.getFirst("Authorization");
		int adminId = AuthUtil.parseToken(token);
		User admin = userService.getUserById(adminId);
		int roleLevel = admin.getRoleLevel();
		if (roleLevel!=3){
			rtInfo.setError_code(1);
			rtInfo.setError_msg("没有创建用户的权限！");
			return rtInfo;
		}


		if (!userService.isNameAvailable(user.getUsername())){
			rtInfo.setError_code(1);
			rtInfo.setError_msg("用户名已存在，请重试！");
			return rtInfo;
		}
		if (user.getEndTime()==null){
			user.setEndTime(new Date(new Date().getTime()+3600*24));
		}
		user.setRoleLevel(0);
		user.setCreateTime(new Date());
		user.setCreateUser(adminId);
		user.setAvatr("d:/bigresearch/avatr_deafult.png");
		boolean bol = userService.createNewUser(user);
		if (bol){
			rtInfo.setRt_msg("账号创建成功！");
		}else {
			rtInfo.setError_code(1);
			rtInfo.setError_msg("用户创建失败，请重试！");
		}
        return rtInfo;
    }



	@RequestMapping("/deleteUser")
	@ResponseBody
	public RtInfo deleteUser(@RequestBody Map<String,Integer> paranMap, @RequestHeader HttpHeaders headers) {
		RtInfo rtInfo=new RtInfo();
		String token = headers.getFirst("Authorization");
		int adminId=AuthUtil.parseToken(token);
		User admin = userService.getUserById(adminId);
		int id = paranMap.get("id");
		if (admin.getRoleLevel()==3){
			boolean b =userService.deleteUser(id);
			if (b){
				rtInfo.setRt_msg("账号删除成功！");
			}else {
				rtInfo.setError_code(1);
				rtInfo.setError_msg("账号删除失败！");
			}
		}else {
			rtInfo.setError_code(1);
			rtInfo.setError_msg("没有账号管理权限！");
		}

		return rtInfo;
	}







	//登录模块

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public RtInfo login(@RequestBody User user) {
    	System.out.println("inLogin");
    	RtInfo rtInfo=new RtInfo();
    	HashMap<String, Object> infoMap = new HashMap<String, Object>();
    	String username = user.getUsername();
    	String password = user.getPassword();
        User realUser = userService.getUserByName(username);
        if (realUser!=null) {
        	if (password.equals(realUser.getPassword())) {
        		//登录时检查有效时间
        		if (realUser.getEndTime()!=null&&realUser.getEndTime().getTime()<new Date().getTime()){
					rtInfo.setError_code(1);
					rtInfo.setError_msg("账号有效时间已过期");
				}else {
					infoMap.put("user", realUser);
					infoMap.put("token", AuthUtil.getToken(realUser.getId(), realUser.getUsername(),realUser.getRoleLevel()));
					rtInfo.setRt_info(infoMap);
				}
			}else {
    			rtInfo.setError_code(2);
    			rtInfo.setError_msg("密码错误");
    		}
		}else {
			rtInfo.setError_code(1);
			rtInfo.setError_msg("该用户不存在");
		}
        
        return rtInfo;
    }
    @RequestMapping(value = "/parseToken", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public RtInfo parseToken(@RequestHeader HttpHeaders headers) {
    	String token = headers.getFirst("Authorization");
    	RtInfo rtInfo=new RtInfo();
    	try {
    		int userId=AuthUtil.parseToken(token);
    		User realUser = userService.getUserById(userId);
    			rtInfo.setRt_info(realUser);
    		} catch (Exception e) {
    		rtInfo.setError_code(1);
    		rtInfo.setError_msg("密钥错误");
    		return rtInfo;
    	}
    	return rtInfo;
    }






    //获取文件头像,输出文件流
	@ResponseBody
	@RequestMapping("/avatr/{id}")
	public ResponseEntity<byte[]> download(@PathVariable("id") Integer userId) throws IOException {
		String avatrPath = userService.getUserById(userId).getAvatr();
		File file  = new File(avatrPath);
		if (!file.exists()){
			String avatrDeaultPath = "d:/bigresearch/avatr_deafult.png";
			file = new File(avatrDeaultPath);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", "avatr.png");
		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers,
				HttpStatus.CREATED);
	}






    
    
}
