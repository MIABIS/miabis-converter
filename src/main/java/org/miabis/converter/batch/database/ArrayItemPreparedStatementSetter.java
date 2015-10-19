package org.miabis.converter.batch.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.util.Assert;

/**
 * <p>Implementation of the {@link ItemPreparedStatementSetter}.  It assumes nothing 
 * about ordering, and assumes that the order the array can be iterated over is the same as
 * the PreparedStatement should be set.</p>
 * 
 * @see ItemPreparedStatementSetter
 */
public class ArrayItemPreparedStatementSetter implements ItemPreparedStatementSetter<String[]> {
	
	@Override
	public void setValues(String[] item, PreparedStatement ps) throws SQLException {
		Assert.isInstanceOf(String[].class, item, "Input to map PreparedStatement parameters must be of type String[].");
		
		System.out.println(String.join(",", item));
		for(int i=0; i<item.length; i++ ){
			StatementCreatorUtils.setParameterValue(ps, i+1, SqlTypeValue.TYPE_UNKNOWN, item[i]);
		}
	}
}
