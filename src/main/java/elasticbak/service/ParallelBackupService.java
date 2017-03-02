package elasticbak.service;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import elasticbak.utilities.BackupEsIndex;

public class ParallelBackupService implements Callable<Long>{
	private BackupEsIndex backup;
	
	public ParallelBackupService(BackupEsIndex backupidx){
		this.backup=backupidx;
	}

	public BackupEsIndex getBackup() {
		return backup;
	}


	public void setBackup(BackupEsIndex backup) {
		this.backup = backup;
	}


	@Override
	public Long call() {
		
	
		try {
			backup.backupIdxMeta();
		} catch (IOException | InterruptedException | ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		
		try {
			
			backup.BackupIdxData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Long(Thread.currentThread().getId());
		
	}

}
