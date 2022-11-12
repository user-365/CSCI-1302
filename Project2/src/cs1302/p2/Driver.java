package cs1302.p2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;

import cs1302.adt.*;
import cs1302.oracle.FancyOracleStringList;

/**
 * Driver is the driver (main method) class for testing the two different
 * implementations of {@code BaseStringList}.
 *
 * <p>
 * Last substantial revision: 2022-11-11
 *
 * @author user-365
 */
public class Driver {
    
    private static Method[] ASLMethods = ArrayStringList.class.getDeclaredMethods();
    private static Method[] LSLMethods = LinkedStringList.class.getDeclaredMethods();
    private static Method[] BSLMethods = BaseStringList.class.getDeclaredMethods();
    
    private static char randomChar() {
        Random r = new Random();
        return (char) (r.nextInt(5) + 'A');
        // A-E
    } // randomChar()

    private static FancyOracleStringList randomStringList() {
        FancyOracleStringList fosl = new FancyOracleStringList();
        for (int i = 0; i < 10; i++) {
            fosl.append(String.valueOf(randomChar()));
        } // for
        return fosl;
    } // randomStringList

    /**
     * https://stackoverflow.com/a/427088
     */
    private static class NestedFor {

        public static interface IAction {
            public Object act(int[] indices);
        } // interface NestedFor.IAction

        private final int begin; // begin of int index
        private final int end; // end of int index (inclusive TK)
        private final IAction action; // what is to be done in innermost loop

        private Stream<Object> actionReturnVal;
        private Stream.Builder<Object> returnValBuilder = Stream.builder();

        public NestedFor(int begin, int end, IAction action) {
            this.begin = begin;
            this.end = end;
            this.action = action;
        } // Constructor

        public void nFor(int depth) { // outward-facing
            n_for(0, new int[0], depth); // 0-based index, depth exclusive
        } // nFor(int)

        private void n_for(int level, int[] indices, int maxLevel) { // recursive helper
            if (level == maxLevel) { // at innermost loop
                returnValBuilder.accept(action.act(indices));
            } else { // at one of outer loops
                int newLevel = level + 1; // go down 1 level/nest once
                int[] newIndices = new int[newLevel]; // corresponding (n+1)-tuple
                System.arraycopy(indices, 0, newIndices, 0, level);
                newIndices[level] = begin; // initialize last/innermost index
                while (newIndices[level] < end) { // the actual for-loop
                    n_for(newLevel, newIndices, maxLevel);
                    ++newIndices[level];
                } // while
            } // if-else
        } // nFor(int, int[], int)
    } // class NestedFor

