package com.sofar.utility.reflect;

import androidx.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * 成员方法
 * getDeclaredMethod是可以获取一个类本身的所有方法.
 * getMethod只能获取类及其父类的public方法.
 */
public class MethodUtil {

  private static Map<String, Method> sMethodCache = new HashMap<>();

  private static String getKey(@NonNull final Class<?> cls, final String methodName, final Class<?>... parameterTypes) {
    StringBuilder sb = new StringBuilder();
    sb.append(cls.toString()).append("#").append(methodName);
    if (parameterTypes != null && parameterTypes.length > 0) {
      for (Class<?> parameterType : parameterTypes) {
        sb.append(parameterType.toString()).append("#");
      }
    } else {
      sb.append(Void.class.toString());
    }
    return sb.toString();
  }


  private static Method getAccessibleMethodFromSuperclass(final Class<?> cls, final String methodName, final Class<?>... parameterTypes) {
    Class<?> parentClass = cls.getSuperclass();
    while (parentClass != null) {
      if (Modifier.isPublic(parentClass.getModifiers())) {
        try {
          return parentClass.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
          ReflectUtil.print(e);
        }
      }
      parentClass = parentClass.getSuperclass();
    }
    return null;
  }

  private static Method getAccessibleMethodFromInterfaceNest(Class<?> cls, final String methodName, final Class<?>... parameterTypes) {
    for (; cls != null; cls = cls.getSuperclass()) {
      // Check the implemented interfaces of the parent class
      Class<?>[] interfaces = cls.getInterfaces();
      for (int i = 0; i < interfaces.length; i++) {
        // Is this interface public?
        if (!Modifier.isPublic(interfaces[i].getModifiers())) {
          continue;
        }

        try {
          return interfaces[i].getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {

        }
        // Recursively check our parent interfaces
        Method method = getAccessibleMethodFromInterfaceNest(interfaces[i], methodName, parameterTypes);
        if (method != null) {
          return method;
        }
      }
    }
    return null;
  }


  private static Method getAccessibleMethod(Method method) {
    if (!MemberUtil.isAccessible(method)) {
      return null;
    }
    // If the declaring class is public, we are done
    final Class<?> cls = method.getDeclaringClass();
    if (Modifier.isPublic(cls.getModifiers())) {
      return method;
    }
    final String methodName = method.getName();
    final Class<?>[] parameterTypes = method.getParameterTypes();

    // Check the implemented interfaces and subinterfaces
    method = getAccessibleMethodFromInterfaceNest(cls, methodName,
      parameterTypes);

    // Check the superclass chain
    if (method == null) {
      method = getAccessibleMethodFromSuperclass(cls, methodName,
        parameterTypes);
    }
    return method;
  }

  public static Method getAccessibleMethod(final String cls, final String methodName, final Class<?>... parameterTypes)
    throws NoSuchMethodException {
    try {
      return getAccessibleMethod(Class.forName(cls), methodName, parameterTypes);
    } catch (Throwable ignore) {
      ReflectUtil.print(ignore);
    }
    return null;
  }

  public static Method getAccessibleMethod(final Class<?> cls, final String methodName, final Class<?>... parameterTypes)
    throws NoSuchMethodException {
    String key = getKey(cls, methodName, parameterTypes);
    Method method;
    synchronized (sMethodCache) {
      method = sMethodCache.get(key);
    }
    if (method != null) {
      if (!method.isAccessible()) {
        method.setAccessible(true);
      }
      return method;
    }

    Method accessibleMethod = getAccessibleMethod(cls.getMethod(methodName,
      parameterTypes));
    synchronized (sMethodCache) {
      sMethodCache.put(key, accessibleMethod);
    }
    return accessibleMethod;

  }

  private static Method getMatchingAccessibleMethod(final Class<?> cls, final String methodName, final Class<?>... parameterTypes) {
    String key = getKey(cls, methodName, parameterTypes);
    Method cachedMethod;
    synchronized (sMethodCache) {
      cachedMethod = sMethodCache.get(key);
    }
    if (cachedMethod != null) {
      if (!cachedMethod.isAccessible()) {
        cachedMethod.setAccessible(true);
      }
      return cachedMethod;
    }

    try {
      final Method method = cls.getMethod(methodName, parameterTypes);
      MemberUtil.setAccessibleWorkaround(method);
      synchronized (sMethodCache) {
        sMethodCache.put(key, method);
      }
      return method;
    } catch (final NoSuchMethodException e) { // NOPMD - Swallow the exception
    }
    // search through all methods
    Method bestMatch = null;
    final Method[] methods = cls.getMethods();
    for (final Method method : methods) {
      // compare name and parameters
      if (method.getName().equals(methodName) && MemberUtil.isAssignable(parameterTypes, method.getParameterTypes(), true)) {
        // get accessible version of method
        final Method accessibleMethod = getAccessibleMethod(method);
        if (accessibleMethod != null && (bestMatch == null || MemberUtil.compareParameterTypes(
          accessibleMethod.getParameterTypes(),
          bestMatch.getParameterTypes(),
          parameterTypes) < 0)) {
          bestMatch = accessibleMethod;
        }
      }
    }
    if (bestMatch != null) {
      MemberUtil.setAccessibleWorkaround(bestMatch);
    }
    synchronized (sMethodCache) {
      sMethodCache.put(key, bestMatch);
    }
    return bestMatch;
  }

  public static Object invokeMethod(final Object object, final String methodName, Object... args)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    args = ReflectUtil.nullToEmpty(args);
    final Class<?>[] parameterTypes = ReflectUtil.toClass(args);
    return invokeMethod(object, methodName, args, parameterTypes);
  }

