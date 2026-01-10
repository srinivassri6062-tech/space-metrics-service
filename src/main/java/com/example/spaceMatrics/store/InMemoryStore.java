package com.example.spaceMatrics.store;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class InMemoryStore {
	private final ConcurrentHashMap<String, Set<String>> idsPerMinute = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Set<String>> endpointsPerMinute = new ConcurrentHashMap<>();

	public void recordId(String id) {
		String minute = currentMinute();
		idsPerMinute.computeIfAbsent(minute, k -> ConcurrentHashMap.newKeySet()).add(id);
	}

	public void recordEndpoint(String endpoint) {
		String minute = currentMinute();
		endpointsPerMinute.computeIfAbsent(minute, k -> ConcurrentHashMap.newKeySet()).add(endpoint);
	}

	public Map<String, Set<String>> drainIds() {
		return new HashMap<>(idsPerMinute);
	}

	public Map<String, Set<String>> drainEndpoints() {
		return new HashMap<>(endpointsPerMinute);
	}

	public void clear(String minute) {
		idsPerMinute.remove(minute);
		endpointsPerMinute.remove(minute);
	}

	private String currentMinute() {
		return LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).toString();
	}
}
