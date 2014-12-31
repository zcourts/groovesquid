package com.groovesquid.model;

public class Clients {
    private Client htmlshark;
    private Client jsqueue;
    
    public Clients(Client htmlshark, Client jsqueue) {
        this.htmlshark = htmlshark;
        this.jsqueue = jsqueue;
    }
    
    public static class Client {
        private String name;
        private String revision;
        private String secret;
        
        public Client(String name, String revision, String secret) {
            this.name = name;
            this.revision = revision;
            this.secret = secret;
        } 

        public String getName() {
            return name;
        }
        
        public String getRevision() {
            return revision;
        }

        public String getSecret() {
            return secret;
        }
        
        @Override
        public String toString() {
            return revision + ", " + secret;
        }
    }

    public Client getHtmlshark() {
        return htmlshark;
    }
    
    public Client getJsqueue() {
        return jsqueue;
    }
    
    @Override
    public String toString() {
        return "htmlshark: " + htmlshark.toString() + "; jsqueue: " + jsqueue.toString();
    }
}
