package cn.edu.dufe.dufedata.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import cn.edu.dufe.dufedata.common.CommonConfig;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class KafkaUtil {
	
	private static KafkaUtil kafkaUtil;
	private String kafkaServer=null;
	private String zkServer=null;
	private boolean useKafka=false;
	private boolean inited=false;
	private Producer<String, String> producer;
	
	public static KafkaUtil getInstance(){
		if (kafkaUtil==null) {
			synchronized (KafkaUtil.class) {
				if (kafkaUtil==null) {
					kafkaUtil=new KafkaUtil();
				}
			}
		}
		if (kafkaUtil.inited==false) {
			kafkaUtil.readKafkaConfig();
		}
		return kafkaUtil;
	}
	
	private KafkaUtil(){
		
	}
	
	public void sendToKafkaServer(String topic,String message){
		if (kafkaServer==null||zkServer==null||useKafka==false||inited==false) {
			return;
		}if (producer!=null) {
			producer.send(new KeyedMessage<String, String>(topic, message));
		}
	}
	
	private void readKafkaConfig(){
		
		File kafkaConfigFile=new File(CommonConfig.KAFKA_CONFIG);
		if (!kafkaConfigFile.exists()) {
			return;
		}
		if (kafkaConfigFile!=null) {
			try {
				List<String> configList=FileUtils.readLines(kafkaConfigFile);
				for (String string : configList) {
					String key=string.split("=")[0];
					String value=string.split("=")[1];
					if (key.equals("usekafka")) {
						useKafka=Boolean.valueOf(value);
					}else if (key.equals("kafkaserver")) {
						kafkaServer=value;
					}else if (key.equals("zkserver")) {
						zkServer=value;
					}
				}
				inited=true;
				if (useKafka==true) {
					Properties props = new Properties();
					props.put("serializer.class", "kafka.serializer.StringEncoder");
					props.put("metadata.broker.list", kafkaServer);
					props.put("request.required.acks","0");
					props.put("producer.type", "async");
					props.put("queue.buffering.max.messages", "100000000");
					producer = new Producer<String, String>(new ProducerConfig(props));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
//	public static void main(String[] args) {
//		KafkaUtil util=KafkaUtil.getInstance();
//		util.setKafkaServer("192.168.1.124:9092");
//		util.setZkServer("192.168.1.124:2181");
//		util.sendToKafkaServer("hexun", "所以现在可以用了？");
//		util.sendToKafkaServer("hexun", "应该是的");
//		util.sendToKafkaServer("hexun", "那就这样吧");
//	}
}
