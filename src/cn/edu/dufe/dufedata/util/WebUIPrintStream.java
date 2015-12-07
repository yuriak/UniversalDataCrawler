package cn.edu.dufe.dufedata.util;
/**//*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.ByteArrayInputStream;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/** *//**
 * 输出到文本组件的流。
 * 
 * @author Chen Wei 
 * @website www.chenwei.mobi
 * @email chenweionline@hotmail.com
 */
public class WebUIPrintStream extends PrintStream {
    
    private ArrayBlockingQueue<String> logQueue;
    private StringBuffer sb = new StringBuffer();
    private Console console;
    public WebUIPrintStream(OutputStream out){
        super(out);
        this.logQueue = logQueue;
        if (System.console()!=null) {
			console=System.console();
		}
    }
    
    /** *//**
     * 重写write()方法，将输出信息填充到GUI组件。
     * @param buf
     * @param off
     * @param len
     */
    @Override
    public void write(byte[] buf, int off, int len){
        String message = new String(buf, off, len); 
        LogQueueUtil.getInstance().addLog(message);
        LogFileUtil.getInstance().addLog(message);
        if (console!=null) {
			console.writer().write(message);
			console.writer().flush();
		}
    }
}