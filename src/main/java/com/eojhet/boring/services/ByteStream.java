package com.eojhet.boring.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ByteStream {

    public static byte[] fileStream(File file) throws IOException {
        FileInputStream fl = new FileInputStream(file);

        byte[] arr = new byte[(int)file.length()];

        fl.read(arr);
        fl.close();

        return arr;
    }
}
