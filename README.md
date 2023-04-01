# JUnitHelper

## Concept
When JUnit tests are used for testing student code, the tests are usually written by the instructor. However, the instructor may not be able to anticipate all the possible errors that the student may make. For example, the student may forget to implement a method, or may implement a method with the wrong return type or access modifiers. In such cases, the instructor's test will fail to compile, and the student will not be able to see the error message. This project aims to solve this problem by providing a set of static methods that can be used to verify the existence of a method, and to invoke the method with a timeout. The methods will throw an exception if the method does not exist, or if the method fails to run within the timeout.

## Description
The project accounts for:
* The compiler error where class doesn't exist.
* The compiler error where method doesn't exist.
* The compiler error where method has wrong return type or access modifiers.
* The runtime error where method fails to run within the timeout.

All of the above will be caught at runtime and transformed into `IllegalArgumentException` with an error message.

## How To Use
All static methods exist within a Java class called `TesterHelper`. The following is a list of all the methods available in the class, and a brief description of what they do.
* `findClass(String className)`returns a `Class<?>` object that represents the class with the name `className`. If the class is not found, then `null` is returned. Note that the class name must also include the package name.
* `createNewInstance(Class<?> c, Object... args)` creates an instance of the class `c` using the constructor that takes in the arguments `args`. If the constructor is not found, an exception is thrown.
* `verify(Class<?> c, String methodName, Class<?>[] argumentTypes, Class<?> returnType, String... modifiers)` verifies that the method `methodName` exists in the class `c`, and that it has the return type `returnType` and the modifiers `modifiers`. If the method does not exist, or if the method does not have the correct return type or modifiers, an exception is thrown.
* `invoke(Class<?> c, String methodName, Class<?>[] argumentTypes, Object instance, int seconds, Object... args)` invokes the method `methodName` in the class `c` using the arguments `args`, with a timeout set in seconds, and returns the result. If the method fails to give a result within the timeout or if it fails to run at all, and exception is thrown.

For example, the following 2 snippets of code are functionally equivalent, except the second one would not generate any compiler error, and that it would be able to pinpoint the error, if any:
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
Class<?> mydll = TesterHelper.findClass("assignment2.MyDoublyLinkedList");

if (mydll == null) Assertions.fail("Class MyDoublyLinkedList not found.");

Object list = null;

Class<?>[] types = {Object.class};
try {
    list = TesterHelper.createNewInstance(mydll, null);
    
    TestHelper.verify(mydll, "add", types, boolean.class, "public");
    TestHelper.invoke(mydll, "add", types, list, 1, 2);
    
    TestHelper.verify(mydll, "addFirst", types, boolean.class, "public");
    TestHelper.invoke(mydll, "addFirst", types, list, 1, 5);
    TestHelper.invoke(mydll, "addFirst", types, list, 1, 9);
    TestHelper.invoke(mydll, "addFirst", types, list, 1, 0);
    
    TestHelper.verify(mydll, "getSize", null, int.class, "public");
    assertEquals(4, TestHelper.invoke(mydll, "getSize", null, list, 1));

    TestHelper.verify(mydll, "peekFirst", null, Object.class, "public");
    assertEquals(0, TestHelper.invoke(mydll, "peekFirst", null, list, 1));

    TestHelper.verify(mydll, "peekLast", null, Object.class, "public");
    assertEquals(2, TestHelper.invoke(mydll, "peekLast", null, list, 1));
} catch (IllegalArgumentException e) {
    Assertions.fail(e.getMessage());
}
```

## Known Issues
This project is a concept demo. It's not meant to be used in production.

### Known Critical Issues

### Known Moderate Issues
* This project has not yet been tested on thread-safety.
* `TestHelper.invoke` must take an array `argumentTypes` while this is conceptually redundant with `verify`. However, the code will not work if `argumentTypes` is not provided, because `Class.getMethod()` requires it to find the correct method.
