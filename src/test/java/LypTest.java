import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.Test;
import sun.misc.URLClassPath;

import java.security.AccessControlContext;

public class LypTest {

    @Test
    public void test01() throws IllegalAccessException, InstantiationException {

        // 创建一个ByteBuddy实例
        ByteBuddy byteBuddy = new ByteBuddy();
        // 继承一个类
        DynamicType.Builder<Object> objectBuilder = byteBuddy.subclass(Object.class);
        // 类似一个筛选器，这里选中的是Object类中的toString()方法
        DynamicType.Builder.MethodDefinition.ImplementationDefinition<Object> method = objectBuilder.method(ElementMatchers.isToString());
        // 提供了了toString()的实现，这里的实现是返回一个固定的值"Hello World ByteBuddy!"
        DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<Object> intercept = method.intercept(FixedValue.value("Hello World ByteBuddy!"));
        // 这个时候，新的类已经被创建出来了，但是还没有被加载到JVM中。这个新的类的表现形式是DynamicType.Unloaded的一个实例，具体地说是DynamicType.Unloaded中包含了新的类的字节码。
        DynamicType.Unloaded<Object> unloadedType = intercept.make();

        byte[] bytes = unloadedType.getBytes();

        // 把它加载到JVM中
        Class<?> dynamicType = unloadedType.load(getClass().getClassLoader()).getLoaded();


        System.out.println(dynamicType.getName());


//        DynamicType.Unloaded unloadedType = new ByteBuddy()
//                .subclass(Object.class)
//                .method(ElementMatchers.isToString())
//                .intercept(FixedValue.value("Hello World ByteBuddy!"))
//                .make();
//
//        Class<?> dynamicType = unloadedType.load(getClass().getClassLoader()).getLoaded();
//
//        Object o = dynamicType.newInstance();


    }

    private AccessControlContext acc;

    private URLClassPath ucp;



}
