/*
 * https://github.com/prog-ai/ArchivR
 *
 * http://opensource.org/licenses/mit-license.php
 */

package vavi.util.archiveR;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.NoSuchElementException;

import vavi.io.InputEngine;
import vavi.io.InputEngineOutputStream;
import vavi.io.OutputEngine;
import vavi.io.OutputEngineInputStream;


/**
 * MoveToFront.
 */
public class MoveToFront {

    /** alphabet size of extended ASCII */
    private static final int R = 256;

    /** */
    public MoveToFront() {
    }

    /** MoveToFront trie node */
    private static class Node {
        private final char c;
        private Node n;

        Node(char c) {
            this.c = c;
        }
    }

    private Node first;
    private Node current;

    private void init() {
        first = new Node('\0');
        current = first;
        for (short i = 1; i < R; i++) {
            current.n = new Node((char) i);
            current = current.n;
        }
    }

    /** apply move-to-front encoding, reading from standard input and writing to standard output */
    public void encode(InputStream is, OutputStream os) {
        init();
        // read the input
        BinaryInputStream in = new BinaryInputStream(is);
        BinaryOutputStream out = new BinaryOutputStream(os);
        char[] input = in.readString().toCharArray();
        for (char c : input) {
            current = first;
            char count = 0;
            for (int j = 0; j < R; j++) {
                if (c == current.c) {
                    out.write(count);
                    break;
                } else if (current.n != null && c == current.n.c) {
                    out.write(++count);
                    Node tmp = current.n;
                    current.n = tmp.n;
                    tmp.n = first;
                    first = tmp;
                    break;
                } else {
                    current = current.n;
                    count++;
                }
            }
        }
        out.flush();
    }

    public static class EncodeInputStream extends FilterInputStream {
        EncodeInputStream(InputStream is) throws IOException {
            super(new OutputEngineInputStream(new OutputEngine() {
                final MoveToFront moveToFront = new MoveToFront();
                final BinaryInputStream in = new BinaryInputStream(is);
                BinaryOutputStream out;

                @Override
                public void initialize(OutputStream outputStream) throws IOException {
                    if (this.out != null) {
                        throw new IOException("Already initialized");
                    } else {
                        this.out = new BinaryOutputStream(outputStream);
                        moveToFront.init();
                    }
                }

                @Override
                public void execute() throws IOException {
                    if (in.isEmpty()) {
                        throw new EOFException();
                    }

                    char c = in.readChar();
                    moveToFront.current = moveToFront.first;
                    char count = 0;
                    for (int j = 0; j < R; j++) {
                        if (c == moveToFront.current.c) {
                            out.write(count);
                            break;
                        } else if (moveToFront.current.n != null && c == moveToFront.current.n.c) {
                            out.write(++count);
                            Node tmp = moveToFront.current.n;
                            moveToFront.current.n = tmp.n;
                            tmp.n = moveToFront.first;
                            moveToFront.first = tmp;
                            break;
                        } else {
                            moveToFront.current = moveToFront.current.n;
                            count++;
                        }
                    }
                }

                @Override
                public void finish() throws IOException {
                    out.flush();
                }
            }));
        }
    }

    /** apply move-to-front decoding, reading from standard input and writing to standard output */
    public void decode(InputStream is, OutputStream os) {
        init();
        BinaryInputStream in = new BinaryInputStream(is);
        BinaryOutputStream out = new BinaryOutputStream(os);
        char[] input = in.readString().toCharArray();
        for (char c : input) {
            current = first;
            char count = c;
            for (short j = 0; j < count - 1; j++) current = current.n;
            if ((int) count == 0) {
                out.write(current.c);
            } else {
                out.write(current.n.c);
                Node tmp = current.n;
                current.n = tmp.n;
                tmp.n = first;
                first = tmp;
            }
        }
        out.flush();
    }

    /** */
    public static class DecodeOutputStream extends FilterOutputStream {
        DecodeOutputStream(OutputStream os) throws IOException {
            super(new InputEngineOutputStream(new InputEngine() {
                final MoveToFront moveToFront = new MoveToFront();
                BinaryInputStream in;
                final BinaryOutputStream out = new BinaryOutputStream(os);

                @Override
                public void initialize(InputStream inputStream) throws IOException {
                    if (this.in != null) {
                        throw new IOException("Already initialized");
                    } else {
                        this.in = new BinaryInputStream(inputStream);
                        moveToFront.init();
                    }
                }

                @Override
                public void execute() throws IOException {
                    char c = in.readChar();
                    moveToFront.current = moveToFront.first;
                    char count = c;
                    for (short j = 0; j < count - 1; j++) moveToFront.current = moveToFront.current.n;
                    if ((int) count == 0) {
                        out.write(moveToFront.current.c);
                    } else {
                        out.write(moveToFront.current.n.c);
                        Node tmp = moveToFront.current.n;
                        moveToFront.current.n = tmp.n;
                        tmp.n = moveToFront.first;
                        moveToFront.first = tmp;
                    }
                }

                @Override
                public void finish() throws IOException {
                    out.flush();
                }
            }));
        }
    }
}

