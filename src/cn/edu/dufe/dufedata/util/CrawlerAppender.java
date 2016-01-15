package cn.edu.dufe.dufedata.util;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class CrawlerAppender extends AppenderSkeleton {

	@Override
	public void close() {
		// TODO Auto-generated method stub
		if (this.closed)
            return;
        this.closed = true;
	}

	@Override
	public boolean requiresLayout() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void append(LoggingEvent arg0) {
		LogQueueUtil.getInstance().addLog(this.getLayout().format(arg0));
		LogFileUtil.getInstance().addLog(this.getLayout().format(arg0));
		KafkaUtil.getInstance().sendToKafkaServer("DataCrawlerLog", this.getLayout().format(arg0));
	}
	
	

}
