package com.insure.server;

import exceptions.DocumentNotFoundException;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Claim {

    private int uuid;
    private String description;
    private String userId;

    private AtomicInteger documentID = new AtomicInteger(0);
    private ConcurrentHashMap<Integer, Document> documentMap = new ConcurrentHashMap<>();

    public Claim(int uuid, String description, String userId) {
        this.uuid = uuid;
        this.description = description;
        this.userId = userId;
    }

    public Collection<Document> getDocumentCollection() {

        return this.documentMap.values();
    }

    public String toString(){
        return "Claim{uuid: " + this.uuid + ", description: " + this.description + ", userId: " + this.userId + "}";
    }

    public int createDocument(int typeNr, String documentContent, String userId, String digitalSignature) {
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

}
