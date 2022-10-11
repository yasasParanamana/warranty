package com.oxcentra.warranty.config.datasource.impl;

import com.oxcentra.warranty.config.datasource.DBConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Profile("dev")
public class DevDBConfiguration implements DBConfiguration {

    @Override
    public DataSource setup() {
        final JndiDataSourceLookup jndiDataSourceLookup = new JndiDataSourceLookup();
        jndiDataSourceLookup.setResourceRef(true);
        System.out.println("---------------------------------------DevDBConfiguration");
        return jndiDataSourceLookup.getDataSource("java:/AWS_WARRANTY");
    }
}
