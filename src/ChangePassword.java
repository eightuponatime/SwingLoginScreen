import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class ChangePassword implements ActionListener {
    JFrame frame;
    JLabel lbl1, lbl2, lbl3;
    JTextField tf1;
    JTextArea ta;
    JPasswordField pf;
    JButton btn1, btn2;
    ChangePassword(String changePasswordUsername) {
        frame = new JFrame("Change Password");

        lbl1 = new JLabel("change password form");
        lbl1.setBounds(150, 90, 200, 20);
        frame.add(lbl1);

        lbl2 = new JLabel("login");
        lbl2.setBounds(120, 125, 100, 20);
        frame.add(lbl2);

        tf1 = new JTextField();
        tf1.setBounds(170, 130, 100, 20);
        tf1.setEditable(false);
        tf1.setText(changePasswordUsername);
        frame.add(tf1);

        lbl3 = new JLabel("password");
        lbl3.setBounds(95, 155, 100, 20);
        frame.add(lbl3);

        pf = new JPasswordField();
        pf.setBounds(170, 160, 100, 20);
        frame.add(pf);

        btn1 = new JButton("signUp");
        btn1.setBounds(130, 200, 120, 25);
        btn1.addActionListener(this);
        frame.add(btn1);

        ta = new JTextArea();
        ta.setBounds(10, 235, 380, 50);
        ta.setText("Введите новый пароль\n" +"Срок действия старого пароля окончен");
        ta.setEditable(false);
        frame.add(ta);

        btn2 = new JButton("back");
        btn2.setBounds(10, 15, 120, 25);
        btn2.addActionListener(this);
        frame.add(btn2);

        frame.setSize(410, 390);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    String complexPasswordRegex =  "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=._-])(?=\\S+$).{8,16}$";
    Pattern passwordPattern = Pattern.compile(complexPasswordRegex);



    @Override
    public void actionPerformed(ActionEvent e) {

        char[] userPassword = pf.getPassword();
        String changedPassword = "";
        String username = tf1.getText();

        for(char psw : userPassword) {
            changedPassword += psw;
        }

        if(e.getSource() == btn1) {
            if(passwordPattern.matcher(changedPassword).matches()) {
                SignUp.registration(username, changedPassword);

                long currentTime = System.currentTimeMillis();
                SignUp.timeOfLogin.put(username, currentTime);

                String dateFile = "waitingForPswdChange.txt";
                List<String> changedPswdUserLines = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader(dateFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] words = line.split(" ");
                        changedPswdUserLines.addAll(Arrays.asList(words));
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                for(int i = 0; i < changedPswdUserLines.size(); i++) {
                    if(changedPswdUserLines.get(i).equals(username)) {
                        changedPswdUserLines.remove(i);
                    }
                }

                try {
                    FileWriter writer = new FileWriter("logins.txt", true);
                    FileWriter pswdWriter = new FileWriter("passwords.txt", true);
                    FileWriter dateWriter = new FileWriter("loginTime.txt", true);
                    FileWriter changedPswdUserWriter = new FileWriter("waitingForPswdChange.txt");

                    String curTimeStr = String.valueOf(currentTime);

                    writer.write(" " + username);
                    pswdWriter.write(" " + changedPassword);
                    dateWriter.write(" " + curTimeStr);

                    for(int i = 0; i < changedPswdUserLines.size(); i++) {
                        changedPswdUserWriter.write(" " + changedPswdUserLines.get(i));
                    }

                    SignUp.accountNewPassword.remove(username);

                    changedPswdUserWriter.close();
                    dateWriter.close();
                    writer.close();
                    pswdWriter.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                SignUp.passwordChanging(username, changedPassword, SignUp.timeOfLogin.get(username));

                JOptionPane.showMessageDialog(null, "Вы успешно изменили пароль",
                        "Поздравляем!", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            }
            else {
                JOptionPane.showMessageDialog(null, "Пароль не удовлетворяет требованиям",
                        "Внимание!", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        if(e.getSource() == btn2) {
            frame.dispose();
        }
    }
}
