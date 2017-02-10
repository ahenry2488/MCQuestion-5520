/*
Still need
- section choice functionality
- section and question dropbox length accurate and load on chapter choice
- database access 
    - user login and upload result
- Radio Button style
 */
package com.mcapp;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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

public class BasicView extends View {

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
    static int numSections;
    static int numQuestions = 1;

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

    public VBox home() {

        //generate a list of strings from 1 to 44 for chapter choice
        for (int i = 1; i <= 44; i++) {
            String str = Integer.toString(i);
            chList.add(str);
        }

        Label prompt = new Label("      Please Choose Chapter, Section and Question");
        prompt.setAlignment(Pos.CENTER);

        Label chLabel = new Label("");

        ComboBox chapter = new ComboBox(chList);
        chapter.setPrefWidth(110);
        chapter.getStyleClass().add(qNumber);
        chapter.setPromptText("Chapter");
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
        ComboBox section = new ComboBox(chList);
        section.setPrefWidth(110);
        section.setPromptText("Section");
        //section.getSelectionModel().selectFirst();
        section.setCellFactory(p -> new ListCell<String>() {
            private String item;
            {
                //setOnTouchPressed(e -> section.getSelectionModel().select(item));
                //setOnMousePressed(e -> section.getSelectionModel().select(item));
                //setOnMouseClicked(e -> section.getSelectionModel().select(item));
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
        //final Spinner<String> question = new Spinner<String>();
        ComboBox question = new ComboBox(chList);
        //question.setSkin(value);       
        //question.getStyleClass().add(question.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL);
        // Value factory.
//        SpinnerValueFactory<String> valueFactory =  new SpinnerValueFactory.ListSpinnerValueFactory(chList);
//        question.setValueFactory(valueFactory);        
        question.setPrefWidth(110);
        question.setPromptText("Question");
        question.setCellFactory(p -> new ListCell<String>() {
            private String item;

            {
                //setOnTouchPressed(e -> question.getSelectionModel().select(item));
                //setOnMousePressed(e -> question.getSelectionModel().select(item));
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
                arr = getText();
                setCenter(generateQuestion(question.getValue().toString()));

            } catch (IOException ex) {
                Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        VBox vb1 = new VBox(25);
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
        prompt.setText("\tThis will connect to your local sql DB \n\tChange line 235 to a relation in your DB \n\n\tIntro To JAVA Programming 11th Ed.\n\tY. Daniel Liang");

        Label Username = new Label();
        Username.setPrefWidth(100);
        Username.setText("User Name");

        Label pWord = new Label();
        pWord.setPrefWidth(100);
        pWord.setText("Password");

        TextField usrNameText = new TextField();
        usrNameText.setPrefWidth(150);
        usrNameText.setText("");
        PasswordField  pWordText = new PasswordField ();
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
                    PreparedStatement pstmt;
                    // Load the JDBC driver
                    Class.forName("com.mysql.jdbc.Driver");
                    System.out.println("Driver loaded");
                    // Establish a connection
                    //Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/wilhsy867?autoReconnect=true", "wilhsy867", "310998007");
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + dBName, userName, passWord);
                    System.out.println("Database connected");
                    //pstmt = conn.prepareStatement("INSERT INTO `scores` (`ch#`, `sec#`, `firstName`, `mI`, `address`, `city`, `state`, `telephone`) VALUES (" + ID + " , '" + lastName + "', '" + firstName + "', '" + mI + "', '" + address + "', '" + city + "', '" + state + "', '" + telephone + "')");
                    //pstmt.executeUpdate();
                    //setCenter(home());
                } catch (ClassNotFoundException ex) {                    
                    prompt.setText(ex.getMessage());
                } catch (SQLException ex) {                                      
                    prompt.setText(ex.getMessage());
                }
            }
            setCenter(home());
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
        String ansLength = Ans.trim();
        System.out.println("Answer is: " + Ans.trim());
        
        

        Label qPrompt = new Label(questionResult.get(0));
        qPrompt.setPrefWidth(300);
        qPrompt.setWrapText(true);

        Label labelresponse = new Label();
        labelresponse.setPrefWidth(200);
        labelresponse.setWrapText(true);

        Label labelHint = new Label();
        labelHint.setPrefWidth(200);
        labelHint.setWrapText(true);

        Button button = new Button("Submit");
        Button btHome = new Button("Try Again");
        Button btHint = new Button("Hint");
        btHint.setVisible(false);

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
            if (radio1.isSelected() && answerVal.contains(radio1.getText().substring(0, 1))) {
                labelresponse.setText("Correct!");
                button.setDisable(true);
            } else if (radio2.isSelected() && answerVal.contains(radio2.getText().substring(0, 1))) {
                labelresponse.setText("Correct!");
                button.setDisable(true);
            } else if (radio3.isSelected() && answerVal.contains(radio3.getText().substring(0, 1))) {
                labelresponse.setText("Correct!");
                button.setDisable(true);
            } else if (radio4.isSelected() && answerVal.contains(radio4.getText().substring(0, 1))) {
                labelresponse.setText("Correct!");
                button.setDisable(true);
            } else if (radio5.isSelected() && answerVal.contains(radio5.getText().substring(0, 1))) {
                labelresponse.setText("Correct!");
                button.setDisable(true);
            } else {
                labelresponse.setText("Wrong answer");
                button.setDisable(true);
                if (answerHint != null && answerHint != "") {
                    btHint.setVisible(true);
                }
            }
        }
        );
        btHint.setOnAction(e -> {
            labelHint.setText(answerHint);
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
        }
        );
        VBox layout = new VBox(5);
        layout.getChildren().addAll(qPrompt, radio1, radio2, radio3, radio4, radio5, button, labelresponse, btHome, btHint, labelHint);
        layout.setPadding(new Insets(15, 15, 15, 15));
        layout.setAlignment(Pos.CENTER_LEFT);
        qScrollPane.setContent(layout);
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
        System.out.println("---------------Answer Result----------------");
        for (String i : answerResult) {
            System.out.print(i + "\n ");
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

    public static String[] getText() throws Exception {
        //http://php.wilhsy867.s156.eatj.com/mcquestions/chapter1.txt
        //URL url = new URL("http://web-students.armstrong.edu/~ws8578/mcquestions/chapter" + chapterNumber + ".txt");
        URL url = new URL("http://php.wilhsy867.s156.eatj.com/mcquestions/chapter" + chapterNumber + ".txt");
        String inputLine;

        ArrayList<String> arr = new ArrayList<String>();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream()))) {
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

}
