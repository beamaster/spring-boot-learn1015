package com.steam.springbootlearn.service.impl;

import com.steam.springbootlearn.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class CommonServiceImpl implements CommonService {


    @Autowired
    @Qualifier("localTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("qqTemplate")
    private JdbcTemplate jdbcTemplate2;

    @Override
    public List<Map<String,Object>> getAreaSimpleList(){
        String sql = "select id,orders,full_name,name,tree_path from xx_area where version = 0 and full_name like '%南京%'";
        return jdbcTemplate.queryForList(sql);
    }


    @Override
    public List<Map<String,Object>> getAreaByNameList(String addressName){
        String sql = "select id,orders,full_name,name,tree_path from xx_area where version = 0 and full_name like CONCAT('%', ?, '%')";
        return jdbcTemplate2.queryForList(sql,addressName);
    }
}
