package com.mcapp;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class BasicView extends View {

    private static PreparedStatement pstmt;

    static ArrayList<String> questionResult = new ArrayList<String>();
    static ArrayList<String> choiceResult = new ArrayList<String>();
    static ArrayList<String> answerResult = new ArrayList<String>();

    //Chapter numbers List for drop Down menu
    static ObservableList<String> chList = FXCollections.observableArrayList();
    //List to store the number of sections
    static ObservableList<String> sList = FXCollections.observableArrayList();
    //List to store the number of questions
    static ObservableList<String> qList = FXCollections.observableArrayList();

    FlowPane flowPane = new FlowPane();
    ScrollPane scrollPane = new ScrollPane();
    static String userName;
    static String passWord;
    static String chapterNumber;
    static String sectionNumber;
    static String qNumber;
    static String answerHint;
    static String answerVal;
    static String[] arr;
    static String[] sectionArr;
    static String[] keyWords = new String[]{"public", "void", "static", "int", "String"};
    static String[] antiKeyWords = new String[]{"integer", "print", "point", "Integer"};
    static int numSections;
    static int numQuestions = 1;
    static String temp = "";
    VBox homeVBox = home();
    //static SwingNode HiLitPrompt = new SwingNode();
    //static SwingNode swingNode = new SwingNode();
    static int userResult;

    private static volatile boolean isThreadRunning = false;

    public BasicView(String name) {
        super(name);
        flowPane.setPadding(new Insets(15, 12, 15, 12));
        flowPane.setAlignment(Pos.TOP_CENTER);
        flowPane.setVgap(75);
        flowPane.setPrefWidth(175);
        
        
        
        
        //flowPane.getChildren().addAll(home());
        flowPane.getChildren().addAll(loginScreen());
        scrollPane.setContent(flowPane);
        setCenter(scrollPane);
    }

    public void saveResult(String usr, String chNum, String secNum, String qNum, int usrScore) {
        try {
            String dBName = "student_records";
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/student_records", userName, passWord);
            //System.out.println("Database connected");
            pstmt = conn.prepareStatement("INSERT INTO `studentresults` (`userName` , `chapterNumber`, `sectionNumber`, `qNumber`, `userresult`) VALUES ('" + userName + "' , '" + chapterNumber + "', '" + sectionNumber + "', '" + qNumber + "', '" + userResult + "')");
            pstmt.executeUpdate();
            //setCenter(home());
        } catch (SQLException ex) {
            //prompt.setText(ex.getMessage());
            System.out.println(ex.getMessage());
        }
        System.out.println(userName + " " + chapterNumber + " " + sectionNumber + " " + qNumber + " " + userResult);

    }

    public VBox home() {

        //chapterNumber = "";
        chList.clear();
        //generate a list of strings from 1 to 44 for chapter choice
        for (int i = 1; i <= 44; i++) {
            String str = Integer.toString(i);
            chList.add(str);
        }

        Label prompt = new Label("      Please Choose Chapter, Section and Question");
        prompt.setAlignment(Pos.CENTER);
        prompt.setStyle("-fx-background-color: rgba(255, 255, 255, 1);");
 

        Label chLabel = new Label("");

        ComboBox chapter = new ComboBox(chList);
        ComboBox section = new ComboBox();
        ComboBox question = new ComboBox();
        chapter.setPrefWidth(110);
        chapter.getStyleClass().add(chapterNumber);
        chapter.setPromptText("Chapter");
        chapter.valueProperty().addListener((observable, oldValue, newValue) -> {

            //System.out.println(oldValue + " is the old value of chapter");
            if (newValue != null) {
                chapterNumber = newValue.toString();
            } else if (oldValue != null) {
                chapterNumber = oldValue.toString();
            }

            try {
                sList.clear();
                sList = setSections(chapterNumber);
            } catch (Exception ex) {
                Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
            }
            section.setItems(sList);
        });
        //chapter.setEditable(true);

        chapter.setCellFactory(p -> new ListCell<String>() {
            private String item;

            {
                //setOnTouchPressed(e -> chapter.getSelectionModel().select(item));
                //setOnSwipeDown(e -> chapter.setValue(item));
                setOnMouseClicked(e -> chapter.getSelectionModel().select(item));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                this.item = item;
                setText(item);
            }
        });

        Label sLabel = new Label("");
        //ComboBox section = new ComboBox(sList);
        section.setPrefWidth(110);
        section.setPromptText("Section");
        section.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                sectionNumber = newValue.toString();
            } else if (oldValue != null) {
                sectionNumber = oldValue.toString();
            }

            try {
                qList.clear();
                qList = setQuestions(chapterNumber, sectionNumber);
            } catch (Exception ex) {
                Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
            }
            question.setItems(qList);
        });
        //section.getSelectionModel().selectFirst();
        section.setCellFactory(p -> new ListCell<String>() {
            private String item;

            {
                //setOnTouchPressed(e -> section.getSelectionModel().select(item));
                //setOnMousePressed(e -> section.getSelectionModel().select(item));
                setOnMouseClicked(e -> section.getSelectionModel().select(item));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                this.item = item;
                setText(item);
            }
        });
        Label qLabel = new Label("");
        //-----------------------------------------------------------

        question.setPrefWidth(110);
        question.setPromptText("Question");
        question.setCellFactory(p -> new ListCell<String>() {
            private String item;

            {
                setOnMouseClicked(e -> question.getSelectionModel().select(item));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                this.item = item;
                setText(item);
            }

        });
        Button btConfirm = new Button();
        btConfirm.setText("Choose Question");

        btConfirm.setOnAction(e -> {
            try {
                chapterNumber = chapter.getValue().toString();
                qNumber = question.getValue().toString();
                arr = getText();
                userResult = -1;
                setCenter(generateQuestion(question.getValue().toString()));
            } catch (IOException ex) {
                Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        VBox vb1 = new VBox(30);
        String image = MCApp.class.getResource("/splash2.png").toExternalForm();
        vb1.setStyle("-fx-background-image: url('" + image + "'); "
                + "-fx-background-position: center center; "
                + "-fx-background-repeat: null; " 
                + "-fx-background-size: cover;");
        
        vb1.setPadding(new Insets(15, 0, 0, 0));
        vb1.getChildren().addAll(prompt, chLabel, chapter, sLabel, section, qLabel, question, btConfirm);
        vb1.setAlignment(Pos.CENTER);
        return vb1;
    }
//-----------------------------------------------------------------------------------------------------------

    public VBox loginScreen() {
        FlowPane loginPane = new FlowPane();
        loginPane.setPrefWidth(200);
        Label prompt = new Label();
        prompt.setWrapText(true);
        prompt.setAlignment(Pos.CENTER);
        prompt.setPrefWidth(275);

        Label Username = new Label();
        Username.setPrefWidth(100);
        Username.setText("User Name");

        Label pWord = new Label();
        pWord.setPrefWidth(100);
        pWord.setText("Password");

        TextField usrNameText = new TextField();
        usrNameText.setPrefWidth(150);
        usrNameText.setText("");
        PasswordField pWordText = new PasswordField();
        pWordText.setPrefWidth(150);
        pWordText.setText("");

        HBox usr = new HBox();
        usr.setAlignment(Pos.CENTER);
        usr.setPadding(new Insets(12, 12, 12, 12));
        HBox pass = new HBox();
        pass.setAlignment(Pos.CENTER);
        pass.setPadding(new Insets(12, 12, 12, 12));
        HBox submitButton = new HBox();
        submitButton.setAlignment(Pos.CENTER);
        submitButton.setPadding(new Insets(12, 12, 12, 12));

        Button loginButton = new Button("Submit");

        usr.getChildren().addAll(Username, usrNameText);
        pass.getChildren().addAll(pWord, pWordText);
        submitButton.getChildren().addAll(loginButton);
        loginPane.getChildren().addAll(prompt, usr, pass, submitButton);
        loginPane.setPadding(new Insets(100, 15, 15, 15));
        loginPane.setAlignment(Pos.CENTER);

        VBox vb1 = new VBox(25);
        vb1.setPadding(new Insets(15, 0, 0, 0));
        vb1.getChildren().addAll(loginPane);
        vb1.setAlignment(Pos.CENTER);

        loginButton.setOnAction(e -> {
            if (pWordText.getText() != "" && usrNameText.getText() != "") {
                try {
                    userName = usrNameText.getText();
                    passWord = pWordText.getText();
                    String dBName = "student_records";
                    //PreparedStatement pstmt;
                    // Load the JDBC driver
                    Class.forName("com.mysql.jdbc.Driver");
                    System.out.println("Driver loaded");
                    // Establish a connection
                    //Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/wilhsy867?autoReconnect=true", "wilhsy867", "310998007");
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + dBName, userName, passWord);
                    System.out.println("Database connected");

                    homeVBox = new VBox(home());
                    setCenter(homeVBox);
                } catch (ClassNotFoundException ex) {
                    prompt.setText(ex.getMessage());
                } catch (SQLException ex) {
                    prompt.setText(ex.getMessage());
                }
            }

            //setCenter(home());
            homeVBox = new VBox(home());
            setCenter(homeVBox);
        }
        );
        return vb1;
    }

    public ScrollPane generateQuestion(String val) throws IOException, Exception {
        ScrollPane qScrollPane = new ScrollPane();
        getQuestions(val);
        getChoices(val);
        String Ans = getAnswers(val);

        if (Ans.contains(" ")) {
            answerVal = Ans.substring(0, Ans.indexOf(' '));
            answerHint = Ans.substring(Ans.indexOf(' ') + 1);
        } else {
            answerVal = Ans;
        }

        System.out.println("Answer is: " + Ans.trim());

        //System.out.println(answerVal.length());
        if (answerVal.length() > 1) {
            SwingNode HiLitPrompt = new SwingNode();

            HiLitPrompt = highLightSyntax(questionResult.get(0));
            //HiLitPrompt = highLightWords(questionResult.get(0));
//            Label qPrompt = new Label(questionResult.get(0));
//            qPrompt.setPrefWidth(300);
//            qPrompt.setWrapText(true);

            Label labelresponse = new Label();
            labelresponse.setPrefWidth(200);
            labelresponse.setWrapText(true);

            Label labelHint = new Label();
            labelHint.setPrefWidth(200);
            labelHint.setWrapText(true);

            Label labelDB = new Label();
            labelDB.setPrefWidth(200);
            labelDB.setWrapText(true);

            Button button = new Button("Submit");
            Button btHome = new Button("Try Again");
            Button btHint = new Button("Hint");
            Button submitDB = new Button("Save Answer");
            submitDB.setVisible(false);
            btHint.setVisible(false);

            CheckBox box1, box2, box3, box4, box5;

            if (choiceResult.size() >= 1) {
                box1 = new CheckBox(choiceResult.get(0));
            } else {
                box1 = new CheckBox("a.");
                box1.setDisable(true);
            }
            box1.setPrefWidth(250);
            box1.setWrapText(true);

            if (choiceResult.size() >= 2) {
                box2 = new CheckBox(choiceResult.get(1));
            } else {
                box2 = new CheckBox("b.");
                box2.setDisable(true);
            }
            box2.setPrefWidth(250);
            box2.setWrapText(true);

            if (choiceResult.size() >= 3) {
                box3 = new CheckBox(choiceResult.get(2));
            } else {
                box3 = new CheckBox("c.");
                box3.setDisable(true);
                box3.setVisible(false);
            }
            box3.setPrefWidth(250);
            box3.setWrapText(true);

            if (choiceResult.size() >= 4) {
                box4 = new CheckBox(choiceResult.get(3));
            } else {
                box4 = new CheckBox("d.");
                box4.setDisable(true);
                box4.setVisible(false);
            }
            box4.setPrefWidth(250);
            box4.setWrapText(true);

            if (choiceResult.size() >= 5) {
                box5 = new CheckBox(choiceResult.get(4));
            } else {
                box5 = new CheckBox("e.");
                box5.setDisable(true);
                box5.setVisible(false);
            }
            box5.setPrefWidth(250);
            box5.setWrapText(true);

            //ToggleGroup question = new ToggleGroup();
            button.setDisable(true);

            box1.setOnAction(e -> button.setDisable(false));
            box2.setOnAction(e -> button.setDisable(false));
            box3.setOnAction(e -> button.setDisable(false));
            box4.setOnAction(e -> button.setDisable(false));
            box5.setOnAction(e -> button.setDisable(false));

            button.setOnAction(e -> {
                temp = "";
                if (box1.isSelected()) {
                    temp += "a";
                }
                if (box2.isSelected()) {
                    temp += "b";
                }
                if (box3.isSelected()) {
                    temp += "c";
                }
                if (box4.isSelected()) {
                    temp += "d";
                }
                if (box5.isSelected()) {
                    temp += "e";
                }
                System.out.println("TEMP " + temp);
                System.out.println("ANSWVAL " + answerVal);
                if (answerVal.contains(temp)) {
                    labelresponse.setText("Correct!");
                    userResult = 1;
                    submitDB.setVisible(true);

                } else {
                    labelresponse.setText("Wrong answer");
                    button.setDisable(true);
                    userResult = 0;
                    if (answerHint != null && answerHint != "") {
                        btHint.setVisible(true);
                        submitDB.setVisible(true);
                    }
                }
            }
            );
            btHint.setOnAction(e -> {
                labelHint.setText(answerHint);
                labelDB.setVisible(false);
            }
            );
            submitDB.setOnAction(e -> {
                saveResult(userName, chapterNumber, sectionNumber, qNumber, userResult);
                submitDB.setDisable(true);
                labelDB.setVisible(true);
                labelHint.setVisible(false);
                labelDB.setText("Successfully Stored to the Database");
            }
            );
            btHome.setOnAction(e -> {
                questionResult.clear();
                answerResult.clear();
                choiceResult.clear();
                answerVal = "";
                answerHint = "";
                numSections = 1;
                numQuestions = 1;
                flowPane.getChildren().clear();
                flowPane.getChildren().addAll(home());
                scrollPane.setContent(flowPane);
                setCenter(scrollPane);

            }
            );
            VBox layout = new VBox(5);
            //layout.getChildren().addAll(qPrompt, box1, box2, box3, box4, box5, button, labelresponse, btHome, submitDB, btHint, labelDB, labelHint);
            layout.getChildren().addAll(HiLitPrompt, box1, box2, box3, box4, box5, button, labelresponse, btHome, submitDB, btHint, labelDB, labelHint);
            layout.setPadding(new Insets(15, 15, 15, 15));
            layout.setAlignment(Pos.CENTER_LEFT);
            qScrollPane.setContent(layout);
        } else {
            SwingNode HiLitPrompt = new SwingNode();

            HiLitPrompt = highLightSyntax(questionResult.get(0));
            //HiLitPrompt = highLightWords(questionResult.get(0));

            //Label qPrompt = new Label(questionResult.get(0));
            //qPrompt.setPrefWidth(300);
            //qPrompt.setWrapText(true);
            Label labelresponse = new Label();
            labelresponse.setPrefWidth(200);
            labelresponse.setWrapText(true);

            Label labelHint = new Label();
            labelHint.setPrefWidth(200);
            labelHint.setWrapText(true);

            Label labelDB = new Label();
            labelDB.setPrefWidth(200);
            labelDB.setWrapText(true);

            Button button = new Button("Submit");
            Button btHome = new Button("Try Again");
            Button btHint = new Button("Hint");
            btHint.setVisible(false);
            Button submitDB = new Button("Save Answer");
            submitDB.setVisible(false);

            RadioButton radio1, radio2, radio3, radio4, radio5;

            if (choiceResult.size() >= 1) {
                radio1 = new RadioButton(choiceResult.get(0));
            } else {
                radio1 = new RadioButton("a.");
                radio1.setDisable(true);
            }
            radio1.setPrefWidth(250);
            radio1.setWrapText(true);

            if (choiceResult.size() >= 2) {
                radio2 = new RadioButton(choiceResult.get(1));
            } else {
                radio2 = new RadioButton("b.");
                radio2.setDisable(true);
            }
            radio2.setPrefWidth(250);
            radio2.setWrapText(true);

            if (choiceResult.size() >= 3) {
                radio3 = new RadioButton(choiceResult.get(2));
            } else {
                radio3 = new RadioButton("c.");
                radio3.setDisable(true);
                radio3.setVisible(false);
            }
            radio3.setPrefWidth(250);
            radio3.setWrapText(true);

            if (choiceResult.size() >= 4) {
                radio4 = new RadioButton(choiceResult.get(3));
            } else {
                radio4 = new RadioButton("d.");
                radio4.setDisable(true);
                radio4.setVisible(false);
            }
            radio4.setPrefWidth(250);
            radio4.setWrapText(true);

            if (choiceResult.size() >= 5) {
                radio5 = new RadioButton(choiceResult.get(4));
            } else {
                radio5 = new RadioButton("e.");
                radio5.setDisable(true);
                radio5.setVisible(false);
            }
            radio5.setPrefWidth(250);
            radio5.setWrapText(true);

            ToggleGroup question = new ToggleGroup();

            radio1.setToggleGroup(question);
            radio2.setToggleGroup(question);
            radio3.setToggleGroup(question);
            radio4.setToggleGroup(question);
            radio5.setToggleGroup(question);

            button.setDisable(true);

            radio1.setOnAction(e -> button.setDisable(false));
            radio2.setOnAction(e -> button.setDisable(false));
            radio3.setOnAction(e -> button.setDisable(false));
            radio4.setOnAction(e -> button.setDisable(false));
            radio5.setOnAction(e -> button.setDisable(false));

            button.setOnAction(e -> {
                submitDB.setVisible(true);
                if (radio1.isSelected() && answerVal.contains(radio1.getText().substring(0, 1))) {
                    userResult = 1;
                    labelresponse.setText("Correct!");
                    button.setDisable(true);
                } else if (radio2.isSelected() && answerVal.contains(radio2.getText().substring(0, 1))) {
                    userResult = 1;
                    labelresponse.setText("Correct!");
                    button.setDisable(true);
                } else if (radio3.isSelected() && answerVal.contains(radio3.getText().substring(0, 1))) {
                    userResult = 1;
                    labelresponse.setText("Correct!");
                    button.setDisable(true);
                } else if (radio4.isSelected() && answerVal.contains(radio4.getText().substring(0, 1))) {
                    userResult = 1;
                    labelresponse.setText("Correct!");
                    button.setDisable(true);
                } else if (radio5.isSelected() && answerVal.contains(radio5.getText().substring(0, 1))) {
                    userResult = 1;
                    labelresponse.setText("Correct!");
                    button.setDisable(true);
                } else {
                    labelresponse.setText("Wrong answer");
                    userResult = 0;
                    button.setDisable(true);
                    if (answerHint != null && answerHint != "") {
                        btHint.setVisible(true);
                    }
                }
            }
            );
            btHint.setOnAction(e -> {
                labelDB.setVisible(false);
                labelHint.setText(answerHint);
            }
            );
            submitDB.setOnAction(e -> {
                saveResult(userName, chapterNumber, sectionNumber, qNumber, userResult);
                submitDB.setDisable(true);
                labelDB.setVisible(true);
                labelHint.setVisible(false);
                labelDB.setText("Successfully Stored to the Database");
            }
            );
            btHome.setOnAction(e -> {
                questionResult.clear();
                answerResult.clear();
                choiceResult.clear();
                answerVal = "";
                answerHint = "";
                numSections = 1;
                numQuestions = 1;
                flowPane.getChildren().clear();
                flowPane.getChildren().addAll(home());
                setCenter(scrollPane);

                //setCenter(homeVBox);
            }
            );
            VBox layout = new VBox(5);
            layout.getChildren().addAll(HiLitPrompt, radio1, radio2, radio3, radio4, radio5, button, labelresponse, btHome, submitDB, btHint, labelDB, labelHint);
            layout.setPadding(new Insets(15, 15, 15, 15));
            layout.setAlignment(Pos.CENTER_LEFT);
            qScrollPane.setContent(layout);

        }
        return qScrollPane;
    }

    private static String getAnswers(String answerNumber) throws FileNotFoundException, IOException, Exception {
        int aNumber = Integer.parseInt(answerNumber);

        System.out.println("-------------------Location---------------------");
        System.out.println("Answer number " + answerNumber);
        System.out.println("chapter number " + chapterNumber);

        for (int i = 0; i < arr.length; i++) {
            if (arr[i].startsWith("Key") || arr[i].startsWith("key")) {
                answerResult.add(arr[i].substring(4, arr[i].length()));
            }
        }
        String result = answerResult.get(aNumber - 1);
        return result;
    }

    private static ArrayList<String> getQuestions(String questionNumber) throws FileNotFoundException, IOException, Exception {
        //File f = new File("C:\\Users\\WiLhS\\Desktop\\mcquestions\\chapter" + chapterNumber + ".txt"); 
        //Get question part
        String str = "";
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].startsWith(questionNumber + ". ") || arr[i].startsWith(questionNumber + ".")) {
                for (int j = i; j < arr.length; j++) {
                    str += arr[j];
                    str += "\n";
                    if (arr[j + 1].startsWith("a.")) {
                        break;
                    }
                }
            }
        }
        //Add question
        questionResult.add(str);

        System.out.println("----------------question result: ---------------");
        for (String i : questionResult) {
            System.out.print(i + "\n ");
        }
        return questionResult;
    }

    private static ArrayList<String> getChoices(String questionNumber) throws FileNotFoundException, IOException, Exception {
        //Get Choices        
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].startsWith(questionNumber + ".")) {
                for (int j = i; j < arr.length; j++) {
                    if (arr[j].startsWith("a.")) {
                        choiceResult.add(arr[j]);
                    } else if (arr[j].startsWith("b.")) {
                        choiceResult.add(arr[j]);
                    } else if (arr[j].startsWith("c.")) {
                        choiceResult.add(arr[j]);
                    } else if (arr[j].startsWith("d.")) {
                        choiceResult.add(arr[j]);
                    } else if (arr[j].startsWith("e.")) {
                        choiceResult.add(arr[j]);
                    }
                    if (arr[j + 1].startsWith("Key:") || arr[j + 1].startsWith("key:")) {
                        break;
                    }
                }
            }
        }
        System.out.println("------------question choices: ---------------");
        for (String i : choiceResult) {
            System.out.print(i + "\n ");
        }
        return choiceResult;
    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> System.out.println("Menu")));
        appBar.setTitleText("CSCI-5520 Project 1");
        appBar.getActionItems().add(MaterialDesignIcon.SEARCH.button(e -> System.out.println("Search")));
    }

    public static ObservableList<String> setSections(String i) throws Exception {
        ObservableList<String> temp = null;
        InputStream is = BasicView.class.getResourceAsStream("/mcquestions/chapter" + i + ".txt");
        String inputLine;
        String[] st;

        ArrayList<String> tempArr = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(is))) {
            while ((inputLine = in.readLine()) != null) {

                if (inputLine.startsWith("Section") || inputLine.startsWith("section")) {
                    st = inputLine.trim().split("\\s+", 3);
                    tempArr.add(st[1]);
                }

            }
        }
        temp = FXCollections.observableArrayList(tempArr);
        return temp;
    }

    public static ObservableList<String> setQuestions(String i, String j) throws Exception {
        ObservableList<String> temp = null;
        InputStream is = BasicView.class.getResourceAsStream("/mcquestions/chapter" + i + ".txt");
        String inputLine;
        boolean makeChange = false;
        boolean sec = false;
        String[] st;

        ArrayList<String> tempArr = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(is))) {
            while ((inputLine = in.readLine()) != null) {

                if (inputLine.startsWith("Section") || inputLine.startsWith("section")) {
                    sec = false;
                }

                if (makeChange && sec) {
                    st = inputLine.trim().split("\\.", 2);
                    tempArr.add(st[0]);
                }

                makeChange = false;

                if (inputLine.startsWith("Section " + j + " ") || inputLine.startsWith("section " + j + "") || inputLine.startsWith("Sections " + j + "") || inputLine.startsWith("sections " + j + "") || inputLine.startsWith("Section: " + j + "")) {
                    makeChange = true;
                    sec = true;
                }

                if (inputLine.startsWith("#")) {
                    makeChange = true;
                }

            }
        }
        temp = FXCollections.observableArrayList(tempArr);
        return temp;
    }

    public static String[] getText() throws Exception {
        InputStream is = null;
        //http://php.wilhsy867.s156.eatj.com/mcquestions/chapter1.txt
        //URL url = new URL("http://web-students.armstrong.edu/~ws8578/mcquestions/chapter" + chapterNumber + ".txt");
        //URL url = new URL("http://php.wilhsy867.s156.eatj.com/mcquestions/chapter" + chapterNumber + ".txt");
        is = BasicView.class.getResourceAsStream("/mcquestions/chapter" + chapterNumber + ".txt");
        String inputLine;

        ArrayList<String> arr = new ArrayList<String>();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(is))) {
            while ((inputLine = in.readLine()) != null) {
                arr.add(inputLine);
                if (inputLine.startsWith("Section") || inputLine.startsWith("section")) {
                    numSections++;
                }

                if (inputLine.startsWith("#")) {
                    numQuestions++;
                }

            }
        }
        System.out.println("The number of questions in chapter " + chapterNumber + " is " + numQuestions);
        System.out.println("The number of sections in chapter " + chapterNumber + " is " + numSections);
        String[] arr2 = arr.toArray(new String[arr.size()]);

        return arr2;
    }

    public static SwingNode highLightSyntax(String str) throws BadLocationException {
        SwingNode swingNode = new SwingNode();

        SwingUtilities.invokeLater(
                new Runnable() {
            @Override
            public void run() {
                JTextPane pane = new JTextPane();
                //-------------------- wrap text -------------------------------
                JPanel noWrapPanel;
                JScrollPane scrollPane;
                noWrapPanel = new JPanel(new BorderLayout());
                scrollPane = new JScrollPane(noWrapPanel);
                //scrollPane.set
                if (str.length() < 150) {

                    noWrapPanel.setPreferredSize(new Dimension(425, 200));
                    noWrapPanel.add(pane);
                    scrollPane.setPreferredSize(new Dimension(425, 200));
                    scrollPane.setViewportView(pane);
                }
                if (str.length() > 150) {
                    noWrapPanel.setPreferredSize(new Dimension(425, 400));
                    noWrapPanel.add(pane);
                    scrollPane.setPreferredSize(new Dimension(425, 400));
                    scrollPane.setViewportView(pane);
                }
                //--------------------------------------------------------------
                SimpleAttributeSet set = new SimpleAttributeSet();

                // Set the attributes before adding text
                pane.setCharacterAttributes(set, true);
                Document doc;
                for (String word : str.split(" ")) {
                    if (word.contains("int") && !word.contains("integer") && !word.contains("print") && !word.contains("point")) {
                        set = new SimpleAttributeSet();
                        StyleConstants.setFontSize(set, 22);
                        String temp1;
                        String temp2;
                        String temp3;
                        int subWord = word.indexOf("int");

                        if (subWord == 0) {
                            try {
                                StyleConstants.setForeground(set, Color.cyan.darker());
                                temp1 = word.substring(subWord, subWord + 3);
                                temp2 = word.substring(subWord + 3, word.length());
                                //insert int first part
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp1, set);
                                //insert the rest of the string
                                StyleConstants.setForeground(set, Color.black);
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp2, set);
                                //Insert space
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), " ", set);
                            } catch (BadLocationException ex) {
                                Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        if (subWord != 0) {

                            try {
                                temp1 = word.substring(0, subWord);
                                temp2 = word.substring(subWord, subWord + 3);
                                temp3 = word.substring(subWord + 3, word.length());
                                //insert first part
                                StyleConstants.setForeground(set, Color.black);
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp1, set);
                                //insert int second part
                                StyleConstants.setForeground(set, Color.cyan.darker());
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp2, set);
                                //insert the rest of the string
                                StyleConstants.setForeground(set, Color.black);
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp3, set);
                                //Insert space
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), " ", set);
                            } catch (BadLocationException ex) {
                                Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                    }

                    if (word.contains("String")) {
                        set = new SimpleAttributeSet();
                        StyleConstants.setFontSize(set, 22);
                        String temp1;
                        String temp2;
                        String temp3;
                        int subWord = word.indexOf("String");

                        if (subWord == 0) {
                            try {
                                StyleConstants.setForeground(set, Color.blue.darker());
                                temp1 = word.substring(subWord, subWord + 6);
                                temp2 = word.substring(subWord + 6, word.length());
                                //insert int first part
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp1, set);
                                //insert the rest of the string
                                StyleConstants.setForeground(set, Color.black);
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp2, set);
                                //Insert space
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), " ", set);
                            } catch (BadLocationException ex) {
                                Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        if (subWord != 0) {

                            try {
                                temp1 = word.substring(0, subWord);
                                temp2 = word.substring(subWord, subWord + 6);
                                temp3 = word.substring(subWord + 6, word.length());
                                //insert first part
                                StyleConstants.setForeground(set, Color.black);
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp1, set);
                                //insert int second part
                                StyleConstants.setForeground(set, Color.blue.darker());
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp2, set);
                                //insert the rest of the string
                                StyleConstants.setForeground(set, Color.black);
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp3, set);
                                //Insert space
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), " ", set);
                            } catch (BadLocationException ex) {
                                Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }

                    if (word.contains("public")) {
                        set = new SimpleAttributeSet();
                        StyleConstants.setFontSize(set, 22);
                        String temp1;
                        String temp2;
                        String temp3;
                        int subWord = word.indexOf("public");

                        if (subWord == 0) {
                            try {
                                StyleConstants.setForeground(set, Color.magenta.darker().darker().darker());
                                temp1 = word.substring(subWord, subWord + 6);
                                temp2 = word.substring(subWord + 6, word.length());
                                //insert int first part
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp1, set);
                                //insert the rest of the string
                                StyleConstants.setForeground(set, Color.black);
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp2, set);
                                //Insert space
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), " ", set);
                            } catch (BadLocationException ex) {
                                Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        if (subWord != 0) {

                            try {
                                temp1 = word.substring(0, subWord);
                                temp2 = word.substring(subWord, subWord + 6);
                                temp3 = word.substring(subWord + 6, word.length());
                                //insert first part
                                StyleConstants.setForeground(set, Color.black);
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp1, set);
                                //insert int second part
                                StyleConstants.setForeground(set, Color.magenta.darker().darker().darker());
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp2, set);
                                //insert the rest of the string
                                StyleConstants.setForeground(set, Color.black);
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp3, set);
                                //Insert space
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), " ", set);
                            } catch (BadLocationException ex) {
                                Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    if (word.contains("class")) {
                        set = new SimpleAttributeSet();
                        StyleConstants.setFontSize(set, 22);
                        String temp1;
                        String temp2;
                        String temp3;
                        int subWord = word.indexOf("class");

                        if (subWord == 0) {
                            try {
                                StyleConstants.setForeground(set, Color.magenta.darker().darker().darker());
                                temp1 = word.substring(subWord, subWord + 5);
                                temp2 = word.substring(subWord + 5, word.length());
                                //insert int first part
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp1, set);
                                //insert the rest of the string
                                StyleConstants.setForeground(set, Color.black);
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp2, set);
                                //Insert space
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), " ", set);
                            } catch (BadLocationException ex) {
                                Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        if (subWord != 0) {

                            try {
                                temp1 = word.substring(0, subWord);
                                temp2 = word.substring(subWord, subWord + 5);
                                temp3 = word.substring(subWord + 5, word.length());
                                //insert first part
                                StyleConstants.setForeground(set, Color.black);
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp1, set);
                                //insert int second part
                                StyleConstants.setForeground(set, Color.magenta.darker().darker().darker());
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp2, set);
                                //insert the rest of the string
                                StyleConstants.setForeground(set, Color.black);
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp3, set);
                                //Insert space
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), " ", set);
                            } catch (BadLocationException ex) {
                                Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }

                    if (word.contains("static")) {
                        set = new SimpleAttributeSet();
                        StyleConstants.setFontSize(set, 22);
                        String temp1;
                        String temp2;
                        String temp3;
                        int subWord = word.indexOf("static");

                        if (subWord == 0) {
                            try {
                                StyleConstants.setForeground(set, Color.magenta.darker().darker().darker());
                                temp1 = word.substring(subWord, subWord + 6);
                                temp2 = word.substring(subWord + 6, word.length());
                                //insert int first part
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp1, set);
                                //insert the rest of the string
                                StyleConstants.setForeground(set, Color.black);
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp2, set);
                                //Insert space
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), " ", set);
                            } catch (BadLocationException ex) {
                                Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        if (subWord != 0) {

                            try {
                                temp1 = word.substring(0, subWord);
                                temp2 = word.substring(subWord, subWord + 6);
                                temp3 = word.substring(subWord + 6, word.length());
                                //insert first part
                                StyleConstants.setForeground(set, Color.black);
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp1, set);
                                //insert int second part
                                StyleConstants.setForeground(set, Color.magenta.darker().darker().darker());
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp2, set);
                                //insert the rest of the string
                                StyleConstants.setForeground(set, Color.black);
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp3, set);
                                //Insert space
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), " ", set);
                            } catch (BadLocationException ex) {
                                Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }

                    if (word.contains("void")) {
                        set = new SimpleAttributeSet();
                        StyleConstants.setFontSize(set, 22);
                        String temp1;
                        String temp2;
                        String temp3;
                        int subWord = word.indexOf("void");

                        if (subWord == 0) {
                            try {
                                StyleConstants.setForeground(set, Color.magenta.darker().darker().darker());
                                temp1 = word.substring(subWord, subWord + 4);
                                temp2 = word.substring(subWord + 4, word.length());
                                //insert int first part
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp1, set);
                                //insert the rest of the string
                                StyleConstants.setForeground(set, Color.black);
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp2, set);
                                //Insert space
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), " ", set);
                            } catch (BadLocationException ex) {
                                Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        if (subWord != 0) {

                            try {
                                temp1 = word.substring(0, subWord);
                                temp2 = word.substring(subWord, subWord + 4);
                                temp3 = word.substring(subWord + 4, word.length());
                                //insert first part
                                StyleConstants.setForeground(set, Color.black);
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp1, set);
                                //insert int second part
                                StyleConstants.setForeground(set, Color.magenta.darker().darker().darker());
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp2, set);
                                //insert the rest of the string
                                StyleConstants.setForeground(set, Color.black);
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), temp3, set);
                                //Insert space
                                doc = pane.getStyledDocument();
                                doc.insertString(doc.getLength(), " ", set);
                            } catch (BadLocationException ex) {
                                Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } else if (!word.contains("String") && !word.contains("public") && !word.contains("class")
                            && !word.contains("static") && !word.contains("void") && !word.equals("int")) {
                        try {
                            set = new SimpleAttributeSet();
                            StyleConstants.setFontSize(set, 22);
                            StyleConstants.setForeground(set, Color.black);

                            doc = pane.getStyledDocument();
                            doc.insertString(doc.getLength(), word + " ", set);
                        } catch (BadLocationException ex) {
                            Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                pane.setCaretPosition(0);
                swingNode.setContent(scrollPane);
            }
        }
        );
        return swingNode;
    }

    //----------------------------------------------------------------------------------------------------
