package elasticbak.service;

import java.io.IOException;
import java.util.concurrent.Callable;

import elasticbak.utilities.RestoreEsIndex;
import net.lingala.zip4j.exception.ZipException;

public class ParallelRestoreDataService implements Callable<Long> {

	private RestoreEsIndex restoreesindex;

	public RestoreEsIndex getRestoreesindex() {
		return restoreesindex;
	}

	public void setRestoreesindex(RestoreEsIndex restoreesindex) {
		this.restoreesindex = restoreesindex;
	}

	public ParallelRestoreDataService(RestoreEsIndex ridx) {
		this.setRestoreesindex(ridx);
	}

	@Override
	public Long call() throws IOException, ZipException {

		if (restoreesindex.getRestordata().getDatafile().getAbsolutePath().endsWith(".data.zip")) {
			restoreesindex.restoreDataFromZipFile();
		} else {
			restoreesindex.restoreDataFromFile();
		}

		return new Long(Thread.currentThread().getId());
	}

}
