package com.lucas.security.signature.model;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "cert")
public class KeyStoreProperties {

    private Mine mine;
    private Map<String, Client> clients;

    public static class Mine{
        private String certPath;
        private String alias;
        private String pwd;
        private String pkPassword;

        public String getCertPath() {
            return certPath;
        }

        public void setCertPath(String certPath) {
            this.certPath = certPath;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }

        public String getPkPassword() {
            return pkPassword;
        }

        public void setPkPassword(String pkPassword) {
            this.pkPassword = pkPassword;
        }
    }

    public static class Client{
        private String certPath;
        private String alias;
        private String pwd;
        private String pkPassword;

        public String getCertPath() {
            return certPath;
        }

        public void setCertPath(String certPath) {
            this.certPath = certPath;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }

        public String getPkPassword() {
            return pkPassword;
        }

        public void setPkPassword(String pkPassword) {
            this.pkPassword = pkPassword;
        }
    }

    public Mine getMine() {
        return mine;
    }

    public void setMine(Mine mine) {
        this.mine = mine;
    }

    public Map<String, Client> getClients() {
        return clients;
    }

    public void setClients(Map<String, Client> clients) {
        this.clients = clients;
    }
}
