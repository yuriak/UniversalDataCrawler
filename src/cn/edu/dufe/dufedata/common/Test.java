package cn.edu.dufe.dufedata.common;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

public class Test {
	public static void main(String[] args) throws Exception {
		RemoteIterator fIterator=FileSystem.get(new URI("hdfs://192.168.10.10:8020"),new Configuration()).listFiles(new Path("/"), true);
		while(fIterator.hasNext()){
			System.out.println(fIterator.next().toString());
		}
	}
}
