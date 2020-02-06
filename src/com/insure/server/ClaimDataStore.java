package com.insure.server;

import cryptography.Signature;
import exceptions.ClaimNotFoundException;
import exceptions.DocumentNotFoundException;
import exceptions.InvalidSignatureException;
import exceptions.NotSameUserException;

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

    public Integer[] getDocumentKeysOfClaim(int claimUuid) throws ClaimNotFoundException {
         return retrieveClaim(claimUuid).getDocumentKeys();
    }

    public int createDocumentOfClaim(int claimUuid, int typeNr, String documentContent, String userId,
                                     String digitalSignature) throws BadPaddingException, InvalidSignatureException,
            NoSuchAlgorithmException, IOException, IllegalBlockSizeException, InvalidKeyException,
            InvalidKeySpecException, ClaimNotFoundException {

        verifyDocumentSignature(documentContent, digitalSignature, userId);

        return this.retrieveClaim(claimUuid).createDocument(typeNr, documentContent, userId, digitalSignature);
    }

    // read all to string
    public String readDocumentOfClaim(int claimUuid, int documentUuid) throws ClaimNotFoundException, DocumentNotFoundException {
        return retrieveClaim(claimUuid).readDocument(documentUuid);
    }

    //read content
    public String readDocumentContentOfClaim(int claimUuid, int documentUuid) throws ClaimNotFoundException, DocumentNotFoundException {
        return retrieveClaim(claimUuid).retrieveDocument(documentUuid).getContent();
    }
    //read user
    public String readDocumentUserOfClaim(int claimUuid, int documentUuid) throws ClaimNotFoundException, DocumentNotFoundException {
        return retrieveClaim(claimUuid).retrieveDocument(documentUuid).getUserId();
    }
    //read signature
    public String readDocumentSignatureOfClaim(int claimUuid, int documentUuid) throws ClaimNotFoundException, DocumentNotFoundException {
        return retrieveClaim(claimUuid).retrieveDocument(documentUuid).getDigitalSignature();
    }

    public void updateDocumentOfClaim(int claimUuid, int documentUuid, String description, String digitalSignature, String userId) throws ClaimNotFoundException, DocumentNotFoundException, NotSameUserException {
        retrieveClaim(claimUuid).updateDocument(documentUuid, description, digitalSignature, userId);
    }

    public void deleteDocumentOfClaim(int claimUuid, int documentUuid, String userId) throws ClaimNotFoundException, DocumentNotFoundException, NotSameUserException {
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
