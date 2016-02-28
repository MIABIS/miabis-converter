package org.miabis.converter.transform;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class TitleAwareFieldSetMapper implements FieldSetMapper<String[]> {

	final static Logger logger = Logger.getLogger(TitleAwareFieldSetMapper.class);
	
	private String[] dbNames;
	private Properties properties;
	private Map<String, String[]> mappings;
	
	public TitleAwareFieldSetMapper(String propertiesPath) {
		
		properties = new Properties();
		try {
			properties.load(new FileInputStream(propertiesPath));
		} catch (Exception e) {
			//Try to load from classpath for testing purpose
			try {
				properties.load(this.getClass().getClassLoader().getResourceAsStream(propertiesPath));
			} catch (IOException e1) {
				e1.printStackTrace();
			}catch(Exception e2){
				logger.error("Cannot load properties file '"+propertiesPath+"'.", e2);
			}
		}
		
		processListKeys();
	}

	@Override
	public String[] mapFieldSet(FieldSet fieldSet) throws BindException {
		
		List<String> record = new ArrayList<String>();
		
		for(String db : dbNames){
			String value = (properties.getProperty(db) != null) ? fieldSet.readString(properties.getProperty(db)) : "";
			value = (value.length() > 0) ? mapValue(db, value) : null; // if value is empty string then assign null
			record.add(value);
		}
		return record.toArray(new String[0]);
	}

	public String[] getDbNames() {
		return dbNames;
	}

	public void setDbNames(String[] dbNames) {
		this.dbNames = dbNames;
	}
	
	private String mapValue(String db, String value){
		
		String key = db.split("\\.")[1];
		if(mappings.containsKey(key)){
			String[] arr = mappings.get(key);
			
			if(arr[0].equalsIgnoreCase(value)){
				value = arr[1];
			}
		}
		return value;
	}
	
	private void processListKeys(){
		
		mappings = new HashMap<String, String[]>();
		Enumeration<Object> keys = properties.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement().toString();
			
			if(key.startsWith("list")){
				String[] arr = key.split("\\.");
				String[] newArr = {arr[2], properties.getProperty(key)};
				mappings.put(arr[1], newArr);
			}
		}
	}
}
