package com.d209.welight.domain.display.service;

import com.d209.welight.domain.display.entity.Display;
import com.d209.welight.domain.display.repository.DisplayRepository;
import com.d209.welight.global.exception.display.DisplayNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisplayServiceImpl implements DisplayService {
    private final DisplayRepository displayRepository;

    @Autowired
    public DisplayServiceImpl(DisplayRepository displayRepository) {
        this.displayRepository = displayRepository;
    }

    @Override
    public List<Display> getAllDisplays() {
        return displayRepository.findAll();
    }

    @Override
    public Display getDisplayById(Long displayId) {
        return displayRepository.findById(displayId)
                .orElseThrow(() -> new DisplayNotFoundException("Display not found"));
    }

    @Override
    public void saveDisplay(Display display) {

    }

    @Override
    public void updateDisplay(Display display) {

    }

    @Override
    public void deleteDisplay(Long displayId) {

    }

}
