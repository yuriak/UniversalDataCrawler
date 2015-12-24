package cn.edu.dufe.dufedata.node;

import java.util.ArrayList;
import java.util.HashMap;

import cn.edu.dufe.dufedata.bean.PluginBean;
import cn.edu.dufe.dufedata.plugin.Plugin;

public class Node {
	/**
	 * @return the fileList
	 */
	public ArrayList<String> getFileList() {
		return fileList;
	}
	/**
	 * @param fileList the fileList to set
	 */
	public void setFileList(ArrayList<String> fileList) {
		this.fileList = fileList;
	}
	/**
	 * @return the role
	 */
	public int getRole() {
		return role;
	}
	/**
	 * @param role the role to set
	 */
	public void setRole(int role) {
		this.role = role;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the iP
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param iP the iP to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the plugins
	 */
	public HashMap<String, PluginBean> getPlugins() {
		return plugins;
	}
	/**
	 * @param plugins the plugins to set
	 */
	public void setPlugins(HashMap<String, PluginBean> plugins) {
		this.plugins = plugins;
	}
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	//角色
	private int role;
	//名称
	private String name;
	//地址，不带http,只有ip:port
	private String address;
	//状态
	private int status;
	//文件待下载列表
	private ArrayList<String> fileList;
	//插件列表<插件ID，插件Bean>
	private HashMap<String, PluginBean> plugins;
}
