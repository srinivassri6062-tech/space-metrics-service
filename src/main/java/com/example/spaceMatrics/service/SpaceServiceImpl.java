package com.example.spaceMatrics.service;

import org.springframework.stereotype.Service;

import com.example.spaceMatrics.http.ExternalClients;
import com.example.spaceMatrics.store.InMemoryStore;

@Service
public class SpaceServiceImpl implements SpaceService {

	private final InMemoryStore store;
	private final ExternalClients client;

	public SpaceServiceImpl(InMemoryStore store, ExternalClients client) {
		this.store = store;
		this.client = client;
	}

	@Override
	public Boolean processRequest(String id, String endPoint) {

		   try {
	            store.recordId(id);
	            if (endPoint != null) {
	                store.recordEndpoint(endPoint);
	                client.callEndpoint(endPoint).subscribe();
	            }
	            return true;
	        } catch (Exception e) {
	            return false;
	        }
	    }
	}