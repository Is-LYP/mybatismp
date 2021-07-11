package com.lyp.test;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Proxy;

/**
 * @author 李宜鹏
 * @date 2021-06-27 21:59
 */
@Service
@SuppressWarnings("unchecked")
public class SpringBuildService extends BuildService {

    private final DefaultListableBeanFactory defaultListableBeanFactory;

    public SpringBuildService(SqlSessionTemplate sqlSessionTemplate, DefaultListableBeanFactory defaultListableBeanFactory) {
        super(sqlSessionTemplate);

        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    /**
     * 创建BaseMapper
     *
     * @param entityClass 实体类class对象
     * @param <T> 实体类
     * @return 实体类对应的BaseMapper对象
     */
    public <T> BaseMapper<T> getMapper(Class<T> entityClass) {
        String beanName = mapperPackage + getMapperClassName(entityClass.getSimpleName());

        try {
            return (BaseMapper<T>) defaultListableBeanFactory.getBean(beanName);
        } catch (NoSuchBeanDefinitionException e) {
            BaseMapper<T> baseMapper = createMapper(getMapperInterface(entityClass));

            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(baseMapper.getClass());
            beanDefinitionBuilder.addConstructorArgValue(Proxy.getInvocationHandler(baseMapper));

            AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
            beanDefinition.setBeanClass(baseMapper.getClass());

            defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getRawBeanDefinition());

            return (BaseMapper<T>) defaultListableBeanFactory.getBean(beanName);
        }
    }

    /**
     * 获取Mapper接口Mapper
     *
     * @param entityClass 实体类class对象
     * @param <T> 实体类
     * @return 实体类对应的BaseMapper接口Class
     */
    public <T> Class<BaseMapper<T>> getMapperInterface(Class<T> entityClass) {
        String classPath = mapperPackage + getMapperClassName(entityClass.getSimpleName());

        Class<BaseMapper<T>> mapperInterface;
        try {
            mapperInterface = (Class<BaseMapper<T>>) Class.forName(classPath);
        } catch (ClassNotFoundException e) {
            mapperInterface = createMapperInterface(entityClass);
        }

        return mapperInterface;
    }

    /**
     * 创建IService
     *
     * @param entityClass 实体类class对象
     * @param <T> 实体类
     * @return 实体类对应的IService对象
     */
    public <T> IService<T> getService(Class<T> entityClass) {
        getMapper(entityClass);

        String serviceInterfaceClassName = servicePackage + getInterfaceServiceClassName(entityClass.getSimpleName());

        Class<IService<T>> serviceInterfaceClass;
        try {
            serviceInterfaceClass = (Class<IService<T>>) Class.forName(serviceInterfaceClassName);
        } catch (ClassNotFoundException e) {
            serviceInterfaceClass = createServiceInterface(entityClass);
        }

        String serviceClassName = serviceImplPackage + getServiceClassName(entityClass.getSimpleName());


        ServiceImpl<BaseMapper<T>, T> serviceImpl;
        try {
            return (ServiceImpl<BaseMapper<T>, T>) defaultListableBeanFactory.getBean(serviceClassName);
        } catch (NoSuchBeanDefinitionException e) {
            try {
                serviceImpl = (ServiceImpl<BaseMapper<T>, T>) Class.forName(serviceClassName).newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException classNotFoundException) {
                serviceImpl = createService(entityClass, serviceInterfaceClass, getMapperInterface(entityClass));
            }

            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(serviceImpl.getClass());

            AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
            beanDefinition.setBeanClass(serviceImpl.getClass());

            defaultListableBeanFactory.registerBeanDefinition(serviceClassName, beanDefinitionBuilder.getRawBeanDefinition());

            return (ServiceImpl<BaseMapper<T>, T>) defaultListableBeanFactory.getBean(serviceClassName);
        }

    }
}
