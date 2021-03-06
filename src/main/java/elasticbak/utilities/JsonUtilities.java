package elasticbak.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtilities {

	private ObjectMapper mapper = new ObjectMapper();

	// json数组转object数组
	public Object[] JsonToArray(String json) {
		ObjectMapper mapper = new ObjectMapper();
		Object[] array = {};
		try {
			array = mapper.readValue(json, Object[].class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return array;
	}

	// 用"."分割的字符串转数组
	public String[] SpliteStringByDot(String string) {
		return string.split("\\.");
	}

	// 通过路径查找Json内容
	public String FindJsonPathContent(String[] path, String jsonstring) {
		ObjectMapper mapper = new ObjectMapper();
		String pathcontent = "";
		try {
			JsonNode tempnode = mapper.readTree(jsonstring);
			for (int i = 0; i < path.length; i++) {
				tempnode = tempnode.path(path[i]);
			}
			pathcontent = tempnode.asText();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return pathcontent;
	}

	// 将json字符串转换为Map
	public Map<String, Object> JsonToMap(String jsonstring) {
		Map<String, Object> jsonmap = new HashMap<String, Object>();
		try {
			jsonmap = mapper.readValue(jsonstring, HashMap.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonmap;

	}

	//将Map转换为json
	public String MapToJson(Map<String,Object> map) throws JsonProcessingException{
		String json = new ObjectMapper().writeValueAsString(map);
		return json;
		
	}
	public String ReadJsonFile(String filePath) throws IOException {

		String jsonstring = "";
		File file = new File(filePath);

		InputStreamReader read = new InputStreamReader(new FileInputStream(file));
		BufferedReader bufferedReader = new BufferedReader(read);
		String lineTxt = null;
		while ((lineTxt = bufferedReader.readLine()) != null) {
			jsonstring += lineTxt;
		}
		read.close();

		return jsonstring;
	}

}
