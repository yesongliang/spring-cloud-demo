package com.springcloud.producer.kafka;

import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.InvalidRecordException;
import org.apache.kafka.common.utils.Utils;

/**
 * 消息的key是用来分区计算的
 * 
 * 自定义分区分配器
 * 
 * @author ysl
 *
 */
public class CustomPartitioner implements Partitioner {

	@Override
	public void configure(Map<String, ?> configs) {
	}

	@Override
	public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
		Integer num = cluster.partitionCountForTopic(topic);
		if (null == keyBytes || !(key instanceof String)) {
			throw new InvalidRecordException("kafka message must have key and key class is string");
		}
		if (num.intValue() == 1) {
			return 0;
		}
		return Math.abs(Utils.murmur2(keyBytes) % (num - 1));
	}

	@Override
	public void close() {

	}

}
