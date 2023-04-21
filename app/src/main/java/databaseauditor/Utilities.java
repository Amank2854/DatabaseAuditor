package databaseauditor;

import java.lang.reflect.Method;

public class Utilities {
    String camelToSnakeCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    int getElapsedTime(String className, String methodName, Object[] params) {
        try {
            Class<?> clazz = Class.forName(className);
            return 1;
            // Method method = clazz.getMethod(methodName, params.getClass());
            // long startTime = System.nanoTime();
            // method.invoke(clazz, params);
            // long endTime = System.nanoTime();
            // return (int) (endTime - startTime);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            return -1;
        }
    }
}