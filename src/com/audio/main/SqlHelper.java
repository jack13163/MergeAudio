package com.audio.main;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlHelper {

	/**
	 * 获取ResultSet中元素的个数
	 * 
	 * @param results
	 * @return
	 * @throws SQLException
	 */
	public static int getResultSetSize(ResultSet results) throws SQLException {
		// 将游标移动到最后一行上
		results.last();

		// 得到当前的 row number，在 JDBC 中，row number 从1开始，所以这里就相当于行数
		int rowCount = results.getRow();

		// 此时游标执行了最后一行，如果我们后面还想从头开始调用 next()遍历整个结果集，我们可以将游标移动到第一行前面
		results.beforeFirst();

		// 通过上述这步操作，我们算是回复了结果集到初始状态（即刚查询出来的状态）
		return rowCount;
	}
}
