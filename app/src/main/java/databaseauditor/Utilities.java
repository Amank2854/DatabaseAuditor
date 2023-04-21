package databaseauditor;

import java.lang.reflect.Method;

public class Utilities {
    public String camelToSnakeCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    public <T> long getElapsedTime(T obj, String methodName, Object[] params, boolean log) {
        try {
            Class<?> clazz = obj.getClass();
            Method methlist[] = clazz.getDeclaredMethods();
            for (int i = 0; i < methlist.length; i++) {
                if (methlist[i].getName().equals(methodName)) {
                    Class<?> pvec[] = methlist[i].getParameterTypes();
                    Method method = clazz.getMethod(methodName, pvec);
                    long startTime = System.nanoTime();
                    int result = (Integer) method.invoke(obj, params);
                    long endTime = System.nanoTime();

                    if (log) {
                        if (result == -1) {
                            System.out.println("ERROR: " + methodName + " failed");
                        } else {
                            System.out.println("INFO: " + methodName + " for " + obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length - 1] + " succeeded in " + (endTime - startTime) + "ns" + " with " + result + " row(s) affected");
                        }
                    }
                    return endTime - startTime;
                }
            }
            return -1;
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return -1;
        }
    }
}