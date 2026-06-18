package com.campusmate.controller;

import com.campusmate.common.result.Result;
import com.campusmate.domain.dto.HomePlazaQuery;
import com.campusmate.domain.vo.HomePlazaVO;
import com.campusmate.service.HomeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/plaza")
    public Result<HomePlazaVO> getHomePlaza(@ModelAttribute HomePlazaQuery query) {
        return Result.ok(homeService.getHomePlaza(query));
    }
}
