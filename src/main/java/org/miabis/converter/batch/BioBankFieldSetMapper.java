package org.miabis.converter.batch;

import org.miabis.exchange.schema.Biobank;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class BioBankFieldSetMapper implements FieldSetMapper<Biobank>{

	@Override
	public Biobank mapFieldSet(FieldSet fieldSet) throws BindException {
		Biobank bBank = new Biobank();
		bBank.setId(fieldSet.readString(0));
		bBank.setName(fieldSet.readString(1));
		bBank.setAcronym(fieldSet.readString(2));
		bBank.setUrl(fieldSet.readString(3));
		bBank.setDescription(fieldSet.readString(4));
		
		//fieldSet.readString("id");
		
		return bBank;
	}

}
