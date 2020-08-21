package com.springcloud.producer.mysqlBinlog.listener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.shyiko.mysql.binlog.BinaryLogClient.EventListener;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.QueryEventData;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.springcloud.producer.mysqlBinlog.handlers.BinLogData;
import com.springcloud.producer.mysqlBinlog.handlers.Ihandler;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class MysqlEventListener implements EventListener {

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
	/** 注册的解析处理器 **/
	private Map<String, Ihandler> registerMap = new HashMap<>();

	public void register(String key, Ihandler handler) {
		registerMap.put(key, handler);
	}

	/**
	 * 本地测试，BinLog格式为mixed
	 */
	@Override
	public void onEvent(Event event) {

		// 解析数据
		EventData data = event.getData();
		// STATEMENT
		if (data instanceof QueryEventData) {
			// TODO 1.若属性为数据库默认值或自动生成，则无法获取到;
			// TODO 2.CHAR/VARCHAR类型存储值不能含以下字符:
			// TODO , and AND ( );
			// TODO 3.增删改SQL语句执行成功就会触发，不关心导致数据修改
			QueryEventData queryEventData = (QueryEventData) data;
			log.info("QueryEventData: {}", queryEventData);
			String sql = queryEventData.getSql().trim();
			// 插入
			if (sql.toLowerCase().startsWith("insert into")) {
				this.database = queryEventData.getDatabase();
				this.queryEventInsert(sql);
				// 更新
			} else if (sql.toLowerCase().startsWith("update")) {
				this.database = queryEventData.getDatabase();
				this.queryEventUpdate(sql);
				// 删除
			} else if (sql.toLowerCase().startsWith("delete from")) {
				this.database = queryEventData.getDatabase();
				this.queryEventDelete(sql);
			}
			// 插入、更新、删除前会触发一次(ROW)
		} else if (data instanceof TableMapEventData) {
			TableMapEventData tableMapEventData = (TableMapEventData) data;
			this.database = tableMapEventData.getDatabase();
			this.table = tableMapEventData.getTable();
			log.info("database: {},table: {}", this.database, this.table);
			// 删除(ROW)
		} else if (data instanceof DeleteRowsEventData) {
			DeleteRowsEventData deleteRowsEventData = (DeleteRowsEventData) data;
			log.info("DeleteRowsEventData: {}", deleteRowsEventData);
			this.rowData = new ArrayList<>();
			this.logFormat = "ROW";
			this.operateType = "DELETE";
			// 获取column ID
			BitSet includedColumns = deleteRowsEventData.getIncludedColumns();
			for (Serializable[] row : deleteRowsEventData.getRows()) {
				Map<Integer, String> map = new HashMap<>();
				for (int i = 0; i < row.length; i++) {
					map.put(includedColumns.nextSetBit(i), String.valueOf(row[i]));
				}
				this.rowData.add(map);
			}
			// 更新(ROW)
		} else if (data instanceof UpdateRowsEventData) {
			UpdateRowsEventData updateRowsEventData = (UpdateRowsEventData) data;
			log.info("UpdateRowsEventData: {}", updateRowsEventData);
			this.rowData = new ArrayList<>();
			this.logFormat = "ROW";
			this.operateType = "UPDATE";
			// 获取column ID
			BitSet includedColumns = updateRowsEventData.getIncludedColumns();
			for (Map.Entry<Serializable[], Serializable[]> row : updateRowsEventData.getRows()) {
				Map<Integer, String> map = new HashMap<>();
				// 只获取更新后的值
				Serializable[] after_value = row.getValue();
				for (int i = 0; i < after_value.length; i++) {
					map.put(includedColumns.nextSetBit(i), String.valueOf(after_value[i]));
				}
				this.rowData.add(map);
			}
			// 插入(ROW)
		} else if (data instanceof WriteRowsEventData) {
			WriteRowsEventData writeRowsEventData = (WriteRowsEventData) data;
			log.info("WriteRowsEventData: {}", writeRowsEventData);
			this.rowData = new ArrayList<>();
			this.logFormat = "ROW";
			this.operateType = "INSERT";
			// 获取column ID
			BitSet includedColumns = writeRowsEventData.getIncludedColumns();
			for (Serializable[] row : writeRowsEventData.getRows()) {
				Map<Integer, String> map = new HashMap<>();
				for (int i = 0; i < row.length; i++) {
					map.put(includedColumns.nextSetBit(i), String.valueOf(row[i]));
				}
				this.rowData.add(map);
			}
		}

		// 处理数据
		this.handle();
	}

	/**
	 * sql='INSERT INTO ad_user(username,token,user_status,create_time,update_time)
	 * VALUES ('ysl1','hjkhsj','1','2019-08-30 12:20:20','2019-08-30 12:20:20')'
	 * 
	 * @param sql
	 */
	private void queryEventInsert(String sql) {
		this.statementData = new HashMap<>();
		this.logFormat = "STATEMENT";
		this.operateType = "INSERT";
		// 获取表名
		int table_beginIndex = "insert into".length() + 1;
		int table_endIndex = sql.indexOf("(");
		this.table = sql.substring(table_beginIndex, table_endIndex).trim();
		// 去除`
		if (this.table.startsWith("`")) {
			this.table = this.table.substring(1, this.table.length() - 1);
		}
		// 获取column字段
		List<String> columns = new ArrayList<>();
		int column_beginIndex = table_endIndex + 1;
		int column_endIndex = sql.indexOf(")");
		for (String str : sql.substring(column_beginIndex, column_endIndex).split(",")) {
			// 去除左右空格
			str = str.trim();
			// 去除`
			if (str.startsWith("`")) {
				str = str.substring(1, str.length() - 1);
			}
			columns.add(str);
		}
		// 获取column值
		int data_beginIndex = sql.lastIndexOf("(") + 1;
		int data_endIndex = sql.lastIndexOf(")");
		String[] split = sql.substring(data_beginIndex, data_endIndex).split(",");
		for (int i = 0; i < split.length; i++) {
			// 去除左右空格
			String str = split[i].trim();
			// 字符类型去除单引号
			if (str.startsWith("'")) {
				str = str.substring(1, str.length() - 1);
			}
			this.statementData.put(columns.get(i), str);
		}
	}

	/**
	 * sql='UPDATE `ad_user` SET `token`='22', `user_status`='1' WHERE (`id`='1')'
	 *
	 * sql='UPDATE ad_user SET token='1',user_status='0' WHERE username='ysl1' AND
	 * create_time='2019-08-30 12:20:20''
	 * 
	 * @param sql
	 */
	private void queryEventUpdate(String sql) {
		this.statementData = new HashMap<>();
		this.logFormat = "STATEMENT";
		this.operateType = "UPDATE";
		// 获取表名
		int table_beginIndex = "update".length() + 1;
		int table_endIndex = sql.toLowerCase().indexOf("set");
		this.table = sql.substring(table_beginIndex, table_endIndex).trim();
		// 去除`
		if (this.table.startsWith("`")) {
			this.table = this.table.substring(1, this.table.length() - 1);
		}
		// 获取set字段及数据
		int set_beginIndex = table_endIndex + 3;
		int set_endIndex = sql.toLowerCase().indexOf("where");
		String[] setArray = sql.substring(set_beginIndex, set_endIndex).split(",");
		this.putMap(setArray, this.statementData);
		// 获取where字段及数据
		int where_beginIndex = set_endIndex + 5;
		String where_string = sql.substring(where_beginIndex).trim();
		// 去除(
		if (where_string.startsWith("(")) {
			where_string = where_string.substring(1, where_string.length() - 1);
		}
		String[] whereArray = where_string.split("and|AND");
		this.putMap(whereArray, this.statementData);
	}

	/**
	 * sql='DELETE FROM ad_user WHERE username='ysl1' AND create_time='2019-08-30
	 * 12:20:20''
	 * 
	 * sql='DELETE FROM `ad_user` WHERE (`id`='1')'
	 * 
	 * @param sql
	 */
	private void queryEventDelete(String sql) {
		this.statementData = new HashMap<>();
		this.logFormat = "STATEMENT";
		this.operateType = "DELETE";
		// 获取表名
		int table_beginIndex = "delete from".length() + 1;
		int table_endIndex = sql.toLowerCase().indexOf("where");
		this.table = sql.substring(table_beginIndex, table_endIndex).trim();
		// 去除`
		if (this.table.startsWith("`")) {
			this.table = this.table.substring(1, this.table.length() - 1);
		}
		// 获取where字段及数据
		int where_beginIndex = sql.toLowerCase().indexOf("where") + 5;
		String where_string = sql.substring(where_beginIndex).trim();
		// 去除(
		if (where_string.startsWith("(")) {
			where_string = where_string.substring(1, where_string.length() - 1);
		}
		String[] whereArray = where_string.split("and|AND");
		this.putMap(whereArray, this.statementData);
	}

	private void putMap(String[] strArray, Map<String, String> map) {
		for (String str : strArray) {
			// 去除左右空格
			str = str.trim();
			String[] split = str.split("=");
			String column_name = split[0].trim();
			String column_data = split[1].trim();
			// 去除`
			if (column_name.startsWith("`")) {
				column_name = column_name.substring(1, column_name.length() - 1);
			}
			// 去除'
			if (column_data.startsWith("'")) {
				column_data = column_data.substring(1, column_data.length() - 1);
			}
			map.put(column_name, column_data);
		}
	}

	private void handle() {
		if (this.operateType != null) {
			// TODO 1、筛选感兴趣的数据库和表
			// TODO 2、做一些转换操作，可将BinLog格式:STATEMENT/ROW的数据统一格式处理
			BinLogData data = new BinLogData(this.database, this.table, this.operateType, this.logFormat, this.rowData, this.statementData);
			for (Map.Entry<String, Ihandler> entry : this.registerMap.entrySet()) {
				Ihandler handler = entry.getValue();
				handler.handle(data);
			}
			this.clear();
		}
	}

	private void clear() {
		this.logFormat = null;
		this.table = null;
		this.operateType = null;
		this.rowData = null;
		this.statementData = null;
	}

}
