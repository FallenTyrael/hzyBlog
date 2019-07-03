# 关于对象的拷贝

- [关于对象的拷贝](#关于对象的拷贝)
  - [1.BeanUtil简单测试](#1beanutil简单测试)
    - [2个类的定义](#2个类的定义)
    - [测试](#测试)
    - [结果](#结果)
    - [结论与问题溯源](#结论与问题溯源)
  - [2.对象序列化](#2对象序列化)
    - [概述](#概述)
    - [实现](#实现)
    - [测试](#测试-1)
    - [结果](#结果-1)
  - [3.总结](#3总结)
  - [4.另一种深拷贝方法](#4另一种深拷贝方法)

## 1.BeanUtil简单测试
在项目中由于需要对某些对象进行拷贝然后进行持久化操作，通过网络查询到apache和spring都提供了BeanUtils的深度拷贝工具包，写了个Demo做测试，定义了两个类Address和Person，其中Person的属性引用了Address类。
### 2个类的定义
```
public class Address {
    private String province;
    private String city;

    public void setAddress(String province,String city){
        this.province = province;
        this.city = city;
    }

    @Override
    public String toString() {
        return "Address [province=" + province + ", city=" + city + "]";
    }

    //get set方法此处省略
}
```
```
public class Person {
    public String name;
    public int age;
    public Address address;
    public Person() {}

    public Person(String name,int age){
        this.name = name;
        this.age = age;
        this.address = new Address();
    }

    public void setAddress(String province,String city ){
        address.setAddress(province, city);
    }
    
    public void display(String name){
        System.out.println(name+":"+"name=" + name + ", age=" + age +", address"+ address);
    }

    //get set方法此处省略
}
```
### 测试
此处在使用apache提供的BeanUtils时，拷贝引用类型的字段（Address）出错，采用spring提供的BeanUtils
```
public static void main(String test[]){
    try{
        Person p1 = new Person("Fallen He",18);
        p1.setAddress("天际", "巨龙");
        Person p2 = new Person();
        BeanUtils.copyProperties(p1, p2);

        System.out.println("p1:"+p1);
        System.out.println("p2:"+p2);
        p1.display("p1");
        p2.display("p2");

        p2.setAddress("罗德兰", "亚诺尔隆德");
        System.out.println("将复制之后的对象地址修改：");
        p1.display("p1");
        p2.display("p2");
    }catch (Exception e){
        System.out.println("error:"+e);
    }
}
```
### 结果
```
p1:com.brg.controller.Person@98129
p2:com.brg.controller.Person@18e2867
p1:name=p1, age=18, addressAddress [province=天际, city=巨龙]
p2:name=p2, age=18, addressAddress [province=天际, city=巨龙]
将复制之后的对象地址修改：
p1:name=p1, age=18, addressAddress [province=罗德兰, city=亚诺尔隆德]
p2:name=p2, age=18, addressAddress [province=罗德兰, city=亚诺尔隆德]
```
### 结论与问题溯源
可以看出user和target的哈希码不相同，但是其中的对象地址都是一样的，BeanUtils只实现了浅拷贝。

查看源码，也可以看出是浅拷贝，并且由于采用了反射，性能比比较差
```
    private static void copyProperties(Object source, Object target, Class<?> editable, String... ignoreProperties) throws BeansException {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");
        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName() + "] not assignable to Editable class [" + editable.getName() + "]");
            }

            actualEditable = editable;
        }

        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        List<String> ignoreList = ignoreProperties != null ? Arrays.asList(ignoreProperties) : null;
        PropertyDescriptor[] var7 = targetPds;
        int var8 = targetPds.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            PropertyDescriptor targetPd = var7[var9];
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null && ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }

                            Object value = readMethod.invoke(source);
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }

                            writeMethod.invoke(target, value);
                        } catch (Throwable var15) {
                            throw new FatalBeanException("Could not copy property '" + targetPd.getName() + "' from source to target", var15);
                        }
                    }
                }
            }
        }

    }
```


## 2.对象序列化

### 概述
序列化是将对象写到流中便于传输，而反序列化则是把对象从流中读取出来。这里写到流中的对象则是原始对象的一个拷贝，因为原始对象还存在 JVM 中，所以我们可以利用对象的序列化产生克隆对象，然后通过反序列化获取这个对象。

注意每个需要序列化的类都要实现 Serializable 接口，如果有某个属性不需要序列化，可以将其声明为 transient，即将其排除在克隆属性之外。

### 实现
对象需要实现Serializable接口，包括引用的其他对象
```
public class Person implements Serializable {
  //上下文内容省略

  public Person clone(){
      Person copy = null;
      try {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ObjectOutputStream oos = new ObjectOutputStream(baos);
          oos.writeObject(this);
          //将流序列化成对象
          ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
          ObjectInputStream ois = new ObjectInputStream(bais);
          copy = (Person) ois.readObject();
      } catch (IOException e) {
          e.printStackTrace();
      } catch (ClassNotFoundException e) {
          e.printStackTrace();
      }
      return copy;
  }
}
```
```
public class Address implements Serializable {
  //上下文内容省略
}
```

### 测试
```
public static void main(String test[]){
    try{
        Person p1 = new Person("Fallen He",18);
        p1.setAddress("天际", "巨龙");
        Person p2 = p1.clone();

        System.out.println("p1:"+p1);
        System.out.println("p2:"+p2);
        p1.display("p1");
        p2.display("p2");
        
        p2.setAddress("罗德兰", "亚诺尔隆德");
        System.out.println("将复制之后的对象地址修改：");
        p1.display("p1");
        p2.display("p2");
    }catch (Exception e){
        System.out.println("error:"+e);
    }
}
```

### 结果
```
p1:com.brg.controller.Person@1663380
p2:com.brg.controller.Person@6b2d4a
p1:name=p1, age=18, addressAddress [province=天际, city=巨龙]
p2:name=p2, age=18, addressAddress [province=天际, city=巨龙]
将复制之后的对象地址修改：
p1:name=p1, age=18, addressAddress [province=天际, city=巨龙]
p2:name=p2, age=18, addressAddress [province=罗德兰, city=亚诺尔隆德]
```

## 3.总结
浅拷贝：创建一个新对象，然后将当前对象的非静态字段复制到该新对象，如果字段是值类型的，那么对该字段执行复制；如果该字段是引用类型的话，则复制引用但不复制引用的对象。因此，原始对象及其副本引用同一个对象。

深拷贝：创建一个新对象，然后将当前对象的非静态字段复制到该新对象，无论该字段是值类型的还是引用类型，都复制独立的一份。当你修改其中一个对象的任何内容时，都不会影响另一个对象的内容。

## 4.另一种深拷贝方法
实现Cloneable接口，再引用对象的内部重写clone方法。

这种方法在有多个引用类型的时候要重写多次clone，比较繁琐
```
public class Psrson implements Cloneable{
  //上下文省略
  @Override
  protected Object clone() throws CloneNotSupportedException {
      Person p = (Person) super.clone();
      p.address = (Address) address.clone();
      return p;
  }
}
```

```
public class Address implements Cloneable{
  //上下文省略
  @Override
  protected Object clone() throws CloneNotSupportedException {
      return super.clone();
  }
}
```