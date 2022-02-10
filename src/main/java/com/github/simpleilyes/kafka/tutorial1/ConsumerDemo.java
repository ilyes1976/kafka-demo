package com.github.simpleilyes.kafka.tutorial1;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
public class ConsumerDemo {
	Logger logger=LoggerFactory.getLogger(ConsumerDemo.class);
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		
		
		new ConsumerDemo().run();
		
		
	}
	
	private ConsumerDemo()
	{
			
	}
	private void run()
	{
		String BootstrapServers="127.0.0.1:9092";
		String groupId="my_six_application";
		String topic="first_topic";
		CountDownLatch latch = new CountDownLatch(1);
		final Runnable myConsumerRunnable = new ConsumerRunnable(BootstrapServers,groupId,topic,latch);
		Thread myThread= new Thread(myConsumerRunnable);
		myThread.start();
		Runtime.getRuntime().addShutdownHook(new Thread( () -> {
			
			logger.info("shutdown Hook");
			((ConsumerRunnable) myConsumerRunnable).shutdown();
		}));
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			logger.error("Application is interrupted",e);
		}finally
		{
			logger.info("Application is closed");
		}
	}
	
public class ConsumerRunnable implements Runnable
{
	private CountDownLatch latch;
	
	private KafkaConsumer<String,String> consumer;
	Logger logger=LoggerFactory.getLogger(ConsumerDemo.class);
	
	public ConsumerRunnable(String BootstrapServers,String groupId,String topic,CountDownLatch latch) 
	{
		// TODO Auto-generated method stub
		this.latch=latch;
		Properties propreties=new Propreties();
		
		// config Consumer
		
		propreties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BootstrapServers);
		propreties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		propreties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,  StringDeserializer.class.getName());
		propreties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,groupId);
		propreties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
		this.consumer=new KafkaConsumer<String,String>(propreties);
		consumer.subscribe(Arrays.asList(topic));
	}
	public void run() 
	{
		// TODO Auto-generated method stub
		try
		{
		
			while(true)	
			{
			ConsumerRecords<String,String> records =consumer.poll(Duration.ofMillis(100));
			for (ConsumerRecord<String, String> record :records)
			{
				logger.info("Key : "+ record.key() + " value : " + record.value());
				logger.info("Partition : "+ record.partition() + " offset : " + record.offset());
			}
			}
		}catch(WakeupException e)
		{
			logger.info("Shutdown received wakeup");
		}finally 
		{
			consumer.close();
			latch.countDown();
			
		}
	}
	public void shutdown() {
		// TODO Auto-generated method stub
		consumer.wakeup();
	}
	
}
}

