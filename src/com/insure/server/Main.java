package com.insure.server;

import javax.xml.ws.Endpoint;

public class Main {
    public static void main(String[] args) throws Exception {
        // creates the endpoint for the webservice
        ClaimDataStore claimDataStore = new ClaimDataStore();
        Endpoint.publish("http://localhost:8090/claimservice", claimDataStore);
    }
}
