package com.insure.server;

import exceptions.ClaimNotFoundException;

public class Main {
    public static void main(String args[]) throws ClaimNotFoundException {
        /*System.out.println("Project Web Service is starting.");
        ClaimDataStore claimDataStore = new ClaimDataStore();
        Endpoint.publish("http://localhost:8090/claimservice", claimDataStore);*/

        final String DEFAULT_CONTENT_TEXT = "default document content text";

        ClaimDataStore claimDS = new ClaimDataStore();
        claimDS.createDocumentOfClaim(1, DEFAULT_CONTENT_TEXT, "user1");
        claimDS.createDocumentOfClaim(1, DEFAULT_CONTENT_TEXT, "user1");
        claimDS.createDocumentOfClaim(1, DEFAULT_CONTENT_TEXT, "user1");
        claimDS.createDocumentOfClaim(1, DEFAULT_CONTENT_TEXT, "user1");

        for (int i = 0; i < 5; i++) {
            System.out.println(claimDS.retrieveClaim(i).toString());
        }
        System.out.println("Documents of Claim 1:");
        for (String document : claimDS.listDocumentsOfClaim(1)) {
            System.out.println(document);
        }
    }
}
