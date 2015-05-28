package org.miabis.converter.transform;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "sample")
public class IndexableSample{
	
	protected String id;
    protected String parentSampleId;
    
    protected XMLGregorianCalendar sampledTime;
    //protected OntologyTerm anatomicalSite;
    //protected Biobank biobank;
	
	public IndexableSample() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentSampleId() {
		return parentSampleId;
	}

	public void setParentSampleId(String parentSampleId) {
		this.parentSampleId = parentSampleId;
	}

	public XMLGregorianCalendar getSampledTime() {
		return sampledTime;
	}

	public void setSampledTime(XMLGregorianCalendar sampledTime) {
		this.sampledTime = sampledTime;
	}

}
