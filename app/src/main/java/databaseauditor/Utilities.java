package databaseauditor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utilities {
    // Method to convert camel case to snake case
    public String camelToSnakeCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    // Method to calculate the execution time of a method
    public <T> long getElapsedTime(T obj, String methodName, Object[] params, boolean log) throws Exception {
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
                        System.out.println("INFO: " + methodName + " for "
                                + obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length
                                        - 1]
                                + " succeeded in " + (endTime - startTime) + "ns" + " with " + result
                                + " row(s) affected");
                    }
                }

                return endTime - startTime;
            }
        }

        throw new Exception("ERROR: Execution time could not be calculated");
    }

    // Method to calculate the consumed memory by a method
    public <T> long getConsumedMemory(T obj, String methodName, Object[] params, boolean log) throws Exception {
        Class<?> clazz = obj.getClass();
        Method methlist[] = clazz.getDeclaredMethods();

        for (int i = 0; i < methlist.length; i++) {
            if (methlist[i].getName().equals(methodName)) {
                Class<?> pvec[] = methlist[i].getParameterTypes();
                Method method = clazz.getMethod(methodName, pvec);
                long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                int result = (Integer) method.invoke(obj, params);
                long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                if (log) {
                    if (result == -1) {
                        System.out.println("ERROR: " + methodName + " failed");
                    } else {
                        System.out.println("INFO: " + methodName + " for "
                                + obj.getClass().getName().split("\\.")[obj.getClass().getName().split("\\.").length
                                        - 1]
                                + " succeeded in " + Math.max(endMemory - startMemory, 0) + " bytes" + " with " + result
                                + " row(s) affected");
                    }
                }
                return Math.max(endMemory - startMemory, 0);
            }
        }

        throw new Exception("ERROR: Consumed memory could not be calculated");
    }

    // Method to instantiate a class
    public Object instantiate(String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        Constructor<?> ctor = clazz.getConstructors()[0];

        int numParam = ctor.getParameterCount();
        Object[] args = new Object[numParam];
        for (int i = 0; i < numParam; i++) {
            args[i] = "1";
        }

        return ctor.newInstance(args);
    }

    // Method to get all instances of the model classes
    public List<Object> getModels() throws Exception {
        // TODO: Get all classes from the Model package instead
        List<String> classes = Arrays.asList("Actor", "Address", "Category", "City", "Country", "Customer", "Film",
                "FilmActor", "FilmCategory", "Inventory", "Language", "Payment", "Rental", "Staff",
                "Store");

        List<Object> data = new ArrayList<Object>();
        for (String className : classes) {
            data.add(instantiate("databaseauditor.Model." + className));
        }

        return data;
    }
}