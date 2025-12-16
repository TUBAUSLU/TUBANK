package com.example.bankapplicationproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Objects;
import java.util.Random;

public class LoginController {

    Connection connection;
    Statement statement;
    PreparedStatement preparedStatement;
    private static final String dbName = "jdbc:sqlite:tubank.db";
    private static final Integer defaultBakiye = 0;
    public static String tc;

    private static final String adminTc = "2022";

    Stage stage;

    @FXML
    AnchorPane scenePane;

    @FXML
    Pane kayitol_pane, girisyap_pane;

    @FXML
    TextField girisyap_tcnumarasi, kayitol_telefon, kayitol_tcnumarasi, kayitol_ad, kayitol_soyad;

    @FXML
    PasswordField kayitol_sifre, girisyap_sifre;

    @FXML
    Button girisyap_kayitol, girisyap_giris, kayitol_kayitol, kayitol_giris, close_button;
    @FXML
    private void buttons_action(ActionEvent event) {
        if (event.getSource() == girisyap_kayitol) {
            kayitol_pane.toFront();

        } else if (event.getSource() == kayitol_giris) {
            girisyap_pane.toFront();

        } else if (event.getSource() == kayitol_kayitol) {
            customerSave();

        } else if (event.getSource() == girisyap_giris) {
            login();

        } else if (event.getSource() == close_button) {
            stage = (Stage) scenePane.getScene().getWindow();
            stage.close();

        }
    }

    private void customerSave() {
        try {
            if (kayitol_tcnumarasi.getText().equals("")
                    || kayitol_telefon.getText().equals("")
                    || kayitol_sifre.getText().equals("")
                    || kayitol_ad.getText().equals("")
                    || kayitol_soyad.getText().equals("")) {

                showMessage("Lütfen Boşlukları Doldurunuz");
            } else {

                try {
                    /*Db yoksa oluşturur varsa bağlanır*/
                    dbCreate(dbName);
                    connection = DriverManager.getConnection(dbName);
                    Statement stmt = connection.createStatement();
                    String query = String.format("INSERT INTO MUSTERILER values(null, %s, '%s','%s','%s','%s','%s','%s')",
                            kayitol_tcnumarasi.getText(),
                            kayitol_ad.getText(),
                            kayitol_soyad.getText(),
                            kayitol_telefon.getText(),
                            ibanGenerate(),
                            defaultBakiye,
                            kayitol_sifre.getText());
                    stmt.executeUpdate(query);
                    connection.close();
                    showMessage("Başarıyla Kayıt Oldunuz");
                    temizle();
                    girisyap_pane.toFront();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
        }
    }

    private void login() {
        if (girisyap_tcnumarasi.getText().equals("") || girisyap_sifre.getText().equals("")) {
            showMessage("Lütfen Boşlukları Doldurunuz");
        } else {
            try {
                connection = DriverManager.getConnection(dbName);
                statement = connection.createStatement();
                String query = "SELECT 'MUSTERI_TC', MUSTERI_SIFRE FROM MUSTERILER " +
                        "WHERE MUSTERI_TC=? AND MUSTERI_SIFRE=?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, girisyap_tcnumarasi.getText());
                preparedStatement.setString(2, girisyap_sifre.getText());
                ResultSet result = preparedStatement.executeQuery();
                if (result.next()) {
                    showMessage("Başarılı giriş yaptın");
                    Stage window = new Stage();
                    Parent root;
                    if (Objects.equals(girisyap_tcnumarasi.getText(), adminTc)) {
                        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("AdminPanel.fxml")));
                    } else {
                        tc=girisyap_tcnumarasi.getText();
                        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("MusteriPanel.fxml")));
                    }
                    window.setTitle("Otomasyon");
                    window.initStyle(StageStyle.UNDECORATED);
                    window.setScene(new Scene(root));
                    window.show();
                    Stage stage = (Stage) scenePane.getScene().getWindow();
                    stage.close();
                } else {
                    showMessage("Lütfen bilgileri doğru girin");
                }
                connection.close();
            } catch (Exception e) {
                showMessage("Giriş yaparken bilinmeyen bir hata:" + e);
            }
        }
    }

    private void showMessage(String message) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null, message);
            }
        });
    }

    private void temizle() {
        kayitol_ad.setText("");
        kayitol_soyad.setText("");
        kayitol_tcnumarasi.setText("");
        kayitol_sifre.setText("");
        kayitol_telefon.setText("");
    }

    private String ibanGenerate() {
        Random rand = new Random();
        String card = "TR";
        for (int i = 0; i < 14; i++) {
            int n = rand.nextInt(10) + 0;
            card += Integer.toString(n);
        }
        return card;
    }

    public void dbCreate(String fileName) throws ClassNotFoundException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(fileName);
            statement = connection.createStatement();
            String sql = "CREATE TABLE if not exists MUSTERILER " +
                    "(ID    INTEGER   PRIMARY KEY   AUTOINCREMENT," +
                    " MUSTERI_TC        INTEGER         NOT NULL, " +
                    " MUSTERI_AD        CHAR(50)        NOT NULL, " +
                    " MUSTERI_SOYAD     CHAR(50)        NOT NULL, " +
                    " MUSTERI_TELEFON   INTEGER         NOT NULL, " +
                    " MUSTERI_IBAN      VARCHAR(25)     NOT NULL, " +
                    " MUSTERI_BAKIYE    DECIMAL(10,5)   NOT NULL, " +
                    " MUSTERI_SIFRE     VARCHAR(25)     NOT NULL) ";
            statement.executeUpdate(sql);
            connection.close();
            System.out.println("Veritabanı Başarılıyla Oluşturuldu...");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}