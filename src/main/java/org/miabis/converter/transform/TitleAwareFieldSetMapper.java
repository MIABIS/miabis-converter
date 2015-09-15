package org.miabis.converter.transform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

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
		
		ArrayList<String> lst = new ArrayList<String>();
		
		for(String db : dbNames){
			String val = (properties.getProperty(db) != null) ? fieldSet.readString(properties.getProperty(db)) : "";
			lst.add(val);
		}
		
		return lst.toArray(new String[0]);
	}

	public String[] getDbNames() {
		return dbNames;
	}

	public void setDbNames(String[] dbNames) {
		this.dbNames = dbNames;
	}
}
