package com.springcloud.producer.kafka;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class Myproducer {
	private static KafkaProducer<String, String> producer;
	static {
		Properties properties = new Properties();
		properties.put("bootstrap.servers", "127.0.0.1:9092");
		properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		properties.put("partitioner.class", "com.springcloud.producer.kafka.CustomPartitioner");
		producer = new KafkaProducer<>(properties);
	}

	private static void sendMessageForgetResult() {
		ProducerRecord<String, String> record = new ProducerRecord<>("test", "name", "forgetResult");
		producer.send(record);
		producer.close();
	}

	private static void sendMessageSync() throws InterruptedException, ExecutionException {
		ProducerRecord<String, String> record = new ProducerRecord<>("test", "name", "sync");
		Future<RecordMetadata> send = producer.send(record);
		RecordMetadata recordMetadata = send.get();

		System.out.println(recordMetadata.topic());
		System.out.println(recordMetadata.partition());
		System.out.println(recordMetadata.offset());

		producer.close();
	}

	private static void sendMessageCallback() {
		ProducerRecord<String, String> record = new ProducerRecord<>("test", "name", "callback");
		producer.send(record, new Callback() {
			@Override
			public void onCompletion(RecordMetadata metadata, Exception exception) {
				if (exception != null) {
					return;
				}
				System.out.println(metadata.topic());
				System.out.println(metadata.partition());
				System.out.println(metadata.offset());
			}
		});
		producer.close();
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
//		sendMessageForgetResult();
//		sendMessageSync();
		sendMessageCallback();
	}
}
