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

	@Parameter(names = "--index", required = false, description = "Index name")
	private String index;

	@Parameter(names = "--dir", description = "Backup directory,default is current")
	private String dir = "./";

	@Parameter(names = "--filesize", description = "Quantity docs per file,default is 500")
	private int filesize = 500;

	@Parameter(names = "--type", description = "Transfor type value is [data,meta,force] and default value is 'meta'.If value is 'metadata' try to create a new empty target index as source;'data' copy source index documents to target index; 'force' delete target index if exists and copy source index to target index.")
	private String type = "meta";

	@Parameter(names = "--dsl", description = "elasticsearch query dsl for Preform a partial transfor based on search results.you must make content of this variable between '',just like  '{\"query\":{\"term\":{\"word.primitive\":{\"value\":\"keywork\"}}}}' ")
	private String dsl;

	@Parameter(names = "--script_file", description = "execute script file write by json ")
	private String script_file;

	@Parameter(names = "--datafolder", description = "The data file store path")
	private String datafolder = "";

	@Parameter(names = "--metafile", description = "Restore Index from metadata,include sttings and mappings")
	private String metafile = "";

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

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
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

	public String getDatafolder() {
		return datafolder;
	}

	public void setDatafolder(String datafolder) {
		this.datafolder = datafolder;
	}

}
