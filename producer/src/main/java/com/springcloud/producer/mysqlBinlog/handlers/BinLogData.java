package com.springcloud.producer.mysqlBinlog.handlers;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一数据格式定义
 * 
 * TODO 此为演示，后续须优化定义
 * 
 * @author ysl
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BinLogData {
	/** 数据库名 **/
	private String database;
	/** 表名 **/
	private String table;
	/** INSERT-插入;UPDATA-更新;DELETE-删除 **/
	private String operateType;
	/** BinLog格式:STATEMENT/ROW **/
	private String logFormat;
	/**
	 * BinLog数据:columnID->column值
	 * 
	 * TODO 获取columnID与column字段的映射关系
	 * 
	 * sql="select table_schema,table_name,column_name,ordinal_position from
	 * information_schema.columns where table_schema='vue-demo' and
	 * table_name='beautyGirls';"
	 * 
	 **/
	private List<Map<Integer, String>> rowData;
	/** BinLog数据:column字段->column值 **/
	private Map<String, String> statementData;

}
