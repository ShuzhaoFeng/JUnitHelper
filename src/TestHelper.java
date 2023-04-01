import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class TestHelper {
    public static Class<?> findClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Object createNewInstance(Class<?> c, Class<?>[] types, Object... args) throws IllegalArgumentException{
        try {
            return c.getDeclaredConstructor(types).newInstance(args);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Class " + c.getName() + " is not instantiable.", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Class " + c.getName() + "'s constructor is not accessible'.", e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("The constructor for Class " + c.getName() + " threw an exception.", e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Class " + c.getName() + " does not have a matching constructor.", e);
        }
    }

    public static void verify(Class<?> c, String methodName, Class<?>[] argumentTypes, Class<?> returnType, String... modifiers) throws IllegalArgumentException {
        try {
            Method m = c.getMethod(methodName, argumentTypes);

            if (!m.getReturnType().equals(returnType)) {
                throw new IllegalArgumentException("Method " + methodName + " in class " + c.getName() + " has incorrect return type.");
            }

            int modifier = m.getModifiers();

            for (String mod : modifiers) {
                switch (mod) {
                    case "public":
                        if (!Modifier.isPublic(modifier)) {
                            throw new IllegalArgumentException("Method " + methodName + " in class " + c.getName() + " is not public.");
                        }
                        break;
                    case "private":
                        if (!Modifier.isPrivate(modifier)) {
                            throw new IllegalArgumentException("Method " + methodName + " in class " + c.getName() + " is not private.");
                        }
                        break;
                    case "protected":
                        if (!Modifier.isProtected(modifier)) {
                            throw new IllegalArgumentException("Method " + methodName + " in class " + c.getName() + " is not protected.");
                        }
                        break;
                    case "static":
                        if (!Modifier.isStatic(modifier)) {
                            throw new IllegalArgumentException("Method " + methodName + " in class " + c.getName() + " is not static.");
                        }
                        break;
                    case "final":
                        if (!Modifier.isFinal(modifier)) {
                            throw new IllegalArgumentException("Method " + methodName + " in class " + c.getName() + " is not final.");
                        }
                        break;
                    case "abstract":
                        if (!Modifier.isAbstract(modifier)) {
                            throw new IllegalArgumentException("Method " + methodName + " in class " + c.getName() + " is not abstract.");
                        }
                        break;
                    case "synchronized":
                        if (!Modifier.isSynchronized(modifier)) {
                            throw new IllegalArgumentException("Method " + methodName + " in class " + c.getName() + " is not synchronized.");
                        }
                        break;
                    case "native":
                        if (!Modifier.isNative(modifier)) {
                            throw new IllegalArgumentException("Method " + methodName + " in class " + c.getName() + " is not native.");
                        }
                        break;
                    case "strict":
                        if (!Modifier.isStrict(modifier)) {
                            throw new IllegalArgumentException("Method " + methodName + " in class " + c.getName() + " is not strict.");
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Modifier " + mod + " is not a valid modifier.");
                }
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Method " + methodName + " in class " + c.getName() + " does not exist.");
        }
    }

    public static Object invoke(Class<?> c, String methodName, Class<?>[] argumentTypes,
                                Object instance, int seconds, Object... args) throws IllegalArgumentException {
        try {
            Method m = c.getMethod(methodName, argumentTypes);

            Object[] result = new Object[1];

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        result[0] = m.invoke(instance, args);
                    } catch (IllegalAccessException e) {
                        throw new IllegalArgumentException("Class " + c.getName() + "'s constructor is not accessible'.", e);
                    } catch (InvocationTargetException e) {
                        throw new IllegalArgumentException("The constructor for Class " + c.getName() + " threw an exception.", e);
                    }
                }
            });

            t.start();
            t.join(seconds * 1000L);

            if (t.isAlive()) {
                t.interrupt();
                throw new IllegalArgumentException("Method " + methodName + " in class " + c.getName() + " did not return within "+seconds+" second.");
            }

            return result[0];
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Method " + methodName + " not found in class " + c.getName()
                    + ", or does not have the correct type/number of arguments.", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
