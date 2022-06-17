package com.epic.rdbsms.mapping.common;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CommonPasswordParam {
    private String paramcode;
    private String description;
    private String unit;

    public CommonPasswordParam() {
    }

    public CommonPasswordParam(String paramcode, String description, String unit) {
        this.paramcode = paramcode;
        this.description = description;
        this.unit = unit;
    }

    public String getParamcode() {
        return paramcode;
    }

    public void setParamcode(String paramcode) {
        this.paramcode = paramcode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
