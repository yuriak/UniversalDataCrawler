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
		String[] data=new String[]{"我国出台新的法案，要求国民素质必须上升一个等级","事实上这一法案与国民的利益密切相关","然而国务院反驳了这个说法，声称这是为了国家未来的发展着想","新华社报道。"};
		parseAndSaveToCSV(data, "data/dict.csv", "data/data.csv");
	}
	
	//这是最终要用的方法，该方法会返回两个文件，一个是字典，一个是数据
	public static ArrayList<File> parseAndSaveToCSV(String[] sourceData,String dictFilename,String dataFileName){
		ArrayList<File> tfidfFiles=new ArrayList<>();
		String[] stringArray=sourceData;
		TFIDFMeasure tf = new TFIDFMeasure(stringArray, new Tokeniser());
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
        String[] terms=tf.getTerms();
        if (!dictFilename.endsWith(".csv")) {
        	dictFilename=dictFilename+".csv";
		}
        if (!dataFileName.endsWith(".csv")) {
        	dataFileName=dataFileName+".csv";
		}
		File dictFile=new File(dictFilename);
		File dataFile=new File(dataFileName);
		StringBuffer buffer=new StringBuffer();
		for (int i = 0; i < sourceData.length; i++) {
			buffer.append(i+","+sourceData[i]+"\n");
		}
		try {
			FileUtils.writeStringToFile(dictFile, buffer.toString());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		buffer=new StringBuffer();
		buffer.append("id,");
		for (int i = 0; i < terms.length; i++) {
			buffer.append(terms[i]);
			if (i!=terms.length-1) {
				buffer.append(",");
			}else {
				buffer.append("\n");
			}
		}
		for (int i = 0; i < data.length; i++) {
			buffer.append(i+",");
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
			FileUtils.writeStringToFile(dataFile, buffer.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tfidfFiles.add(dictFile);
		tfidfFiles.add(dataFile);
		return tfidfFiles;
	}
	
}
