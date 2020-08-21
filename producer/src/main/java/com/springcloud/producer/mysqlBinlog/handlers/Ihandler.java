package com.springcloud.producer.mysqlBinlog.handlers;

public interface Ihandler {

	void handle(BinLogData data);
}
