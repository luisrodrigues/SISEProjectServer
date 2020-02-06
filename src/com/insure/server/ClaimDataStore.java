package com.insure.server;

import cryptography.Signature;
import exceptions.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.jws.WebService;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@WebService
public class ClaimDataStore {

    // Used concurrency reasons, to create incremental claim d
    private AtomicInteger claimID = new AtomicInteger(0);
    // DataStore to store the claims thread-safely
    private ConcurrentHashMap<Integer, Claim> claimStore = new ConcurrentHashMap<>();
    //Used to verify signatures
    private Signature signature;

    public ClaimDataStore() throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.signature = new Signature();
    }

    //   [Claim Methods]

    public int createClaim(String description, String userId) throws InvalidClaimDescriptionException,
            InvalidUserException {
        //must be the userId of an insured user
        int id = claimID.getAndIncrement();
        storeClaim(id, new Claim(id, description, userId));
        return id;
    }

    public Claim retrieveClaim(int uuid) throws ClaimNotFoundException {

        if(!claimStore.containsKey(uuid)) {
            throw new ClaimNotFoundException("Claim " + uuid + " does not exist!");
        }

        return claimStore.get(uuid);
    }

    public void updateClaim(int uuid, String description) throws ClaimNotFoundException {
        retrieveClaim(uuid).setDescription(description);
    }

    public String printClaim(int claimUuid ) throws ClaimNotFoundException {
        return retrieveClaim(claimUuid).toString();
    }

    public void storeClaim(int id, Claim claim) {
        claimStore.putIfAbsent(id, claim);
    }

    //   [Document Methods]

    //return the list of existing document keys inside a claim
    public Integer[] getDocumentKeysOfClaim(int claimUuid) throws ClaimNotFoundException {
         return retrieveClaim(claimUuid).getDocumentKeys();
    }

    public int createDocumentOfClaim(int claimUuid, int typeNr, String documentContent, String userId,
                                     String digitalSignature) throws BadPaddingException, InvalidSignatureException,
            NoSuchAlgorithmException, IOException, IllegalBlockSizeException, InvalidKeyException,
            InvalidKeySpecException, ClaimNotFoundException, InvalidUserException, InvalidDocumentTypeException,
            InvalidDocumentContentException {

        verifyDocumentSignature(documentContent, digitalSignature, userId);

        return this.retrieveClaim(claimUuid).createDocument(typeNr, documentContent, userId, digitalSignature);
    }

    // read document into a string
    public String readDocumentOfClaim(int claimUuid, int documentUuid) throws ClaimNotFoundException,
            DocumentNotFoundException {
        return retrieveClaim(claimUuid).readDocument(documentUuid);
    }

    //read the document's content
    public String readDocumentContentOfClaim(int claimUuid, int documentUuid) throws ClaimNotFoundException,
            DocumentNotFoundException {
        return retrieveClaim(claimUuid).retrieveDocument(documentUuid).getContent();
    }

    //used in tampering simulation situation
    public void tamperDocumentContentOfClaim(int claimUuid, int documentUuid) throws ClaimNotFoundException,
            DocumentNotFoundException {
        retrieveClaim(claimUuid).retrieveDocument(documentUuid).setContent("this has been tampered");
    }
    //read the document's user
    public String readDocumentUserOfClaim(int claimUuid, int documentUuid) throws ClaimNotFoundException,
            DocumentNotFoundException {
        return retrieveClaim(claimUuid).retrieveDocument(documentUuid).getUserId();
    }
    //read the document's signature
    public String readDocumentSignatureOfClaim(int claimUuid, int documentUuid) throws ClaimNotFoundException,
            DocumentNotFoundException {
        return retrieveClaim(claimUuid).retrieveDocument(documentUuid).getDigitalSignature();
    }

    public void updateDocumentOfClaim(int claimUuid, int documentUuid, String description, String digitalSignature,
                                      String userId) throws ClaimNotFoundException, DocumentNotFoundException, NotSameUserException {
        retrieveClaim(claimUuid).updateDocument(documentUuid, description, digitalSignature, userId);
    }

    public void deleteDocumentOfClaim(int claimUuid, int documentUuid, String userId) throws ClaimNotFoundException,
            DocumentNotFoundException, NotSameUserException {
        retrieveClaim(claimUuid).deleteDocument(documentUuid, userId);
    }

    private void verifyDocumentSignature(String content, String digitalSignature, String userId)
            throws InvalidSignatureException, BadPaddingException, NoSuchAlgorithmException, IOException,
            IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException {
        if (!this.signature.verify(content, digitalSignature, "keys/" + userId + "PublicKey")) {
            throw new InvalidSignatureException("Invalid signature: cannot add document");
        }
    }

}
