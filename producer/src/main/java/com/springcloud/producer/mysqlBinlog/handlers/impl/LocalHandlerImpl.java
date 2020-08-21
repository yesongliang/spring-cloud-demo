package com.springcloud.producer.mysqlBinlog.handlers.impl;

import com.springcloud.producer.mysqlBinlog.handlers.BinLogData;
import com.springcloud.producer.mysqlBinlog.handlers.Ihandler;

import lombok.extern.slf4j.Slf4j;

//@Service("localHandler")
@Slf4j
public class LocalHandlerImpl implements Ihandler {

	@Override
	public void handle(BinLogData data) {
		if ("STATEMENT".equals(data.getLogFormat())) {
			log.info("localHandler-----,database: {},table: {},operateType: {},logFormat: {},statementData: {}", data.getDatabase(), data.getTable(), data.getOperateType(), data.getLogFormat(),
					data.getStatementData());
		} else if ("ROW".equals(data.getLogFormat())) {
			log.info("localHandler-----,database: {},table: {},operateType: {},logFormat: {},rowData: {}", data.getDatabase(), data.getTable(), data.getOperateType(), data.getLogFormat(),
					data.getRowData());
		}
	}

}
