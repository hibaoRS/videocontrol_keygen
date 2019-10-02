import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;


public class Keygen extends Application {


    private String validatePattern;

    private String salt1 = "HAIBAO";
    private String salt2 = "RECORD";
    private String salt3 = "SYSTEM";
    private String salt4 = "haibaoLB";
    private MessageDigest messageDigest;
    private String[] times = {"01", "03", "06", "12", "24", "36", "60", "99"};


    public Keygen() {
        validatePattern = "X{8}-X{4}-X{4}-X{4}-X{12}"
                .replaceAll("X", "[0-9a-fA-F]");
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("启动失败");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(Keygen.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        //1.序列号
        Label productIdName = new Label("序列号：");
        TextField productId = new TextField();
        productId.setMinWidth(540);
        productId.setAlignment(Pos.CENTER);
        productId.setPromptText("请输入设备序列号");
        productId.getStyleClass().add("persistent-prompt");
        productId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 36) {
                productId.setText(newValue.substring(0, 36));
            }
        });


        HBox hBox1 = new HBox();
        hBox1.setSpacing(20);
        hBox1.setAlignment(Pos.CENTER);
        hBox1.getChildren().addAll(productIdName, productId);


        //2.有效期
        Label expiryName = new Label("有效期：");

        ComboBox<String> cb = new ComboBox<>();
        cb.getItems().addAll("1个月", "3个月", "半年", "1年", "2年", "3年", "5年", "永久");
        cb.setMinWidth(540);
        cb.getSelectionModel().select(0);


        HBox hBox5 = new HBox();
        hBox5.setAlignment(Pos.CENTER);
        hBox5.getChildren().addAll(cb);
        hBox5.setMinWidth(540);


        HBox hBox4 = new HBox();
        hBox4.setSpacing(20);
        hBox4.setAlignment(Pos.CENTER);
        hBox4.getChildren().addAll(expiryName, cb);


        //3.激活码
        Label activationCodeName = new Label("激活码：");
        TextField activationCode = new TextField();
        activationCode.setMinWidth(540);
        activationCode.setAlignment(Pos.CENTER);
        activationCode.setEditable(false);
        activationCode.setPromptText("按回车生成激活码");
        activationCode.getStyleClass().addAll("code", "persistent-prompt");

        HBox hBox2 = new HBox();
        hBox2.setSpacing(20);
        hBox2.setAlignment(Pos.CENTER);
        hBox2.getChildren().addAll(activationCodeName, activationCode);


        //4.操作
        Button generate = new Button("生成激活码");
        generate.setDefaultButton(true);
        generate.setOnAction(event -> {
            if (Pattern.matches(validatePattern, productId.getText())) {
                String code = md5((salt1 + productId.getText()).toLowerCase()).toLowerCase();
                code = md5(code).toLowerCase();
                code = md5((code + salt2).toLowerCase()).toLowerCase();
                code = md5((new StringBuilder(code).insert(16, salt3).toString()).toUpperCase()).toUpperCase();
                code = md5(code).toUpperCase().substring(8, 24);
                code = new StringBuilder(code)
                        .insert(8, times[cb.getSelectionModel().getSelectedIndex()])
                        .insert(18, DESUtils.randomString(1))
                        .insert(13, DESUtils.randomString(1))
                        .insert(9, DESUtils.randomString(1))
                        .insert(4, DESUtils.randomString(1))
                        .insert(0, DESUtils.randomString(1))
                        .toString();
                code = DESUtils.bytesToHexString(DESUtils.encrypt(code.getBytes(), salt4));

                activationCode.setText(code);
            } else {
                activationCode.setText("生成失败，设备序列号格式有误");
            }
        });
        Button exit = new Button("退出工具");
        exit.setCancelButton(true);
        exit.setOnAction(event -> Platform.exit());
        HBox hBox3 = new HBox();
        hBox3.setSpacing(20);
        hBox3.setAlignment(Pos.CENTER);
        hBox3.getChildren().addAll(generate, exit);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(20, 20, 20, 20));
        vBox.getChildren().addAll(hBox1, hBox4, hBox2, hBox3);
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());


        primaryStage.setScene(scene);
        primaryStage.setMinHeight(230);
        primaryStage.setMinWidth(680);
        primaryStage.setResizable(false);
        primaryStage.setTitle("海豹录播系统注册机");
        primaryStage.getIcons().add(new Image(
                Keygen.class.getResourceAsStream("key.png")));
        primaryStage.show();
    }

    private String md5(String str) {
        return DESUtils.bytesToHexString(messageDigest.digest(str.getBytes()));
    }


}
