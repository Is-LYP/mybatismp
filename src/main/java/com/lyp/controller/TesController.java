package com.lyp.controller;

import com.lyp.test.SpringBuildService;
import com.lyp.test.entity.Borrw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author 李宜鹏
 */
@Controller
@RequestMapping("/test")
public class TesController {

    @Autowired
    private SpringBuildService springBuildService;

    @GetMapping("/list")
    @ResponseBody
    public List<Borrw> list() {
        return springBuildService.getService(Borrw.class).list();
    }
}
