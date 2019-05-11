/*
 * unsit - Macintosh StuffIt file extractor
 *
 * April 3, 1989
 */

package vavi.util.stuffit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jp.gr.java_conf.dangan.util.lha.CRC16;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import vavi.util.Debug;
import vavi.util.StringUtil;


/**
 * This program will unpack a Macintosh StuffIt file into separate files.
 * The data fork of a StuffIt file contains both the data and resource
 * forks of the packed files.  The program will unpack each Mac file into
 * separate .data, .rsrc., and .info files that can be downloaded to a
 * Mac using macput.  The program is much like the "unpit" program for
 * breaking apart Packit archive files.
 *
 * ***** IMPORTANT *****
 * To extract StuffIt files that have been compressed with the Lempel-Ziv
 * compression method, unsit pipes the data through the "compress"
 * program with the appropriate switches, rather than incorporate the
 * uncompression routines within "unsit".  Therefore, it is necessary to
 * have the "compress" program on the system and in the search path to
 * make "unsit" work.  "Compress" is available from the comp.sources.unix
 * archives.
 *
 * The program syntax is much like unpit and macput/macget, with some added
 * options:
 *
 * unsit [-rdulvqfm] stuffit-file.data
 *
 * The -r and -d flags will cause only the resource and data forks to be
 * written.  The -u flag will cause only the data fork to be written and
 * to have carriage return characters changed to Unix newline characters.
 * The -l flag will make the program only list the files in the StuffIt
 * file.  The -v flag causes the program to list the names, sizes, type,
 * and creators of the files it is writing.  The -q flag causes it to
 * list the name, type and size of each file and wait for a 'y' or 'n'
 * for either writing that file or skipping it, respectively.  The -m
 * flag is used when the input file in in the MacBinary format instead of
 * three separate .data, .info, and .rsrc files.  It causes the program
 * to skip the 128 byte MacBinary header before looking for the StuffIt
 * header.
 *
 * Version 1.5 of the unsit supports extracting files and folders as
 * implemented by StuffIt 1.5's "Hierarchy Maintained Folder" feature.
 * Each folder is extracted as a subdirectory on the Unix system with the
 * files in the folder placed in the corresponding subdirectory.  The -f
 * option can be used to "flatten" out the hierarchy and unsit will store
 * all the files in the current directory.  If the query option (-q) is
 * used and a "n" response is given to a folder name, none of the files
 * or folders in that folder will be extraced.
 *
 * Some of the program is borrowed from the macput.c/macget.c programs.
 * Many, many thanks to Raymond Lau, the author of StuffIt, for including
 * information on the format of the StuffIt archives in the
 * documentation.  Several changes and enhancements supplied by David
 * Shanks (cde@atelabs.UUCP) have been incorporated into the program for
 * doing things like supporting System V and recognizing MacBinary files.
 * I'm always glad to receive advice, suggestions, or comments about the
 * program so feel free to send whatever you think would be helpful
 *
 * TODO 未完成
 *
 * @author Allan G. Weber weber%brand.usc.edu@oberon.usc.edu ...sdcrdcf!usc-oberon!brand!weber
 * @version 1.5c, for StuffIt 1.5 August 3, 1989
 */
public class StuffIt {

    /** 22 bytes */
    class SitHdr {
        /** = 'SIT!' -- for verification */
        String signature;
        /** number of files in archive */
        int numFiles;
        /** length of entire archive incl. */
        long    arcLength;
        /**
         * hdr. -- for verification
         * rLau -- for verification
         */
        String signature2;
        /** version number */
        byte version;
        byte[] reserved = new byte[7];
    }

    /** 112 bytes */
    class FileHdr {
        /** rsrc fork compression method */
        byte compRMethod;
        /** data fork compression method */
        byte compDMethod;
        /** a STR63 */
        byte[] fName = new byte[64];
        /** file type */
        String fType;
        /** er... */
        String fCreator;
        /**
         * copy of Finder flags.  For our
         * purposes, we can clear:
         * busy,onDesk
         */
        short fndrFlags;
        long creationDate;
        /** !restored-compat w/backup prgms */
        long modDate;
        /** decompressed lengths */
        long rsrcLength;
        long dataLength;
        /** compressed lengths */
        long compRLength;
        long compDLength;
        /** crc of rsrc fork */
        int rsrcCRC;
        /** crc of data fork */
        int dataCRC;
        byte[] reserved = new byte[6];
        /** crc of file header */
        int hdrCRC;
    }

