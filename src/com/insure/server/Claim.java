package com.insure.server;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Claim {

    private final int uuid;
    private String description;

    private AtomicInteger documentID = new AtomicInteger(0);
    private static ConcurrentHashMap<Integer, Document> documentMap = new ConcurrentHashMap<>();

    public Claim(int uuid, String description, String userIdentifier){
        this.uuid = uuid;
        this.description = description;
}

    public void setDescription(String description) {
        this.description = description;
    }

    //add more attributes
    public String toString(){
        return "Claim{uuid: " + this.uuid + ", description: " + this.description + "}";
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

    public void createDocument(String documentContent, String userId) {
        documentMap.putIfAbsent(documentID.get(), new Document(documentID.getAndIncrement(), documentContent, userId));
    }

    public String readDocument(int documentUuid) {
        return documentMap.get(documentUuid).toString();
    }

    public void updateDocument(int documentUuid, String newContent) {
        documentMap.get(documentUuid).setContent(newContent);
    }

    public void deleteDocument(int documentUuid) {
        documentMap.remove(documentUuid);
    }

}
