package elasticbak.Entities;

import com.beust.jcommander.Parameter;

public class ArgsSettingEntity {

	@Parameter(names = "--help", help = true)
	private boolean help = false;

	@Parameter(names = "--exp", description = "export mode", arity = 0)
	private boolean exp = false;

	@Parameter(names = "--imp", description = "import mode", arity = 0)
	private boolean imp = false;

	@Parameter(names = "--cluster", description = "Elasticsearch cluster name,default is 'elasticsearch")
	private String cluster = "elasticsearch";

	@Parameter(names = "--host", description = "Elasticsearch cluster one of master ip address,default is '127.0.0.1'.")
	private String host = "127.0.0.1";

	@Parameter(names = "--port", description = "Elasticsearch port,default is 9300")
	private int port = 9300;

	@Parameter(names = "--backupindexes", required = false, description = "Index name")
	private String backupindexes;

	@Parameter(names = "--restoreindex", required = false, description = "Index name")
	private String restoreindex;

	@Parameter(names = "--backupdir", description = "Backup directory,default is current")
	private String backupdir = "./";

	@Parameter(names = "--filesize", description = "Quantity docs per file,default is 500")
	private int filesize = 500;

	@Parameter(names = "--threads", description = "Threads for backup or restore,default is 2")
	private int threads = 2;

	@Parameter(names = "--restoretype", description = "Transfor type value is [dataonly,meta,normal,force] and default value is 'normal'.If value is 'meta' only create index from meta file;'dataonly' index has exists only restore data; 'force' delete exists index and restore.")
	private String restoretype = "normal";

	@Parameter(names = "--dsl", description = "elasticsearch query dsl for Preform a partial transfor based on search results.you must make content of this variable between '',just like  '{\"query\":{\"term\":{\"word.primitive\":{\"value\":\"keywork\"}}}}' ")
	private String dsl;

	@Parameter(names = "--script_file", description = "execute script file write by json ")
	private String script_file;

	@Parameter(names = "--backupset", description = "The folder stored backup data include .meta file and some .data files ")
	private String backupset;

	@Parameter(names = "--metafile", description = "Restore Index from metadata,include sttings and mappings")
	private String metafile = "";

	@Parameter(names = "--zip", description = "Zip datafile ,default is false", arity = 0)
	private Boolean zip = false;

	public Boolean getZip() {
		return zip;
	}

	public void setZip(Boolean zip) {
		this.zip = zip;
	}

	public boolean isHelp() {
		return help;
	}

	public void setHelp(boolean help) {
		this.help = help;
	}

	public boolean isExp() {
		return exp;
	}

	public void setExp(boolean exp) {
		this.exp = exp;
	}

	public boolean isImp() {
		return imp;
	}

	public void setImp(boolean imp) {
		this.imp = imp;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getBackupindexes() {
		return backupindexes;
	}

	public void setBackupindexes(String backupindexes) {
		this.backupindexes = backupindexes;
	}

	public String getRestoretype() {
		return restoretype;
	}

	public void setRestoretype(String restoretype) {
		this.restoretype = restoretype;
	}

	public String getDsl() {
		return dsl;
	}

	public void setDsl(String dsl) {
		this.dsl = dsl;
	}

	public String getScript_file() {
		return script_file;
	}

	public void setScript_file(String script_file) {
		this.script_file = script_file;
	}

	public String getRestoreindex() {
		return restoreindex;
	}

	public void setRestoreindex(String restoreindex) {
		this.restoreindex = restoreindex;
	}

	public String getBackupdir() {
		return backupdir;
	}

	public void setBackupdir(String backupdir) {
		this.backupdir = backupdir;
	}

	public int getFilesize() {
		return filesize;
	}

	public void setFilesize(int filesize) {
		this.filesize = filesize;
	}

	public String getMetafile() {
		return metafile;
	}

	public void setMetafile(String metafile) {
		this.metafile = metafile;
	}

	public String getBackupset() {
		return backupset;
	}

	public void setBackupset(String backupset) {
		this.backupset = backupset;
	}

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

}
