package it.sisal.digital.phoenix.utils.security.signature.conf;

import it.sisal.digital.phoenix.utils.security.signature.model.ClientsModel;
import it.sisal.digital.phoenix.utils.security.signature.model.KeyStoreModel;
import it.sisal.digital.phoenix.utils.security.signature.model.KeyStoreProperties;
import it.sisal.digital.phoenix.utils.security.signature.service.SecurityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyStoreException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class KeyStoreConf {
    private static Logger logger = LoggerFactory.getLogger(SecurityService.class);

    @Autowired
    KeyStoreProperties keyStoreProperties;

    @Bean("mineKeyStore")
    public KeyStoreModel mineKeyStore() throws KeyStoreException {
        KeyStoreModel mineKS;
        if(keyStoreProperties != null && keyStoreProperties.getMine() != null){
            KeyStoreProperties.Mine mine = keyStoreProperties.getMine();
            mineKS = new KeyStoreModel(mine.getCertPath(), mine.getAlias(), mine.getPwd(), mine.getPkPassword());
        }else{
            logger.warn("Certificate not loaded");
            mineKS = new KeyStoreModel();
        }
        return mineKS;
    }

    @Bean("callerKeyStore")
    public ClientsModel callerKeyStore() throws KeyStoreException {
        ClientsModel clientsModel = new ClientsModel();

        if(keyStoreProperties != null && keyStoreProperties.getClients() != null){
            Map<String, KeyStoreProperties.Client> clients = keyStoreProperties.getClients();

            HashMap<String, KeyStoreModel> clientsModelMap = clients.entrySet().stream()
                    .map(c ->  new AbstractMap.SimpleEntry<String, KeyStoreModel>(c.getKey(),
                                    new KeyStoreModel(
                                            c.getValue().getCertPath(),
                                            c.getValue().getAlias(),
                                            c.getValue().getPwd(),
                                            c.getValue().getPkPassword()
                                    )
                )
            ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (prev, next) -> next, HashMap::new));
            clientsModel.setClients(clientsModelMap);
        }else{
            logger.warn("Clients certificate not loaded");
        }

        return clientsModel;
    }

}
