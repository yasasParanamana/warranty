package com.epic.rdbsms.bean.common;

public class UserParamCategory {
    private String paramCategoryCode;
    private String description;

    public String getParamCategoryCode() {
        return paramCategoryCode;
    }

    public void setParamCategoryCode(String paramCategoryCode) {
        this.paramCategoryCode = paramCategoryCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class CommonPasswordParamBean {
        private String paramcode;
        private String description;
        private String unit;

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
}
