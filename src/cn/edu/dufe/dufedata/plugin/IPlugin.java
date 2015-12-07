package cn.edu.dufe.dufedata.plugin;

import java.io.File;

import cn.edu.dufe.dufedata.common.IModule;

public interface IPlugin extends IModule {
	public abstract void crawl();
	public abstract File[] getResultFiles();
	public abstract File[] getTFIDFFiles();
}
