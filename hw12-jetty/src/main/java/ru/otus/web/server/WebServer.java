package ru.otus.web.server;

public interface WebServer {

    void init();

    void start() throws Exception;

    void stop() throws Exception;

}
