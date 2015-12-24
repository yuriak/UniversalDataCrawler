package cn.edu.dufe.dufedata.bean;

public class PluginBean {
	/**
	 * @return the pluginID
	 */
	public String getPluginID() {
		return pluginID;
	}
	/**
	 * @param pluginID the pluginID to set
	 */
	public void setPluginID(String pluginID) {
		this.pluginID = pluginID;
	}
	/**
	 * @return the pluginStatus
	 */
	public Integer getPluginStatus() {
		return pluginStatus;
	}
	/**
	 * @param pluginStatus the pluginStatus to set
	 */
	public void setPluginStatus(Integer pluginStatus) {
		this.pluginStatus = pluginStatus;
	}
	/**
	 * @return the pluginVersion
	 */
	public String getPluginVersion() {
		return pluginVersion;
	}
	/**
	 * @param pluginVersion the pluginVersion to set
	 */
	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}
	/**
	 * @return the pluginAuthor
	 */
	public String getPluginAuthor() {
		return pluginAuthor;
	}
	/**
	 * @param pluginAuthor the pluginAuthor to set
	 */
	public void setPluginAuthor(String pluginAuthor) {
		this.pluginAuthor = pluginAuthor;
	}
	private String pluginID;
	private Integer pluginStatus;
	private String pluginVersion;
	private String pluginAuthor;
}
