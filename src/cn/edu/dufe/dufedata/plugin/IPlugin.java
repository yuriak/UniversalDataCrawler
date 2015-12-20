package cn.edu.dufe.dufedata.plugin;

import java.io.File;

import cn.edu.dufe.dufedata.common.IModule;

public interface IPlugin extends IModule {
	public abstract void crawl() throws Exception;
	public abstract void stop() throws Exception;
	public abstract File[] getResultFiles() throws Exception;
}
