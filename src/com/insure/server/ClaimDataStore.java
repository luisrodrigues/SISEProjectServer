package com.insure.server;

import exceptions.ClaimNotFoundException;

import javax.jws.WebService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@WebService
public class ClaimDataStore {
    // just for testing
    public static final int NR_CLAIMS_TO_CREATE = 5;
    // Unique ID
    private AtomicInteger claimID = new AtomicInteger(0);
    // Collection to store the data
    private static ConcurrentHashMap<Integer, Claim> claimStore = new ConcurrentHashMap<>();

    public ClaimDataStore(){
        // preload data (create a few claims)
        for (int i = 0; i < NR_CLAIMS_TO_CREATE; i++) {
            //use create and store
            createClaim("user" + i, "claiming " + i);
        }
    }
    // specify and implement the methods to
    // create/retrieve/store/update
    // claims on the data store safely.

    public int createClaim(String description, String userId) {
        //must be the userId of an insured user
        storeClaim(new Claim(claimID.get(), userId, description));
        return claimID.getAndIncrement();
    }

    public static Claim retrieveClaim(int uuid) throws ClaimNotFoundException {

        if(!claimStore.containsKey(uuid)) {
            throw new ClaimNotFoundException("Cannot find claim....");
        }

        return claimStore.get(uuid);
    }

    public void storeClaim(Claim claim) {
        claimStore.putIfAbsent(claimID.get(), claim);
    }

    public void updateClaim(int uuid, String description) throws ClaimNotFoundException {
        retrieveClaim(uuid).setDescription(description);
    }

    // list/create/read/update/delete documents of claims on the data store safely.

    public String[] listDocumentsOfClaim(int claimUuid) throws ClaimNotFoundException {
        return retrieveClaim(claimUuid).listDocuments();
    }

    public void createDocumentOfClaim(int claimUuid, String documentContent, String userId) throws ClaimNotFoundException {
        //userId can be of the original original user who created
        //the claim or any InSure officer
        retrieveClaim(claimUuid).createDocument(documentContent, userId);
    }

    public void readDocumentOfClaim(int claimUuid, int documentUuid) throws ClaimNotFoundException {
        retrieveClaim(claimUuid).readDocument(documentUuid);
    }

    public void updateDocumentOfClaim(int claimUuid, int documentUuid, String description) throws ClaimNotFoundException {
        retrieveClaim(claimUuid).updateDocument(documentUuid, description);
    }

    public void deleteDocumentOfClaim(int claimUuid, int documentUuid) throws ClaimNotFoundException {
        retrieveClaim(claimUuid).deleteDocument(documentUuid);
    }
}
