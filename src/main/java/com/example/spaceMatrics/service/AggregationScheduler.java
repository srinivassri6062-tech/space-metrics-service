package com.example.spaceMatrics.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.example.spaceMatrics.entity.MinutesStatsEntity;
import com.example.spaceMatrics.http.ExternalClients;
import com.example.spaceMatrics.repo.MinutesStatsRepo;
import com.example.spaceMatrics.store.InMemoryStore;

@Component
@EnableScheduling
public class AggregationScheduler {

	private static final Logger log = LoggerFactory.getLogger(ExternalClients.class);

	private final InMemoryStore store;
	private final MinutesStatsRepo repo;
	private final ExternalClients client;

	public AggregationScheduler(InMemoryStore store, MinutesStatsRepo repo, ExternalClients client) {
		this.store = store;
		this.repo = repo;
		this.client = client;
	}

	@Scheduled(cron = "0 * * * * *")
	public void aggregate() {
		String minute = LocalDateTime.now().minusMinutes(1).truncatedTo(ChronoUnit.MINUTES).toString();

		var ids = store.drainIds().getOrDefault(minute, Collections.emptySet());
		var endpoints = store.drainEndpoints().getOrDefault(minute, Collections.emptySet());

		int count = ids.size();

		log.info("[AGGREGATION] {} -> {} unique ids", minute, count);

		repo.save(new MinutesStatsEntity(minute, count));

		if (!endpoints.isEmpty() && count > 0) {
			Map<String, Object> payload = Map.of("minuteStart", minute, "uniqueIdCount", count);
			endpoints.forEach(ep -> client.postMetrics(ep, payload).subscribe());
		}

		store.clear(minute);
	}
}
