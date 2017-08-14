package elasticbak.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import elasticbak.Entities.IndexMeta;
import elasticbak.Entities.RestoreDataEntity;
import elasticbak.Entities.RestoreIndexEntity;
import net.lingala.zip4j.exception.ZipException;

public class RestoreEsIndex {

	private BufferedReader reader;
	private JsonUtilities jsonutil = new JsonUtilities();
	private RestoreIndexEntity restoreindex;
	private RestoreDataEntity restordata;
	private Map<String, Object> logmsg;
	private Logger logger;
	private InputStreamReader isr;

	public RestoreEsIndex() {
		logger = LoggerFactory.getLogger("elasticbak");
		logmsg = new HashMap<String, Object>();
	}

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

	public void CreateIdxFromMetaFile() throws FileNotFoundException, IOException, ClassNotFoundException {
		this.CreateIdxFromMetaFile(restoreindex);
	}

	public void CreateIdxFromMetaFile(RestoreIndexEntity ridx)
			throws FileNotFoundException, ClassNotFoundException, IOException {
		this.CreateIdxFromMetaFile(ridx.getClient(), ridx.getIndexname(), ridx.getMetafile());
	}

	public void CreateIdxFromMetaFile(Client client, String indexname, File metafile)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		Settings.Builder settingbuilder = Settings.builder();

		// 判断文件编码
		FileInputStream fis = new FileInputStream(metafile);
		isr = new InputStreamReader(fis);
		String fileecoding = isr.getEncoding();
		// 读取文件
		String json = FileUtils.readFileToString(metafile, fileecoding);

		ObjectMapper mapper = new ObjectMapper();
		IndexMeta indexmeta = mapper.readValue(json, IndexMeta.class);

		// 获取setting和mapping
		Map<String, String> settings = (Map<String, String>) indexmeta.getIdxsetting();
		HashMap<String, Object> mappings = (HashMap<String, Object>) indexmeta.getIdxmapping();

		// 处理settings
		for (String key : settings.keySet()) {
			if (key.equals("index.uuid") || key.equals("index.version.created") || key.equals("index.creation_date")
					|| key.equals("index.provided_name")) {
				continue;
			} else {
				settingbuilder.put(key, settings.get(key));
			}
		}

		// 创建索引
		client.admin().indices().prepareCreate(indexname).setSettings(settingbuilder).get();
		logmsg.clear();
		logmsg.put("Action", "Create index");
		logmsg.put("Index", indexname);
		logger.info(jsonutil.MapToJson(logmsg));

		// 创建索引mapping
		for (String key : mappings.keySet()) {
			client.admin().indices().preparePutMapping(indexname).setType(key).setSource((Map<?, ?>) mappings.get(key))
					.get();
		}
		logmsg.clear();
		logmsg.put("Action", "Add index mapping");
		logmsg.put("Index", indexname);
		logmsg.put("mapping", mappings);
		logger.info(jsonutil.MapToJson(logmsg));
	}

	public void restoreDataFromFile() throws IOException {
		this.restoreDataFromFile(restordata);
	}

	public void restoreDataFromZipFile() throws IOException, ZipException {
		this.restoreDataFromZipFile(restordata);
	}

	public void restoreDataFromFile(RestoreDataEntity rdata) throws IOException {
		this.restoreDataFromFile(rdata.getClient(), rdata.getIndexname(), rdata.getDatafile());
	}

	public void restoreDataFromZipFile(RestoreDataEntity rdata) throws IOException, ZipException {
		this.restoreDataFromZipFile(rdata.getClient(), rdata.getIndexname(), rdata.getDatafile());
	}

	public void restoreDataFromFile(Client client, String indexname, File datafile) throws IOException {

		if (datafile.getName().endsWith(".data")) {
			JsonUtilities jsonutil = new JsonUtilities();
			Map<String, Object> map;

			reader = new BufferedReader(new FileReader(datafile));
			BulkRequestBuilder bulkRequest = client.prepareBulk();

			String tempString = null;
			// 一次读一行，读入null时文件结束
			while ((tempString = reader.readLine()) != null) {
				map = jsonutil.JsonToMap(tempString);

				bulkRequest.add(client.prepareIndex(indexname, (String) map.get("_type"), (String) map.get("_id"))
						.setSource(jsonutil.MapToJson((Map<String, Object>) map.get("_source")), XContentType.JSON));

			}
			bulkRequest.execute().actionGet();
			client.admin().indices().prepareRefresh(indexname).get();

			logmsg.clear();
			logmsg.put("Action", "Restore data");
			logmsg.put("Index", indexname);
			logmsg.put("FileName", datafile.getAbsolutePath());
			logger.info(jsonutil.MapToJson(logmsg));
		}

	}

	public void restoreDataFromZipFile(Client client, String indexname, File zipfile) throws IOException, ZipException {
		if (zipfile.getName().endsWith(".data.zip")) {
			ZipUtilities ziputil = new ZipUtilities();
			FileUtilities fileutil = new FileUtilities();
			ziputil.unzipfile(zipfile, zipfile.getAbsolutePath().replace(zipfile.getName(), ""), "");
			logmsg.clear();
			logmsg.put("Action", "Unzip file");
			logmsg.put("Index", indexname);
			logmsg.put("FileName", zipfile.getAbsolutePath());
			logger.info(jsonutil.MapToJson(logmsg));
			this.restoreDataFromFile(client, indexname, new File(zipfile.getAbsolutePath().replace(".zip", "")));
			fileutil.deleteFile(zipfile.getAbsolutePath().replace(".zip", ""));
		}

	}

}
