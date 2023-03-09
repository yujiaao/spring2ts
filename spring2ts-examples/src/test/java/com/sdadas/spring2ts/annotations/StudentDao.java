package com.sdadas.spring2ts.annotations;

import java.util.Map;

/**
 * @author 陌意随影
 TODO :学生dao测试类
 *2020年7月26日  下午11:02:10
 */
public interface StudentDao<T,V> {
	/**
	 * 获取所有对象
	 * @return 返回一个map
	 */
	public Map<T,V> getAll();
	/**
	 * 获取一个T对象
	 * @return 返回T
	 */
	public T getT();
	/**
	 * 获取一个V对象
	 * @return 返回V
	 */
	public	V getV();

}

