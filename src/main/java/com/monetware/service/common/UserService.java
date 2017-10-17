package com.monetware.service.common;

import com.monetware.mapper.common.UserMapper;
import com.monetware.model.common.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User getUserByName(String username){
        User user=userMapper.getUserByName(username);
        return user;
    }

	public User getUserById(int id) {
		User user = userMapper.selectByPrimaryKey(id);
		return user;
	}



	//用户查询





	public List<User> getUserList(HashMap<String, Object> queryMap) {
		return userMapper.getUserList(queryMap);
	}



	public long getUserNo(int userId,String name) {
		return userMapper.getUserNo(userId,name);
	}


	public long getMonthUserNo(int userId,String name) {
		//获取前月的第一天
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal_1=Calendar.getInstance();//获取当前日期
		cal_1.add(Calendar.MONTH, -1);
		cal_1.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
		Timestamp firstDay = new Timestamp(cal_1.getTime().getTime());
		//获取下个月得第一天
		Calendar cale = Calendar.getInstance();
		cale.set(Calendar.DAY_OF_MONTH,0);//设置为1号,当前日期既为本月第一天
		return userMapper.getMonthUserNo(userId,name,firstDay);
	}






















	public int updateBasic(User user){
		return userMapper.updateByPrimaryKeySelective(user);
	}

	public int updatePassword(int id,String oldPassword,String newPassword){
		User user = userMapper.selectByPrimaryKey(id);
		if (!user.getPassword().equals(oldPassword)){

			return 0;
		}

		User user2 = new User();
		user2.setId(id);
		System.out.println("new password:"+newPassword);
		user2.setPassword(newPassword);
		return userMapper.updateByPrimaryKeySelective(user2);

	}

	//判断是否重名
	public boolean isNameAvailable(String username){
		return getUserByName(username)==null?true:false;

	}

	//新增用户
	public boolean createNewUser(User user){
		return userMapper.insertSelective(user)>0?true: false;

	}

	public boolean deleteUser(int id){
		return userMapper.deleteByPrimaryKey(id)>0?true:false;
	}



}
