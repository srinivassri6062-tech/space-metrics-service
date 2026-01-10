package com.example.spaceMatrics.http;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ExternalClients {

	private static final Logger log = LoggerFactory.getLogger(ExternalClients.class);

	private final WebClient webClient;

	public ExternalClients(WebClient webClient) {
		super();
		this.webClient = webClient;
	}

	 public Mono<Void> callEndpoint(String url) {
	        return webClient.get()
	                .uri(url)
	                .retrieve()
	                .toBodilessEntity()
	                .timeout(Duration.ofSeconds(2))
	                .doOnNext(res -> log.info("[OUTBOUND GET] {} -> {}", url, res.getStatusCode()))
	                .onErrorResume(err -> Mono.empty())
	                .then();
	    }

	    public Mono<Void> postMetrics(String url, Object body) {
	        return webClient.post()
	                .uri(url)
	                .bodyValue(body)
	                .retrieve()
	                .toBodilessEntity()
	                .timeout(Duration.ofSeconds(2))
	                .doOnNext(res -> log.info("[OUTBOUND POST] {} -> {}", url, res.getStatusCode()))
	                .onErrorResume(err -> Mono.empty())
	                .then();
	    }
	}
