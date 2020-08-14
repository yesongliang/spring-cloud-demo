package com.springcloud.producer.mysqlBinlog;

import java.io.IOException;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.springcloud.producer.mysqlBinlog.listener.MysqlEventListener;

public class MysqlBinlogClient {
	public static void main(String[] args) throws IOException {

//		 mysql version:mysql Ver 14.14 Distrib 5.7.30, for Linux (x86_64) usingEditLine wrapper
		BinaryLogClient binaryLogClient = new BinaryLogClient("127.0.0.1", 3306, "kedacom", "kedacom");

//		// 设置反序列化信息
//		EventDeserializer eventDeserializer = new EventDeserializer();
//		eventDeserializer.setCompatibilityMode(EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
//				EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY);
//		binaryLogClient.setEventDeserializer(eventDeserializer);

//		// 默认为最新的BinLog日志开始监听
//		// 指定BinLog文件开始
//		binaryLogClient.setBinlogFilename(binlogFilename);
//		// 指定BinLog文件开始位置
//		binaryLogClient.setBinlogPosition(binlogPosition);
		binaryLogClient.registerEventListener(new MysqlEventListener());
		binaryLogClient.connect();

	}
}
