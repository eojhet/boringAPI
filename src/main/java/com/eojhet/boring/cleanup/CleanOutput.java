package com.eojhet.boring.cleanup;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

public class CleanOutput {

    public static void cleanIfOver(int maxFiles) {
        File folder = new File("output/");
        if (folder.listFiles().length > maxFiles) {
            int toDelete = maxFiles/2;

            File[] files = folder.listFiles();
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return Long.valueOf(o1.lastModified()).compareTo(o2.lastModified());
                }
            });

            for (int i = 0; i < toDelete; i++) {
                files[i].delete();
            }
        }
    }

    public static void main(String[] args) {
        CleanOutput.cleanIfOver(15);

    }
}
