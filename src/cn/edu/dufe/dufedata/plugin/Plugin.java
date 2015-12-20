package cn.edu.dufe.dufedata.plugin;

import java.io.File;

import cn.edu.hfut.dmic.webcollector.crawler.Crawler;

public abstract class Plugin implements IPlugin {
	/**
	 * @return the readmeFile
	 */
	public File getReadmeFile() {
		return readmeFile;
	}

	/**
	 * @param readmeFile the readmeFile to set
	 */
	public void setReadmeFile(File readmeFile) {
		this.readmeFile = readmeFile;
	}

	public int getPluginStatus() {
		return pluginStatus;
	}

	public void setPluginStatus(int pluginStatus) {
		this.pluginStatus = pluginStatus;
	}

	public Crawler getCrawler() {
		return crawler;
	}

	public void setCrawler(Crawler crawler) {
		this.crawler = crawler;
	}

	public String getPluginAuthor() {
		return pluginAuthor;
	}

	public void setPluginAuthor(String pluginAuthor) {
		this.pluginAuthor = pluginAuthor;
	}

	public String getPluginID() {
		return pluginID;
	}

	public void setPluginID(String pluginID) {
		this.pluginID = pluginID;
	}

	public String getPluginVersion() {
		return pluginVersion;
	}

	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}
	private int pluginStatus=0;
	private String pluginID;
	private String pluginVersion;
	private String pluginAuthor;
	private File readmeFile;
	public Plugin(String pluginID,String pluginVersion,String pluginAuthor){
		this.pluginID=pluginID;
		this.pluginVersion=pluginVersion;
		this.pluginAuthor=pluginAuthor;
	}
	
	private Crawler crawler;
	
	@Override
	public boolean equals(Object o){
		Plugin plugin=(Plugin) o;
		if (this.pluginID.equals(plugin.getPluginID())&&this.pluginVersion.equals(plugin.getPluginVersion())) {
			return true;
		}
		return false;
	}
	
	@Override
	public abstract void crawl() throws Exception;
	@Override
	public abstract void init(String[] args) throws Exception;
	@Override
	public abstract void stop() throws Exception;
	
	public abstract File[] getResultFiles() throws Exception;
}
