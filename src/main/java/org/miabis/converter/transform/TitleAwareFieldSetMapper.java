package org.miabis.converter.transform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class TitleAwareFieldSetMapper implements FieldSetMapper<Map<String,String>> {

	private String[] dbNames;
	private Properties properties;
	
	public TitleAwareFieldSetMapper(String propertiesPath) {
		
		properties = new Properties();
		try {
			properties.load(this.getClass().getClassLoader().getResourceAsStream(propertiesPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<String,String> mapFieldSet(FieldSet fieldSet) throws BindException {
		
		Map<String,String> record = new HashMap<String,String>();
		
		for(String db : dbNames){
			String value = (properties.getProperty(db) != null) ? fieldSet.readString(properties.getProperty(db)) : "";
			String key = (db.indexOf(".") != -1 ) ? db.substring(db.indexOf(".") + 1) : db;
			record.put(key, value);
		}
		return record;
	}

	public String[] getDbNames() {
		return dbNames;
	}

	public void setDbNames(String[] dbNames) {
		this.dbNames = dbNames;
	}
}
