package elasticbak.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import com.fasterxml.jackson.databind.ObjectMapper;

import elasticbak.Entities.BackupEntity;
import elasticbak.Entities.IndexMeta;

public class BackupEsIndex {
	private Logger logger;
	private JsonUtilities jsonutil = new JsonUtilities();
	private BackupEntity backup;
	private Map<String, Object> logmsg;

	public BackupEntity getBackup() {
		return backup;
	}

	public void setBackup(BackupEntity backup) {
		this.backup = backup;
	}

	public BackupEsIndex(BackupEntity backupentity) {
		this.backup = backupentity;
		logger = LoggerFactory.getLogger("elasticbak");
	}

	public BackupEsIndex() {
		logger = LoggerFactory.getLogger("elasticbak");

	}

	/**
	 * 备份索引settings
	 * 
	 * @param client
	 * @param indexname
	 * @param file
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public void backupIdxMeta() throws FileNotFoundException, IOException, InterruptedException, ExecutionException {

		logmsg = new HashMap<String, Object>();
		// 获取index settings
		ClusterState cs = backup.getClient().admin().cluster().prepareState().setIndices(backup.getIndexname())
				.execute().actionGet().getState();
		IndexMetaData imd = cs.getMetaData().index(backup.getIndexname());
		IndexMeta indexmeta = new IndexMeta();

		Settings settings = imd.getSettings();
	
		Map<String, String> set = settings.getAsMap();
		indexmeta.setIdxsetting(set);

		// 获取index mapping
		Map<String, Object> mapping = new HashMap<String, Object>();
		GetMappingsResponse res = backup.getClient().admin().indices()
				.getMappings(new GetMappingsRequest().indices(backup.getIndexname())).get();
		ImmutableOpenMap<String, MappingMetaData> idxmapping = res.mappings().get(backup.getIndexname());
		for (ObjectObjectCursor<String, MappingMetaData> c : idxmapping) {
			mapping.put(c.key, c.value.getSourceAsMap());
		}
		indexmeta.setIdxmapping(mapping);

		// 序列化对象到文件
		if (!backup.getBackuppath().endsWith(File.separator)) {
			backup.setBackuppath(backup.getBackuppath() + File.separator);
		}

		if (0 == backup.getBackuppath().indexOf("./")) {
			backup.setBackuppath(backup.getBackuppath().replace("./", ""));
		}

		// IndexMetaData 转 json
		ObjectMapper mapper = new ObjectMapper();
		String indexmetajson = mapper.writeValueAsString(indexmeta);

		// 写入文件
		String filename = backup.getBackuppath() + backup.getIndexname() + ".meta";
		FileWriter fw = new FileWriter(filename);
		fw.write(indexmetajson);
		fw.flush();
		fw.close();

		// 写日志
		logmsg.put("Action", "backup index meta");
		logmsg.put("Index", backup.getIndexname());
		logmsg.put("backupfile", new File(backup.getBackuppath() + backup.getIndexname() + ".meta").getAbsolutePath());
		logger.info(jsonutil.MapToJson(logmsg));
	}

	/**
	 * 备份索引数据
	 * 
	 * @param client
	 * @param index
	 * @param path
	 * @param docsperfile
	 * @throws IOException
	 */
	public void BackupIdxData() throws IOException {
		long count = 0;
		long filenumber = 0;
		QueryBuilder qb = QueryBuilders.matchAllQuery();
		StringBuffer doc = new StringBuffer();
		Map<String, Object> docmap = new HashMap<String, Object>();

		doc.delete(0, doc.length());
		docmap.clear();
		SearchResponse scrollResp = backup.getClient().prepareSearch(backup.getIndexname())
				.addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC).setScroll(new TimeValue(60000)).setQuery(qb)
				.setSize(backup.getDocsperfile()).execute().actionGet();

		if (!backup.getBackuppath().endsWith(File.separator)) {
			backup.setBackuppath(backup.getBackuppath() + File.separator);
		}

		if (0 == backup.getBackuppath().indexOf("./")) {
			backup.setBackuppath(backup.getBackuppath().replace("./", ""));
		}

		logmsg = new HashMap<String, Object>();

		while (true) {

			String filename = backup.getBackuppath() + backup.getIndexname() + "_" + filenumber + ".data";
			FileWriter fw = new FileWriter(filename);

			for (SearchHit hit : scrollResp.getHits().getHits()) {
				docmap.clear();

				docmap.put("_type", hit.getType());
				docmap.put("_id", hit.getId());
				docmap.put("_source", hit.getSource());
				fw.write(jsonutil.MapToJson(docmap) + System.getProperty("line.separator"));
				count++;
			}
			scrollResp = backup.getClient().prepareSearchScroll(scrollResp.getScrollId())
					.setScroll(new TimeValue(60000)).execute().actionGet();
			fw.flush();
			fw.close();
			filenumber++;

			logmsg.clear();
			logmsg.put("Action", "backup index data");
			logmsg.put("Index", backup.getIndexname());
			logmsg.put("backupfile",
					new File(backup.getBackuppath() + backup.getIndexname() + "_" + filenumber + ".data")
							.getAbsolutePath());
			logger.info(jsonutil.MapToJson(logmsg));
			// 压缩data文件
			if (backup.getZip()) {
				ZipUtilities ziputil = new ZipUtilities();
				FileUtilities fileutil = new FileUtilities();
				ziputil.zipfile(filename, filename + ".zip", null);
				fileutil.deleteFile(filename);
				logmsg.clear();
				logmsg.put("Action", "Create zip file");
				logmsg.put("Index", backup.getIndexname());
				logmsg.put("zipfile",
						new File(backup.getBackuppath() + backup.getIndexname() + "_" + filenumber + ".data" + ".zip")
								.getAbsolutePath());
				logger.info(jsonutil.MapToJson(logmsg));
			}

			if (scrollResp.getHits().getHits().length == 0) {
				break;
			}
		}
		logmsg.clear();
		logmsg.put("Action", "backup index data");
		logmsg.put("Index", backup.getIndexname());
		logmsg.put("Backupset", new File(backup.getBackuppath()));
		logmsg.put("Index docs", count);
		logger.info(jsonutil.MapToJson(logmsg));
	}

}
