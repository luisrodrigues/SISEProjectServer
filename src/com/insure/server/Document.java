package com.insure.server;

import exceptions.InvalidDocumentContentException;
import exceptions.InvalidDocumentTypeException;
import exceptions.InvalidUserException;

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

    public Document(int uuid, int typeNr, String content, String userId, String digitalSignature) throws InvalidUserException, InvalidDocumentTypeException, InvalidDocumentContentException {
        Date date = new Date();

        this.uuid = uuid;

        if(typeNr < 1 || typeNr > 3) {
            throw new InvalidDocumentTypeException("Invalid document type!");
        }

        this.setType(typeNr);

        if(content == null || content.equals("")) {
            throw new InvalidDocumentContentException("Invalid document content!");
        }

        this.content = content;
        this.timestamp = new Timestamp(date.getTime());

        if(userId == null || userId.equals("")) {
            throw new InvalidUserException("Invalid userId!");
        }

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
