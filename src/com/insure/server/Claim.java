package com.insure.server;

import exceptions.DocumentNotFoundException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Claim {

    private final int uuid;
    private String description;
    private String userId;

    private AtomicInteger documentID = new AtomicInteger(0);
    private static ConcurrentHashMap<Integer, Document> documentMap = new ConcurrentHashMap<>();

    public Claim(int uuid, String description, String userId) {
        this.uuid = uuid;
        this.description = description;
        this.userId = userId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString(){
        return "Claim{uuid: " + this.uuid + ", description: " + this.description + ", userId: " + this.userId + "}";
    }

    public String[] listDocuments() {
        String[] documentList = new String[documentMap.size()];
        int i = 0;
        for (Map.Entry<Integer, Document> entry : documentMap.entrySet()) {
            documentList[i] = entry.getValue().toString();
            i++;
        }
        return documentList;
    }

    public int createDocument(String documentContent, String userId) {
        documentMap.putIfAbsent(documentID.get(), new Document(documentID.get(), documentContent, userId));
        return documentID.getAndIncrement();
    }

    public Document retrieveDocument(int documentUuid) throws DocumentNotFoundException {

        if(!documentMap.containsKey(documentUuid)) {
            throw new DocumentNotFoundException("Cannot find document....");
        }

        return documentMap.get(documentUuid);
    }

    public String readDocument(int documentUuid) throws DocumentNotFoundException {
        return this.retrieveDocument(documentUuid).toString();
    }

    public void updateDocument(int documentUuid, String newContent) throws DocumentNotFoundException {
        this.retrieveDocument(documentUuid).setContent(newContent);
    }

    public void deleteDocument(int documentUuid) {
        documentMap.remove(documentUuid);
    }

}
