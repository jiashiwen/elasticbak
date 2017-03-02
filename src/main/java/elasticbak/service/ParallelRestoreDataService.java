package elasticbak.service;

import java.io.IOException;
import java.util.concurrent.Callable;

import elasticbak.Entities.RestoreDataEntity;
import elasticbak.utilities.RestoreEsIndex;

public class ParallelRestoreDataService implements Callable<Long>{
	

	private RestoreEsIndex restoreesindex;

	public RestoreEsIndex getRestoreesindex() {
		return restoreesindex;
	}




	public void setRestoreesindex(RestoreEsIndex restoreesindex) {
		this.restoreesindex = restoreesindex;
	}

	public ParallelRestoreDataService(RestoreEsIndex ridx){
		this.setRestoreesindex(ridx);
	}


	@Override
	public Long call() {
		
		try {
			restoreesindex.restoreDataFromFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new Long(Thread.currentThread().getId());
	}

}
