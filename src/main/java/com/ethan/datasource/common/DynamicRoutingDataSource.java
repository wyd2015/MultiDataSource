package com.ethan.datasource.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Slf4j
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        log.info("当前数据源: " + DynamicDataSourceContextHolder.get());
        return DynamicDataSourceContextHolder.get();
    }
}
