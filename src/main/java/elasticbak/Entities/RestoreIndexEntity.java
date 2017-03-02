package elasticbak.Entities;

import java.io.File;

import org.elasticsearch.client.Client;

public class RestoreIndexEntity {

	private Client client;
	private String indexname;
	private File metafile;

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

	public File getMetafile() {
		return metafile;
	}

	public void setMetafile(File metafile) {
		this.metafile = metafile;
	}

}
