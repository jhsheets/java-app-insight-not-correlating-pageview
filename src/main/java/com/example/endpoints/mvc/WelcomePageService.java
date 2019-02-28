package com.example.endpoints.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@RestController
@RequestMapping("/html/welcome")
public class WelcomePageService
{
    private String version;


    @Autowired
    WelcomePageService(@Value("${info.app.version}") final String version)
    {
        this.version = version;
    }

    /**
     * A basic landing page once the user has logged-in
     */
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView displayWelcomePage()
    {
        final ModelAndView modelAndView = new ModelAndView("WelcomePage");
        modelAndView.addObject("version", version);
        return modelAndView;
    }
}

