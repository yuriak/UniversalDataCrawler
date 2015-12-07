package cn.edu.dufe.dufedata.util;

import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;

public class Json2CSV {
	public static String toCsv(String fileString) throws Exception{
		JSONArray array=new JSONArray();
		if (fileString.startsWith("[")) {
			array=new JSONArray(fileString);
		}else if (fileString.startsWith("{")) {
			JSONObject object=new JSONObject(fileString);
			String keyString="";
			if (object.keySet().iterator().hasNext()) {
				keyString=object.keySet().iterator().next().toString();
			}
			array=object.getJSONArray(keyString);
		}else {
			System.out.println("not a json file");
		}
		return CDL.toString(array);
	}
	
	public static String toJson(String fileString){
		String filtered=fileString.replaceAll("\r\n", "\n");
		return CDL.toJSONArray(filtered).toString();
	}
}
