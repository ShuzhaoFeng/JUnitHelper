import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestManager {
    private int timeout = 1000;

    public TestManager() {
    }

    public TestManager(int timeout) {
        this.timeout = timeout;
    }

    public Class<?> getClass(String className) throws IllegalArgumentException, InterruptedException {
        final Class<?>[] c = new Class<?>[1];

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    c[0] = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("Class " + className + " does not exist.");
                }
            }
        });

        t.start();
        t.join(timeout);

        return c[0];
    }

    public Object createInstance(Class<?> c, Object... args) throws IllegalArgumentException, InterruptedException {
        Object[] instance = new Object[1];

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (Constructor<?> constructor : c.getConstructors()) {
                    try {
                        instance[0] = constructor.newInstance(args);
                        return;
                    } catch (Exception ignored) {
                    }
                }
                throw new IllegalArgumentException("Class " + c.getName() + " is not instantiable.");
            }});

        t.start();
        t.join(timeout);

        return instance[0];
    }

    public Object createInstance(Class<?> c, Class<?>[] argumentTypes, Object... args) throws IllegalArgumentException, InterruptedException {
        Object[] instance = new Object[1];

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    instance[0] = c.getConstructor(argumentTypes).newInstance(args);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e.getMessage(), e);
                }
            }});

        t.start();
        t.join(timeout);

        return instance[0];
    }

    public Object createInstance(String className, Object... args) throws IllegalArgumentException, InterruptedException {
        Class<?> c = getClass(className);
        return createInstance(c, args);
    }

    public Object createInstance(String className, Class<?>[] argumentTypes, Object... args) throws IllegalArgumentException, InterruptedException {
        Class<?> c = getClass(className);
        return createInstance(c, argumentTypes, args);
    }

    public Object invoke(Class<?> c, Object instance, String methodName, Object... args) throws IllegalArgumentException, InterruptedException {
        Object[] result = new Object[1];

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                AtomicBoolean success = new AtomicBoolean(false);
                Arrays.stream(c.getMethods()).filter(method -> method.getName().equals(methodName)).forEach(method -> {
                    try {
                        if (success.get()) return;

                        result[0] = method.invoke(instance, args);

                        success.set(true);
                    } catch (Exception ignored) {
                        success.set(false);
                    }
                });
                if (!success.get()) {
                    throw new IllegalArgumentException("No matching method " + methodName + " in class " + c.getName() + " accepts the given arguments.");
                }
            }
        });

        t.start();
        t.join(timeout);

        return result[0];
    }

    public Object invoke(Class<?> c, String methodName, Class<?>[] argumentTypes, Object instance, Object... args) throws IllegalArgumentException, InterruptedException {
        Object[] result = new Object[1];

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    result[0] = c.getMethod(methodName, argumentTypes).invoke(instance, args);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e.getMessage(), e);
                }
            }
        });

        t.start();
        t.join(timeout);

        return result[0];
    }

    public Object invoke(String className, Object instance, String methodName, Object... args) throws IllegalArgumentException, InterruptedException {
        Class<?> c = getClass(className);
        return invoke(c, instance, methodName, args);
    }

    public Object invoke(String className, String methodName, Class<?>[] argumentTypes, Object instance, Object... args) throws IllegalArgumentException, InterruptedException {
        Class<?> c = getClass(className);
        return invoke(c, methodName, argumentTypes, instance, args);
    }

    public void verifyMethod(Class<?> c, String methodName, Class<?>[] argumentTypes, Class<?> returnType, String... modifiers) throws NoSuchMethodException {
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
                case "strictfp":
                    if (!Modifier.isStrict(modifier)) {
                        throw new IllegalArgumentException("Method " + methodName + " in class " + c.getName() + " is not strict.");
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Modifier " + mod + " is not a valid modifier.");
            }
        }
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
