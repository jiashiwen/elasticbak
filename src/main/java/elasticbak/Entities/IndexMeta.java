package elasticbak.Entities;

import java.io.Serializable;
import java.util.Map;

public class IndexMeta implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<String, String> idxsetting;

	private Map<String, Object> idxmapping;



	public Map<String, String> getIdxsetting() {
		return idxsetting;
	}

	public void setIdxsetting(Map<String, String> idxsetting) {
		this.idxsetting = idxsetting;
	}

	public Map<String, Object> getIdxmapping() {
		return idxmapping;
	}

	public void setIdxmapping(Map<String, Object> idxmapping) {
		this.idxmapping = idxmapping;
	}


	

}
