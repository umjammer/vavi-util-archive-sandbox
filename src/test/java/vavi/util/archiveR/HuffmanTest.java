/*
 * https://github.com/prog-ai/ArchivR
 *
 * http://opensource.org/licenses/mit-license.php
 */

package vavi.util.archiveR;

/**
 * HuffmanTest.
 */
public class HuffmanTest {

    /**
     * Sample client that calls {@code compress()} if the command-line
     * argument is "-" an {@code expand()} if it is "+".
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        if (args[0].equals("-")) Huffman.compress(System.in, System.out);
        else if (args[0].equals("+")) Huffman.expand(System.in, System.out);
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
