package com.lucas.security.signature.model;

import java.util.Map;

public class ClientsModel {
    Map<String, KeyStoreModel> clients;

    public ClientsModel(){
        this.clients = clients;
    }

    public ClientsModel( Map<String, KeyStoreModel> clients){
        this.clients = clients;
    }

    public Map<String, KeyStoreModel> getClients() {
        return clients;
    }

    public void setClients(Map<String, KeyStoreModel> clients) {
        this.clients = clients;
    }
}
