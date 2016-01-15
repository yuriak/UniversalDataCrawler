package org.edu.dufe.dufedata.tfidf;

import java.util.List;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;


public class WordExcluder {
	
	public static String[] stopWordsList=new String[] {"的",
        "我们","要","自己","之","将","“","”","，","（","）","后","应","到","某","后",
        "个","是","位","新","一","两","在","中","或","有","更","好","快讯","中国"
	};
	
	public static String [] stopPropertyOfWords={"c","p","w"};

	public List<String> excludeStopWord(List<String> words){
		
		return null;
	}
	
	public static void main(String[] args) {
		List<Term> terms=ToAnalysis.parse("天了噜！广州两豪车车主彼此看不对眼，在机场高速玩飙车，结果相撞宝马翻车。广州交警今日公布了这起飙车案细节，据悉这是飙车入刑后广州第一宗案件。");
		for (Term term : terms) {
			System.out.println(term.toString());
		}
	}
}