    /*
     * file format is:
     *    sitArchiveHdr
     *        file1Hdr
     *            file1RsrcFork
     *            file1DataFork
     *        file2Hdr
     *            file2RsrcFork
     *            file2DataFork
     *        .
     *        .
     *        .
     *        fileNHdr
     *            fileNRsrcFork
     *            fileNDataFork
     */

    /* compression methods */
    /** just read each byte and write it to archive */
    private static final int noComp = 0;
    /** RLE compression */
    private static final int rleComp = 1;
    /** LZW compression */
    private static final int lzwComp = 2;
    /** Huffman compression */
    private static final int hufComp = 3;

    /** bit set if encrypted.  ex: encrypted+lpzComp */
//  private static final int encrypted = 16;

    /** marks start of a new folder */
    private static final int startFolder = 32;
    /** marks end of the last folder "started" */
    private static final int endFolder = 33;

    /* all other numbers are reserved */

    /*
     * The following defines the name of the compress program that is used for
     * the uncompression of Lempel-Ziv compressed files.
     * If the path is set up to include the right directory, this should work.
     */
    private static final String COMPRESS = "compress";

    private static final int IOBUFSIZ = 4096;

    private static final int MACBINHDRSIZE =128;

    private static final int INIT_CRC = 0;

    private static final int INFOBYTES = 128;

    private static final int BYTEMASK = 0xff;

    private static final int S_SIGNATURE = 0;
    private static final int S_NUMFILES = 4;
    private static final int S_ARCLENGTH = 6;
    private static final int S_SIGNATURE2 = 10;
//  private static final int S_VERSION = 14;
    private static final int SITHDRSIZE = 22;

    private static final int F_COMPRMETHOD = 0;
    private static final int F_COMPDMETHOD = 1;
    private static final int F_FNAME = 2;
    private static final int F_FTYPE = 66;
    private static final int F_CREATOR = 70;
    private static final int F_FNDRFLAGS = 74;
    private static final int F_CREATIONDATE = 76;
    private static final int F_MODDATE = 80;
    private static final int F_RSRCLENGTH = 84;
    private static final int F_DATALENGTH = 88;
    private static final int F_COMPRLENGTH = 92;
    private static final int F_COMPDLENGTH = 96;
    private static final int F_RSRCCRC = 100;
    private static final int F_DATACRC = 102;
    private static final int F_HDRCRC = 110;
    private static final int FILEHDRSIZE = 112;

    private static final int F_NAMELEN = 63;
    /** 63 + strlen(".info") + 1 */
//  private static final int I_NAMELEN = 69;

    /** The following are copied out of macput.c/macget.c */
    private static final int I_NAMEOFF = 1;
    /** 65 <. 80 is the FInfo structure */
    private static final int I_TYPEOFF = 65;
    private static final int I_AUTHOFF = 69;
    private static final int I_FLAGOFF = 73;
//  private static final int I_LOCKOFF = 81;
    private static final int I_DLENOFF = 83;
    private static final int I_RLENOFF = 87;
    private static final int I_CTIMOFF = 91;
    private static final int I_MTIMOFF = 95;

    /** offset to byte with Inited flag */
//  private static final int INITED_OFF    = I_FLAGOFF;
    /** mask to '&' with byte to reset it */
//  private static final int INITED_MASK = ~1;

    private static final int TEXT = 0;
    private static final int DATA = 1;
    private static final int RSRC = 2;
    private static final int FULL = 3;
    private static final int DUMP = 4;

    private static final int NODECODE = 0;
    private static final int DECODE = 1;

    private static final int H_ERROR = -1;
    private static final int H_EOF = 0;
    private static final int H_WRITE = 1;
    private static final int H_SKIP = 2;

    class Node {
        int flag;
        int byte_;
        Node one;
        Node zero;
    }

    /** 512 should be big enough */
    private Node[] nodelist = new Node[512];
    private int nodeptr;

    private SitHdr sitHdr = new SitHdr();

    private String f_info;
    private String f_data;
    private String f_rsrc;

