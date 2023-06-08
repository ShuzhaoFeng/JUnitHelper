# JavaTestHelper

## Concept
When Java tests are used for testing student code, the tests are usually written by the instructor. However, the instructor may not be able to anticipate all the possible errors that the student may make. For example, the student may forget to implement a method, or may implement a method with the wrong return type or access modifiers. In such cases, the instructor's test will fail to compile, and the student will not be able to see the error message. This project aims to solve this problem by providing a set of static methods that can be used to verify the existence of a method, and to invoke the method with a timeout. The methods will throw an exception if the method does not exist, or if the method fails to run within the timeout.

## Description
The project accounts for:
* The compiler error where class doesn't exist.
* The compiler error where method doesn't exist.
* The compiler error where method has wrong return type or access modifiers.
* The runtime error where method fails to run within a certain timeout.

All of the above will be caught at runtime and transformed into a RuntimeException, so that it wouldn't fail to compile.

## How To Use
The project uses a `TestManager` instance to execute all the methods. The library currently supports the following methods:
#### `TestManager.getClass(String className)`
Returns the class with the given name. Functionally equivalent to `MyClass.class`.
#### `TestManager.createInstance(Class<?> c, Object... args)`
Creates an instance of the given class with the given arguments. Functionally equivalent to `new MyClass(args)`.
#### `TestManager.createInstance(String className, Object... args)`
Creates an instance of the given class name with the given arguments.
#### `TestManager.createInstance(Class<?> c, Class<?>[] argumentTypes, Object... args)`
Creates an instance of the given class with the given arguments, using the constructor specified by the given argument types.
#### `TestManager.createInstance(String className, Class<?>[] argumentTypes, Object... args)`
Creates an instance of the given class name with the given arguments, using the constructor specified by the given argument types.
#### `TestManager.invoke(Class<?> c, Object instance, String methodName, Object... args)`
Invokes the method with the given name on the given instance of the given class with the given arguments. Functionally equivalent to `instance.myMethod(args)`.
#### `TestManager.invoke(String className, Object instance, String methodName, Object... args)`
Invokes the method with the given name on the given instance of the given class name with the given arguments.
#### `TestManager.invoke(Class<?> c, Object instance, String methodName, Class<?>[] argumentTypes, Object... args)`
Invokes the method with the given name on the given instance of the given class with the given arguments, using the method specified by the given argument types.
#### `TestManager.invoke(String className, Object instance, String methodName, Class<?>[] argumentTypes, Object... args)`
Invokes the method with the given name on the given instance of the given class name with the given arguments, using the method specified by the given argument types.
#### `TestManager.verifyMethod(Class<?> c, String methodName, Class<?>[] argumentTypes, Class<?> returnType, String... modifiers)`
Verify whether the method with the given name exists on the given class, with the given argument types, return type, and modifiers. The modifiers are optional, and can be any of the following: `public`, `private`, `protected`, `static`, `abstract`, `final`, `synchronized`, `native`, `strictfp`, `default`.

All methods should be called on a `TestManager` instance.

For example, the following 2 snippets of code are functionally equivalent, except the second one is using this helper library.

```Java
MyDoublyLinkedList<Integer> list = new MyDoublyLinkedList<>();

list = new MyDoublyLinkedList<>();
list.add(2);
list.addFirst(5);
list.addFirst(9);
list.addFirst(0);

assertEquals(4, list.getSize());
assertEquals(0, list.peekFirst());
assertEquals(2, list.peekLast());
```

```Java
TestManager manager = new TestManager();

Class<?> listClass = manager.getClass("MyDoublyLinkedList");
manager.verifyMethod(listClass, "getSize", new Class<?>[0], int.class, "public"); // optional

Object list = manager.createInstance(listClass);
manager.invoke(listClass, list, "add", 2);
manager.invoke(listClass, list, "addFirst", 5);
manager.invoke(listClass, list, "addFirst", 9);
manager.invoke(listClass, list, "addFirst", 0);

assertEquals(4, manager.invoke(listClass, list, "getSize"));
assertEquals(0, manager.invoke(listClass, list, "peekFirst"));
assertEquals(2, manager.invoke(listClass, list, "peekLast"));
```

## Known Issues
As of now, this project is a concept demo. It's not meant to be used in production. All suggestions and/or contributions are welcome.

### Known Critical Issues

### Known Moderate Issues
* This project has not yet been tested on thread-safety.
