package ru.taste;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Boot {
    public static void main(String[] args) throws InterruptedException {
        Instance.get().initialize(SpringApplication.run(Boot.class, args));
    }
}
