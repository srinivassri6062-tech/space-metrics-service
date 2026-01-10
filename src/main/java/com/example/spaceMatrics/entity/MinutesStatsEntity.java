package com.example.spaceMatrics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "MINUTE_STATS")
public class MinutesStatsEntity {

	@Id
	@Column(name = "MINUTES")
	private String minutes;

	@Column(name = "UNIQUE_COUNT")
	private Integer uniqueCount;

	public MinutesStatsEntity() {
		super();
	}

	public MinutesStatsEntity(String minutes, Integer uniqueCount) {
		super();
		this.minutes = minutes;
		this.uniqueCount = uniqueCount;
	}

	public String getMinutes() {
		return minutes;
	}

	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}

	public Integer getUniqueCount() {
		return uniqueCount;
	}

	public void setUniqueCount(Integer uniqueCount) {
		this.uniqueCount = uniqueCount;
	}

}
