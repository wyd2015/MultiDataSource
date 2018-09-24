package com.ethan.datasource.common;

import com.ethan.datasource.annotation.TargetDataSource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Order(-1)
@Component
public class DynamicDataSourceAspect {

    @Pointcut("execution(* com.ethan.datasource.service.*.get*(..))")
    public void pointCut(){}

    /**
     * 方法执行之前更换数据源
     * @param joinPoint
     * @param targetDataSource
     */
    @Before("@annotation(targetDataSource)")
    public void doBefore(JoinPoint joinPoint, TargetDataSource targetDataSource){
        DataSourceKey dataSourceKey = targetDataSource.dataSourceKey();
        DynamicDataSourceContextHolder.set(dataSourceKey);

        if(dataSourceKey == DataSourceKey.DB_OTHER){
            log.info("设置当前数据源为： " + DataSourceKey.DB_OTHER);
        }else{
            log.info("使用默认数据源： " + DataSourceKey.DB_MASTER);
        }
    }

    /**
     * 执行方法后清除数据源
     * @param joinPoint
     * @param targetDataSource
     */
    @After("@annotation(targetDataSource)")
    public void doAfter(JoinPoint joinPoint, TargetDataSource targetDataSource){
        log.info("清除前的数据源： " + targetDataSource.dataSourceKey());
        DynamicDataSourceContextHolder.clear();
        log.info("清除后的数据源： " + DynamicDataSourceContextHolder.get());
    }

    /**
     * 使用从库执行方法前
     * @param joinPoint
     */
    @Before(value = "pointCut()")
    public void doBeforeWithSlave(JoinPoint joinPoint){
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //获取当前切点方法对象
        Method method = methodSignature.getMethod();
        if(method.getDeclaringClass().isInterface()){
            try {
                method = joinPoint.getTarget().getClass()
                        .getDeclaredMethod(joinPoint.getSignature().getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                log.error("方法不存在！", e);
            }
        }

        if(method.getAnnotation(TargetDataSource.class) == null){
            DynamicDataSourceContextHolder.setSlave();
        }
    }
}
