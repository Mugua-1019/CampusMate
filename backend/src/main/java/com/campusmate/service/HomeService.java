package com.campusmate.service;

import com.campusmate.domain.dto.HomePlazaQuery;
import com.campusmate.domain.dto.HomePostReportRequest;
import com.campusmate.domain.dto.HomePostReplyRequest;
import com.campusmate.domain.entity.HomePostReport;
import com.campusmate.domain.vo.HomePlazaVO;

public interface HomeService {

    HomePlazaVO getHomePlaza(HomePlazaQuery query);

    HomePlazaVO.HomePostVO getMatchPostDetail(Long id, Long userId);

    HomePlazaVO.HomePostVO getVentPostDetail(Long id, Long userId);

    HomePlazaVO.HomePostComfortVO submitVentPostComfort(Long postId, Long userId);

    HomePlazaVO.HomePostReplyVO submitVentPostReply(Long postId, HomePostReplyRequest request);

    java.util.List<HomePlazaVO.HomeNotificationVO> getNotifications(Long userId);

    void markNotificationsRead(Long userId);

    HomePostReport submitMatchPostReport(Long postId, HomePostReportRequest request);
}
