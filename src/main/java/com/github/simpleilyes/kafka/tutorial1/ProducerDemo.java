package com.github.simpleilyes.kafka.tutorial1;

import java.util.Properties;
import java.util.logging.Logger;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.LoggerFactory;

public class ProducerDemo {

	public static void main(String[] args) {
		final org.slf4j.Logger logger = LoggerFactory.getLogger(ProducerDemo.class);
		System.out.println("hello world");
		String BootstrapServers="127.0.0.1:9092";
		Properties propreties= new Properties();
		propreties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BootstrapServers);
		propreties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		propreties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,  StringSerializer.class.getName());
		KafkaProducer<String,String> producer=new KafkaProducer<String,String>(propreties);
		
		for(int i=0;i<10;i++)
		{
			String topic="first_topic";
			String value="hello world1" + Integer.toString(i);
				String key="Key_"+ Integer.toString(i);
		ProducerRecord<String,String> record = new ProducerRecord<String,String>(topic,key,value);
		
		producer.send(record,new Callback() {
            public void onCompletion(RecordMetadata metadata, Exception e) {
                if(e != null) {
             
                   e.printStackTrace();
                } else {
                	
                   	logger.info("received Data. \n"+
                            "Topic : " + metadata.topic() +" \n"+
                            "Partition : " + metadata.partition() +" \n"+
                            "offset : " + metadata.offset() +" \n"+
                            "Timestamp : " + metadata.timestamp() +" \n");
                  
                }
            }
        });
		}
		producer.flush();
		producer.close();
		
	}

}
