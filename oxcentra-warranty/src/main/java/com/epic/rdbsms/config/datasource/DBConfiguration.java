package com.epic.rdbsms.config.datasource;

import javax.sql.DataSource;

@FunctionalInterface
public interface DBConfiguration {

    /**
     * @Author shalika_w
     * @CreatedTime 2021-01-06 02:31:29 PM
     * @Version V1.00
     * @MethodName setup
     * @MethodParams []
     * @MethodDescription - This method returns the datasource object for give active profile.
     */
    DataSource setup();
}
