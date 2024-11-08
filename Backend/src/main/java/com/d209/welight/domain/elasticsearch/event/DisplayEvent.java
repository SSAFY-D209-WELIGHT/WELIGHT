package com.d209.welight.domain.elasticsearch.event;

import com.d209.welight.domain.display.entity.Display;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DisplayEvent {
    private final String eventType; // CREATE, UPDATE, DELETE
    private final Display display;
} 