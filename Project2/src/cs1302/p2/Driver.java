package cs1302.p2;

import cs1302.adt.*;

public class Driver {
    
    public static void test(StringList sl) {
        // Testing isEmpty on an empty list
        if (sl.isEmpty()) {
            System.out.println("TEST: isEmpty: Test Passed");
        } else {
            System.out.println("TEST: isEmpty: Test Failed");
            System.exit(0);
        } // if

        // Testing size on an empty list
        if (sl.size() == 0) {
            System.out.println("TEST: size: Test Passed");
        } else {
            System.out.println("TEST: size: Test Failed");
            System.exit(0);
        } // if

        sl.add(0, "two");
        sl.add(0, "one");
        sl.add(0, "zero");

        // Testing size on non-empty list
        if (sl.size() == 3) {
            System.out.println("TEST: size of non-empty: Test Passed");
        } else {
            System.out.println("TEST: size of non-empty: Test Failed");
            System.exit(0);
        } // if

        // Testing whether allows adding empty item
        try {
            sl.add(0, "");
            System.out.println("TEST: cannot add empty: Test Failed");
            System.exit(0);
        } catch (Exception e) {
            System.out.println("TEST: cannot add empty: Test Passed");
        }

        System.out.println("TEST: before gaps: " + sl.toString());

        // Testing whether allow introducing gaps
        try {
            sl.add(4, "five");
            System.out.println("TEST: cannot introduce gaps: Test Failed");
            System.exit(0);
        } catch (Exception e) {
            System.out.println("TEST: cannot introduce gaps: Test Passed");
        }

        sl.clear();
        sl.add(0, "Bread");
        sl.add(0, "Cheese");
        sl.add(1, "Milk");
        sl.add(3, "Ice Cream");

        if (sl.remove(0).equals("Cheese")) {
            System.out.println("TEST: removed item: Test Passed");
        } else {
            System.out.println("TEST: removed item: Test Failed");
            System.exit(0);
        } // if

        if (sl.size() == 3) {
            System.out.println("TEST: size after modifying: Test Passed");
        } else {
            System.out.println("TEST: size after modifying: Test Failed");
            System.exit(0);
        } // if
        
        if (sl.makeString("", ", ", "").equals("Milk, Bread, Ice Cream")) {
            System.out.println("TEST: makestring: Test Passed");
        } else {
            System.out.println("TEST: makestring: Test Failed");
            System.exit(0);
        } // if

        if (sl.get(0).equals("Milk")) {
            System.out.println("TEST: get: Test Passed");
        } else {
            System.out.println("TEST: get: Test Failed");
            System.exit(0);
        } // if

        sl.clear();

        if (sl.size() == 0 && sl.makeString("", ", ", "").equals("")) {
            System.out.println("TEST: clearing: Test Passed");
        } else {
            System.out.println("TEST: clearing: Test Failed");
            System.exit(0);
        } // if

        // Testing whether add allows negative indices
        try {
            sl.add(-1, "hello");
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
            sl.remove(-1);
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
            sl.get(-1);
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
            sl.add(0, null);
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
            sl.add(0, "");
            System.out.println("TEST: cannot add empty: Test Failed");
            System.exit(0);
        } catch (IllegalArgumentException npe) {
            System.out.println("TEST: cannot add empty: Test Passed");
        } catch (Exception e) {
            System.out.println("TEST: cannot add empty: Test Failed");
            System.exit(0);
        } // try-catch-catch

        // Testing slice(0,0)
        if (sl.slice(0, 0).isEmpty()) {
            System.out.println("TEST: slice(0,0): Test Passed");
        } else {
            System.out.println("TEST: slice(0,0): Test Failed");
            System.exit(0);
        } // if

        System.out.print("testAddNegative: ");
        try {
            sl.add(-5, "hello");
            System.out.println("FAIL: expected IOOB; however, no exception was encountered");
            System.exit(0);
        } catch (IndexOutOfBoundsException ioob) {
            System.out.println("PASS: expected IOOB; IOOB was encountered");
        } catch (Throwable e) {
            System.out.println("FAIL: expected IOOB, but got " + e);
            System.exit(0);
        } // try



        System.out.println("TEST: finished testing " + sl.getClass());
    } // test

    public static void main(String[] args) {
        
        test(new ArrayStringList());
        System.out.println("-".repeat(80));
        test(new LinkedStringList());

    } // main
}
