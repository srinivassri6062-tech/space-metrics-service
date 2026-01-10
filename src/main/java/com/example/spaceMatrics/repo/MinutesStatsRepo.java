package com.example.spaceMatrics.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spaceMatrics.entity.MinutesStatsEntity;

public interface MinutesStatsRepo extends JpaRepository<MinutesStatsEntity, String>{

}
