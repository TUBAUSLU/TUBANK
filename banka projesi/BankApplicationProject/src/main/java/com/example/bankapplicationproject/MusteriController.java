package com.example.bankapplicationproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;

public class MusteriController implements Initializable {

    Connection connection;
    Statement statement;
    private static final String dbName = "jdbc:sqlite:tubank.db";


    @FXML
    TextField tc_txtfield, bakiye_txtfield, isim_txtfield, soyisim_txtfield, telno_txtfield, iban_txtfield, gonderilecekMiktar_txtfield, gonderilecek_iban_txtfield;
    @FXML
    PasswordField sifre_passwordfield;
    @FXML
    private TableView<MusteriModel> personel_table;
    @FXML
    private TableColumn<MusteriModel, String> tc_Col;
    @FXML
    private TableColumn<MusteriModel, String> iban_Col;
    @FXML
    private TableColumn<MusteriModel, String> isim_Col;
    @FXML
    private TableColumn<MusteriModel, String> soyisim_Col;
    @FXML
    private TableColumn<MusteriModel, String> telno_Col;
    @FXML
    private TableColumn<MusteriModel, String> bakiye_Col;
    @FXML
    AnchorPane scenePane;
    Stage stage;
    MusteriModel musteriModel = null;
    ObservableList<MusteriModel> personelModelList = FXCollections.observableArrayList();

