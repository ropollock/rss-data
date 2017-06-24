package com.russpollock.rss.utils;

import java.io.*;

public class File {
    public static String readResource(final InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream)))
        {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        }

        return sb.toString();
    }
}
