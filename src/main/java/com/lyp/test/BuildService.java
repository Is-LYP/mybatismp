package com.lyp.test;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Description: 基础mp构建服务
 * CreateDate:  2020/8/26 14:25
 *
 * @author 李宜鹏
 * @version 1.0
 * @date 2021-06-22 09:21
 */
@Service
@SuppressWarnings("unchecked")
public class BuildService {

    private final String prefix = "Ap";

    private final String directoryName = Objects.requireNonNull(getClass().getClassLoader().getResource("")).getFile();

    private final String packageName = "com.lyp.test";

    /**
     * Mapper文件存放路径
     */
    protected final String mapperPackage = packageName.concat(".mapper.");
    /**
     * Service文件存放路径
     */
    protected final String servicePackage = packageName.concat(".service.");
    /**
     * Service文件存放路径
     */
    protected final String serviceImplPackage = servicePackage.concat("impl.");

    private final SqlSessionTemplate sqlSessionTemplate;

    public BuildService(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    /**
     * 创建Mapper接口类
     *
     * @param <T> 实体类
     * @param entityClass 实体类的Class
     * @return 实体类对应的BaseMapper接口Class
     */
    public <T> Class<BaseMapper<T>> createMapperInterface(Class<T> entityClass) {
        TypeDescription.Generic genericSuperClass = TypeDescription.Generic.Builder.parameterizedType(BaseMapper.class, entityClass).build();

        String className = mapperPackage + getMapperClassName(entityClass.getSimpleName());

        ByteBuddy byteBuddy = new ByteBuddy();
        DynamicType.Unloaded<?> unloaded = byteBuddy.makeInterface(genericSuperClass).name(className).make();

        try {
            unloaded.saveIn(new File(directoryName));
            return (Class<BaseMapper<T>>) Class.forName(className);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();

            return null;
        }
    }

    /**
     * 创建Mapper实现类
     *
     * @param <T> 实体类
     * @param mapperClass 实体类对应的BaseMapper接口Class
     * @return 实体类对应的BaseMapper对象
     */
    public <T> BaseMapper<T> createMapper(Class<BaseMapper<T>> mapperClass) {
        MapperFactoryBean<BaseMapper<T>> mapperFactoryBean = new MapperFactoryBean<>(mapperClass);

        // 将mapperInterface注册到configuration中。
        this.sqlSessionTemplate.getConfiguration().addMapper(mapperClass);

        // MapperProxy代理生成，通过SqlSessionTemplate去获取我们得Mapper代理。
        mapperFactoryBean.setSqlSessionTemplate(this.sqlSessionTemplate);

        return mapperFactoryBean.getSqlSession().getMapper(mapperClass);
    }

    /**
     *
     * @param entityClass 实体类class对象
     * @param <T> 实体类
     * @return 实体类对应的IService接口Class
     */
    public <T> Class<IService<T>> createServiceInterface(Class<T> entityClass) {
        TypeDescription.Generic genericSuperClass = TypeDescription.Generic.Builder.parameterizedType(IService.class, entityClass).build();

        // 获取类的名称
        String className = servicePackage + getInterfaceServiceClassName(entityClass.getSimpleName());

        ByteBuddy byteBuddy = new ByteBuddy();
        DynamicType.Unloaded<?> unloaded = byteBuddy.makeInterface(genericSuperClass).name(className).make();

        try {
            unloaded.saveIn(new File(directoryName));
            return (Class<IService<T>>) Class.forName(className);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();

            return null;
        }
    }

    /**
     * 创建Service 实现类
     *
     * @param <T> 实体类
     * @return 实体类对应的Service实现类
     */
    public <T> ServiceImpl<BaseMapper<T>, T> createService(Class<T> entityClass, Class<IService<T>> serviceInterface, Class<BaseMapper<T>> mapperInterface) {

        String className = serviceImplPackage + getServiceClassName(entityClass.getSimpleName());

        TypeDescription.Generic generic = TypeDescription.Generic.Builder.parameterizedType(ServiceImpl.class, mapperInterface, entityClass).build();
        ByteBuddy byteBuddy = new ByteBuddy();
        DynamicType.Unloaded<?> unloaded = byteBuddy.subclass(generic)
                .implement(TypeDescription.Generic.Builder.rawType(serviceInterface).build())
                .name(className)
                .annotateType(AnnotationDescription.Builder.ofType(Service.class).define("value", className).build())
                .make();

        try {
            unloaded.saveIn(new File(directoryName));

            Class<?> aClass = Class.forName(className);

            return (ServiceImpl<BaseMapper<T>, T>) aClass.newInstance();
        } catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();

            return null;
        }
    }

    /**
     * 生成实体类Class名称
     *
     * @param tableName 表名
     * @return 实体类名称
     * @author 李宜鹏
     * @date 2021-06-22 09:40
     */
    public String getEntityClassName(String tableName) {
        return prefix + tableName;
    }

    /**
     * 生成Service接口Class名称
     *
     * @param entityClassName 实体类名称
     * @return Service接口Class名称
     */
    public String getInterfaceServiceClassName(String entityClassName) {
        return String.format("%sService", entityClassName);
    }

    /**
     * 生成Service Class名称
     *
     * @param entityClassName 实体类名称
     * @return Service实现类Class名称
     * @author 李宜鹏
     * @date 2021-06-22 09:40
     */
    public String getServiceClassName(String entityClassName) {
        return String.format("%sServiceImpl", entityClassName);
    }

    /**
     * 生成Mapper接口Class名称
     *
     * @param entityClassName 实体类名称
     * @return Mapper接口Class名称
     * @author 李宜鹏
     * @date 2021-06-22 09:40
     */
    public String getMapperClassName(String entityClassName) {
        return String.format("%sMapper", entityClassName);
    }
}