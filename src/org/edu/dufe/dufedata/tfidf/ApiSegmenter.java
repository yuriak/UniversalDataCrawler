package org.edu.dufe.dufedata.tfidf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import cn.edu.dufe.dufedata.util.MyMapUtils;
import cn.edu.dufe.dufedata.util.HttpUtil;

public class ApiSegmenter implements ISegmenter  {
	
	
	private String url;
	private String method;
	private String key;
	
	public ApiSegmenter(String url,String method,String key){
		this.url=url;
		this.method=method;
		this.key=key;
	}
	
	@Override
	public List<String> partition(String input) {
		String result=sendRequest(input);
		HashMap<String, String> wordsMap=new HashMap<>();
		JSONObject object=new JSONObject(result);
		List<String> words=new ArrayList<>();
		if (object.has("words")&&object.getJSONArray("words").length()>0) {
			JSONArray wordsArray=object.getJSONArray("words");
			for (int i = 0; i < wordsArray.length(); i++) {
				JSONObject wordObject=wordsArray.getJSONObject(i);
				wordsMap.put(wordObject.getString("word"), "property");
			}
		}
		Iterator<String> iterator=wordsMap.keySet().iterator();
		while (iterator.hasNext()) {
			String word=iterator.next();
			String property=wordsMap.get(word);
			if (!StopWordsHandler.IsStopword(word)&&!StopWordsHandler.IsStopProperty(property)) {
				words.add(word);
			}
			
		}
		return words;
	}
	
	private String sendRequest(String input){
		try {
			if (method.toLowerCase().equals("get")) {
				return Jsoup.connect(url+"?"+key+"="+input.trim()).ignoreContentType(true).ignoreHttpErrors(true).get().text();
			}else if (method.toLowerCase().equals("post")) {
				return HttpUtil.post(url, MyMapUtils.fillMap(key,input));
			}else {
				return Jsoup.connect(url+"?"+key+"="+input.trim()).ignoreContentType(true).ignoreHttpErrors(true).get().text();
			}
		} catch (Exception e) {
			return "{words:[]}";
		}
		
	}
}
