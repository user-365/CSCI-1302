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
        System.out.println("TEST: finished testing "+sl.getClass());
    }

    public static void main(String[] args) {
        test(new ArrayStringList());
        System.out.println("-".repeat(80));
        test(new LinkedStringList());

    } // main
}
