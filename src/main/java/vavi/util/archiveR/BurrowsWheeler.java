/*
 * https://github.com/prog-ai/ArchivR
 *
 * http://opensource.org/licenses/mit-license.php
 */

package vavi.util.archiveR;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import edu.princeton.cs.algs4.Queue;
import vavi.io.InputEngine;
import vavi.io.InputEngineOutputStream;


/**
 * BurrowsWheeler.
 */
public class BurrowsWheeler {

    private static final int R = 256;

    /** apply Burrows-Wheeler transform, reading from standard input and writing to standard output */
    public static void transform(InputStream is, OutputStream os) {
        BinaryInputStream in = new BinaryInputStream(is);
        BinaryOutputStream out = new BinaryOutputStream(os);
        transform(in.readString(), out);
        out.flush();
    }

    /**
     * the transform proper. it needs the whole input at once, because every
     * rotation of {@code s} takes part in the sort.
     */
    private static void transform(String s, BinaryOutputStream out) {
        char[] c = s.toCharArray();
        CircularSuffixArray sufArr = new CircularSuffixArray(s);
        for (int i = 0; i < sufArr.length(); i++) if (sufArr.index(i) == 0) out.write(i);
        for (int i = 0; i < sufArr.length(); i++)
            out.write(c[(sufArr.index(i) + sufArr.length() - 1) % sufArr.length()]);
    }

    static class TransformOutputStream extends FilterOutputStream {
        TransformOutputStream(OutputStream os) throws IOException {
            super(new InputEngineOutputStream(new InputEngine() {
                // don't wrap in BinaryInputStream: the engine stream returns -1
                // when it's temporarily drained, which is not a real EOF
                InputStream in;
                final BinaryOutputStream out = new BinaryOutputStream(os);
                /** nothing can be written until the last byte is in, see {@link #transform(String, BinaryOutputStream)} */
                final ByteArrayOutputStream pending = new ByteArrayOutputStream();
                final byte[] buf = new byte[1024];

                @Override
                public void initialize(InputStream inputStream) throws IOException {
                    if (this.in != null) {
                        throw new IOException("Already initialized");
                    } else {
                        this.in = inputStream;
                    }
                }

                @Override
                public void execute() throws IOException {
                    int r;
                    while ((r = in.read(buf, 0, buf.length)) > 0) {
                        pending.write(buf, 0, r);
                    }
                }

                @Override
                public void finish() throws IOException {
                    execute(); // whatever was written since the last flush
                    transform(pending.toString(StandardCharsets.ISO_8859_1), out);
                    out.close();
                }
            }));
        }
    }

    /** apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard output */
    @SuppressWarnings("unchecked")
    public static void inverseTransform(InputStream is, OutputStream os) {
        BinaryInputStream in = new BinaryInputStream(is);
        BinaryOutputStream out = new BinaryOutputStream(os);
        int first = in.readInt();
        char[] c = in.readString().toCharArray();
        int[] next = new int[c.length];
        char[] sortedIn = c.clone();
        char[] aux = new char[c.length];
        int[] count = new int[R + 1];
        int counts = first;
        Queue<Integer>[] q = (Queue<Integer>[]) new Queue[R];

        for (int i = 0; i < c.length; i++) count[sortedIn[i] + 1]++;
        for (int r = 0; r < R; r++) count[r + 1] += count[r];
        for (int i = 0; i < c.length; i++) aux[count[sortedIn[i]]++] = sortedIn[i];
        for (int i = 0; i < c.length; i++) sortedIn[i] = aux[i];
        for (int i = 0; i < R; i++) q[i] = new Queue<>();
        for (int i = 0; i < c.length; i++) q[c[i]].enqueue(i);
        for (int i = 0; i < c.length; i++) next[i] = q[sortedIn[i]].dequeue();
        for (int i = 0; i < c.length; i++) {
            out.write(sortedIn[counts]);
            counts = next[counts];
        }
        out.flush();
    }
}
