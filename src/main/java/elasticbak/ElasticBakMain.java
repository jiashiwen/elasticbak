package elasticbak;

import java.util.List;

import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import elasticbak.Entities.ArgsSettingEntity;
import elasticbak.Entities.IndexesRelationEntity;
import elasticbak.Entities.ScriptEntity;
import elasticbak.utilities.ElasticsearchConnector;
import elasticbak.utilities.ElasticsearchCopyIndex;
import elasticbak.utilities.ElasticsearchIndexTools;
import elasticbak.utilities.JsonUtilities;

public class ElasticBakMain {

	private static final Logger logger = LoggerFactory.getLogger(ElasticBakMain.class);

	public static void main(String[] args) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		ArgsSettingEntity argssetting = new ArgsSettingEntity();
		ElasticsearchCopyIndex cpidx = new ElasticsearchCopyIndex();
		ElasticsearchIndexTools esidxtools = new ElasticsearchIndexTools();
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
		
		if (argssetting.isHelp()) {
			jc.usage();
			System.exit(0);
		}

		//计算exp和imp做异或运算即次两个参数必须有一个且只有一个为真
		if(!(argssetting.isExp()^argssetting.isImp())){
			jc.usage();
			System.exit(0);
		}
		

		
		// 解析脚本文件并执行相关操作
//		if (argssetting.getScript_file() != null) {
//			String scriptstring = new JsonUtilities().ReadJsonFile(argssetting.getScript_file());
//			ScriptEntity script = (ScriptEntity) objectMapper.readValue(scriptstring, ScriptEntity.class);
//			String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(script);
//			logger.info("Your Script setting is: \n\t" + json);
//			JsonNode node = objectMapper.readTree(scriptstring);
//
//			if (node.get("indexes") == null) {
//				logger.info("Script 'indexes' must be set!");
//				return;
//			}
//
//			List<IndexesRelationEntity> indexrelation = script.getIndexesrelation();
//
//			for (IndexesRelationEntity idx : indexrelation) {
//				sourceclient = new ElasticsearchConnector(script.getSource_cluster(), script.getSource_host(),
//						script.getSource_port()).getClient();
//				targetclient = new ElasticsearchConnector(script.getTarget_cluster(), script.getTarget_host(),
//						script.getTarget_port()).getClient();
//				String idxjson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(idx);
//				logger.info("\n\t" + idxjson);
//				if (idx.getSource_index() == null || idx.getTarget_index() == null) {
//					logger.info("In script 'indexes.source_index' and 'indexes.target_index' must been set!");
//					continue;
//				}
//
//				switch (idx.getType().toUpperCase()) {
//				case "DATA":
//					System.out.println("TYPE IS DATA OK!");
//					if (idx.getDsl() != null) {
//						cpidx.CopyIndexByQueryDsl(sourceclient, idx.getSource_index(), targetclient,
//								idx.getTarget_index(), idx.getDsl().toString());
//
//					} else {
//						cpidx.CopyIndex(sourceclient, idx.getSource_index(), targetclient, idx.getTarget_index());
//					}
//					break;
//				case "META":
//					System.out.println("TYPE IS META OK!");
//					cpidx.CopyIndexMetadata(sourceclient, idx.getSource_index(), targetclient, idx.getTarget_index());
//					break;
//				case "FORCE":
//					System.out.println("FORCE OK!");
//					if (esidxtools.IndexExistes(targetclient, idx.getTarget_index())) {
//						esidxtools.DeleteIndex(targetclient, idx.getTarget_index());
//					}
//
//					cpidx.CopyIndexMetadata(sourceclient, idx.getSource_index(), targetclient, idx.getTarget_index());
//
//					if (idx.getDsl() != null) {
//						cpidx.CopyIndexByQueryDsl(sourceclient, idx.getSource_index(), targetclient,
//								idx.getTarget_index(), idx.getDsl().toString());
//
//					} else {
//						cpidx.CopyIndex(sourceclient, idx.getSource_index(), targetclient, idx.getTarget_index());
//					}
//					break;
//				default:
//					System.out.println("type must be set [data,meta,force]");
//					break;
//				}
//
//				sourceclient.close();
//				targetclient.close();
//
//			}
//
//			System.exit(0);
//		}

		if (argssetting.getIndex() == null ) {
			logger.info("index name must been set!");
			jc.usage();
			System.exit(0);
		}

		String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(argssetting);
		logger.info("Your command line setting is: \n\t" + json);

		client = new ElasticsearchConnector(argssetting.getCluster(), argssetting.getHost(),
				argssetting.getPort()).getClient();

		System.out.println(argssetting.getDsl());

		Client targetclient;
	  
//		switch (argssetting.getType().toUpperCase()) {
//		case "DATA":
//			System.out.println("TYPE IS DATA OK!");
//			if (argssetting.getDsl() != null) {
//				cpidx.CopyIndexByQueryDsl(client, argssetting.getIndex(), targetclient,
//						argssetting.getTarget_index(), argssetting.getDsl());
//
//			} else {
//				cpidx.CopyIndex(client, argssetting.getIndex(), targetclient,
//						argssetting.getTarget_index());
//			}
//			break;
//		case "META":
//			System.out.println("TYPE IS META OK!");
//			cpidx.CopyIndexMetadata(client, argssetting.getIndex(), targetclient,
//					argssetting.getTarget_index());
//			break;
//		case "FORCE":
//			System.out.println("FORCE OK!");
//			if (esidxtools.IndexExistes(targetclient, argssetting.getTarget_index())) {
//				esidxtools.DeleteIndex(targetclient, argssetting.getTarget_index());
//			}
//
//			cpidx.CopyIndexMetadata(client, argssetting.getIndex(), targetclient,
//					argssetting.getTarget_index());
//
//			if (argssetting.getDsl() != null) {
//				cpidx.CopyIndexByQueryDsl(client, argssetting.getIndex(), targetclient,
//						argssetting.getTarget_index(), argssetting.getDsl());
//
//			} else {
//				cpidx.CopyIndex(client, argssetting.getIndex(), targetclient,
//						argssetting.getTarget_index());
//			}
//			break;
//		default:
//			System.out.println("type must be set [data,meta,force]");
//			break;
//		}

		client.close();


	}

}
