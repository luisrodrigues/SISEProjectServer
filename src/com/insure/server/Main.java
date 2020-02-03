package com.insure.server;

import exceptions.ClaimNotFoundException;

import javax.crypto.NoSuchPaddingException;
import javax.xml.ws.Endpoint;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String args[]) throws ClaimNotFoundException, NoSuchPaddingException, NoSuchAlgorithmException {
        System.out.println("Data Store WebService is starting.");
        ClaimDataStore claimDataStore = new ClaimDataStore();
        Endpoint.publish("http://localhost:8090/claimservice", claimDataStore);
    }
}
