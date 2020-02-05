package com.insure.server;

import cryptography.Signature;
import exceptions.ClaimNotFoundException;
import exceptions.DocumentNotFoundException;
import exceptions.InvalidSignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.jws.WebService;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.HashMap;
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

    public String printClaim(int claimUuid ) throws ClaimNotFoundException {
        return retrieveClaim(claimUuid).toString();
    }

    public void storeClaim(int id, Claim claim) {
        claimStore.putIfAbsent(id, claim);
    }

    //   [Document Methods]

    public String[] listDocumentsOfClaim(int claimUuid) throws ClaimNotFoundException, BadPaddingException,
            InvalidKeySpecException, NoSuchAlgorithmException, IOException, IllegalBlockSizeException,
            InvalidKeyException, InvalidSignatureException {
         HashMap<Integer, Document> documentMap = retrieveClaim(claimUuid).getDocumentMap();
         Collection<Document> documentCollection = documentMap.values();
         String[] documentList = new String[documentMap.size()];
         int i = 0;
         for (Document doc : documentCollection) {
             verifyDocumentSignature(doc.getContent(), doc.getDigitalSignature(), doc.getUserId());
             documentList[i] = doc.toString();
             i++;
         }
         return documentList;
    }

    public int createDocumentOfClaim(int claimUuid, String documentContent, String userId,
                                     String digitalSignature) throws BadPaddingException, InvalidSignatureException,
            NoSuchAlgorithmException, IOException, IllegalBlockSizeException, InvalidKeyException,
            InvalidKeySpecException, ClaimNotFoundException {

        verifyDocumentSignature(documentContent, digitalSignature, userId);

        return this.retrieveClaim(claimUuid).createDocument(documentContent, userId, digitalSignature);
    }

    public String readDocumentOfClaim(int claimUuid, int documentUuid) throws ClaimNotFoundException,
            BadPaddingException, InvalidSignatureException, NoSuchAlgorithmException, IOException,
            IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException, DocumentNotFoundException {

        Document document = retrieveClaim(claimUuid).retrieveDocument(documentUuid);

        verifyDocumentSignature(document.getContent(), document.getDigitalSignature(), document.getUserId());

        return retrieveClaim(claimUuid).readDocument(documentUuid);
    }

    private void verifyDocumentSignature(String content, String digitalSignature, String userId)
            throws InvalidSignatureException, BadPaddingException, NoSuchAlgorithmException, IOException,
            IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException {
        if (!this.signature.verify(content, digitalSignature, "keys/" + userId + "PublicKey")) {
            throw new InvalidSignatureException("This signature is invalid...");
        }
    }

}
