package com.smuraha.service.impl;

import com.smuraha.model.RawData;
import com.smuraha.repository.RawDataRepo;
import com.smuraha.service.RawDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RawDataServiceImpl implements RawDataService {

    private final RawDataRepo rawDataRepo;

    @Override
    public void save(RawData rawData) {
        rawDataRepo.save(rawData);
    }
}