    private byte[] info = new byte[INFOBYTES];
    private byte[] mname = new byte[F_NAMELEN + 1];
    private byte[] uname = new byte[F_NAMELEN + 1];
    private byte[] iobuf = new byte[IOBUFSIZ];

    private int mode;
    private boolean txtmode;
    private boolean listonly, verbose, query, flatten;
    private int bit, numfiles, depth;
    private boolean chkcrc;
    private InputStream infp;

    /** */
    public static void main(String[] args) throws Exception {
        new StuffIt(args);
    }

    /** */
    StuffIt(String[] args) throws IOException, ParseException {
        boolean macbin = false;

        mode = FULL;
        flatten = false;
        numfiles = 0;
        depth = 0;

        Options options = new Options();
        options.addOption("r", false, "only the resource forks to be written");
        options.addOption("d", false, "only data forks to be written");
        options.addOption("u", false, "only the data fork to be written and to have carriage return characters changed to Unix newline characters");
        options.addOption("l", false, "l");
        options.addOption("q", false, "q");
        options.addOption("v", false, "v");
        options.addOption("x", false, "x");
        options.addOption("f", false, "f");
        options.addOption("m", false, "input file in in the MacBinary format");
        options.addOption("?", false, "display help");

        CommandLineParser parser = new BasicParser();

        CommandLine cl = parser.parse(options, args);

        if (cl.hasOption("?")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("unsit", options, true);
            return;
        }

        if (cl.hasOption("r")) {
            mode = RSRC;
        }
        if (cl.hasOption("d")) {
            mode = DATA;
        }
        if (cl.hasOption("u")) {
            mode = TEXT;
        }
        if (cl.hasOption("l")) {
            listonly = true;
        }
        if (cl.hasOption("q")) {
            query = true;
        }
        if (cl.hasOption("v")) {
            verbose = true;
        }
        if (cl.hasOption("x")) {
            mode = DUMP;
        }
        if (cl.hasOption("m")) {
            macbin = true;
        }
        if (cl.hasOption("f")) {
            flatten = true;
        }

        try {
            infp = new FileInputStream(cl.getArgs()[0]);
        } catch (IOException e) {
System.err.println("Can't open input file \"" + cl.getArgs()[0] + "\"");
            System.exit(1);
        }

        if (macbin) {
            try {
                infp.skip(MACBINHDRSIZE);
            } catch (IOException e) {
System.err.println("Can't skip over MacBinary header");
                System.exit(1);
            }
        }

        if (readSitHdr(sitHdr) == 0) {
System.err.println("Can't read file header");
            System.exit(1);
        }
//System.out.println("numfiles=" + sitHdr.numFiles + ", arclength=" + sitHdr.arcLength);

        int status = extract("", false);
        System.exit((status < 0) ? 1 : 0);
    }

    /**
     * Extract all files from the current folder.
     * @param parent    name of parent folder
     * @param skip    true to skip all files and folders in this one
     *                  false to extract them
     * @return 1 if came an endFolder record
     *         0 if EOF
     *         -1 if error (bad fileHdr, bad file, etc.)
     */
    private int extract(String parent, boolean skip) throws IOException {
        FileHdr filehdr = new FileHdr();
        int status, rstat;
        boolean skipit;
        String name = null;

        while (true) {
            rstat = readFileHdr(filehdr, skip);
            if (rstat == H_ERROR || rstat == H_EOF) {
                status = rstat;
                break;
            }
//System.err.println("compr=" + filehdr.compRMethod + ", compd=" + filehdr.compDMethod + ", rsrclen=" + filehdr.compRLength + ", datalen=" + filehdr.compDLength + ", rsrccrc=" + filehdr.rsrcCRC + ", datacrc=" + filehdr.dataCRC);

            skipit = rstat == H_SKIP;

            if (filehdr.compRMethod == endFolder &&
                filehdr.compDMethod == endFolder) {
                status = 1;        // finished with this folder
                break;
            } else if (filehdr.compRMethod == startFolder &&
                     filehdr.compDMethod == startFolder) {
                if (!listonly && rstat == H_WRITE && !flatten) {
                    File file = new File(new String(uname));
                    if (!file.exists()) {    // directory doesn't exist
                        if (!file.mkdirs()) {
System.err.println("Can't create subdirectory " + uname);
                            return -1;
                        }
                    } else {        // something exists with this name
                        if (!file.isDirectory()) {
System.err.println("Directory name " + uname + " already in use");
                            return -1;
                        }
                    }
//                  if (chdir(uname) == -1) {
//System.err.println("Can't chdir to " + uname);
//                      return -1;
//                  }
                    name = parent + ":" + new String(uname);
                }
                depth++;
                status = extract(new String(name), skipit);
                depth--;
                if (status != 1) {
                    break;        // problem with folder
                }
                if (depth == 0)    { // count how many top-level files done
                    numfiles++;
                }
//              if (!flatten) {
//                  chdir("..");
//              }
            } else {
                if ((status = extractFile(filehdr, skipit)) != 1) {
                    break;
                }
                if (depth == 0)    { // count how many top-level files done
                    numfiles++;
                }
            }
            if (numfiles == sitHdr.numFiles) {
                break;
            }
        }
        return status;
    }

