package org.miabis.converter.batch.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;

import org.elasticsearch.common.base.Joiner;
import org.elasticsearch.common.base.Splitter;
import org.elasticsearch.common.collect.Lists;
import org.miabis.exchange.schema.ContactInformation;
import org.miabis.exchange.schema.Disease;
import org.miabis.exchange.schema.OntologyTerm;

public class MiabisEncoder {

	private final String DELIMITER = Util.DELIMITER_VERTICAL_BAR;
	//Escape | since string.split expects regex argument. An unescaped | is parsed as a regex meaning "empty string or empty string"
	private final String DECODE_DELIMITER = "\\"+Util.DELIMITER_VERTICAL_BAR;
	private final String CONTACT_DELIMITER = Util.DELIMITER_BACKSLASH;
	
	public MiabisEncoder() {}
	
	/**
	 * Returns a string that represents a contact. The string is delimited by CONTACT_DELIMITER
	 * @param contact
	 * @return
	 */
	public String encodeContactInformation(ContactInformation contact){
		String encode = "";
		
		if(contact != null){
			List<String> contactLst =  Arrays.asList(contact.getId(), contact.getFirstname(), contact.getLastname(), 
			                                  contact.getPhone(), contact.getEmail(), contact.getAddress(), 
			                                  contact.getZip(), contact.getCity() , contact.getCountry());
			
			encode = Joiner.on(CONTACT_DELIMITER).useForNull("").join(contactLst);
		}	
		return encode;
	}
	
	public String encodeOntologyTerm(OntologyTerm term){
		
		String encode = "";
		
		if(term != null){
			List<String> termLst = Arrays.asList(term.getId(), term.getOntology(), term.getVersion(),
					term.getCode(), term.getDescription());

			encode = Joiner.on(CONTACT_DELIMITER).useForNull("").join(termLst);
		}
		
		return encode;
	}
	
	public OntologyTerm decodeOntologyTerm(String term){
		List<String> values = getTokens(term);
		OntologyTerm oTerm = null;
		if(values.size() == 5){
			oTerm = new OntologyTerm();
			oTerm.setId(values.get(0));
			oTerm.setOntology(values.get(1));
			oTerm.setVersion(values.get(2));
			oTerm.setCode(values.get(3));
			oTerm.setDescription(values.get(4));
		}
		return oTerm;
	}
	
	/**
	 * Calls processContact and concatenates the resulting String with DELIMITER
	 * @param contactLst
	 * @return a String representing a list of contacts
	 */
	public String encodeContact(List<ContactInformation> contactLst){
		
		List<String> cStrLst = new ArrayList<String>();
		for(ContactInformation ci : contactLst){
			cStrLst.add(encodeContactInformation(ci));
		}
		
		return String.join(DELIMITER, cStrLst);
	}
	
	/**
	 * Returns a String that represents a disease. The String is delimited by CONTACT_DELIMITER
	 * @param disease
	 * @return	a String representing a disease
	 */
	public String encodeDisease(Disease disease){
		
		String encode = "";
		
		if(disease != null){
			List<String> diseaseLst = Arrays.asList(disease.getId(), disease.getOntology(), disease.getVersion(), 
					disease.getCode(), disease.getDescription(), disease.getFreeText());

			encode = Joiner.on(CONTACT_DELIMITER).useForNull("").join(diseaseLst);
		}
		
		return encode;
	}
	
	/**
	 * Decodes a formatted string and returns a <i>Disease</i> object
	 * @param str
	 * @return disease object
	 */
	public Disease decodeDisease(String str){
		List<String> values = getTokens(str);
		
		Disease d = null;
		if(values.size() == 6){
			d = new Disease();
			d.setId(values.get(0));
			d.setOntology(values.get(1));
			d.setVersion(values.get(2));
			d.setCode(values.get(3));
			d.setDescription(values.get(4));
			d.setFreeText(values.get(5));
		}
		return d;
	}
	
	/**
	 * Extracts the value of each object in the list and returns a String of values concatenated by DELIMITER. 
	 * @param lst
	 * @return a String representing a list of objects
	 */
	public String encodeValues(List<?> lst){
		
		List<String> values = new ArrayList<String>();
		
		try{
			if(lst.size()>0){
				Method  method = lst.get(0).getClass().getDeclaredMethod ("value", new Class[0]);
				for(Object obj : lst){
					String val = (String) method.invoke(obj, new Object[0]);
					values.add(val);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return String.join(DELIMITER, values);
	}
	
	/**
	 * Decodes a formatted string and returns a <i>ContactInformation</i> object
	 * @param str
	 * @return contact information object
	 */
	public ContactInformation decodeContactInformation(String str){
		
		List<String> values = getTokens(str);

		ContactInformation ci = null;
		if(values.size() == 9){
			ci = new ContactInformation();
			ci.setId(values.get(0));
			ci.setFirstname(values.get(1));
			ci.setLastname(values.get(2));
			ci.setPhone(values.get(3));
			ci.setEmail(values.get(4));
			ci.setAddress(values.get(5));
			ci.setZip(values.get(6));
			ci.setCity(values.get(7));
			ci.setCountry(values.get(8));
		}
		return ci;
	}
	
	/**
	 * Splits a String around matches of <i>|</i> and filters null values
	 * @param str a String
	 * @return a string Stream
	 */
	public Stream<String> getListStream(String str){
		str = (str == null) ? "" : str;
		return Arrays.asList(str.split(DECODE_DELIMITER)).stream().filter(s -> s == null || s.length() > 0);
	}

	/**
	 * Splits a String around matches of <i>\</i>
	 * @param str a String
	 * @return a list of Strings
	 */
	private List<String> getTokens(String str){
		
		List<String> values = Lists.newArrayList(Splitter.on(CONTACT_DELIMITER).trimResults().split(str));
		ListIterator<String> i = values.listIterator();
		
		while(i.hasNext()){
			String nxt = i.next();
			if (nxt.equals("")) i.set(null);
		}
		return values;
	}
	
}
