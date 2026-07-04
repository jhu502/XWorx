package com.flame.type;

public class XThingCode {
	private Integer domainId = 0;
	private Long instanceId = 0L;

	public XThingCode() {
	}

	public XThingCode(Integer domainId, Long instanceId) {
		this.domainId = domainId;
		this.instanceId = instanceId;
	}

	public Integer getDomainId() {
		return domainId;
	}

	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}

	public Long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}
}
