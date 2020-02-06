package com.insure.server;

import javax.crypto.NoSuchPaddingException;
import javax.xml.ws.Endpoint;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException {
        // creates the endpoint for the webservice
        ClaimDataStore claimDataStore = new ClaimDataStore();
        Endpoint.publish("http://localhost:8090/claimservice", claimDataStore);
    }
}
