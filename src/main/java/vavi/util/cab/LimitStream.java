/*
 * You may modify, copy, and redistribute this code under the terms of
 * the GNU Library Public License version 2.1, with the exception of
 * the portion of clause 6a after the semicolon (aka the "obnoxious
 * relink clause")
 */

package vavi.util.cab;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * LimitStream.
 *
 * @author Adam Megacz <adam@ibex.org>
 */
class LimitStream extends FilterInputStream {
    int limit;

    /** */
    public LimitStream(InputStream is, int limit) {
        super(is);
        this.limit = limit;
    }

    /** */
    public int read() throws IOException {
        if (limit == 0) {
            return -1;
        }

        int ret = super.read();
        if (ret != -1) {
            limit--;
        }
        return ret;
    }

    /** */
    public int read(byte[] b, int off, int len) throws IOException {
        if (len > limit) {
            len = limit;
        }
        if (limit == 0) {
            return -1;
        }

        int ret = super.read(b, off, len);
        limit -= ret;
        return ret;
    }
}

/* */