    private void refreshtable() {
        try {
            personelModelList.clear();
            connection = DriverManager.getConnection(dbName);
            statement = connection.createStatement();
            String query = String.format("SELECT * FROM MUSTERILER WHERE MUSTERI_TC = '%s'", LoginController.tc);
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                personelModelList.add(new MusteriModel(resultSet.getInt("MUSTERI_TC"),
                        resultSet.getString("MUSTERI_AD"), resultSet.getString("MUSTERI_SOYAD"),
                        resultSet.getInt("MUSTERI_TELEFON"), resultSet.getString("MUSTERI_IBAN"),
                        resultSet.getDouble("MUSTERI_BAKIYE")));

                personel_table.setItems(personelModelList);
            }
            personel_table.setItems(personelModelList);
            connection.close();
        } catch (Exception e) {
            showMessage("Refresh yaparken oluşan bilinmeyen bir hata:" + e);
        }

    }

    private void loaddata() {

        refreshtable();
        tc_Col.setCellValueFactory(new PropertyValueFactory<>("tc"));
        isim_Col.setCellValueFactory(new PropertyValueFactory<>("isim"));
        soyisim_Col.setCellValueFactory(new PropertyValueFactory<>("soyisim"));
        telno_Col.setCellValueFactory(new PropertyValueFactory<>("telno"));
        iban_Col.setCellValueFactory(new PropertyValueFactory<>("iban"));
        bakiye_Col.setCellValueFactory(new PropertyValueFactory<>("bakiye"));
    }


    public void guncelle() {
        if (tc_txtfield.getText().equals("")
                && isim_txtfield.getText().equals("") && soyisim_txtfield.getText().equals("")
                && telno_txtfield.getText().equals("") && bakiye_txtfield.getText().equals("")
                && sifre_passwordfield.getText().equals("")) {

            showMessage("Lütfen Boşlukları Doldurunuz");

        } else {
            try {
                String query = String.format("UPDATE MUSTERILER set MUSTERI_TC='%s',MUSTERI_AD='%s',MUSTERI_SOYAD='%s'," +
                                "MUSTERI_TELEFON='%s',MUSTERI_IBAN='%s'," + " MUSTERI_SIFRE='%s' WHERE MUSTERI_IBAN='%s'",
                        tc_txtfield.getText(), isim_txtfield.getText(), soyisim_txtfield.getText(),
                        telno_txtfield.getText(), iban_txtfield.getText(), sifre_passwordfield.getText(),
                        iban_txtfield.getText());
                connection = DriverManager.getConnection(dbName);
                statement = connection.createStatement();
                statement.executeUpdate(query);
                connection.close();
                showMessage("Kayit Basariyla Guncellendi");
                refreshtable();
                temizle();

            } catch (Exception hata) {
                showMessage("Beklenmedik Hata Oluştu Güncelleme Yapılamadı : " + hata);
            }
        }
    }


    public void gonder() {
        if (tc_txtfield.getText().equals("")
                || isim_txtfield.getText().equals("") || soyisim_txtfield.getText().equals("")
                || telno_txtfield.getText().equals("") || bakiye_txtfield.getText().equals("")
                || sifre_passwordfield.getText().equals("")) {

            showMessage("Para Göndermek İçin Lütfen Boşlukları Doldurunuz");
        } else {
            try {
                String query = String.format("SELECT * FROM MUSTERILER WHERE MUSTERI_TC='%s' " +
                                "AND MUSTERI_IBAN='%s' AND MUSTERI_SIFRE='%s'",
                        tc_txtfield.getText(), iban_txtfield.getText(), sifre_passwordfield.getText());

                String kisiSorgula = String.format("SELECT * FROM MUSTERILER WHERE MUSTERI_IBAN='%s'", gonderilecek_iban_txtfield.getText());
                connection = DriverManager.getConnection(dbName);
                statement = connection.createStatement();
                if (!statement.execute(kisiSorgula)) {
                    showMessage("Göndermek istediğiniz Iban'a ait kayıt bulunamadı. Lütfen geçerli Iban giriniz.");
                }
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    double musteriBakiye = resultSet.getDouble("MUSTERI_BAKIYE");
                    double gondermek_istenen_mikyar = Double.parseDouble(gonderilecekMiktar_txtfield.getText());
                    if (musteriBakiye < gondermek_istenen_mikyar) {
                        showMessage("Bakiyeniz yetersiz! Lütfen bakiyenizi aşmayan miktar giriniz...");
                    } else {

                        double guncel_bakiye = musteriBakiye - gondermek_istenen_mikyar;
                        ResultSet rs = statement.executeQuery(kisiSorgula);
                        double gonderileninBakiyesi = 0;
                        while (rs.next()) {
                            gonderileninBakiyesi = resultSet.getDouble("MUSTERI_BAKIYE");
                        }
                        String gonderen_hesabı_guncelle = String.format("UPDATE MUSTERILER set MUSTERI_BAKIYE='%f' WHERE MUSTERI_IBAN='%s' ", guncel_bakiye, iban_txtfield.getText());
                        String gonderilen_hesabı_guncelle = String.format("UPDATE MUSTERILER set MUSTERI_BAKIYE='%f' WHERE MUSTERI_IBAN='%s' ", (gondermek_istenen_mikyar + gonderileninBakiyesi), gonderilecek_iban_txtfield.getText());
                        statement.execute(gonderen_hesabı_guncelle);
                        statement.execute(gonderilen_hesabı_guncelle);
                        showMessage("Gönderme İşlemi Başarıyla Gerçekleşti");
                        refreshtable();
                        temizle();
                    }
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void close() {
        stage = (Stage) scenePane.getScene().getWindow();
        stage.close();
    }

    public void temizle() {

        tc_txtfield.setText("");
        isim_txtfield.setText("");
        soyisim_txtfield.setText("");
        telno_txtfield.setText("");
        iban_txtfield.setText("");
        bakiye_txtfield.setText("");
        soyisim_txtfield.setText("");
        sifre_passwordfield.setText("");
    }

    public void tableviev_click() {

        try {
            musteriModel = personel_table.getSelectionModel().getSelectedItem();

            tc_txtfield.setText(String.valueOf(musteriModel.getTc()));
            isim_txtfield.setText(musteriModel.getIsim());
            soyisim_txtfield.setText(musteriModel.getSoyisim());
            telno_txtfield.setText(String.valueOf(musteriModel.getTelno()));
            iban_txtfield.setText(musteriModel.getIban().toString());
            bakiye_txtfield.setText(musteriModel.bakiye.toString());
        } catch (Exception e) {
            showMessage("Bilinmeyen bir hata oluştu :" + e);
        }
    }

    public void cikis() throws IOException {
        Stage window = new Stage();
        Parent fxmlLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LoginPanel.fxml")));
        window.setTitle("TUBANK Musteri");
        window.initStyle(StageStyle.UNDECORATED);
        window.setScene(new Scene(fxmlLoader));
        window.show();
        Stage stage = (Stage) scenePane.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loaddata();
    }

    private void showMessage(String message) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null, message);
            }
        });
    }
}
