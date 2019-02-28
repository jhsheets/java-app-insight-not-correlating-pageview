package com.example.endpoints.mvc;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@RestController
public class ForbiddenService
{

    @RequestMapping(value="/forbidden")
    public ModelAndView forbiddenPage()
    {
        return new ModelAndView("forbidden");
    }

}