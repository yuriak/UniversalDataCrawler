package cn.edu.dufe.dufedata.util;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Jsoup;
//!看！又是一个Util!，这个Util是封装post方法的，以字符串形式返回response的结果
public class PostUtil {
	public static String post(String url,Map<String, String> params){
		try {
			return Jsoup.connect(url).data(params).ignoreContentType(true).ignoreHttpErrors(true).post().text();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		return "";
	}
}
