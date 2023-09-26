package ru.taste.utilities.web;

import org.springframework.web.servlet.ModelAndView;

public class ResponseUtils {
    public static ModelAndView info(String message) {
        return new ModelAndView("Response").addObject("title", "Info").addObject("message", message);
    }

    public static ModelAndView success(String message) {
        return new ModelAndView("Response").addObject("title", "Success").addObject("message", message);
    }

    public static ModelAndView error(String message) {
        return new ModelAndView("Response").addObject("title", "Error").addObject("message", message);
    }

    public static ModelAndView warning(String message) {
        return new ModelAndView("Response").addObject("title", "Warning").addObject("message", message);
    }
}
