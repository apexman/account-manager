package com.maksimov.accountManager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorHandlerController implements ErrorController {
    private Logger logger = LoggerFactory.getLogger(ErrorHandlerController.class);

    private static final String PATH = "/error";

    @RequestMapping(value = "/error")
    public ModelAndView handleError() {
        logger.debug("Error");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error-404.html");
        return modelAndView;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
