package com.home.ApplicationCosmetos.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer  {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/image/**")
                .addResourceLocations("classpath:/image/");



    }
    //пагинация начинается с 0, чтобы она начиналась с 1 нужен этот метод
//    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
//        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
//        resolver.setOneIndexedParameters(true);
//        argumentResolvers.add(resolver);
//        WebMvcConfigurer.super.addArgumentResolvers(argumentResolvers);
//    }
}




