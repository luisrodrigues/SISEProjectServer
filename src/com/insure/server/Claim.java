package com.insure.server;

import exceptions.*;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Claim {

    private int uuid;
    private String description;
    private String userId;

    // Used concurrency reasons, to create incremental document id
    private AtomicInteger documentID = new AtomicInteger(0);
    // DataStore to store the documents thread-safely on each claim
    private ConcurrentHashMap<Integer, Document> documentMap = new ConcurrentHashMap<>();

    public Claim(int uuid, String description, String userId) throws InvalidClaimDescriptionException, InvalidUserException {
        this.uuid = uuid;

        if(description == null || description.equals("")) {
            throw new InvalidClaimDescriptionException("Invalid description!");
        }

        this.description = description;

        if(userId == null || userId.equals("")) {
            throw new InvalidUserException("Invalid userId!");
        }

        this.userId = userId;
    }

    public Integer[] getDocumentKeys() {
        Set<Integer> set = this.documentMap.keySet();
        return this.documentMap.keySet().toArray(new Integer[set.size()]);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString(){
        return "Claim{uuid: " + this.uuid + ", description: " + this.description + ", userId: " + this.userId + "}";
    }

    // Document-related methods

    public int createDocument(int typeNr, String documentContent, String userId, String digitalSignature) throws InvalidUserException, InvalidDocumentTypeException, InvalidDocumentContentException {
        int id = documentID.getAndIncrement();
        documentMap.putIfAbsent(id, new Document(id, typeNr, documentContent, userId, digitalSignature));
        return id;
    }

    public Document retrieveDocument(int documentUuid) throws DocumentNotFoundException {

        if(!documentMap.containsKey(documentUuid)) {
            throw new DocumentNotFoundException("Document " + documentUuid + " of Claim " + this.uuid
                    + " does not exist!");
        }

        return documentMap.get(documentUuid);
    }

    public String readDocument(int documentUuid) throws DocumentNotFoundException {
        return this.retrieveDocument(documentUuid).toString();
    }

    //only accepts content updates from the author
    public void updateDocument(int documentUuid, String newContent, String digitalSignature, String userId) throws DocumentNotFoundException, NotSameUserException {
        Document document = this.retrieveDocument(documentUuid);
        if (document.getUserId().equals(userId)) {
            document.setContent(newContent);
            document.setDigitalSignature(digitalSignature);
        } else {
            throw new NotSameUserException("You are not the author of this document!");
        }
    }

    //only the author can delete the content
    public void deleteDocument(int documentUuid, String userId) throws DocumentNotFoundException, NotSameUserException {
        Document document = this.retrieveDocument(documentUuid);
        if (document.getUserId().equals(userId)) {
            documentMap.remove(documentUuid);
        } else {
            throw new NotSameUserException("You are not the author of this document!");
        }
    }

}
