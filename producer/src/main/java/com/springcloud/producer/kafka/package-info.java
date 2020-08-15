/**
 * kafka 相关命令
 * 启动zookeeper： bin/zookeeper-server-start.sh config/zookeeper.properties
 * 
 * 启动Kafka：bin/kafka-server-start.sh config/server.properties 
 * 
 * 创建主题：bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic topicName
 * 
 * 查看主题：bin/kafka-topics.sh --zookeeper localhost:2181 --list 
 * 		   bin/kafka-topics.sh --zookeeper localhost:2181 --topic topicName --describe
 * 生产者生产消息：bin/kafka-console-producer.sh --broker-list localhost:9092 --topic topicName
 * 
 * 消费者消费消息：bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic topicName --from-beginning <br>
 * 
 * 
 * 
 * @author ysl
 */
package com.springcloud.producer.kafka;
