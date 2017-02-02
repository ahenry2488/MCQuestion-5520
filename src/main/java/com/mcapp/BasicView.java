/*
Still need
- section choice functionality
- section and question dropbox length accurate and load on chapter choice
- database access 
    - user login and upload result
    - access MC files from DB
- Hint printout
- Insets left 
- Radio Button style


 */
package com.mcapp;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.impl.charm.a.b.b.s;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import static jdk.nashorn.internal.objects.ArrayBufferView.buffer;

public class BasicView extends View {

    static ArrayList<String> questionResult = new ArrayList<String>(100);
    static ArrayList<String> choiceResult = new ArrayList<String>(100);
    static ArrayList<String> answerResult = new ArrayList<String>(100);

    //Chapter numbers List for drop Down menu
    static ObservableList<String> chList = FXCollections.observableArrayList();
    //List to store the number of sections
    static ObservableList<String> sList = FXCollections.observableArrayList();
    //List to store the number of questions
    static ObservableList<String> qList = FXCollections.observableArrayList();

    FlowPane flowPane = new FlowPane();
    ScrollPane scrollPane = new ScrollPane();

    static String chapterNumber;
    static String sectionNumber;
    static String qNumber;
    static String answerHint;
    static String answerVal;

    public BasicView(String name) {
        super(name);
        flowPane.setPadding(new Insets(15, 12, 15, 12));
        flowPane.setAlignment(Pos.TOP_CENTER);
        flowPane.setVgap(75);
        flowPane.setPrefWidth(175);
        flowPane.getChildren().addAll(home());
        scrollPane.setContent(flowPane);
        setCenter(scrollPane);
    }

    public VBox home() {

        //generate a list of strings from 1 to 40 for chapter choice
        for (int i = 1; i <= 44; i++) {
            String str = Integer.toString(i);
            chList.add(str);
        }

        Label prompt = new Label("Please Choose Chapter Section and Question");
        prompt.setAlignment(Pos.CENTER);

        Label chLabel = new Label("Chapter");

        ComboBox<String> chapter = new ComboBox(chList);
        chapter.setEditable(false);
        chapter.setPrefWidth(75);
        chapter.setValue(chList.get(0));

        Label sLabel = new Label("Section");
        ComboBox<String> section = new ComboBox(chList);
        section.setEditable(false);
        section.setPrefWidth(75);
        section.getSelectionModel().selectFirst();

        Label qLabel = new Label("Question");
        ComboBox<String> question = new ComboBox(chList);
        question.setEditable(false);
        question.setPrefWidth(75);
        question.getSelectionModel().selectFirst();

        Button btConfirm = new Button();
        btConfirm.setText("Choose Question");

        btConfirm.setOnAction(e -> {
            try {
                chapterNumber = chapter.getValue();
                setCenter(generateQuestion(question.getValue()));

            } catch (IOException ex) {
                Logger.getLogger(BasicView.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        VBox vb1 = new VBox(25);
        vb1.setPadding(new Insets(15, 0, 0, 0));
        vb1.getChildren().addAll(prompt, chLabel, chapter, sLabel, section, qLabel, question, btConfirm);
        vb1.setAlignment(Pos.CENTER);
        return vb1;
    }

    public ScrollPane generateQuestion(String val) throws IOException {
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

        Label qPrompt = new Label(questionResult.get(0));
        qPrompt.setPrefWidth(200);
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
                btHint.setVisible(true);

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
            setCenter(scrollPane);
        }
        );
        VBox layout = new VBox(5);
        layout.getChildren().addAll(qPrompt, radio1, radio2, radio3, radio4, radio5, button, labelresponse, btHome, btHint, labelHint);
        layout.setPadding(new Insets(15, 15, 15, 70));
        layout.setAlignment(Pos.CENTER_LEFT);
        qScrollPane.setContent(layout);
        return qScrollPane;
    }

    private static String getAnswers(String answerNumber) throws FileNotFoundException, IOException {
        int aNumber = Integer.parseInt(answerNumber);

        System.out.println("-------------------Location---------------------");
        System.out.println("Answer number" + answerNumber);
        System.out.println("chapter number" + chapterNumber);

        String s = new Scanner(new URL("http://web-students.armstrong.edu/~ws8578/mcquestions/chapter" + chapterNumber + ".txt").openStream(), "UTF-8").useDelimiter("\\A").next();
        //split into array
        String[] arr = s.split("\n");

        for (int i = 0; i < arr.length; i++) {
            if (arr[i].startsWith("Key") || arr[i].startsWith("key")) {
                answerResult.add(arr[i].substring(4, arr[i].length()));
//                String str = Integer.toString(i);
//                qList.add(str);
            }
        }
        System.out.println("---------------Answer Result----------------");
        for (String i : answerResult) {
            System.out.print(i + "\n ");
        }
        String result = answerResult.get(aNumber - 1);
        return result;
    }
////////////////////////////////////////////////////////////////////////////////

    private static ArrayList<String> getQuestions(String questionNumber) throws FileNotFoundException, IOException {
        //File f = new File("C:\\Users\\WiLhS\\Desktop\\mcquestions\\chapter" + chapterNumber + ".txt");
        //File f = new File("C:\\Users\\WiLhS\\Desktop\\mcquestions\\chapter" + chapterNumber + ".txt");

        String s = new Scanner(new URL("http://web-students.armstrong.edu/~ws8578/mcquestions/chapter" + chapterNumber + ".txt").openStream(), "UTF-8").useDelimiter("\\A").next();
        String[] arr = s.split("\n");

        //Get question part
        String str = "";
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].startsWith(questionNumber + ". ") || arr[i].startsWith(questionNumber + ".")) {
                for (int j = i; j < arr.length; j++) {
                    str += arr[j];
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

    private static ArrayList<String> getChoices(String questionNumber) throws FileNotFoundException, IOException {

        String s = new Scanner(new URL("http://web-students.armstrong.edu/~ws8578/mcquestions/chapter" + chapterNumber + ".txt").openStream(), "UTF-8").useDelimiter("\\A").next();

        //Split into array for processing
        String[] arr = s.split("\n");

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

    public static String getText(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        return response.toString();
    }

}
