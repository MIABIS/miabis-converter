package org.miabis.converter.batch.writers;

import static org.springframework.util.Assert.state;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.miabis.exchange.schema.Sample;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Writes <i>IndexableSamples</i> in a elastic search instance.
 * @author jvillaveces
 */
public class ElasticSearchSampleWriter implements ItemWriter<Sample>, InitializingBean{

	private TransportClient client;
	private String clusterNodes;
	private String clusterName;
	private String index;
	private ObjectMapper mapper;
	
	/** Whether or not to delete a sample */
	private boolean delete = false;
	

	@Override
	public void afterPropertiesSet() throws Exception {
		state(clusterNodes != null, "cluster nodes are required.");
		state(clusterName != null, "cluster name is required.");
		state(index != null, "index is required.");
		
		createClient();
		mapper = new ObjectMapper();
	}
	
	@Override
	public void write(List<? extends Sample> items) throws Exception {
		
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		
		for(Sample sample : items){
			byte[] jsonSample = mapper.writeValueAsBytes(sample);
			
			if(delete){
				bulkRequest.add(client.prepareDelete(index, "sample", sample.getId()));
			}else{
				bulkRequest.add(client.prepareIndex(index, "sample", sample.getId()).setSource(jsonSample));
			}
		}
		
		BulkResponse res = bulkRequest.execute().actionGet();
	}
	
	private void createClient(){
		Settings settings = Settings.settingsBuilder()
		        .put("cluster.name", clusterName).build();
		client = TransportClient.builder().settings(settings).build();
		
		String[] nodes = clusterNodes.split(";");
		for(int i=0; i<nodes.length; i++){
			
			String host = nodes[i];
			int port = 9300;
			
			if(host.contains(":")){
				String[] tmp = host.split(":");
				host = tmp[0];
				try{
					port = Integer.parseInt(tmp[1]);
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
			}
			
			try{
				client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
			}catch(UnknownHostException e){
				e.printStackTrace();
			}
		}
	}
	
	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getClusterNodes() {
		return clusterNodes;
	}

	public void setClusterNodes(String clusterNodes) {
		this.clusterNodes = clusterNodes;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	/**
	 * Whether or not to delete a sample
	 * @param delete a boolean indicating whether or not to delete a sample
	 */
	public void setDelete(boolean delete) {
		this.delete = delete;
	}
} 
