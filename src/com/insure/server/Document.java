package com.insure.server;

import java.sql.Timestamp;
import java.util.Date;

public class Document {

    //possible document types
    private static final String TYPE_EXPERT = "Expert Report";
    private static final String TYPE_POLICE = "Police Report";
    private static final String TYPE_MEDICAL = "Medical Report";

    private int uuid;
    private String docType;
    private String content;
    private Timestamp timestamp;
    private String userId;
    private String digitalSignature;

    public Document(int uuid, int typeNr, String content, String userId, String digitalSignature) {
        Date date = new Date();

        this.uuid = uuid;
        this.setType(typeNr);
        this.content = content;
        this.timestamp = new Timestamp(date.getTime());
        this.userId = userId;
        this.digitalSignature = digitalSignature;
    }

    private void setType(int typeNr) {
        if (typeNr == 1) {
            this.docType = TYPE_EXPERT;
        } else if (typeNr == 2) {
            this.docType = TYPE_POLICE;
        } else {
            this.docType = TYPE_MEDICAL;
        }
    }

    public void setDigitalSignature(String digitalSignature) {
         this.digitalSignature = digitalSignature;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDigitalSignature() {
        return this.digitalSignature;
    }

    public String getContent() {
        return this.content;
    }

    public String getUserId() {
        return this.userId;
    }

    public String toString(){
        return "Document{uuid: " + this.uuid + ", type: " + this.docType + ", content: " + this.content + ", timestamp: " +
                this.timestamp + ", userId: " + this.userId + ", digitalSignature: " + this.digitalSignature + "}";
    }
}
