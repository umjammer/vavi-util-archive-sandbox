/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.d88;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import vavi.util.archive.Archive;
import vavi.util.archive.Entry;


/**
 * N88DiskBasic �� {@link java.util.zip.ZipFile} �݂����Ȃ��̂ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 *          0.01 021123 nsano rename class <br>
 *          0.02 021123 nsano independ of D88 <br>
 */
public class N88DiskBasicFile implements Archive {

    /** */
    private static String encoding = "MS932";
    
    /** */
    private Map<String,Entry> entries = new HashMap<String,Entry>();
    /** */
    private DiskImage diskImage;
    /** */
    private InputStream is;
    /** */
    private String name;

    /**
     *
     */
    public N88DiskBasicFile(String name) throws IOException {
        this(new BufferedInputStream(new FileInputStream(name)));
        this.name = name;
    }

    /**
     *
     */
    public N88DiskBasicFile(File file) throws IOException {
        this(new BufferedInputStream(new FileInputStream(file)));
        this.name = file.getPath();
    }

    /**
     *
     */
    private N88DiskBasicFile(InputStream is) throws IOException {

        this.is = is;
        this.diskImage = DiskImage.Factory.readFrom(is);

System.err.println("-fname----:aREP   m: SC");
    	// �f�B���N�g��
    	//  1D(5inch)	Track 18           Sector 1 - 12
    	//  2D(5inch)	Track 18 Surface 1 Sector 1 - 12
    	//  2D(8inch)	Track 35 Surface 0 Sector 1 - 22
    	// 2D �̂� TODO ���̑�
    	for (int i = 0; i < 12; i++) {
    	    byte[] data = diskImage.readData(18, 1, i + 1);
    	    for (int j = 0; j < 16; j++) {
        		switch (data[j * 16]) {
        		case 0x00:
System.err.println("killed");
        		    break;
        		case (byte) 0xff:
//System.err.println("not used");
        		    break;
                default:
                    String name = new String(data, j * 16, 6, encoding) + "." +
                                  new String(data, j * 16 + 6, 3, encoding);
                    N88DiskBasicEntry entry = new N88DiskBasicEntry(name,
                                                                    data[j * 16 + 9],
                                                                    data[j * 16 + 10] & 0xff);
                    entries.put(name, entry);
entry.print();
System.err.println();
                    break;
        		}
    	    }
    	}

    	// ID Sector
    	// 1D(5inch) Track 18 Sector 13
    	//  2D(5inch)	Track 18 Surface 1 Sector 13
    	//  2D(8inch)	Track 35 Surface 0 Sector 23
    	// 0x00		�f�B�X�N�S�̂̑��� @see Entry.attribte
    	// 0x01		��x�� OPEN �ł���t�@�C����
    	// 0x02 - 0xff	BASIC Text
    }

    /** */
    public String getName() {
        if (name == null) {
            return is.toString();
        } else {
            return name;
        }
    }

    /**
     *
     */
    public Entry[] entries() {
        Entry[] result = new Entry[entries.size()];
        Iterator<Entry> i = entries.values().iterator();
        int c = 0;
        while (i.hasNext()) {
            result[c++] = i.next(); 
        }
        return result;
    }

    /**
     * �t�@�C�����̃G���g���̐���Ԃ��܂��B
     */
    public int size() {
        return entries.size();
    }

    /**
     *
     */
    public void close() throws IOException {
        is.close();
    }

    /**
     *
     */
    public Entry getEntry(String name) {
        return entries.get(name);
    }

    /**
     * Cluster
     * <pre>
     *  1D (5inch)	Cluster = Track * 2 + Sector / 9
     *			Track   = Cluster / 2
     *			Sector  = (Cluster % 2) * 8 + 1 [~ 8]
     *  2D (5inch)	Cluster = Track * 4 + Surface * 2 + Sector / 9
     *			Track   = Cluster / 4
     *			Surface = (Cluster % 4) / 2
     *			Sector  = (Cluster % 2) * 8 + 1 [~ 8]
     *  2D (8inch)	Cluster = Track * 2 + Surface
     *			Track   = Cluster / 2
     *			Surface = Cluster % 2
     *			Sector  = 1 [~ 26]
     * </pre>
     * TODO 2D �̂�
     */
    private byte[][] readCluster(int cluster) {
    	int track   = cluster / 4;
    	int surface = (cluster % 4) / 2;
    	int sector  = (cluster % 2) * 8 + 1;
    
    	byte[][] data = new byte[8][];
    
    	for (int i = 0; i < 8; i++) {
    	    data[i] = diskImage.readData(track, surface, sector + i);
//System.err.println(Integer.toHexString(cluster)+": "+track+", "+surface+", "+(sector+i));
    	}

    	return data;
    }

    /**
     * @param entry
     */
    public InputStream getInputStream(Entry entry) {
    	// FAT
    	//  1D(5inch)	Track 18           Sector 14, 15, 16 (all same)
    	//  2D(5inch)	Track 18 Surface 1 Sector 14, 15, 16 (all same)
    	//  2D(8inch)	Track 35 Surface 0 Sector 24, 25, 26 (all same)
    	// TODO 2D �̂�
    	byte[] data = diskImage.readData(18, 1, 14);
    
    	ByteArrayOutputStream os = new ByteArrayOutputStream();
    
    	int c = ((int[]) entry.getExtra())[1]; // startCluster
//System.err.print(" " + Integer.toHexString(nc));

    	while (true) {

            int nc = data[c] & 0xff;

            byte[][] tmp = readCluster(c);

            // TODO 2D �̂�
            int max = (nc < 0xc1) ? 8 : nc & 0x1f;
            for (int i = 0; i < max; i++) {
                os.write(tmp[i], 0, tmp[i].length);
            }

//System.err.print(" " + Integer.toHexString(c) + "(" + max + ")");

            if (nc > 0xc0) {
                break;
            }

    	    c = nc;
    	}
//System.err.println();

    	return new ByteArrayInputStream(os.toByteArray());
    }

    //-------------------------------------------------------------------------

    /**
     * java N88DiskBasicFile file
     */
    public static void main(String[] args) throws Exception {

        N88DiskBasicFile disk = new N88DiskBasicFile(args[0]);
System.err.println(disk);

//    	Enumeration e = disk.entries();
//    	while (e.hasMoreElements()) {
//    	    int n = 0;
//            N88DiskBasicEntry entry = (N88DiskBasicEntry) e.nextElement();
//    	    InputStream is = disk.getInputStream(entry);
////Debug.dump(is);
//top:	    while (true) {
//System.err.println("---- " + n++ + " ----");
//                for (int y = 0; y < 16; y++) {
//                    for (int x = 0; x < 16; x++) {
//            			int c = is.read();
//            			if (c == -1) {
//            			    break top;
//                        }
//            			String s = "0" + Integer.toHexString(c).toUpperCase();
//            			System.err.print(s.substring(s.length() - 2) + " ");
//        		    }
//        		    System.err.println();
//        		}
//        		System.err.println();
//    	    }
//    	}
    }
}

/* */
