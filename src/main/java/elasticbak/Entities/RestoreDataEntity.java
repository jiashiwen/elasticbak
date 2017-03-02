package elasticbak.Entities;

import java.io.File;

import org.elasticsearch.client.Client;

public class RestoreDataEntity {
	private Client client;
	private String indexname;
	private File datafile;

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public String getIndexname() {
		return indexname;
	}

	public void setIndexname(String indexname) {
		this.indexname = indexname;
	}

	public File getDatafile() {
		return datafile;
	}

	public void setDatafile(File datafile) {
		this.datafile = datafile;
	}

}
