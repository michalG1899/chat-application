package org.example.chat_client.file;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FileData implements Serializable {
    private String fileName;
    private byte[] data;
    private String recipient;
}
