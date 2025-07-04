package com.candlenaturals.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseInitializer {

    @PostConstruct
    public void initFirebase() {
        try {
            FileInputStream serviceAccount =
                    new FileInputStream("src/main/resources/firebase/credenciales.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error inicializando Firebase: " + e.getMessage());
        }
    }
}

