package com.steam.springbootlearn.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


public interface CommonService {

    List<Map<String,Object>> getAreaSimpleList();

    List<Map<String,Object>> getAreaByNameList(String addressName);
}
