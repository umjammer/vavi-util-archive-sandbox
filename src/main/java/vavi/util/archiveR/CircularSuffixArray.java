/*
 * https://github.com/prog-ai/ArchivR
 *
 * http://opensource.org/licenses/mit-license.php
 */

package vavi.util.archiveR;

import java.util.Arrays;


/**
 * This class provides methods for printing strings and numbers to standard output.
 *
 * @author Dumitru Hanciu
 */
public class CircularSuffixArray {

    private final Integer[] index;

    public CircularSuffixArray(String s) {
        if (s == null)
            throw new IllegalArgumentException("String must not be null.");

        int length = s.length();
        char[] chars = s.toCharArray();
        index = new Integer[length];

        // initialize each array position with its location
        for (int i = 0; i < s.length(); i++)
            index[i] = i;

        // sort the index array using the given comparator
        Arrays.sort(index, (Integer i1, Integer i2) -> {
            int com = chars[i1] - chars[i2];
            for (int count = 0; com == 0 && count < length; count++) {
                int substringAIndex = ++i1 % length;
                int substringBIndex = ++i2 % length;
                char charA = chars[substringAIndex];
                char charB = chars[substringBIndex];
                com = Character.compare(charA, charB);
            }
            return com;
        });
    }

    /**
     * Returns the length of s.
     *
     * @return the length of s
     */
    public int length() {
        return index.length;
    }

    /**
     * Returns index of ith sorted suffix.
     *
     * @param i the ith sorted suffix
     * @return the index of ith sorted suffix
     * @throws IllegalArgumentException if {@code i < 0 || i >= index.length}
     */
    public int index(int i) {
        if (i < 0 || i >= index.length)
            throw new IllegalArgumentException("provided index \"" + i + "\"" +
                                                       " does not satisfy: 0 <= i < length");
        return index[i];
    }
}
