package com.d209.welight.domain.display.service;

import com.d209.welight.domain.display.entity.DisplayLike;
import com.d209.welight.domain.display.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;

    @Autowired
    public LikeServiceImpl(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @Override
    public void likeDisplay(Long userId, Long displayId) {
        if (!likeRepository.existsByDisplayUidAndUserUid(displayId, userId)) {
            DisplayLike like = new DisplayLike(userId, displayId);
            likeRepository.save(like);
        }
    }

    @Override
    public void unlikeDisplay(Long userId, Long displayId) {

    }

    @Override
    public boolean isLikedByUser(Long userId, Long displayId) {
        return false;
    }


}
