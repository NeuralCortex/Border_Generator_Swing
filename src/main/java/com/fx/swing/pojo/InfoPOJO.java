package com.fx.swing.pojo;

import com.fx.swing.tools.HelperFunctions;

public class InfoPOJO {
    
    private final String param;
    private final String value;
    private final String code;
    
    public InfoPOJO(String param, String value, String code) {
        this.param = param;
        this.value = value;
        this.code = code;
    }
    
    public String getParam() {
        return param;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getCode() {
        return HelperFunctions.alpha2ToAlpha3(code);
    }
}
