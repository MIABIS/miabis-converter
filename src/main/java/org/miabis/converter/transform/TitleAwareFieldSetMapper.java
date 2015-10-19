package org.miabis.converter.transform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class TitleAwareFieldSetMapper implements FieldSetMapper<String[]> {

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
	public String[] mapFieldSet(FieldSet fieldSet) throws BindException {
		
		List<String> record = new ArrayList<String>();
		
		for(String db : dbNames){
			String value = (properties.getProperty(db) != null) ? fieldSet.readString(properties.getProperty(db)) : "";
			value = (value.length() > 0) ? value : null; // if value is empty string then assign null
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
}