    /**
     * Surface check.
     * @param dubious  FSL to be tested
     * @param trusted FSL has correct behavior
     * @param methodName dubious FSL's method to be tested
     * @return true if implemented/dubious method's behavior matches the trusted's method behavior
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public static boolean compareAllCombos(FancyStringList dubious,
                                            FancyOracleStringList trusted,
                                            Method method) {
        // First, get number of parameters (of FSL methods) by type
        Class<?>[] paramTypes = method.getParameterTypes();
        int ints = 0, strings = 0, sls = 0; // three (expected) types of params
        for (Class<?> type : paramTypes) {
            switch (type.getSimpleName()) {
                case "int": // primitive yeah yeah
                    ints++;
                    break;
                case "String":
                    strings++;
                    break;
                case "StringList":
                    sls++;
                    break;
            } // switch
        } // foreach
        final int[] paramCount = { ints, strings, sls };
        // Then, invoke each (FSL) method, passing in the right parameters
        NestedFor.IAction testAction = new NestedFor.IAction() {
            @Override
            public Object act(int[] indices) {
                boolean checkReturnValue = false, checkSideEffect = false, checkException = false;
                Character item = paramCount[1] > 0 ? randomChar() : null; // "String" item for FSL
                FancyStringList other = paramCount[2] > 0 ? randomStringList() : null; // FSL to be added
                // each method `m` requires different args,
                // so use a variable-length Stream (which apparently casts to a varargs)
                System.out.println();
                System.out.println("-".repeat(20));
                System.out.println("indices array: " + Arrays.toString(indices));
                System.out.println("non-numeric args: " + item + ", " + other);
                Stream<Object> args = Stream.concat(Arrays.stream(indices).boxed(), Stream.of(item, other))
                        .flatMap(Stream::of)
                        .filter(java.util.Objects::nonNull);
                // returnValues elements will be .equal()ed
                Object[] returnValues = new Object[2];
                if (!method.getReturnType().getName().equals("void")) { // not void
                    System.out.println("v=============v");
                    System.out
                            .println("TESTING: " + method.getName() + "(" + args.map(obj -> obj.toString()).collect(Collectors.joining(",")) + ")");
                    try {
                        returnValues[0] = method.invoke(trusted, args);
                        try {
                            returnValues[1] = method.invoke(dubious, args);
                        } catch (Exception TesteeException) {
                            return false; // dubious throws, trusted doesn't
                        } // try-catch
                        checkReturnValue = returnValues[0].equals(returnValues[0]);
                    } catch (Exception trustedException) {
                        try {
                            returnValues[1] = method.invoke(dubious, args);
                            return false; // trusted throws, dubious doesn't
                        } catch (Exception TesteeException) {
                            checkException = trustedException.getClass() == TesteeException.getClass();
                        } // try-catch
                    } // try-catch
                    checkSideEffect = dubious.toString().equals(trusted.toString());
                    System.out.println("v=============v");
                    System.out.println("Check R/T: " + (checkReturnValue || checkException));
                    System.out.println("Check S.E.: " + checkSideEffect);
                    return Boolean.valueOf((checkReturnValue || checkException) && checkSideEffect);
                    // either return or throw
                } else { // method is void
                    System.out.println("v=============v\n");
                    System.out
                            .println("TESTING: " + method.getName() + "()");
                    try {
                        // invoke, expect exception
                        method.invoke(trusted, args);
                        try {
                            method.invoke(dubious, args);
                        } catch (Exception TesteeException) {
                            return false; // dubious throws, trusted doesn't
                        } // try-catch
                    } catch (Exception trustedException) {
                        try {
                            method.invoke(dubious, args);
                            return false; // trusted throws, dubious doesn't
                        } catch (Exception TesteeException) {
                            checkException = trustedException.getClass() == TesteeException.getClass();
                        } // try-catch
                    } // try-catch
                    checkSideEffect = dubious.toString().equals(trusted.toString());
                    System.out.println("v=============v");
                    System.out.println("Check R: " + (checkException));
                    System.out.println("Check S.E.: " + checkSideEffect);
                    return Boolean.valueOf(checkException && checkSideEffect);
                } // if-else
            } // act (implemented)
        }; // testAction
        NestedFor comparisonFor = new NestedFor(-1, 5, testAction);
        comparisonFor.nFor(paramCount[0]);
        // ^deciding how deep we want to nest based on number of params.
        // TK what to do for non-numeric params?
        BinaryOperator<Boolean> or = (Boolean result, Boolean element) -> result || element;
        comparisonFor.actionReturnVal = comparisonFor.returnValBuilder.build();
        return comparisonFor.actionReturnVal.map(b -> (Boolean) b)
                .<Boolean>reduce(Boolean.FALSE, or, or);
    } // compare

    public static void test(FancyStringList fsl) throws NoSuchMethodException, SecurityException {
        // Testing isEmpty on an empty list
        if (fsl.isEmpty()) {
            System.out.println("TEST: isEmpty: Test Passed");
        } else {
            System.out.println("TEST: isEmpty: Test Failed");
            System.exit(0);
        } // if

        // Testing size on an empty list
        if (fsl.size() == 0) {
            System.out.println("TEST: size: Test Passed");
        } else {
            System.out.println("TEST: size: Test Failed");
            System.exit(0);
        } // if

        fsl.add(0, "two");
        fsl.add(0, "one");
        fsl.add(0, "zero");

        // Testing size on non-empty list
        if (fsl.size() == 3) {
            System.out.println("TEST: size of non-empty: Test Passed");
        } else {
            System.out.println("TEST: size of non-empty: Test Failed");
            System.exit(0);
        } // if

        // Testing whether allows adding empty item
        try {
            fsl.add(0, "");
            System.out.println("TEST: cannot add empty: Test Failed");
            System.exit(0);
        } catch (Exception e) {
            System.out.println("TEST: cannot add empty: Test Passed");
        }

        System.out.println("TEST: before gaps: " + fsl.toString());

        // Testing whether allow introducing gaps
        try {
            fsl.add(4, "five");
            System.out.println("TEST: cannot introduce gaps: Test Failed");
            System.exit(0);
        } catch (Exception e) {
            System.out.println("TEST: cannot introduce gaps: Test Passed");
        }

        fsl.clear();
        fsl.add(0, "Bread");
        fsl.add(0, "Cheese");
        fsl.add(1, "Milk");
        fsl.add(3, "Ice Cream");

        if (fsl.remove(0).equals("Cheese")) {
            System.out.println("TEST: removed item: Test Passed");
        } else {
            System.out.println("TEST: removed item: Test Failed");
            System.exit(0);
        } // if

        if (fsl.size() == 3) {
            System.out.println("TEST: size after modifying: Test Passed");
        } else {
            System.out.println("TEST: size after modifying: Test Failed");
            System.exit(0);
        } // if
        
        if (fsl.makeString("", ", ", "").equals("Milk, Bread, Ice Cream")) {
            System.out.println("TEST: makestring: Test Passed");
        } else {
            System.out.println("TEST: makestring: Test Failed");
            System.exit(0);
        } // if

        if (fsl.get(0).equals("Milk")) {
            System.out.println("TEST: get: Test Passed");
        } else {
            System.out.println("TEST: get: Test Failed");
            System.exit(0);
        } // if

        fsl.clear();

        if (fsl.size() == 0 && fsl.makeString("", ", ", "").equals("")) {
            System.out.println("TEST: clearing: Test Passed");
        } else {
            System.out.println("TEST: clearing: Test Failed");
            System.exit(0);
        } // if

        // Testing whether add allows negative indices
        try {
            fsl.add(-1, "hello");
            System.out.println("TEST: cannot negative index: Test Failed");
            System.exit(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("TEST: cannot negative index: Test Passed");
        } catch (NullPointerException npe) {
            System.out.println("TEST: cannot negative index: Test Failed");
            System.exit(0);
        } // try-catch-catch

        // Testing whether remove allows negative indices
        try {
            fsl.remove(-1);
            System.out.println("TEST: cannot negative index: Test Failed");
            System.exit(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("TEST: cannot negative index: Test Passed");
        } catch (NullPointerException npe) {
            System.out.println("TEST: cannot negative index: Test Failed");
            System.exit(0);
        } // try-catch-catch

        // Testing whether get allows negative indices
        try {
            fsl.get(-1);
            System.out.println("TEST: cannot negative index: Test Failed");
            System.exit(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("TEST: cannot negative index: Test Passed");
        } catch (NullPointerException npe) {
            System.out.println("TEST: cannot negative index: Test Failed");
            System.exit(0);
        } // try-catch-catch

        // Testing whether add throws NPE
        try {
            fsl.add(0, (String) null);
            System.out.println("TEST: cannot add null: Test Failed");
            System.exit(0);
        }catch (NullPointerException npe) {
            System.out.println("TEST: cannot add null: Test Passed");
        } catch (Exception e) {
            System.out.println("TEST: cannot add null: Test Failed");
            System.exit(0);
        } // try-catch-catch

        // Testing whether add throws IAE
        try {
            fsl.add(0, "");
            System.out.println("TEST: cannot add empty: Test Failed");
            System.exit(0);
        } catch (IllegalArgumentException npe) {
            System.out.println("TEST: cannot add empty: Test Passed");
        } catch (Exception e) {
            System.out.println("TEST: cannot add empty: Test Failed");
            System.exit(0);
        } // try-catch-catch

        // Testing slice(0,0)
        if (fsl.slice(0, 0).isEmpty()) {
            System.out.println("TEST: slice(0,0): Test Passed");
        } else {
            System.out.println("TEST: slice(0,0): Test Failed");
            System.exit(0);
        } // if
        
        // Testing negative insertion
        try {
            fsl.add(-5, "hello");
            System.out.println("TEST: add @ negative: Test Failed");
            System.exit(0);
        } catch (IndexOutOfBoundsException ioob) {
            System.out.println("TEST: add @ negative: Test Passed");
        } catch (Throwable e) {
            System.out.println("TEST: add @ negative: Test Failed" + e);
            System.exit(0);
        } // try

        // Testing copy constructor
        FancyOracleStringList list = new FancyOracleStringList();
        list.add(0, "a");
        list.add(1, "b");
        list.add(2, "c");
        // deep copy of different type using copy constructor
        try {
            fsl = fsl.getClass().getConstructor(StringList.class).newInstance(list);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // before; continued from previous example
        if (list.toString().equals(fsl.toString())) {
            System.out.println("TEST: copy constructor: Test Passed");
        } else {
            System.out.println("TEST: copy constructor: Test Failed");
            System.exit(0);
        } // if
                
        // Testing adding (again)
        // modify each list
        fsl.add(1, "x");
        if (fsl.toString().equals("[a, x, b, c]")) {
            System.out.println("TEST: adding (again): Test Passed");
        } else {
            System.out.println("TEST: adding (again): Test Failed");
            System.exit(0);
        } // if
        fsl.add(2, "y");
        if (fsl.toString().equals("[a, x, y, b, c]")) {
            System.out.println("TEST: adding (again): Test Passed");
        } else {
            System.out.println("TEST: adding (again): Test Failed");
            System.exit(0);
        } // if

        // first list
        fsl.clear();
        fsl.add(0, "a");
        fsl.add(1, "b");
        // second list
        list = new FancyOracleStringList();
        list.add(0, "0");
        list.add(1, "1");
        // insert second list into first list
        fsl.add(1, list);
        if (fsl.toString().equals("[a, 0, 1, b]")) {
            System.out.println("TEST: insert second list: Test Passed");
        } else {
            System.out.println("TEST: insert second list: Test Failed");
            System.exit(0);
        } // if

        // insert modified first list into itself
        fsl.add(1, fsl);
        if (fsl.toString().equals("[a, a, 0, 1, b, 0, 1, b]")) {
            System.out.println("TEST: insert w/ self-reference: Test Passed");
        } else {
            System.out.println("TEST: insert w/ self-reference: Test Failed");
            System.exit(0);
        } // if
        
        try {
            fsl = fsl.getClass().getConstructor(StringList.class).newInstance(new ArrayStringList(
                new String[]{"a", "b", "c", "d", "e", "f", "g"}
                ));
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // copy constructor (again)
        if (fsl.toString().equals("[a, b, c, d, e, f, g]")) {
            System.out.println("TEST: copy constructor (again): Test Passed");
        } else {
            System.out.println("TEST: copy constructor (again): Test Failed");
            System.exit(0);
        } // if

        // slicing 0
        StringList slice0 = fsl.slice(0, 7, 1);
        if (slice0.toString().equals("[a, b, c, d, e, f, g]")) {
            System.out.println("TEST: slicing 0: Test Passed");
        } else {
            System.out.println("TEST: slicing 0: Test Failed");
            System.exit(0);
        } // if
        
        // slicing 1
        StringList slice1 = fsl.slice(1, 7, 2);
        if (slice1.toString().equals("[b, d, f]")) {
            System.out.println("TEST: slicing 1: Test Passed");
        } else {
            System.out.println("TEST: slicing 1: Test Failed");
            System.exit(0);
        } // if

        // slicing 2
        StringList slice2 = fsl.slice(0, 7, 2);
        if (slice2.toString().equals("[a, c, e, g]")) {
            System.out.println("TEST: slicing 2: Test Passed");
        } else {
            System.out.println("TEST: slicing 2: Test Failed");
            System.exit(0);
        } // if

        // slicing 3
        StringList slice3 = fsl.slice(0, 7, 3);
        if (slice3.toString().equals("[a, d, g]")) {
            System.out.println("TEST: slicing 3: Test Passed");
        } else {
            System.out.println("TEST: slicing 3: Test Failed");
            System.exit(0);
        } // if

        // slicing 4
        StringList slice4 = fsl.slice(1, 1, 1);
        if (slice4.toString().equals("[]")) {
            System.out.println("TEST: slicing 3: Test Passed");
        } else {
            System.out.println("TEST: slicing 3: Test Failed");
            System.exit(0);
        } // if

        fsl.clear();
        // isEmpty (again)
        if (fsl.isEmpty()) {
            System.out.println("TEST: isEmpty (again): Test Passed");
        } else {
            System.out.println("TEST: isEmpty (again): Test Failed");
            System.exit(0);
        } // if
        
        ArrayStringList s2 = new ArrayStringList(
            new String[] { "1", "2", "3", "4" }
            );

        // Testing new prepend/append
        fsl.prepend(s2);
        fsl.append(s2);
        if (fsl.toString().equals("[1, 2, 3, 4, 1, 2, 3, 4]")) {
            System.out.println("TEST: new prepend: Test Passed");
        } else {
            System.out.println("TEST: new prepend: Test Failed");
            System.exit(0);
        } // if
        
        // Testing new add
        fsl.add(0, fsl);
        if (fsl.toString().equals("[1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4]")) {
            System.out.println("TEST: new prepend: Test Passed");
        } else {
            System.out.println("TEST: new prepend: Test Failed");
            System.exit(0);
        } // if

        // Testing reverse
        fsl = fsl.reverse();
        if (fsl.toString().equals("[4, 3, 2, 1, 4, 3, 2, 1, 4, 3, 2, 1, 4, 3, 2, 1]")) {
            System.out.println("TEST: reverse: Test Passed");
        } else {
            System.out.println("TEST: reverse: Test Failed");
            System.exit(0);
        } // if

        // Testing contains
        if (fsl.contains(0, "1") && fsl.contains(0, "2") && fsl.contains(0, "3") && fsl.contains(0, "4") && !fsl.contains(0, "5")) {
            System.out.println("TEST: contains: Test Passed");
        } else {
            System.out.println("TEST: contains: Test Failed");
            System.exit(0);
        } // if

        // Testing indexOf
        for (int x = 0; x < fsl.size() - 4; x++) {
            int oneToFour = -~x % 4 == 0 ? 4 : -~x % 4;
            int index = fsl.indexOf(x, Integer.toString(oneToFour));
            if (Integer.valueOf(fsl.get(index)) + Integer.valueOf(fsl.get(x)) == 5) {
                continue;
            } else {
                System.out.println("TEST: indexOf: Test Failed");
                System.exit(0);
            } // if
        } // for
        System.out.println("TEST: indexOf: Test Passed");

        System.out.println("TEST: finished testing " + fsl.getClass().getSimpleName());

    } // test

    public static void autotest(FancyStringList fsl) {

        FancyOracleStringList trusted = new FancyOracleStringList();

        char FSLclassAlpha = fsl.getClass().getSimpleName().charAt(0);
        boolean FSLclass = FSLclassAlpha == 'A';

        for (Method m : ArrayUtils.addAll(FSLclass ? ASLMethods : LSLMethods, BSLMethods)) {
            boolean b = compareAllCombos(fsl, trusted, m);
            System.out.println(Character.toString(FSLclassAlpha) + "SL: " + m.getName() + "(" + Arrays.stream(
                    m.getParameterTypes()).map(type -> type.getSimpleName()).collect(Collectors.joining(",")) + ")"
                    + ": " + b + "\n^=============^");
            if (!b) {
                System.out.println("Error in class: " + Character.toString(FSLclassAlpha) + "SL");
                System.exit(0);
            } // if
        } // foreach

        System.out.println("TEST: finished testing " + fsl.getClass().getSimpleName());

    } // autoTest(FancyStringList)

    public static void main(String[] args) {
        
        try {
            autotest(new ArrayStringList());
            System.out.println("-".repeat(80));
            autotest(new LinkedStringList());
        } catch (SecurityException /*| NoSuchMethodException*/ e) {
            e.printStackTrace();
        }
        

    } // main
}