  public static Object invokeMethod(final Object object, final String methodName, Object[] args, Class<?>[] parameterTypes)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    parameterTypes = ReflectUtil.nullToEmpty(parameterTypes);
    args = ReflectUtil.nullToEmpty(args);
    final Method method = getMatchingAccessibleMethod(object.getClass(),
      methodName, parameterTypes);
    if (method == null) {
      throw new NoSuchMethodException("No such accessible method: "
        + methodName + "() on object: "
        + object.getClass().getName());
    }
    return method.invoke(object, args);
  }


  public static Object invokeStaticMethod(final String clazz, final String methodName, Object... args)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    try {
      return invokeStaticMethod(Class.forName(clazz), methodName, args);
    } catch (Throwable ignore) {
      ReflectUtil.print(ignore);
    }
    return null;
  }

  public static Object invokeStaticMethod(final String clazz, final String methodName, Object[] args, Class<?>[] parameterTypes)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    try {
      return invokeStaticMethod(Class.forName(clazz), methodName, args, parameterTypes);
    } catch (Throwable ignore) {
    }
    return null;
  }


  public static Object invokeStaticMethod(final Class clazz, final String methodName, Object... args)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    args = ReflectUtil.nullToEmpty(args);
    final Class<?>[] parameterTypes = ReflectUtil.toClass(args);
    return invokeStaticMethod(clazz, methodName, args, parameterTypes);
  }

  public static Object invokeStaticMethod(final Class clazz, final String methodName, Object[] args, Class<?>[] parameterTypes)
    throws NoSuchMethodException, IllegalAccessException,
    InvocationTargetException {
    parameterTypes = ReflectUtil.nullToEmpty(parameterTypes);
    args = ReflectUtil.nullToEmpty(args);
    final Method method = getMatchingAccessibleMethod(clazz,
      methodName, parameterTypes);
    if (method == null) {
      throw new NoSuchMethodException("No such accessible method: "
        + methodName + "() on object: "
        + clazz.getName());
    }
    return method.invoke(null, args);
  }

  public static <T> T invokeConstructor(final Class<T> cls, Object... args)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
    InstantiationException {
    args = ReflectUtil.nullToEmpty(args);
    final Class<?> parameterTypes[] = ReflectUtil.toClass(args);
    return invokeConstructor(cls, args, parameterTypes);
  }

  public static <T> T invokeConstructor(final Class<T> cls, Object[] args, Class<?>[] parameterTypes)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
    InstantiationException {
    args = ReflectUtil.nullToEmpty(args);
    parameterTypes = ReflectUtil.nullToEmpty(parameterTypes);
    final Constructor<T> ctor = getMatchingAccessibleConstructor(cls, parameterTypes);
    if (ctor == null) {
      throw new NoSuchMethodException("No such accessible constructor on object: " + cls.getName());
    }
    return ctor.newInstance(args);
  }

  public static <T> Constructor<T> getMatchingAccessibleConstructor(final Class<T> cls, final Class<?>... parameterTypes) {
    Validate.isTrue(cls != null, "class cannot be null");
    // see if we can find the constructor directly
    // most of the time this works and it's much faster
    try {
      final Constructor<T> ctor = cls.getConstructor(parameterTypes);
      MemberUtil.setAccessibleWorkaround(ctor);
      return ctor;
    } catch (final NoSuchMethodException e) { // NOPMD - Swallow
    }
    Constructor<T> result = null;
    /*
     * (1) Class.getConstructors() is documented to return Constructor<T> so as
     * long as the array is not subsequently modified, everything's fine.
     */
    final Constructor<?>[] ctors = cls.getConstructors();

    // return best match:
    for (Constructor<?> ctor : ctors) {
      // compare parameters
      if (MemberUtil.isAssignable(parameterTypes, ctor.getParameterTypes(), true)) {
        // get accessible version of constructor
        ctor = getAccessibleConstructor(ctor);
        if (ctor != null) {
          MemberUtil.setAccessibleWorkaround(ctor);
          if (result == null
            || MemberUtil.compareParameterTypes(ctor.getParameterTypes(), result
            .getParameterTypes(), parameterTypes) < 0) {
            // temporary variable for annotation, see comment above (1)
            @SuppressWarnings("unchecked") final Constructor<T> constructor = (Constructor<T>) ctor;
            result = constructor;
          }
        }
      }
    }
    return result;
  }

  private static <T> Constructor<T> getAccessibleConstructor(final Constructor<T> ctor) {
    Validate.isTrue(ctor != null, "constructor cannot be null");
    return MemberUtil.isAccessible(ctor) && isAccessible(ctor.getDeclaringClass()) ? ctor : null;
  }

  private static boolean isAccessible(final Class<?> type) {
    Class<?> cls = type;
    while (cls != null) {
      if (!Modifier.isPublic(cls.getModifiers())) {
        return false;
      }
      cls = cls.getEnclosingClass();
    }
    return true;
  }

}
