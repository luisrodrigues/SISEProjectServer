package com.insure.server;

import java.sql.Timestamp;
import java.util.Date;

public class Document {

    private final int uuid;
    private String content;
    private Timestamp timestamp;
    private String userId;
    private String digitalSignature;

    public Document(int uuid, String content, String userId) {
        Date date = new Date();

        this.uuid = uuid;
        this.content = content;
        this.timestamp = new Timestamp(date.getTime());
        this.userId = userId;
    }

    public String getDigitalSignature() {
        return this.digitalSignature;
    }

    public void sign(String digitalSignature) {
        this.digitalSignature = digitalSignature;
    }

    public void setContent(String newContent) {
        this.content = newContent;
    }

    public String toString(){
        return "Document{uuid: " + this.uuid + ", content: " + this.content + ", timestamp: " + this.timestamp + ", userId: " + this.userId + "}";
    }
}
