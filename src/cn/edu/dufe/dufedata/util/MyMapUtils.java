package cn.edu.dufe.dufedata.util;

import java.util.HashMap;

//map封装工具，只要不停的往里面放参数就好了！，因为是可变参数得到!
public class MyMapUtils {
	public static HashMap<String, String> fillMap(String... params) throws Exception{
		if (params.length%2!=0) {
			throw new Exception("Invalid argument");
		}
		HashMap<String, String> resultMap=new HashMap<>();
		for (int i = 0; i < params.length; i+=2) {
			resultMap.put(params[i], params[i+1]);
		}
		return resultMap;
	}
}
