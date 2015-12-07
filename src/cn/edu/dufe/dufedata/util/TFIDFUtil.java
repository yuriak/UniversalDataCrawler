package cn.edu.dufe.dufedata.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.edu.dufe.dufedata.tfidf.*;
import org.omg.CORBA.PRIVATE_MEMBER;


public class TFIDFUtil {
		public static double[][] parse(String[] sourceData){
		String[] stringArray=sourceData;
		TFIDFMeasure tf = new TFIDFMeasure(stringArray, new Tokeniser());
		TFIDFMeasure tfidfMeasure=new TFIDFMeasure(stringArray, new Tokeniser());
        //生成k-means的输入数据，是一个联合数组，第一维表示文档个数，
        //第二维表示所有文档分出来的所有词
        double[][] data = new double[stringArray.length][];
        int docCount = stringArray.length; //文档个数
        int dimension = tf.get_numTerms();//所有词的数目
        for (int i = 0; i < docCount; i++)
        {
            for (int j = 0; j < dimension; j++)
            {
                data[i] = tf.GetTermVector2(i); //获取第i个文档的TFIDF权重向量
            }
        }
        
        return data;
	}
	
	public static File saveAsCSV(double[][] data,String fileName){
		if (!fileName.endsWith(".csv")) {
			fileName=fileName+".csv";
		}
		File file=new File(fileName);
		StringBuffer buffer=new StringBuffer();		
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				buffer.append(String.valueOf(data[i][j]));
				if (j!=data[i].length-1) {
					buffer.append(",");
				}
			}
			if (i!=data.length-1) {
				buffer.append("\n");
			}
		}
		try {
			FileUtils.writeStringToFile(file, buffer.toString(),"utf-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}
	
	public static void main(String[] args) {
		double[][] data=new double[3][3];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data.length; j++) {
				data[i][j]=(i+1)*j;
				System.out.println(data[i][j]);
			}
		}
		saveAsCSV(data, "test.csv");
		
	}
	
}
