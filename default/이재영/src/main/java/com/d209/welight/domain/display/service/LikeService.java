package com.d209.welight.domain.display.service;

public interface LikeService {

    void likeDisplay(Long userId, Long displayId);
    void unlikeDisplay(Long userId, Long displayId);
    boolean isLikedByUser(Long userId, Long displayId);

}