    /** */
    private String getAsciizString(byte[] b) {
        int l = 0;
        while (b[l] != 0x00) {
            l++;
        }
        return new String(b, 0, l);
    }

    /** */
    private int extractFile(FileHdr fh, boolean skip) throws IOException {
        int crc;
        FileOutputStream fp = null;

        // figure out what file names to use and what to do
        if (!listonly && !skip) {
            switch (mode) {
            case FULL:         // do both rsrc and data forks
                f_data = getAsciizString(uname) + ".data";
                f_rsrc = getAsciizString(uname) + ".rsrc";
                f_info = getAsciizString(uname) + ".info";
                break;
            case RSRC:         // rsrc fork only
                f_rsrc = getAsciizString(uname) + ".rsrc";
                break;
            case DATA:         // data fork only
            case TEXT:
                f_data = getAsciizString(uname);
                break;
            case DUMP:         // for debugging, dump data as is
                f_data = getAsciizString(uname) + ".ddump";
                f_rsrc = getAsciizString(uname) + ".rdump";
                fh.compRMethod = fh.compDMethod = noComp;
                break;
            }
        }

        if (f_info != null && checkAccess(f_info) != -1) {
            try {
                fp = new FileOutputStream(new String(f_info));
            } catch (IOException e) {
                System.err.println(e);
                System.exit(1);
            }
            fp.write(info, 0, INFOBYTES);
            fp.close();
        }

        if (f_rsrc != null) {
            txtmode = false;
            crc = writeFile(f_rsrc, fh.compRLength,
                            fh.rsrcLength, fh.compRMethod);
            if (chkcrc && (fh.rsrcCRC != crc)) {
                System.err.println("CRC error on resource fork: need 0x" + StringUtil.toHex4(fh.rsrcCRC) + ", got 0x" + StringUtil.toHex4(crc));
                return -1;
            }
        } else {
            infp.skip(fh.compRLength); // SEEK_CUR
        }
        if (f_data != null) {
            txtmode = (mode == TEXT);
            crc = writeFile(f_data, fh.compDLength,
                            fh.dataLength, fh.compDMethod);
            if (chkcrc && (fh.dataCRC != crc)) {
                System.err.println("CRC error on data fork: need 0x" + StringUtil.toHex4(fh.dataCRC) + ", got 0x" + StringUtil.toHex4(crc));
                return -1;
            }
        } else {
            infp.skip(fh.compDLength); // SEEK_CUR
        }
        return 1;
    }

    /** */
    private int readSitHdr(SitHdr s) throws IOException {
        byte[] temp = new byte[FILEHDRSIZE];
        int count = 0;

        while (true) {
            if (infp.read(temp, 0, SITHDRSIZE) != SITHDRSIZE) {
System.err.println("Can't read file header");
                return 0;
            }

Debug.dump(temp);
            if (new String(temp, S_SIGNATURE,  4).equals("SIT!") &&
                new String(temp, S_SIGNATURE2, 4).equals("rLau")) {
                s.numFiles = get2(temp, S_NUMFILES);
                s.arcLength = get4(temp, S_ARCLENGTH);
                return 1;
            }

            if (++count == 2) {
System.err.println("Not a StuffIt file");
                return 0;
            }

            if (infp.read(temp, SITHDRSIZE, FILEHDRSIZE - SITHDRSIZE) !=
                FILEHDRSIZE - SITHDRSIZE) {
System.err.println("Can't read file header");
                return 0;
            }

            if (new String(temp, I_TYPEOFF, 4).equals("SIT!") &&
                new String(temp, I_AUTHOFF, 4).equals("SIT!")) {
                // MacBinary format
                // Skip over header
                infp.skip(INFOBYTES - FILEHDRSIZE); // SEEK_CUR
            }
        }
    }

