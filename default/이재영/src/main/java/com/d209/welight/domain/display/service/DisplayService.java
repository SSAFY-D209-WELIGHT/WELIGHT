package com.d209.welight.domain.display.service;

import com.d209.welight.domain.display.entity.Display;

import java.util.List;

public interface DisplayService {

    List<Display> getAllDisplays();
    Display getDisplayById(Long displayId);
    void saveDisplay(Display display);
    void updateDisplay(Display display);
    void deleteDisplay(Long displayId);
}
