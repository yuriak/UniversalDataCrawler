package cn.edu.dufe.dufedata.bean;


public class LogBean {
	/**
	 * @return the maxCurrsor
	 */
	public int getMaxCurrsor() {
		return maxCurrsor;
	}
	/**
	 * @param maxCurrsor the maxCurrsor to set
	 */
	public void setMaxCurrsor(int maxCurrsor) {
		this.maxCurrsor = maxCurrsor;
	}
	/**
	 * @return the cursor
	 */
	public int getCurrentCursor() {
		return currentCursor;
	}
	/**
	 * @param cursor the cursor to set
	 */
	public void setCurrentCursor(int currentCursor) {
		this.currentCursor = currentCursor;
	}
	/**
	 * @return the log
	 */
	public String getLog() {
		return log;
	}
	/**
	 * @param log the log to set
	 */
	public void setLog(String log) {
		this.log = log;
	}
	//最大游标位置
	private int maxCurrsor;
	//当前游标位置
	private int currentCursor;
	//日志内容
	private String log;
}
