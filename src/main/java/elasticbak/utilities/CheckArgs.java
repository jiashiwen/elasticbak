package elasticbak.utilities;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Client;

import com.carrotsearch.hppc.cursors.ObjectCursor;

import elasticbak.Entities.ArgsSettingEntity;

public class CheckArgs {
	private static final Logger logger = LogManager.getLogger(CheckArgs.class);
	private ArgsSettingEntity args;
	private Set<String> allindeces = new HashSet<String>();
	private Set<String> backupindeces = new HashSet<String>();
	private DealString dealstring = new DealString();
	public static Client client;

	// 参数检查
	public CheckArgs(ArgsSettingEntity argsentity) throws Exception {
		this.setArgs(argsentity);
		initclient();
	}

	public ArgsSettingEntity getArgs() {
		return args;
	}

	public void setArgs(ArgsSettingEntity args) {
		this.args = args;
	}

	public void initclient() throws Exception {
		client = new ElasticsearchConnector(args.getCluster(), args.getHost(), args.getPort()).getClient();
	}

	public boolean check() {

		if (args == null) {
			return false;
		}

		// 判断脚本是否存在
		if (args.getScript_file() != null) {
			File script = new File(args.getScript_file());
			if (script.exists()) {
				return true;
			}
		}

		// 判断--exp和--imp有且只有一个
		if (!args.isExp() ^ args.isImp()) {
			logger.error("--imp and --exp must only one be set");
			return false;
		}

		if (args.isExp()) {

			if (args.getBackupindexes() == null) {
				logger.error(MessageFormat.format("['--backupindexes '{0}]:{1}", args.getBackupindexes(),
						new String("Backup indexes name must be set!")));
				return false;
			}

			// 获取集群所有索引
			for (ObjectCursor<String> k : client.admin().cluster().prepareState().execute().actionGet().getState()
					.getMetaData().getIndices().keys()) {
				allindeces.add(k.value);
			}

			String[] backupindexes = args.getBackupindexes().split(",");

			if (!args.getBackupdir().endsWith(File.separator)) {
				args.setBackupdir(args.getBackupdir() + File.separator);
			}

			// 生成备份索引集合
			for (String bakidx : backupindexes) {
				if (dealstring.includewildcard(bakidx)) {
					for (String idec : allindeces) {
						if (dealstring.wildcardmatch(bakidx, idec)) {
							backupindeces.add(idec);
						}
					}
				} else {
					if (allindeces.contains(bakidx)) {
						backupindeces.add(bakidx);
					}
				}
			}

			// 校验备份集是否存在
			for (String backupindex : backupindeces) {
				if ((new File(args.getBackupdir() + backupindex).exists())) {
					logger.error(MessageFormat.format("[{0}]:{1}", (args.getBackupdir() + backupindex),
							new String("The backupset has been exists!")));
					return false;
				}
			}

		}

		if (args.isImp()) {
			
			if (args.getRestoretype().toLowerCase().equals("meta")
					|| args.getRestoretype().toLowerCase().equals("force")) {
				if (!(new File(args.getMetafile()).exists())) {
					logger.error(MessageFormat.format("['--metafile '{0}]:{1}", args.getMetafile(), "File not exists!"));
					return false;
				}
				if (!(new File(args.getMetafile()).isFile())) {
					logger.error(MessageFormat.format("['--metafile '{0}]:{1}", args.getMetafile(),
							"Metafile must be file!"));
					return false;
				}
			}

			if (!(new File(args.getBackupset()).exists())) {
				logger.error(MessageFormat.format("['--datafolder '{0}]:{1}", args.getBackupset(), "Not Exists!"));
				return false;
			}
			if (!(new File(args.getBackupset()).isDirectory())) {
				logger.error(MessageFormat.format("['--datafolder '{0}]:{1}", args.getBackupset(), "Not a Directory!"));
				return false;
			}

		}
		return true;
	}

	public Set<String> getBackupindeces() {
		return backupindeces;
	}

}
