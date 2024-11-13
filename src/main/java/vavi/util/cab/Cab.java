/*
 * You may modify, copy, and redistribute this code under the terms of
 * the GNU Library Public License version 2.1, with the exception of
 * the portion of clause 6a after the semicolon (aka the "obnoxious
 * relink clause")
 */

package vavi.util.cab;

import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.getLogger;


/**
 * Represents a CAB file structure.
 *
 * @author Adam Megacz <adam@ibex.org>
 */
public class Cab {

    private static final Logger logger = getLogger(Cab.class.getName());

    /** */
    private final CabHeader header;

    /** */
    private final List<CabFolder> folders = new ArrayList<>();

    /**
     * @return Returns the folders.
     */
    public List<CabFolder> getFolders() {
        return this.folders;
    }

    /**
     * @return Returns the header.
     */
    public CabHeader getHeader() {
        return this.header;
    }

    /** */
    public Cab(InputStream is, int skipHeaders) throws IOException {

logger.log(Level.DEBUG, "before: " + is.available());
        while (skipHeaders > 0) {
            seekMSCF(is);
            skipHeaders--;
        }
logger.log(Level.DEBUG, "after: " + is.available());

        header = new CabHeader();
        header.read(is);
logger.log(Level.DEBUG, header);
header.print(System.err);

logger.log(Level.DEBUG, "header.getFolderNumber: " + header.getFolderNumber());
        for (int i = 0; i < header.getFolderNumber(); i++) {
            CabFolder folder = new CabFolder(header.getPerCFFOLDERReservedSize());
            folder.read(is);
            folders.add(folder);
        }

logger.log(Level.DEBUG, header.getFileNumber());
        for (int i = 0; i < header.getFileNumber(); i++) {
            CabFile file = new CabFile();
            file.read(is);
            int folderIndex = file.getFolderIndex();
            CabFolder folder = folders.get(folderIndex);
            folder.addFile(file);
        }
    }

    /** */
    private static void seekMSCF(InputStream is) throws IOException {
        int state;

        // skip up to and including the 'MSCF' signature
        state = 0;
        while (state != 4) {
            // M
            while ((state == 0) && (is.read() != 0x4d)) {
            }
            state = 1;

            // S
            switch (is.read()) {
            case 0x53:
                state = 2;
                break;
            case 0x4d:
                state = 1;
                continue;
            default:
                state = 0;
                continue;
            }

            // C
            if (is.read() == 0x43) {
                state = 3;
            } else {
                state = 0;
                continue;
            }

            // F
            if (is.read() == 0x46) {
                state = 4;
            } else {
                state = 0;
            }
        }
    }
}
