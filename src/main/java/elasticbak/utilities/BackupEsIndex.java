package elasticbak.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.sort.SortParseElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.cursors.ObjectObjectCursor;

import elasticbak.Entities.BackupEntity;
import elasticbak.Entities.IndexMeta;

public class BackupEsIndex {
	private Logger logger;
	private JsonUtilities jsonutil = new JsonUtilities();
	private BackupEntity backup;

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

		// 获取index settings
		ClusterState cs = backup.getClient().admin().cluster().prepareState().setIndices(backup.getIndexname())
				.execute().actionGet().getState();
		IndexMetaData imd = cs.getMetaData().index(backup.getIndexname());
		IndexMeta indexmeta = new IndexMeta();

		Settings settings = imd.getSettings();
		Set<Entry<String, String>> set = settings.getAsMap().entrySet();
		indexmeta.setIdxsetting(set);

		// 获取index mapping
		Map<String, Object> mapping = new HashMap<>();
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

		ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(backup.getBackuppath() + backup.getIndexname() + ".meta"));
		oos.writeObject(indexmeta);
		oos.close();
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
					.addSort(SortParseElement.DOC_FIELD_NAME, SortOrder.ASC).setScroll(new TimeValue(60000))
					.setQuery(qb).setSize(backup.getDocsperfile()).execute().actionGet();

			while (true) {
				if (!backup.getBackuppath().endsWith(File.separator)) {
					backup.setBackuppath(backup.getBackuppath() + File.separator);
				}
				String filename = backup.getBackuppath() + backup.getIndexname() + "_" + filenumber + ".data";
				FileWriter fw = new FileWriter(filename);

				for (SearchHit hit : scrollResp.getHits().getHits()) {
					docmap.clear();
					// System.out.println(hit.getSourceAsString().replaceAll("\\s+",
					// ""));
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
				logger.info("Write file " +backup.getBackuppath()+filename + " complete!");
				
				if (scrollResp.getHits().getHits().length == 0) {
					break;
				}
			}

			logger.info("Backup index " + backup.getIndexname() + " " + count + " documents" + " to "
					+ backup.getBackuppath() + " complete!");
		
	}

}