//    public static SwingNode highLightWords(String str) throws BadLocationException {
//        SwingNode swingNode = new SwingNode();
//
//        SwingUtilities.invokeLater(
//                new Runnable() {
//            @Override
//            public void run() {
//                JTextPane pane = new JTextPane();
//                //-------------------- wrap text -------------------------------
//                JPanel noWrapPanel;
//                JScrollPane scrollPane;
//                noWrapPanel = new JPanel(new BorderLayout());
//                scrollPane = new JScrollPane(noWrapPanel);
//                //scrollPane.set
//                if (str.length() < 150) {
//
//                    noWrapPanel.setPreferredSize(new Dimension(425, 200));
//                    noWrapPanel.add(pane);
//                    scrollPane.setPreferredSize(new Dimension(425, 200));
//                    scrollPane.setViewportView(pane);
//                }
//                if (str.length() > 150) {
//                    noWrapPanel.setPreferredSize(new Dimension(425, 400));
//                    noWrapPanel.add(pane);
//                    scrollPane.setPreferredSize(new Dimension(425, 400));
//                    scrollPane.setViewportView(pane);
//                }
//                //--------------------------------------------------------------
//                SimpleAttributeSet set = new SimpleAttributeSet();
//
//                // Set the attributes before adding text
//                pane.setCharacterAttributes(set, true);
//                Document doc;
//                StyleConstants.setFontSize(set, 22);
//                String temp1;
//                String temp2;
//                String temp3;
//                String trimmedWord;
//                String currentWord = null;
//                int keyLocation;
//                boolean inList = false;
//                boolean antiInList = false;
//                //-----------------------------------------------------------------------
//                //Get a word using whitespace as a delimiter
//                for (String word : str.split(" ")) {
//                    for (String trim : keyWords) {
//                        if (word.contains(trim)) {
//                            inList = true;
//                            trimmedWord = trim;
//                            //get index of matched value
//                            keyLocation = Arrays.asList(keyWords).indexOf(trimmedWord);
//                            //get string value of match
//                            currentWord = keyWords[keyLocation];
//                        }
//                    }
//                    for (String trim : antiKeyWords) {
//                        if (word.contains(trim)) {
//                            antiInList = true;
//                        }
//                    }
//                    //check if the word is in the list and neglect false positives
//                    if (inList == true && antiInList == false && currentWord != null) {
//
//                        int subWord = word.indexOf(currentWord);
//                        if (subWord == 0) {
//                            try {
//                                StyleConstants.setForeground(set, Color.cyan.darker());
//                                temp1 = word.substring(subWord, subWord + currentWord.length());
//                                temp2 = word.substring(subWord + currentWord.length(), word.length());
//                                //insert int first part
//                                doc = pane.getStyledDocument();
//                                doc.insertString(doc.getLength(), temp1, set);
//                                //insert the rest of the string
//                                StyleConstants.setForeground(set, Color.black);
//                                doc = pane.getStyledDocument();
//                                doc.insertString(doc.getLength(), temp2, set);
//                                //Insert space
//                                doc = pane.getStyledDocument();
//                                doc.insertString(doc.getLength(), " ", set);
//                            } catch (BadLocationException ex) {
//                                Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
//                            }
//                        }
//
//                        if (subWord != 0 && subWord != -1) {
//
//                            try {
//                                temp1 = word.substring(0, subWord);
//                                temp2 = word.substring(subWord, subWord + currentWord.length());
//                                temp3 = word.substring(subWord + currentWord.length(), word.length());
//                                //insert first part
//                                StyleConstants.setForeground(set, Color.black);
//                                doc = pane.getStyledDocument();
//                                doc.insertString(doc.getLength(), temp1, set);
//                                //insert int second part
//                                StyleConstants.setForeground(set, Color.cyan.darker());
//                                doc = pane.getStyledDocument();
//                                doc.insertString(doc.getLength(), temp2, set);
//                                //insert the rest of the string
//                                StyleConstants.setForeground(set, Color.black);
//                                doc = pane.getStyledDocument();
//                                doc.insertString(doc.getLength(), temp3, set);
//                                //Insert space
//                                doc = pane.getStyledDocument();
//                                doc.insertString(doc.getLength(), " ", set);
//                            } catch (BadLocationException ex) {
//                                Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
//                            }
//                        }
//
//                    } //-----------------------------------------------------------------------
//                    else if (!inList) {
//                        try {
//                            set = new SimpleAttributeSet();
//                            StyleConstants.setFontSize(set, 22);
//                            StyleConstants.setForeground(set, Color.black);
//
//                            doc = pane.getStyledDocument();
//                            doc.insertString(doc.getLength(), word + " ", set);
//                        } catch (BadLocationException ex) {
//                            Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
//                }
//                pane.setCaretPosition(0);
//                swingNode.setContent(scrollPane);
//            }
//        }
//        );
//        return swingNode;
//    }
}
