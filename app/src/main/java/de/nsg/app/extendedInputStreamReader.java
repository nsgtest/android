package de.nsg.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class extendedInputStreamReader extends InputStreamReader {
    extendedInputStreamReader(InputStream inputstream) {
        super(inputstream);
    }

    String fetch() throws IOException {
        BufferedReader bufferedreader = new BufferedReader(this);
        StringBuilder stringbuilder = new StringBuilder();
        String string;

        while ((string = bufferedreader.readLine()) != null) {
            stringbuilder.append(string);
        }

        bufferedreader.close();

        return stringbuilder.toString();
    }
}