package com.d209.welight.domain.elasticsearch.listener;

import com.d209.welight.domain.display.entity.Display;
import com.d209.welight.domain.display.entity.DisplayTag;
import com.d209.welight.domain.display.entity.DisplayText;
import com.d209.welight.domain.elasticsearch.event.DisplayEvent;
import com.d209.welight.domain.display.repository.DisplayTagRepository;
import com.d209.welight.domain.display.repository.DisplayTextRepository;
import com.d209.welight.domain.elasticsearch.Document.DisplayDocument;
import com.d209.welight.domain.elasticsearch.repository.DisplaySearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DisplayEventListener {
    private final DisplaySearchRepository searchRepository;
    private final DisplayTagRepository displayTagRepository;
    private final DisplayTextRepository displayTextRepository;

    @EventListener
    public void handleDisplayEvent(DisplayEvent event) {
        try {
            switch (event.getEventType()) {
                case "CREATE":
                case "UPDATE":
                    DisplayDocument document = convertToDocument(event.getDisplay());
                    searchRepository.save(document);
                    log.info("Display document saved/updated: {}", event.getDisplay().getDisplayUid());
                    break;
                case "DELETE":
                    searchRepository.deleteByDisplayUid(event.getDisplay().getDisplayUid());
                    log.info("Display document deleted: {}", event.getDisplay().getDisplayUid());
                    break;
            }
        } catch (Exception e) {
            log.error("Error processing display event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process display event", e);
        }
    }

    private DisplayDocument convertToDocument(Display display) {
        return DisplayDocument.builder()
                .displayUid(display.getDisplayUid())
                .creatorUid(display.getCreatorUid())
                .displayName(display.getDisplayName())
                .displayThumbnailUrl(display.getDisplayThumbnailUrl())
                .displayIsPosted(display.getDisplayIsPosted())
                .displayCreatedAt(display.getDisplayCreatedAt())
                .displayDownloadCount(display.getDisplayDownloadCount())
                .displayLikeCount(display.getDisplayLikeCount())
                .tags(getDisplayTags(display))
                .displayTexts(getDisplayTexts(display))
                .build();
    }

    private List<String> getDisplayTags(Display display) {
        return displayTagRepository.findByDisplay(display)
                .stream()
                .map(DisplayTag::getDisplayTagText)  // 또는 DisplayTag::getDisplayTagText
                .collect(Collectors.toList());
    }

    private List<String> getDisplayTexts(Display display) {
        return displayTextRepository.findByDisplay(display)
                .stream()
                .map(DisplayText::getDisplayTextDetail)  // 또는 DisplayText::getDisplayTextDetail
                .collect(Collectors.toList());
    }
}