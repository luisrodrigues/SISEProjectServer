package com.insure.server;

import java.sql.Timestamp;
import java.util.Date;

public class Document {

    private final int uuid;
    private String content;
    private Timestamp timestamp;

    private String digitalSignature; // <- do this: userIdentifier used here

    public Document(int uuid, String content, String userId){
        Date date = new Date();

        this.uuid = uuid;
        this.content = content;
        this.timestamp = new Timestamp(date.getTime());
    }

    public void setContent(String newContent) {
        this.content = newContent;
    }

    //add more attributes
    public String toString(){
        return "Document{uuid: " + this.uuid + ", description: " + this.content + ", timestamp: " + this.timestamp +"}";
    }
}
