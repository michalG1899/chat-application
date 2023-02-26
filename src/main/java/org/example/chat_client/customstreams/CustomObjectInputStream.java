package org.example.chat_client.customstreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class CustomObjectInputStream extends ObjectInputStream {

    public CustomObjectInputStream(InputStream in) throws IOException {
        super(in);
    }
}