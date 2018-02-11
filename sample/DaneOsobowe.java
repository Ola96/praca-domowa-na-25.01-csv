package sample;


import javafx.event.ActionEvent;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import com.opencsv.CSVReader;
import javafx.util.StringConverter;

import javax.xml.transform.Result;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.ArrayList;


public class DaneOsobowe implements HierarchicalController<MainController> {

    public TextField imie;
    public TextField nazwisko;
    public TextField pesel;
    public TextField indeks;
    public TableView<Student> tabelka;
    private MainController parentController;

    public DaneOsobowe() throws FileNotFoundException {
    }

    public void dodaj(ActionEvent actionEvent) {
        Student st = new Student();
        st.setName(imie.getText());
        st.setSurname(nazwisko.getText());
        st.setPesel(pesel.getText());
        st.setIdx(indeks.getText());
        tabelka.getItems().add(st);
    }

    public void setParentController(MainController parentController) {
        this.parentController = parentController;
        tabelka.getItems().addAll(parentController.getDataContainer().getStudents());
        tabelka.setEditable(true);
    }

    public void usunZmiany() {
        tabelka.getItems().clear();
        tabelka.getItems().addAll(parentController.getDataContainer().getStudents());
    }

    public MainController getParentController() {
        return parentController;
    }

    public void initialize() {
        for (TableColumn<Student, ?> studentTableColumn : tabelka.getColumns()) {
            if ("imie".equals(studentTableColumn.getId())) {
                TableColumn<Student, String> imieColumn = (TableColumn<Student, String>) studentTableColumn;
                imieColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                imieColumn.setCellFactory(TextFieldTableCell.forTableColumn());
                imieColumn.setOnEditCommit((val) -> {
                    val.getTableView().getItems().get(val.getTablePosition().getRow()).setName(val.getNewValue());
                });
            } else if ("nazwisko".equals(studentTableColumn.getId())) {
                studentTableColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
                ((TableColumn<Student, String>) studentTableColumn).setCellFactory(TextFieldTableCell.forTableColumn());
            } else if ("pesel".equals(studentTableColumn.getId())) {
                studentTableColumn.setCellValueFactory(new PropertyValueFactory<>("pesel"));
                ((TableColumn<Student, String>) studentTableColumn).setCellFactory(TextFieldTableCell.forTableColumn());
            } else if ("indeks".equals(studentTableColumn.getId())) {
                studentTableColumn.setCellValueFactory(new PropertyValueFactory<>("idx"));
                ((TableColumn<Student, String>) studentTableColumn).setCellFactory(TextFieldTableCell.forTableColumn());
            }
        }

    }

    public void synchronizuj(ActionEvent actionEvent) {
        parentController.getDataContainer().setStudents(tabelka.getItems());
    }

    public void dodajJesliEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            dodaj(new ActionEvent(keyEvent.getSource(), keyEvent.getTarget()));
        }
    }

//Stwórz nową wersję aplikacji, która będzie zapisywać i odczytywać dane do pliku CSV


    public void zapisz(ActionEvent actionEvent) throws IOException {
        Writer writer = null;
        try {

            writer = new BufferedWriter(new FileWriter(("Dane Osobowe.csv")));
            ArrayList<Student> studentsList = new ArrayList<>(tabelka.getItems());

            for (Student s : studentsList)
                writer.write(s.getName() + ";" + s.getSurname() + ";" + s.getPesel() + ';' + s.getIdx() + ';' + s.getGrade() + ';' + s.getGradeDetailed()
                        + ";"
                        +'\n');

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            FileOutputStream file = new FileOutputStream("Dane Osobowe.csv");
            writer.flush();
            writer.close();
        }
    }


    public void wczytaj(ActionEvent actionEvent) {

        try //(FileInputStream ois = new FileInputStream("Dane Osobowe.csv"))
        {
            BufferedReader CSVFile = new BufferedReader(new FileReader("Dane Osobowe.csv"));
            String dataRow = CSVFile.readLine();
            ArrayList<Student> students = new ArrayList<>();

            while (dataRow != null) {
                Student s = new Student();
                String[] dataArray = dataRow.split(";");
                if (!dataArray[0].equals( "null")){
                s.setName(dataArray[0]);}

                if (!dataArray[1].equals( "null")){
                s.setSurname(dataArray[1]);}

                if (!dataArray[2].equals( "null")){
                s.setPesel(dataArray[2]);}

                if (!dataArray[3].equals( "null")){
                s.setIdx(dataArray[3]);}

               if (!dataArray[4].equals( "null")){
                   s.setGrade(Double.parseDouble(dataArray[4]));
               }

                if (!dataArray[5].equals( "null")){
                s.setGradeDetailed(dataArray[5]);
               }else if (dataArray[5].equals("")){
                    s.setGradeDetailed(null);
                }


                students.add(s);
                dataRow = CSVFile.readLine(); // Read next line of data.
            }
            //tabelka.getItems().clear();
            parentController.getDataContainer().setStudents(students);
            initialize();
            tabelka.getItems().addAll(students);

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }
}

