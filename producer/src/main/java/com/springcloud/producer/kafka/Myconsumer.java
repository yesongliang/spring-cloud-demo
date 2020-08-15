package com.springcloud.producer.kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;

public class Myconsumer {

	private static KafkaConsumer<String, String> consumer;

	private static Properties properties;

	static {
		properties = new Properties();
		properties.put("bootstrap.servers", "127.0.0.1:9092");
		properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		properties.put("group.id", "kafkaStudy");
	}

	/**
	 * 自动提交位移（offset）
	 */
	private static void generalConsumeMessageAutoCommit() {
		properties.put("enable.auto.commit", true);
		consumer = new KafkaConsumer<>(properties);
		// 订阅主题
		consumer.subscribe(Collections.singleton("test"));

		try {
			while (true) {
				boolean flag = true;
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
				for (ConsumerRecord<String, String> record : records) {
					System.out.println(String.format("topic = %s, partition = %s, key = %s, value = %s", record.topic(), record.partition(), record.key(), record.value()));
					if (record.value().equals("stop")) {
						flag = false;
					}
				}
				if (!flag) {
					break;
				}
			}
		} finally {
			consumer.close();
		}
	}

	/**
	 * 同步提交位移
	 */
	private static void generalConsumeMessageSyncCommit() {
		properties.put("enable.auto.commit", false);
		consumer = new KafkaConsumer<>(properties);
		// 订阅主题
		consumer.subscribe(Collections.singleton("test"));

		try {
			while (true) {
				boolean flag = true;
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
				for (ConsumerRecord<String, String> record : records) {
					System.out.println(String.format("topic = %s, partition = %s, key = %s, value = %s", record.topic(), record.partition(), record.key(), record.value()));
					if (record.value().equals("stop")) {
						flag = false;
					}
				}
				consumer.commitSync();
				if (!flag) {
					break;
				}
			}
		} finally {
			consumer.close();
		}
	}

	/**
	 * 异步提交位移
	 */
	private static void generalConsumeMessageAsyncCommit() {
		properties.put("enable.auto.commit", false);
		consumer = new KafkaConsumer<>(properties);
		// 订阅主题
		consumer.subscribe(Collections.singleton("test"));

		try {
			while (true) {
				boolean flag = true;
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
				for (ConsumerRecord<String, String> record : records) {
					System.out.println(String.format("topic = %s, partition = %s, key = %s, value = %s", record.topic(), record.partition(), record.key(), record.value()));
					if (record.value().equals("stop")) {
						flag = false;
					}
				}
				consumer.commitAsync();
				if (!flag) {
					break;
				}
			}
		} finally {
			consumer.close();
		}
	}

	/**
	 * 异步提交位移,带回调
	 */
	private static void generalConsumeMessageAsyncCommitWithCallback() {
		properties.put("enable.auto.commit", false);
		consumer = new KafkaConsumer<>(properties);
		// 订阅主题
		consumer.subscribe(Collections.singleton("test"));

		try {
			while (true) {
				boolean flag = true;
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
				for (ConsumerRecord<String, String> record : records) {
					System.out.println(String.format("topic = %s, partition = %s, key = %s, value = %s", record.topic(), record.partition(), record.key(), record.value()));
					if (record.value().equals("stop")) {
						flag = false;
					}
				}
				consumer.commitAsync(new OffsetCommitCallback() {
					@Override
					public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
						if (exception != null) {
							System.out.println("commit failed for offset: " + exception.getMessage());
						}
					}
				});
				if (!flag) {
					break;
				}
			}
		} finally {
			consumer.close();
		}
	}

	/**
	 * 异步同步混合提交位移
	 */
	private static void generalConsumeMessageMixedCommit() {
		properties.put("enable.auto.commit", false);
		consumer = new KafkaConsumer<>(properties);
		// 订阅主题
		consumer.subscribe(Collections.singleton("test"));

		try {
			while (true) {
				boolean flag = true;
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
				for (ConsumerRecord<String, String> record : records) {
					System.out.println(String.format("topic = %s, partition = %s, key = %s, value = %s", record.topic(), record.partition(), record.key(), record.value()));
					if (record.value().equals("stop")) {
						flag = false;
					}
				}
				try {
					consumer.commitAsync();
				} catch (RuntimeException e) {
					consumer.commitSync();
				}
				if (!flag) {
					break;
				}
			}
		} finally {
			consumer.close();
		}
	}

	public static void main(String[] args) {
		generalConsumeMessageAutoCommit();
	}
}
