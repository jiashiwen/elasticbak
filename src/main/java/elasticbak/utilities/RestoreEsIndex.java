package elasticbak.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;

import elasticbak.Entities.IndexMeta;
import elasticbak.Entities.RestoreDataEntity;
import elasticbak.Entities.RestoreIndexEntity;

public class RestoreEsIndex {

	private BufferedReader reader;
	private ObjectInputStream objectInputStream;
	private RestoreIndexEntity restoreindex;
	private RestoreDataEntity restordata;

	public RestoreIndexEntity getRestoreindex() {
		return restoreindex;
	}

	public void setRestoreindex(RestoreIndexEntity restoreindex) {
		this.restoreindex = restoreindex;
	}

	public RestoreDataEntity getRestordata() {
		return restordata;
	}

	public void setRestordata(RestoreDataEntity restordata) {
		this.restordata = restordata;
	}

	public void CreateIdxFromMetaFile() throws FileNotFoundException, ClassNotFoundException, IOException {
		this.CreateIdxFromMetaFile(restoreindex);
	}

	public void CreateIdxFromMetaFile(RestoreIndexEntity ridx)
			throws FileNotFoundException, ClassNotFoundException, IOException {
		this.CreateIdxFromMetaFile(ridx.getClient(), ridx.getIndexname(), ridx.getMetafile());
	}

	public void CreateIdxFromMetaFile(Client client, String indexname, File metafile)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		Settings.Builder settingbuilder = Settings.builder();

		objectInputStream = new ObjectInputStream(new FileInputStream(metafile));
		IndexMeta indexmeta = (IndexMeta) objectInputStream.readObject();

		// 获取setting和mapping
		Set<Entry<String, String>> settings = (Set<Entry<String, String>>) indexmeta.getIdxsetting();
		HashMap<String, Object> mappings = (HashMap<String, Object>) indexmeta.getIdxmapping();

		// 处理settings
		for (Map.Entry<String, String> m : settings) {

			if (m.getKey().equals("index.uuid") || m.getKey().equals("index.version.created")
					|| m.getKey().equals("index.creation_date")) {
				continue;
			} else {
				settingbuilder.put(m.getKey(), m.getValue());
			}
		}

		// 创建索引
		client.admin().indices().prepareCreate(indexname).setSettings(settingbuilder).get();

		// 创建索引mapping
		for (String key : mappings.keySet()) {
			System.out.println(mappings.get(key) instanceof Map<?, ?>);
			client.admin().indices().preparePutMapping(indexname).setType(key).setSource((Map<?, ?>) mappings.get(key))
					.get();
		}

	}

	public void restoreDataFromFile() throws IOException {
		this.restoreDataFromFile(restordata);
	}

	public void restoreDataFromFile(RestoreDataEntity rdata) throws IOException {
		this.restoreDataFromFile(rdata.getClient(), rdata.getIndexname(), rdata.getDatafile());
	}

	public void restoreDataFromFile(Client client, String indexname, File datafile) throws IOException {
		if (datafile.getName().endsWith(".data")) {
			JsonUtilities jsonutil = new JsonUtilities();
			Map<String, Object> map;
			System.out.println(datafile.getAbsolutePath());

			reader = new BufferedReader(new FileReader(datafile));
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			
			String tempString = null;
			// 一次读一行，读入null时文件结束
			while ((tempString = reader.readLine()) != null) {
				map = jsonutil.JsonToMap(tempString);

				bulkRequest.add(client.prepareIndex(indexname, (String) map.get("_type"), (String) map.get("_id"))
						.setSource(jsonutil.MapToJson((Map<String, Object>) map.get("_source"))));

			}
			bulkRequest.execute().actionGet();
			
			System.out.println(datafile.getAbsolutePath() + ":End!!!");
			client.admin().indices().prepareRefresh(indexname).get();
		}

	}

}
