package com.example.csc325_firebase_webview_auth.modelview;

import com.example.csc325_firebase_webview_auth.App;
import com.example.csc325_firebase_webview_auth.models.Person;
import com.example.csc325_firebase_webview_auth.viewmodel.AccessDataViewModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class AccessFBView
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
    @FXML
    private Button writeButton;
    @FXML
    private Button readButton;
    @FXML
    private TextArea outputField;
    @FXML
    private Text label;
    @FXML
    private MenuItem menuItem;
    private boolean bool;
    private Scene scene;
    private boolean key;
    private ObservableList<Person> listOfUsers = FXCollections.observableArrayList();
    private Person person;

    public AccessFBView() {
    }

    public ObservableList<Person> getListOfUsers() {
        return listOfUsers;
    }

    void initialize()
    {
        AccessDataViewModel accessDataViewModel = new AccessDataViewModel();
        nameField.textProperty().bindBidirectional(accessDataViewModel.userNameProperty());
        majorField.textProperty().bindBidirectional(accessDataViewModel.userMajorProperty());
        writeButton.disableProperty().bind(accessDataViewModel.isWritePossibleProperty().not());
        List<TextField> textFieldList = List.of(usernameField, passwordField, confirmField, nameField, majorField,ageField);
        readButton.setDisable(true);
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
        nameField.focusedProperty().addListener((observable, oldValue, newValue) ->
        {
            label.setText("Please enter your full name.");
            if (nameField.getText().matches("[A-Za-z]{2,12}[\\s][A-Za-z]{2,13}"))
            {
                label.setText("Please enter your major.");
                nameField.setBorder(null);
                majorField.setEditable(true);
            }
            else
            {
                label.setText("This name is invalid.");
                majorField.setEditable(false);
                ageField.setEditable(false);
                passwordField.setEditable(false);
                confirmField.setEditable(false);
                bool = true;
            }
        });
        majorField.focusedProperty().addListener((observable, oldValue, newValue) ->
        {
            label.setText("Please enter your major.");
            if (majorField.getText().toUpperCase().matches("[A-Z]{3}"))
            {
                label.setText("Please enter your age.");
                majorField.setBorder(null);
                ageField.setEditable(true);
            }
            else
            {
                label.setText("This major is invalid.");
                ageField.setEditable(false);
                passwordField.setEditable(false);
                confirmField.setEditable(false);
                majorField.requestFocus();
                bool = true;
            }
        });
        ageField.focusedProperty().addListener((observable, oldValue, newValue) ->
        {
            label.setText("Please enter your age.");
            if (ageField.getText().matches("(1[3-9]||[2-9][0-9])$"))
            {
                label.setText("Please enter your password.");
                ageField.setBorder(null);
                passwordField.setEditable(true);
                confirmField.setEditable(true);

            }
            else
            {
                label.setText("This age is invalid.");
                passwordField.setEditable(false);
                confirmField.setEditable(false);
                majorField.requestFocus();
                bool = true;
            }
        });
        usernameField.focusedProperty().addListener((observable, oldValue, newValue) ->
        {
        label.setText("Please enter your username.");
        if (usernameField.getText().matches("[a-zA-Z0-9]{2,16}")) {
            usernameField.setBorder(null);
            label.setText("Please enter your full name.");
            nameField.setEditable(true);
        }
        else
        {
            label.setText("This username is invalid.");
            nameField.setEditable(false);
            majorField.setEditable(false);
            ageField.setEditable(false);
            passwordField.setEditable(false);
            confirmField.setEditable(false);
            usernameField.requestFocus();
            bool = true;
        }
        });
        passwordField.focusedProperty().addListener((observable, oldValue, newValue) ->
        {
            label.setText("Please enter your password.");
            if (passwordField.getText().matches("[a-zA-Z0-9]{8,16}"))
            {
                passwordField.setBorder(null);
            }
            else
            {
                label.setText("This password is invalid.");
                passwordField.requestFocus();
                bool = true;
            }
        });
        confirmField.focusedProperty().addListener((observable, oldValue, newValue) ->
        {
            label.setText("Please enter your password again.");
            if (confirmField.getText().equals(passwordField.getText())) {
                confirmField.setBorder(null);
                readButton.setDisable(false);
            }
            else
            {
                label.setText("The passwords must match.");
                confirmField.requestFocus();
                readButton.setDisable(false);
                bool = true;
            }
        });
    }

    @FXML
    private void addRecord(ActionEvent event) {
        addData();
    }

        @FXML
    private void readRecord(ActionEvent event) {
        readFirebase();
    }

            @FXML
    private void regRecord(ActionEvent event) {
        registerUser();
    }

     @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("WebContainer.fxml");
    }

    public void addData() {

        DocumentReference docRef = App.fstore.collection("References").document(UUID.randomUUID().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("Name", nameField.getText());
        data.put("Major", majorField.getText());
        data.put("Age", Integer.parseInt(ageField.getText()));
        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
    }

        public boolean readFirebase()
         {
             key = false;

        //asynchronously retrieve all documents
        ApiFuture<QuerySnapshot> future =  App.fstore.collection("References").get();
        // future.get() blocks on response
        List<QueryDocumentSnapshot> documents;
        try
        {
            documents = future.get().getDocuments();
            if(documents.size()>0)
            {
                System.out.println("Outing....");
                for (QueryDocumentSnapshot document : documents)
                {
                    outputField.setText(outputField.getText()+ document.getData().get("Name")+ " , Major: "+
                            document.getData().get("Major")+ " , Age: "+
                            document.getData().get("Age")+ " \n ");
                    System.out.println(document.getId() + " => " + document.getData().get("Name"));
                    person  = new Person(String.valueOf(document.getData().get("Name")),
                            document.getData().get("Major").toString(),
                            Integer.parseInt(document.getData().get("Age").toString()));
                    listOfUsers.add(person);
                }
            }
            else
            {
               System.out.println("No data");
            }
            key=true;

        }
        catch (InterruptedException | ExecutionException ex)
        {
             ex.printStackTrace();
        }
        return key;
    }

        public void sendVerificationEmail() {
        try {
            UserRecord user = App.fauth.getUser("name");
            //String url = user.getPassword();

        } catch (Exception e) {
        }
    }

    public boolean registerUser() {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail("user@example.com")
                .setEmailVerified(false)
                .setPassword("secretPassword")
                .setPhoneNumber("+11234567890")
                .setDisplayName("John Doe")
                .setDisabled(false);

        UserRecord userRecord;
        try {
            userRecord = App.fauth.createUser(request);
            System.out.println("Successfully created new user: " + userRecord.getUid());
            return true;

        } catch (FirebaseAuthException ex) {
           // Logger.getLogger(FirestoreContext.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }
}
