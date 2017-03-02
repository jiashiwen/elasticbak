package elasticbak.Entities;

import java.io.File;
import java.util.List;

public class BackupSetEntity {

	private File metafile;
	private List<File> datafilelist;


	public File getMetafile() {
		return metafile;
	}

	public void setMetafile(File metafile) {
		this.metafile = metafile;
	}

	public List<File> getDatafilelist() {
		return datafilelist;
	}

	public void setDatafilelist(List<File> datafilelist) {
		this.datafilelist = datafilelist;
	}

}
