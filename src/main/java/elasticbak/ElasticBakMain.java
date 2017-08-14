package elasticbak;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.fasterxml.jackson.databind.ObjectMapper;

import elasticbak.Entities.ArgsSettingEntity;
import elasticbak.Entities.BackupEntity;
import elasticbak.Entities.RestoreDataEntity;
import elasticbak.service.ParallelBackupService;
import elasticbak.service.ParallelRestoreDataService;
import elasticbak.utilities.BackupEsIndex;
import elasticbak.utilities.CheckArgs;
import elasticbak.utilities.ElasticsearchConnector;
import elasticbak.utilities.ElasticsearchIndexTools;
import elasticbak.utilities.FileUtilities;
import elasticbak.utilities.RestoreEsIndex;

public class ElasticBakMain {

	private static final Logger logger = LoggerFactory.getLogger(ElasticBakMain.class);

	public static void main(String[] args) throws Exception {
		// 获取系统当前时间
		Date dt = new Date();
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String nowtime = df.format(dt);
		ElasticsearchIndexTools estools = new ElasticsearchIndexTools();

		ObjectMapper objectMapper = new ObjectMapper();
		ArgsSettingEntity argssetting = new ArgsSettingEntity();

		FileUtilities fileutilities = new FileUtilities();
		CheckArgs check;
		Client client;

		JCommander jc = new JCommander();
		jc.setProgramName("java -jar elasticbak.jar <exp/imp>");

		jc.addObject(argssetting);

		/*
		 * 解析输入参数
		 */
		try {
			jc.parse(args);
		} catch (Exception e) {
			jc.usage();
			e.printStackTrace();
			System.exit(0);
		}

		check = new CheckArgs(argssetting);
		if (argssetting.isHelp()) {
			jc.usage();
			System.exit(0);
		}

		if (!check.check()) {
			jc.usage();
			System.exit(0);
		}

		String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(argssetting);
		// String json = objectMapper.writeValueAsString(argssetting);
		logger.info("Your command line setting is: " + json);

		// 索引备份
		if (argssetting.isExp()) {
			ExecutorService execservice = Executors.newFixedThreadPool(argssetting.getThreads());
			CompletionService<Long> completionService = new ExecutorCompletionService<Long>(execservice);

			// 如果备份路径不以文件分隔符结尾，自动添加文件分隔符
			if (!argssetting.getBackupdir().endsWith(File.separator)) {
				argssetting.setBackupdir(argssetting.getBackupdir() + File.separator);
			}

			client = new ElasticsearchConnector(argssetting.getCluster(), argssetting.getHost(), argssetting.getPort())
					.getClient();

			for (String bakidx : check.getBackupindeces()) {
				BackupEntity backup = new BackupEntity();
				String backpath = argssetting.getBackupdir() + bakidx + File.separator;
				backup.setClient(client);
				backup.setBackuppath(backpath);
				backup.setIndexname(bakidx);
				backup.setZip(argssetting.getZip());
				backup.setDocsperfile(argssetting.getFilesize());

				// 创建备份目录
				fileutilities.createFolder(backpath);

				completionService.submit(new ParallelBackupService(new BackupEsIndex(backup)));
			}

			for (String bakidx : check.getBackupindeces()) {
				completionService.take().get();
			}
			System.exit(0);
		}

		// 恢复索引
		if (argssetting.isImp()) {
			ExecutorService execservice = Executors.newFixedThreadPool(argssetting.getThreads());
			CompletionService<Long> completionService = new ExecutorCompletionService<Long>(execservice);
			int tasks = 0;

			client = new ElasticsearchConnector(argssetting.getCluster(), argssetting.getHost(), argssetting.getPort())
					.getClient();

			RestoreEsIndex restoreindex = new RestoreEsIndex();

			if (argssetting.getRestoretype().toLowerCase().equals("meta")
					|| argssetting.getRestoretype().toLowerCase().equals("force")) {
				// 从备份meta文件重建索引
				restoreindex.CreateIdxFromMetaFile(client, argssetting.getRestoreindex(),
						new File(argssetting.getMetafile()));
			}

			if (argssetting.getRestoretype().toLowerCase().equals("normal")
					&& !estools.IndexExistes(client, argssetting.getRestoreindex())) {
				// 判断meta文件是否存在
				File file = new File(argssetting.getMetafile());
				if (!file.exists()) {
					logger.error("Metafile not exists");
					System.exit(0);
				}
				// 从备份meta文件重建索引
				restoreindex.CreateIdxFromMetaFile(client, argssetting.getRestoreindex(),
						new File(argssetting.getMetafile()));
			}

			if (argssetting.getRestoretype().toLowerCase().equals("normal")
					|| argssetting.getRestoretype().toLowerCase().equals("dataonly")
					|| argssetting.getRestoretype().toLowerCase().equals("force")) {
				// 恢复数据
				List<File> datafiles = fileutilities.getFilesInTheFolder(argssetting.getBackupset());
				for (File f : datafiles) {
					if (f.getName().endsWith(".data") || f.getName().endsWith(".data.zip")) {
						RestoreDataEntity data = new RestoreDataEntity();
						data.setClient(client);
						data.setIndexname(argssetting.getRestoreindex());
						data.setDatafile(f);
						RestoreEsIndex ridx = new RestoreEsIndex();
						ridx.setRestordata(data);
						completionService.submit(new ParallelRestoreDataService(ridx));
						tasks++;
					}
				}

				for (int i = 0; i < tasks; i++) {
					completionService.take().get();
				}
			}

			client.close();
			System.exit(0);

		}

		// client = new ElasticsearchConnector(argssetting.getCluster(),
		// argssetting.getHost(), argssetting.getPort())
		// .getClient();
		// client.close();
		System.exit(0);

	}

}
