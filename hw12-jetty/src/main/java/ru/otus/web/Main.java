package ru.otus.web;

import ru.otus.web.server.JettyWebServer;

public class Main {
    public static void main(String[] args) throws Exception {
        var server = new JettyWebServer(8080);
        server.init();
        server.start();
    }
}