    /**
     * readfilehdr - reads the file header for each file and the folder start
     * and end records.
     *
     * returns: H_ERROR = error
     * H_EOF   = EOF
     * H_WRITE = write file/folder
     * H_SKIP  = skip file/folder
     */
    private int readFileHdr(FileHdr f, boolean skip) throws IOException {
        int crc;
        int i, n;
        boolean write_it;
        boolean isfolder;
        byte[] hdr = new byte[FILEHDRSIZE];
        int ch;
        int mp, up;
        int tp;
        byte[] temp = new byte[10];

        for (i = 0; i < INFOBYTES; i++) {
            info[i] = '\0';
        }

        // read in the next file header, which could be folder start/end record
        n = infp.read(hdr, 0, FILEHDRSIZE);
        if (n == 0) {            // return 0 on EOF
            return H_EOF;
        } else if (n != FILEHDRSIZE) {
System.err.println("Can't read file header");
            return H_ERROR;
        }

        // check the CRC for the file header
        crc = INIT_CRC;
        crc = updateCrc(crc, hdr, FILEHDRSIZE - 2);
        f.hdrCRC = get2(hdr, F_HDRCRC);
        if (f.hdrCRC != crc) {
System.err.println("Header CRC mismatch: got 0x" + StringUtil.toHex4(f.hdrCRC) + ", need 0x" + StringUtil.toHex4(crc));
            return H_ERROR;
        }

        // grab the name of the file or folder
        n = hdr[F_FNAME] & BYTEMASK;
        if (n > F_NAMELEN) {
            n = F_NAMELEN;
        }
        info[I_NAMEOFF] = (byte) n;
        System.arraycopy(hdr, F_FNAME + 1, info, I_NAMEOFF + 1, n);
        System.arraycopy(hdr, F_FNAME + 1, mname, 0, n);
        mname[n] = '\0';
        // copy to a string with no illegal Unix characters in the file name
        mp = 0;
        up = 0;
        while ((ch = mname[mp++]) != '\0') {
            if (ch <= ' ' ||
                ch > '~' ||
                "/!()[]*<>?\\\"$\';&`".indexOf(ch) != -1) {
                ch = '_';
            }
            uname[up++] = (byte) ch;
        }
        uname[up] = '\0';

        // get lots of other stuff from the header
        f.compRMethod = hdr[F_COMPRMETHOD];
        f.compDMethod = hdr[F_COMPDMETHOD];
        f.rsrcLength = get4(hdr, F_RSRCLENGTH);
        f.dataLength = get4(hdr, F_DATALENGTH);
        f.compRLength = get4(hdr, F_COMPRLENGTH);
        f.compDLength = get4(hdr, F_COMPDLENGTH);
        f.rsrcCRC = get2(hdr, F_RSRCCRC);
        f.dataCRC = get2(hdr, F_DATACRC);

        // if it's an end folder record, don't need to do any more
        if (f.compRMethod == endFolder && f.compDMethod == endFolder) {
            return H_WRITE;
        }

        // prepare an info file in case its needed

        System.arraycopy(hdr, F_FTYPE,        info, I_TYPEOFF, 4);
        System.arraycopy(hdr, F_CREATOR,      info, I_AUTHOFF, 4);
        System.arraycopy(hdr, F_FNDRFLAGS,    info, I_FLAGOFF, 2);
        System.arraycopy(hdr, F_DATALENGTH,   info, I_DLENOFF, 4);
        System.arraycopy(hdr, F_RSRCLENGTH,   info, I_RLENOFF, 4);
        System.arraycopy(hdr, F_CREATIONDATE, info, I_CTIMOFF, 4);
        System.arraycopy(hdr, F_MODDATE,      info, I_MTIMOFF, 4);

        isfolder = f.compRMethod == startFolder && f.compDMethod == startFolder;

        // list the file name if verbose or listonly mode, also if query mode
        if (skip) {    // skip = 1 if skipping all in this folder
            write_it = false;
        } else {
            write_it = true;
            if (listonly || verbose || query) {
                for (i = 0; i < depth; i++) {
                    System.out.print(' ');
                }
                if (isfolder) {
                    System.out.println("Folder: \"" + uname + "\"");
                } else {
                    System.out.println("name=\"" + uname +
                                       "\", type=" + new String(hdr, F_FTYPE, 4) +
                                       ", author=" + new String(hdr, F_CREATOR, 4) +
                                       ", data=" + f.dataLength +
                                       ", rsrc=" + f.rsrcLength);
                }
                if (query) {    // if querying, check with the boss
                    System.out.println(" ? ");
                    System.in.read(temp, 0, temp.length - 1);
                    tp = 0;
                    write_it = false;
                    while (temp[tp] != '\0') {
                        if (temp[tp] == 'y' || temp[tp] == 'Y') {
                            write_it = true;
                            break;
                        } else {
                            tp++;
                        }
                    }
                } else {        // otherwise, terminate the line
                    System.out.println();
                }
            }
        }
        return write_it ? H_WRITE : H_SKIP;
    }

