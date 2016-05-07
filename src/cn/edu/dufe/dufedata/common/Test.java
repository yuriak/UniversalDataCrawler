package cn.edu.dufe.dufedata.common;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.jsoup.Jsoup;

import cn.edu.dufe.dufedata.util.MyMapUtils;

public class Test {
//	public static void main(String[] args) throws Exception {
//		RemoteIterator fIterator=FileSystem.get(new URI("hdfs://192.168.10.10:8020"),new Configuration()).listFiles(new Path("/"), true);
//		while(fIterator.hasNext()){
//			System.out.println(fIterator.next().toString());
//		}
//	}
	public static void main(String[] args) {
		try {
			System.out.println(Jsoup.connect("http://localhost:8080/OES_EndSide/API/test").ignoreContentType(true).ignoreHttpErrors(true).data(MyMapUtils.fillMap("userName","hshs")).post());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
