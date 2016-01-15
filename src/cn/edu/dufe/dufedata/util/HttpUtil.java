package cn.edu.dufe.dufedata.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.jsoup.Jsoup;
//!看！又是一个Util!，这个Util是封装post方法的，以字符串形式返回response的结果
public class HttpUtil {
	public static String post(String url,Map<String, String> params){
		try {
			return Jsoup.connect(url).data(params).ignoreContentType(true).ignoreHttpErrors(true).post().text();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		return "";
	}
	
	public static String post(String url,Map<String, String> params,Map<String, String> cookie){
		try {
			return Jsoup.connect(url).data(params).cookies(cookie).ignoreContentType(true).ignoreHttpErrors(true).post().text();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	
	public static String get(String url, Map<String, String> cookie){
		try {
			return Jsoup.connect(url).ignoreContentType(true).ignoreHttpErrors(true).cookies(cookie).get().text();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}
	
	public static String get(String url){
		try {
			return Jsoup.connect(url).ignoreContentType(true).ignoreHttpErrors(true).get().text();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}
	
	public static String put(String url){
		try {
			HttpClient client=HttpClientBuilder.create().build();
			HttpPut put = new HttpPut(url);
//			put.
//			put.setHeader("Content-type", "application/json");

//			StringEntity params =new StringEntity(jo.toString());
//			put.setEntity(params);


			HttpResponse response = client.execute(put);
			System.out.println("Response Code:"+response.getStatusLine().getStatusCode());
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
			result.append(line);
			}
			System.out.println("result:"+result);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}
	
	public static String delete(){
		
		return "";
	}
	public static void main(String[] args) {
		put("http://192.168.10.10:14000/http?op=MKDIRS&user.name=root");
//		System.out.println(get("http://192.168.10.10:14000/webhdfs/v1/http?op=liststatus&user.name=root"));
	}
	
}
