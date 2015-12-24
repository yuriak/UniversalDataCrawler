package cn.edu.dufe.dufedata.common;

import java.io.File;

import org.eclipse.jdt.internal.compiler.flow.FinallyFlowContext;

public class CommonConfig {
	//程序运行根目录
	public static final String BASE_DIR=System.getProperty("user.dir");
	public static final String DATA_DIR=BASE_DIR+File.separator+"data";
	public static final String RES_DIR=BASE_DIR+File.separator+"res";
	public static final String PLUGIN_FOLDER=BASE_DIR+File.separator+"plugins";
	public static final String CONF_DIR=BASE_DIR+File.separator+"conf";
	//运行模式：单机和分布式
	public static final int SINGLE=0;
	public static final int DISTRIBUTED=1;
	public static void main(String[] args) {
		
	}
}