    /** return 0 if OK to write on file fname, -1 otherwise */
    private int checkAccess(String fname) throws IOException {
        byte[] temp = new byte[10];
        int tp;

        if (!new File(fname).exists()) {
            return 0;
        } else {
            System.out.println(fname + " exists. Overwrite? ");
            System.in.read(temp, 0, temp.length);
            tp = 0;
            while (temp[tp] != '\0') {
                if (temp[tp] == 'y' || temp[tp] == 'Y') {
                    return 0;
                } else {
                    tp++;
                }
            }
        }
        return -1;
    }

    /** */
    private int writeFile(String fname, long ibytes, long obytes, byte type)
        throws IOException {

        int i, n, ch, lastch = 0;
        OutputStream outf = null;

        int crc = INIT_CRC;
        chkcrc = true;        // usually can check the CRC

        if (checkAccess(new String(fname)) == -1) {
            infp.skip(ibytes);    // SEEK_CUR
            chkcrc = false;    // inhibit crc check if file not written
            return -1;
        }

        switch (type) {
        case noComp:         // no compression
            try {
                outf = new FileOutputStream(new String(fname));
            } catch (IOException e) {
                System.err.println(e);
                System.exit(1);
            }
            while (ibytes > 0) {
                n = (ibytes > IOBUFSIZ) ? IOBUFSIZ : (int) ibytes;
                n = infp.read(iobuf, 0, n);
                if (n == 0) {
                    break;
                }
                crc = updateCrc(crc, iobuf, n);
                outf.write(iobuf, 0, n);
                ibytes -= n;
            }
            outf.close();
            break;
        case rleComp:         // run length encoding
            try {
                outf = new FileOutputStream(new String(fname));
            } catch (IOException e) {
                System.err.println(e);
                System.exit(1);
            }
            while (ibytes > 0) {
                ch = infp.read();
                ibytes--;
                if (ch == 0x90) {    // see if its the repeat marker
                    n = infp.read();    // get the repeat count
                    ibytes--;
                    if (n == 0) {    // 0x90 was really an 0x90
                        iobuf[0] = (byte) 0x90;
                        crc = updateCrc(crc, iobuf, 1);
                        outc(iobuf, 1, outf);
                    } else {
                        n--;
                        for (i = 0; i < n; i++) {
                            iobuf[i] = (byte) lastch;
                        }
                        crc = updateCrc(crc, iobuf, n);
                        outc(iobuf, n, outf);
                    }
                } else {
                    iobuf[0] = (byte) ch;
                    crc = updateCrc(crc, iobuf, 1);
                    lastch = ch;
                    outc(iobuf, 1, outf);
                }
            }
            outf.close();
            break;
        case lzwComp:         // LZW compression
            String temp = COMPRESS + " -d -c -n -b 14 ";
            if (txtmode) {
                temp += "| tr \'\\015\' \'\\012\' ";
                chkcrc = false;        // can't check CRC in this case
            }
            temp += "> '";
            temp += new String(fname);
            temp += "'";
            try {
                outf = new FileOutputStream(temp);
            } catch (IOException e) {
                System.err.println(e);
                System.exit(1);
            }
            while (ibytes > 0) {
                n = (ibytes > IOBUFSIZ) ? IOBUFSIZ : (int) ibytes;
                n = infp.read(iobuf, 0, n);
                if (n == 0) {
                    break;
                }
                outc(iobuf, n, outf);
                ibytes -= n;
            }
            outf.close();
            if (chkcrc) {
                InputStream is = null;
                try {
                    // read the file to get CRC value
                    is = new FileInputStream(new String(fname));
                } catch (IOException e) {
                    System.err.println(e);
                    System.exit(1);
                }
                while (true) {
                    n = is.read(iobuf, 0, IOBUFSIZ);
                    if (n == 0) {
                        break;
                    }
                    crc = updateCrc(crc, iobuf, n);
                }
                is.close();
            }
            break;
        case hufComp:         // Huffman compression
            try {
                outf = new FileOutputStream(new String(fname));
            } catch (IOException e) {
                System.err.println(e);
                System.exit(1);
            }
            nodeptr = 0;
            bit = 0;        // put us on a byte boundary
            read_tree();
            while (obytes > 0) {
                n = (obytes > IOBUFSIZ) ? IOBUFSIZ : (int) obytes;
                for (i = 0; i < n; i++)
                    iobuf[i] = (byte) getHuffByte(DECODE);
                crc = updateCrc(crc, iobuf, n);
                outc(iobuf, n, outf);
                obytes -= n;
            }
            outf.close();
            break;
        default:
System.err.println("Unknown compression method: " + type);
            chkcrc = false;    // inhibit crc check if file not written
            return -1;
        }

        return crc & 0xffff;
    }

