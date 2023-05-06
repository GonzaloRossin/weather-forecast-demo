package com.service.weather.controller;

import java.util.Random;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.service.weather.entity.objective.CurrentWeatherSummary;
import com.service.weather.util.CustomException;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.CseSpringDemoCodegen", date = "2017-11-01T10:26:36.166+08:00")

@RestController
@RefreshScope
@RequestMapping(path = "/weather", produces = MediaType.APPLICATION_JSON)
public class WeatherImpl {

  private int calledTimes = 0;

  private static final Logger LOGGER = LoggerFactory.getLogger(WeatherImpl.class);

  @Autowired
  private WeatherImplDelegate userCurrentweatherdataDelegate;

  @Value("${randomException.enabled}")
  private boolean allowRandomException = false;

  @Value("${timewait.enabled}")
  private boolean timewait = false;

  private int latencyTime = 0;

  @Value("${weather.returnErr:false}")
  private boolean returnErr;

  @Value("${weather.returnTimeOut:0}")
  private int returnTimeOut;

  @PostConstruct
  public void init() {
    LOGGER.info("Init success");
    DynamicIntProperty latency = DynamicPropertyFactory.getInstance().getIntProperty("latency", 0);
    latency.addCallback(() -> {
      latencyTime = latency.get();
      LOGGER.info("Latency time change to {}", latencyTime);
    });
    latencyTime = latency.get();
  }

  @RequestMapping(value = "/show",
      produces = {"application/json"},
      method = RequestMethod.GET)
  public CurrentWeatherSummary showCurrentWeather(@RequestParam(value = "city", required = true) String city,
      @RequestParam(value = "user", required = false) String user) throws Exception {

    calledTimes++;
    System.out.println("has received " + calledTimes + " calls");

    if (allowRandomException) {
      if (calledTimes % 5 != 0) {
        throw new CustomException();
      } else {
        calledTimes = 0;
      }
    }

    if (latencyTime > 0) {
      try {
        Thread.sleep(latencyTime);
      } catch (Exception e) {

      }
    }

    if (returnErr) {
      throw new CustomException();
    }

    if (returnTimeOut > 0) {
      Thread.sleep(returnTimeOut * 1000);
      throw new CustomException();
    }

    LOGGER.info("showCurrentWeather() is called, city = [{}], user = [{}]", city, user);
    return userCurrentweatherdataDelegate.showCurrentWeather(city);
  }
}
