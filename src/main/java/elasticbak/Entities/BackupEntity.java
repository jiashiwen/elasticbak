package elasticbak.Entities;

import org.elasticsearch.client.Client;

public class BackupEntity {
	private Client client;
	private String backuppath;
	private String indexname;
	private Boolean zip;
	private int docsperfile;

	public Boolean getZip() {
		return zip;
	}

	public void setZip(Boolean zip) {
		this.zip = zip;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public String getBackuppath() {
		return backuppath;
	}

	public void setBackuppath(String backuppath) {
		this.backuppath = backuppath;
	}

	public String getIndexname() {
		return indexname;
	}

	public void setIndexname(String indexname) {
		this.indexname = indexname;
	}

	public int getDocsperfile() {
		return docsperfile;
	}

	public void setDocsperfile(int docsperfile) {
		this.docsperfile = docsperfile;
	}

}