    /** */
    private void outc(byte[] p, int n, OutputStream fp) throws IOException {
        int p1 = 0;
        if (txtmode) {
            for (int i = 0; i < n; i++, p1++) {
                if ((p[p1] & BYTEMASK) == '\r') {
                    p[p1] = '\n';
                }
            }
        }
        fp.write(p, 0, n);
    }

    /** */
    private long get4(byte[] b, int p) {
        long value = 0;

        for (int i = 0; i < 4; i++) {
            value <<= 8;
            value |= (b[p] & BYTEMASK);
            p++;
        }
        return value;
    }

    /** */
    private int get2(byte[] b, int p) {
        int value = 0;

        for (int i = 0; i < 2; i++) {
            value <<= 8;
            value |= (b[p] & BYTEMASK);
            p++;
        }
        return value;
    }

    /**
     * This routine recursively reads the Huffman encoding table and builds
     * and decoding tree.
     */
    private Node read_tree() throws IOException {
        Node np;
        np = nodelist[nodeptr++];
        if (getbit() == 1) {
            np.flag = 1;
            np.byte_ = getHuffByte(NODECODE);
        } else {
            np.flag = 0;
            np.zero = read_tree();
            np.one = read_tree();
        }
        return np;
    }

    /**
     * This routine returns the next bit in the input stream (MSB first)
     */
    private int getbit() throws IOException {
        int b = 0;
        if (bit == 0) {
            b = infp.read();
            bit = 8;
        }
        bit--;
        return (b >> bit) & 1;
    }

    /**
     * This routine returns the next 8 bits.  If decoding is on, it finds the
     * byte in the decoding tree based on the bits from the input stream.  If
     * decoding is not on, it either gets it directly from the input stream or
     * puts it together from 8 calls to getbit(), depending on whether or not
     * we are currently on a byte boundary
     */
    private int getHuffByte(int decode) throws IOException {
        int b;
        if (decode == DECODE) {
            Node np = nodelist[nodeptr];
            while (np.flag == 0) {
                np = (getbit() != 0) ? np.one : np.zero;
            }
            b = np.byte_;
        } else {
            if (bit == 0) {    // on byte boundary?
                b = infp.read();
            } else {        // no, put a byte together
                b = 0;
                for (int i = 8; i > 0; i--) {
                    b = (b << 1) + getbit();
                }
            }
        }
        return b;
    }

    //----

    /** */
    private CRC16 crc16 = new CRC16();

    /** */
    private int updateCrc(int crc, byte[] icp, int count) {
        crc16.update(crc);
        crc16.update(icp, 0, count);
        return (int) crc16.getValue();
    }
}

/* */
