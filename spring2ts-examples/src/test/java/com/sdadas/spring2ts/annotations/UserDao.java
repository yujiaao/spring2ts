package com.sdadas.spring2ts.annotations;

import java.util.List;



/**
 * @author 陌意随影
 TODO :UserDao测试接口
 *2020年7月26日  下午10:58:04
 */
public interface UserDao {

/**
 * 获取所有用户
 * @return 返回所有用户的集合
 */
	public List<User> getAll();
/**
 * 通过指定的id获取一个用户对象
 * @param id: 指定的id
 * @return 返回一个User
 */
	public User getOne(int id);

}

