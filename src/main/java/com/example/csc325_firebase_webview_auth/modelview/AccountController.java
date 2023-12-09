package com.example.csc325_firebase_webview_auth.modelview;

import com.example.csc325_firebase_webview_auth.App;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AccountController implements Initializable
{
    @FXML
    private TextField nameField;
    @FXML
    private TextField majorField;
    @FXML
    private TextField ageField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField confirmField;
    public Button createAccountButton;
    private static boolean userHasRegisteredStatus = false;
    private boolean bool = false;
    private Scene scene;
    @FXML
    private Text label;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<TextField> textFieldList = List.of(usernameField, passwordField, confirmField, nameField, majorField,ageField);
        createAccountButton.setDisable(true);
        label.setText("Please enter a username.");
        textFieldList.forEach(textField ->
        {
            textField.setOnKeyPressed(event ->
            {
                if (event.getCode() != KeyCode.TAB && bool)
                {
                    textField.setStyle("-fx-border-color: #12c812 ; -fx-border-width: 1px ;");
                    bool = false;
                }
            });
            textField.setOnMouseClicked(mouseEvent ->
            {
                textField.requestFocus();
            });
        });
        usernameField.focusedProperty().addListener((observable, oldValue, newValue) ->
        {
            if (usernameField.getText().matches("[a-zA-Z0-9]{2,16}")) {
                label.setText("Please enter your name.");
                usernameField.setBorder(null);
                nameField.setEditable(true);
            } else {
                label.setText("This username is invalid.");
                usernameField.setVisible(true);
                nameField.setEditable(false);
                majorField.setEditable(false);
                ageField.setEditable(false);
                passwordField.setEditable(false);
                confirmField.setEditable(false);
                usernameField.requestFocus();
                bool = true;
            }
        });

        nameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (nameField.getText().matches("[A-Za-z]{2,12}[\\s][A-Za-z]{2,13}")) {
                label.setText("Please enter your major.");
                nameField.setBorder(null);
                majorField.setEditable(true);
            } else {
                label.setText("This name is invalid.");
                nameField.setVisible(true);
                majorField.setEditable(false);
                ageField.setEditable(false);
                passwordField.setEditable(false);
                confirmField.setEditable(false);
                nameField.requestFocus();
                bool = true;
            }
        });

        majorField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (majorField.getText().toUpperCase().matches("[A-Z]{3}")) {
                label.setText("Please enter your age.");
                majorField.setBorder(null);
                ageField.setEditable(true);
            } else {
                label.setText("This major is invalid.");
                majorField.setVisible(true);
                ageField.setEditable(false);
                passwordField.setEditable(false);
                confirmField.setEditable(false);
                majorField.requestFocus();
                bool = true;
            }
        });

        ageField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (ageField.getText().matches("(1[3-9]||[2-9][0-9])$")) {
                label.setText("Please enter a password.");
                ageField.setBorder(null);
                passwordField.setEditable(true);
                confirmField.setEditable(true);

            } else {
                label.setText("This age is invalid.");
                ageField.setVisible(true);
                passwordField.setEditable(false);
                confirmField.setEditable(false);
                majorField.requestFocus();
                bool = true;
            }
        });

        passwordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (passwordField.getText().matches("[a-zA-Z0-9]{8,16}")) {
                passwordField.setBorder(null);
                label.setText("REENTER \nPASSWORD");
            }
            else
            {
                label.setText("INVALID \nPASSWORD \nENTRY");
                passwordField.requestFocus();
                bool = true;
            }
        });

        confirmField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (confirmField.getText().equals(passwordField.getText()))
            {
                confirmField.setBorder(null);
                createAccountButton.setDisable(false);
            }
            else
            {
                label.setText("The passwords must match.");
                confirmField.requestFocus();
                createAccountButton.setDisable(false);
                bool = true;
            }
        });
    }

    @FXML
    protected void createAccount(ActionEvent event) throws IOException, InterruptedException {
        DocumentReference docRef = App.fstore.collection("References").document(UUID.randomUUID().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("username", usernameField.getText());
        data.put("password", passwordField.getText());
        data.put("name", nameField.getText());
        data.put("major", majorField.getText());
        data.put("age", Integer.parseInt(ageField.getText()));
        ApiFuture<WriteResult> result = docRef.set(data);

        scene = App.getScene();
        scene = new Scene(App.loadFXML("AccessFBView.fxml"));
        App.setScene(scene);
        App.setStage();
    }

    @FXML
    private void returnToLogin(ActionEvent event) throws IOException {
        App.setScene(new Scene(App.loadFXML("login-screen.fxml")));
        App.setStage();
    }

    public static boolean userHasRegistered() {
        return userHasRegisteredStatus;
    }

}
