package com.oxcentra.warranty.config.datasource.impl;

import com.oxcentra.warranty.config.datasource.DBConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Profile("uat")
public class UatDBConfiguration implements DBConfiguration {

    @Override
    public DataSource setup() {
        final JndiDataSourceLookup jndiDataSourceLookup = new JndiDataSourceLookup();
        jndiDataSourceLookup.setResourceRef(true);
        //return jndiDataSourceLookup.getDataSource("java:/PB");
        //return jndiDataSourceLookup.getDataSource("jdbc/ORACLEPB");
//        return jndiDataSourceLookup.getDataSource("java:/COMBSMS");
        return jndiDataSourceLookup.getDataSource("java:/RDBSMS");
    }
}
