package com.smuraha.repository;

import com.smuraha.model.RawData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawDataRepo extends JpaRepository<RawData,Long> {
}
