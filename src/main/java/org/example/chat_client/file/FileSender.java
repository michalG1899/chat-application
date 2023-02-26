package org.example.chat_client.file;

import lombok.extern.java.Log;
import org.example.chat_client.message.model.ChatMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Log
public class FileSender {

    private static final String PATH_SEND = "src/main/resources/to-send/";
    private static final String PATH_RECEIVE = "src/main/resources/inbox/";

    static {
        File toSend = new File(PATH_SEND);
        File received = new File(PATH_RECEIVE);
        if (!toSend.exists()) toSend.mkdirs();
        if (!received.exists()) received.mkdirs();
    }

    public static void saveFile(ChatMessage chatMessage) {
        String fileName = chatMessage.getFile().getFileName();
        byte[] fileData = chatMessage.getFile().getData();
        File fileToDownload = new File(PATH_RECEIVE + fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
            fileOutputStream.write(fileData);
            fileOutputStream.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static FileData getFile(String fileName, String recipient) {
        FileData fileData = new FileData();
        try {
            fileData.setFileName(fileName);
            fileData.setRecipient(recipient);
            fileData.setData(Files.readAllBytes(Paths.get(PATH_SEND + fileName)));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return fileData;
    }
}
