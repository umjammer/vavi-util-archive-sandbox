/*
 * https://github.com/prog-ai/ArchivR
 *
 * http://opensource.org/licenses/mit-license.php
 */

package vavi.util.archiveR;

import edu.princeton.cs.algs4.In;


/**
 * CircularSuffixArrayTest.
 */
public class CircularSuffixArrayTest {

    /**
     * Unit tests the {@code CircularSuffixArray} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        String str = in.readString();
        CircularSuffixArray abra = new CircularSuffixArray(str);
        System.out.println("Length of string is: " + abra.length());
        System.out.println("Indexes are as follows: ");
        for (int i = 0; i < abra.length(); i++) {
            System.out.println(abra.index(i));
        }
    }
}
