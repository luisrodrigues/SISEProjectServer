package com.insure.server;

import cryptography.Signature;
import exceptions.ClaimNotFoundException;
import exceptions.DocumentNotFoundException;
import exceptions.InvalidSignatureException;

import javax.crypto.NoSuchPaddingException;
import javax.jws.WebService;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@WebService
public class ClaimDataStore {
    // Unique ID
    private AtomicInteger claimID = new AtomicInteger(0);
    // Collection to store the data
    private ConcurrentHashMap<Integer, Claim> claimStore = new ConcurrentHashMap<>();
    //Signatures
    private Signature signature;

    public ClaimDataStore() throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.signature = new Signature();
    }

    //   [Claim Methods]

    public int createClaim(String description, String userId) {
        //must be the userId of an insured user
        int id = claimID.getAndIncrement();
        storeClaim(id, new Claim(id, description, userId));
        return id;
    }

    public Claim retrieveClaim(int uuid) throws ClaimNotFoundException {

        if(!claimStore.containsKey(uuid)) {
            throw new ClaimNotFoundException("Cannot find claim....");
        }

        return claimStore.get(uuid);
    }

    public void storeClaim(int id, Claim claim) {
        claimStore.putIfAbsent(id, claim);
    }

    public void updateClaim(int uuid, String description) throws ClaimNotFoundException {
        retrieveClaim(uuid).setDescription(description);
    }

    //   [Document Methods]

    public String[] listDocumentsOfClaim(int claimUuid) throws ClaimNotFoundException {
        return retrieveClaim(claimUuid).listDocuments();
    }

    public int createDocumentOfClaim(int claimUuid, String documentContent, String userId) throws Exception {
        return this.retrieveClaim(claimUuid).createDocument(documentContent, userId);
    }

    public void signDocumentOfClaim(int claimUuid, int documentId, String digitalSignature) throws ClaimNotFoundException, DocumentNotFoundException {
        Document document = this.retrieveClaim(claimUuid).retrieveDocument(documentId);
        document.sign(digitalSignature);
    }

    public String readDocumentOfClaim(int claimUuid, int documentUuid) throws Exception {
        return retrieveClaim(claimUuid).readDocument(documentUuid);
    }

    public void updateDocumentOfClaim(int claimUuid, int documentUuid, String description, String userId) throws Exception {
        Document document = retrieveClaim(claimUuid).retrieveDocument(documentUuid);
        String documentData = this.readDocumentOfClaim(claimUuid, documentUuid);

        verifySignature(userId, document, documentData);
        this.retrieveClaim(claimUuid).updateDocument(documentUuid, description);
    }

    //test
    public void deleteDocumentOfClaim(int claimUuid, int documentUuid, String userId) throws Exception {
        Document document = retrieveClaim(claimUuid).retrieveDocument(documentUuid);
        String documentData = this.readDocumentOfClaim(claimUuid, documentUuid);

        verifySignature(userId, document, documentData);

        this.retrieveClaim(claimUuid).deleteDocument(documentUuid);
    }

    //utility methods
    private void verifySignature(String userId, Document document, String documentData) throws Exception {
        if(!this.signature.verify(documentData, document.getDigitalSignature(), "keys/" + userId + "PublicKey")) {
            throw new InvalidSignatureException("This signature is invalid...");
        }
    }

    public String printClaim(int claimUuid ) throws ClaimNotFoundException {
        return retrieveClaim(claimUuid).toString();
    }

}
