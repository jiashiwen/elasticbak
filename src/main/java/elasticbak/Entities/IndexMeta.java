package elasticbak.Entities;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;

public class IndexMeta implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Set<?> idxsetting;

	private Map<String, Object> idxmapping;

	public Set<?> getIdxsetting() {
		return idxsetting;
	}

	public void setIdxsetting(Set<?> idxsetting) {
		this.idxsetting = idxsetting;
	}

	public Map<String, Object> getIdxmapping() {
		return idxmapping;
	}

	public void setIdxmapping(Map<String, Object> idxmapping) {
		this.idxmapping = idxmapping;
	}


	

}
