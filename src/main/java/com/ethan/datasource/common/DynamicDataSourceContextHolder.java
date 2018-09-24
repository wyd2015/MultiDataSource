package com.ethan.datasource.common;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class DynamicDataSourceContextHolder {

    private static final ThreadLocal<DataSourceKey> currentDataSource = new ThreadLocal<>();

    /**
     * 清除当前数据源
     */
    public static void clear(){
        currentDataSource.remove();
    }

    /**
     * 获取当前使用的数据源
     */
    public static DataSourceKey get(){
        return currentDataSource.get();
    }

    /**
     * 设置当前使用的数据源
     * @param key 需要设置的数据源ID
     */
    public static void set(DataSourceKey key){
        currentDataSource.set(key);
    }

    /**
     * 设置从从库读取数据（只有主库可写）
     * 采用简单生成随机数的方式切换不同的从库
     */
    public static void setSlave(){
        int random = new Random().nextInt(2);
        if(random > 0){
            DynamicDataSourceContextHolder.set(DataSourceKey.DB_SLAVE1);
        }else{
            DynamicDataSourceContextHolder.set(DataSourceKey.DB_SLAVE2);
        }
    }
}
