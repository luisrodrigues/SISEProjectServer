package com.insure.server;

import cryptography.Signature;
import exceptions.ClaimNotFoundException;
import exceptions.InvalidSignatureException;

import javax.crypto.NoSuchPaddingException;
import javax.jws.WebService;
import java.security.NoSuchAlgorithmException;
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

    public String[] listDocumentsOfClaim(int claimUuid) throws Exception {
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
                                     String digitalSignature) throws Exception {

        verifyDocumentSignature(documentContent, digitalSignature, userId);

        return this.retrieveClaim(claimUuid).createDocument(documentContent, userId, digitalSignature);
    }

    public String readDocumentOfClaim(int claimUuid, int documentUuid) throws Exception {

        Document document = retrieveClaim(claimUuid).retrieveDocument(documentUuid);

        verifyDocumentSignature(document.getContent(), document.getDigitalSignature(), document.getUserId());

        return retrieveClaim(claimUuid).readDocument(documentUuid);
    }

    private void verifyDocumentSignature(String content, String digitalSignature, String userId) throws Exception {
        if (!this.signature.verify(content, digitalSignature, "keys/" + userId + "PublicKey")) {
            throw new InvalidSignatureException("This signature is invalid...");
        }
    }

}
