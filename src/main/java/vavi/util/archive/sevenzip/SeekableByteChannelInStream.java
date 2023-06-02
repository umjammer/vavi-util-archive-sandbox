/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.sevenzip;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.SevenZipException;


/**
 * An implementation of {@link IInStream} using {@link SeekableByteChannel}.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-09-23 nsano initial version <br>
 */
public class SeekableByteChannelInStream implements IInStream {

    private final SeekableByteChannel sbc;

    /**
     * Constructs instance of the class from seekable byte channel.
     *
     * @param sbc seekable byte channel to use
     */
    public SeekableByteChannelInStream(SeekableByteChannel sbc) {
        this.sbc = sbc;
    }

    @Override
    public synchronized long seek(long offset, int seekOrigin) throws SevenZipException {
        try {
            switch (seekOrigin) {
            case SEEK_SET:
                sbc.position(offset);
                break;

            case SEEK_CUR:
                sbc.position(sbc.position() + offset);
                break;

            case SEEK_END:
                sbc.position(sbc.size() + offset);
                break;

            default:
                throw new RuntimeException("Seek: unknown origin: " + seekOrigin);
            }

            return sbc.position();
        } catch (IOException e) {
            throw new SevenZipException("Error while seek operation", e);
        }
    }

    @Override
    public synchronized int read(byte[] data) throws SevenZipException {
        try {
            int read = sbc.read(ByteBuffer.wrap(data));
            if (read == -1) {
                return 0;
            } else {
                return read;
            }
        } catch (IOException e) {
            throw new SevenZipException("Error reading random access file", e);
        }
    }

    @Override
    public synchronized void close() throws IOException {
        sbc.close();
    }
}
