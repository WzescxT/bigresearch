package com.monetware.util;

import java.text.ParseException;
import java.util.Map;

import net.minidev.json.JSONObject;

import com.monetware.model.common.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
//import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
public  class AuthUtil {
	//可有无，随意设置的属性值
	static String MONETWARE_SHORTNAME = "monetware-2016";
	//站点秘钥
	static String MONETWARE_SECRET = "HQFI23+0E3E,32EJ1.EE31138/93U=E=3E2103E3E2JJD";
	public static String getToken(int id,String username,int role_level){
		JSONObject userInfo = new JSONObject();
        //网站二级域名
        userInfo.put("short_name", MONETWARE_SHORTNAME);//必须项
        //用户名(用户在网站中的唯一标示)
        //这里添加一个javabean对象，会出现类型异常
        userInfo.put("user_id", id);//必须项
        //flag设置到期时间
        userInfo.put("user_name", username);
        //用户id

        userInfo.put("role_level", role_level);//可选项
        //加密混淆
        userInfo.put("mix_code", "wqeio+dew|opfp!q_ehd-e@wfeDFQF.QE");
        
        Payload payload = new Payload(userInfo);

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
//        header.setContentType("jwt");


        // Create JWS object
        JWSObject jwsObject = new JWSObject(header, payload);

        // Create HMAC signer
        try {
        JWSSigner signer = new MACSigner(MONETWARE_SECRET.getBytes());
                jwsObject.sign(signer);
        } catch (JOSEException e) {

                System.err.println("Couldn't sign JWS object: " + e.getMessage());
                return "error";
        }
        // Serialise JWS object to compact format
        String token = jwsObject.serialize();
        return token;
	}

	public static int  parseToken(String token){
		 try {
				JWT t=JWTParser.parse(token);
				//获取Claims // 包括需要传递的用户信息； { "sub": "1234567890", "name": "John Doe", "admin": true }
				JWTClaimsSet s=t.getJWTClaimsSet();
				Map<String, Object> map=s.getClaims();
				User user=new User();
				int user_id= Integer.parseInt(map.get("user_id").toString());
				
				
				return user_id;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//解析错误
				return -1;
			}catch ( java.lang.ClassCastException e) {
				//类型转换异常
				return 0;
			}

	}
	/*public static void main(String[] args) {
		int id=1;
		int level=2;
		String token =getToken(id,"venbill",level);
		System.out.println(token);
		int user_key=parseToken(token);
		System.out.println(user_key);
//		System.out.println("id:"+u.getId()+", flag:"+u.getFlag());
	}*/






}
