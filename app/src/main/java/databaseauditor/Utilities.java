package databaseauditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
        List<String> classes = new ArrayList<String>();
        File file = new File(
                System.getProperty("user.dir")
                        + "/src/main/java/databaseauditor/Model/Models.txt");
        BufferedReader bfr = new BufferedReader(new FileReader(file));

        String cur = "";
        while ((cur = bfr.readLine()) != null) {
            classes.add(cur.substring(1, cur.length() - 2));
        }

        bfr.close();

        List<Object> data = new ArrayList<Object>();
        for (String className : classes) {
            data.add(instantiate("databaseauditor.Model." + className));
        }

        return data;
    }

    // Method to get all relationships between the model classes
    public List<List<String>> getRelationships() throws Exception {
        List<List<String>> relationships = new ArrayList<List<String>>();
        File file = new File(
                System.getProperty("user.dir")
                        + "/src/main/java/databaseauditor/Model/Relationships.txt");
        BufferedReader bfr = new BufferedReader(new FileReader(file));

        String cur = "";
        while ((cur = bfr.readLine()) != null) {
            String[] line = cur.substring(0, cur.length() - 1).split(",");
            List<String> relationship = new ArrayList<String>();
            for (String word : line) {
                relationship.add(word.trim().substring(1, word.trim().length() - 1));
            }

            relationships.add(relationship);
        }

        bfr.close();
        return relationships;
    }

    // Method calculate the mean of an array
    public double mean(long arr[], int len) {
        long sum = 0;
        for (int i = 0; i < len; i++) {
            sum += arr[i];
        }

        return (double) sum / (double) len;
    }

    // Method to calculate the median of an array
    public double median(long arr[], int len) {
        if (len % 2 == 0) {
            return (double) (arr[len / 2] + arr[len / 2 - 1]) / 2.0;
        } else {
            return (double) arr[len / 2];
        }
    }

    // Method calculate the standard deviation of an array
    public double standardDeviation(long arr[], int len) {
        double mean = mean(arr, len), sum = 0;
        for (int i = 0; i < len; i++) {
            sum += Math.pow(arr[i] - mean, 2);
        }

        return Math.sqrt(sum / (double) len);
    }

    public void writeResults(long[] postgresTimes, long[] mongoTimes, long[] neoTimes, long[] postgresMemory,
            long[] mongoMemory, long[] neoMemory, int numIterations, String type, List<String> entities)
            throws Exception {
        List<Double> timeMeans = Arrays.asList(this.mean(postgresTimes, numIterations),
                this.mean(mongoTimes, numIterations),
                this.mean(neoTimes, numIterations));
        List<Double> timeMedians = Arrays.asList(this.median(mongoTimes, numIterations),
                this.median(mongoTimes, numIterations),
                this.median(neoTimes, numIterations));
        List<Double> timeStdDevs = Arrays.asList(this.standardDeviation(neoTimes, numIterations),
                this.standardDeviation(mongoTimes, numIterations),
                this.standardDeviation(neoTimes, numIterations));

        List<Double> memoryMeans = Arrays.asList(this.mean(postgresMemory, numIterations),
                this.mean(mongoMemory, numIterations),
                this.mean(neoMemory, numIterations));
        List<Double> memoryMedians = Arrays.asList(this.median(mongoMemory, numIterations),
                this.median(mongoMemory, numIterations),
                this.median(neoMemory, numIterations));
        List<Double> memoryStdDevs = Arrays.asList(this.standardDeviation(neoMemory, numIterations),
                this.standardDeviation(mongoMemory, numIterations),
                this.standardDeviation(neoMemory, numIterations));

        FileWriter writerObj = new FileWriter(System.getProperty("user.dir")
                + "/src/results/results.txt", true);
        
        writerObj.write(numIterations + " \"" + type + "\"");
        if (entities.size() >= 1) {
            writerObj.write(" for: " + entities + "\n");
        } else {
            writerObj.write("\n");
        }

        writerObj.write("Mean execution times: " + timeMeans + "\n");
        writerObj.write("Median execution times: " + timeMedians + "\n");
        writerObj.write("Standard deviation of execution times: " + timeStdDevs + "\n");
        writerObj.write("Mean memory consumption: " + memoryMeans + "\n");
        writerObj.write("Median memory consumption: " + memoryMedians + "\n");
        writerObj.write("Standard deviation of memory consumption: " + memoryStdDevs + "\n\n");
        writerObj.close();
    }
}