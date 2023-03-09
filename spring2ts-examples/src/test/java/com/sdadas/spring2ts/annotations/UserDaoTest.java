package com.sdadas.spring2ts.annotations;


import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.junit.jupiter.api.Test;

class UserDaoTest {

	@Test
	void test() throws Exception {
	 //根据UserDao接口的全限定类名通过反射获取该接口的字节码
	Class<?> userDaoClass = Class.forName("com.sdadas.spring2ts.annotations.UserDao");
	//获取UserDao所有的方法
	Method[] methods = userDaoClass.getMethods();
	for(Method method: methods) {
		//获取方法的名称
		String methodName = method.getName();
		//判断是否是UserDao中的getAll()或者getOne(int id)方法，
		if(methodName.startsWith("get")) {
			//返回一个Type对象，表示由该方法对象表示的方法的正式返回类型。
			//比如public List<User> getAll();那么返回的是List<User>
		  Type genericReturnType = method.getGenericReturnType();
		  //获取实际返回的参数名
		  String returnTypeName = genericReturnType.getTypeName();
		  System.out.println(methodName+"的返回参数是："+returnTypeName);
		  //判断是否是参数化类型
		  if(genericReturnType instanceof ParameterizedType) {
			  //如果是参数化类型,则强转
			  ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
			//获取实际参数类型数组，比如List<User>，则获取的是数组[User]，Map<User,String> 则获取的是数组[User,String]
			  Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			  for(Type type:actualTypeArguments) {
				  //强转
				  Class<?> actualTypeArgument = (Class<?>) type;
				 //获取实际参数的类名
				  String name = actualTypeArgument.getName();
				  System.out.println(methodName+"的返回值类型是参数化类型，其类型为："+name);
			  }

		  }else {
			  //不是参数化类型,直接获取返回值类型
			  Class<?> returnType = method.getReturnType();
              //获取返回值类型的类名
			  String name = returnType.getName();
			  System.out.println(methodName+"的返回值类型不是参数化类型其类型为："+name);

		  }

		}


	}
	}


}

