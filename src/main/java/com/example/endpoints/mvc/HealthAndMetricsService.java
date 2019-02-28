package com.example.endpoints.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/html/healthAndMetrics")
public class HealthAndMetricsService
{
    @GetMapping(value = "")
    public ModelAndView displayRegistrationCreation()
    {
        return new ModelAndView("healthAndMetrics");
    }
}
