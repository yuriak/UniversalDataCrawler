package cn.edu.dufe.dufedata.util;

import java.io.File;
import java.text.DecimalFormat;

public class MyFileUtils {

	public static void sortByDate(File[] files){
		for (int i = 0; i < files.length -1; i++){    
            for(int j = 0 ;j < files.length - i - 1; j++){   
                if(files[j].lastModified() < files[j + 1].lastModified()){    //把小的值交换到后面
                    File temp = files[j];
                    files[j] = files[j + 1];
                    files[j + 1] = temp;
                }
            }            
        }
	}
	
	public static String calculateSize(long size){
		String result="";
		DecimalFormat    df   = new DecimalFormat("######0.00");
		double tmpResult=size;
		if(size>1024){
			tmpResult=tmpResult/1024;
			if (tmpResult>1024) {
				tmpResult=tmpResult/1024;
				if (tmpResult>1024) {
					tmpResult=tmpResult/1024;
					if (tmpResult>1024) {
						tmpResult=tmpResult/1024;
						result=df.format(tmpResult)+"TB";
					}else {
						result=df.format(tmpResult)+"GB";
					}
				}else {
					result=df.format(tmpResult)+"MB";
				}
			}else {
				result=df.format(tmpResult)+"KB";
			}
		}else {
			result=df.format(tmpResult)+"B";
		}
		return result;
	}
}
