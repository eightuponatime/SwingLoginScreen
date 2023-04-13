import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.Timer;
import java.util.regex.Pattern;

public class SignUp implements ActionListener {

    static JFrame frame;
    JLabel lbl1, lbl2, lbl3;
    JTextField tf1;
    JTextArea ta;
    JPasswordField pf;
    JButton btn1, btn2;

    SignUp() {
        frame = new JFrame("sign up window");

        lbl1 = new JLabel("sign up form");
        lbl1.setBounds(150, 90, 200, 20);
        frame.add(lbl1);

        lbl2 = new JLabel("login");
        lbl2.setBounds(120, 125, 100, 20);
        frame.add(lbl2);

        tf1 = new JTextField();
        tf1.setBounds(170, 130, 100, 20);
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
        ta.setText("8-16 символов пароль, минимум одна цифра, минимум одна\n" +
                "строчная буква, хотя бы одна прописная буква, хотя бы один\n" +
                "специальный символ без пробелов");
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

    public static Map<String, String> userAccount = new HashMap<>();

    public static Map<String, String> accountNewPassword = new HashMap<>();

    public static Map<String, Long> timeOfLogin = new HashMap<>();

    public static void registration(String username, String password) {
        userAccount.put(username, password);
    }

    String complexPasswordRegex =  "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=._-])(?=\\S+$).{8,16}$";
    Pattern passwordPattern = Pattern.compile(complexPasswordRegex);
    String complexUsername = "^[.a-zA-Z0-9_-]{2,20}$";
    Pattern usernamePattern = Pattern.compile(complexUsername);

    public static void passwordChanging(String username, String password, long loginTime) {
        new Timer().schedule(
                new TimerTask() {
                    public void run() {
                        if ((System.currentTimeMillis() - loginTime) >= -30000) {
                            App.tf2.setText("working");

                            //список для аккаунтов с истекшим сроком действия пароля
                            accountNewPassword.put(username, password);

                            String fileName = "logins.txt";
                            List<String> loginLines = new ArrayList<>();
                            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                                String line;
                                while ((line = br.readLine()) != null) {
                                    String[] words = line.split(" ");
                                    loginLines.addAll(Arrays.asList(words));
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }

                            String nameFile = "passwords.txt";
                            List<String> passwordLines = new ArrayList<>();
                            try (BufferedReader br = new BufferedReader(new FileReader(nameFile))) {
                                String line;
                                while ((line = br.readLine()) != null) {
                                    String[] words = line.split(" ");
                                    passwordLines.addAll(Arrays.asList(words));
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }

                            String dateFile = "loginTime.txt";
                            List<String> dateLines = new ArrayList<>();
                            try (BufferedReader br = new BufferedReader(new FileReader(dateFile))) {
                                String line;
                                while ((line = br.readLine()) != null) {
                                    String[] words = line.split(" ");
                                    dateLines.addAll(Arrays.asList(words));
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }

                            for(int i = 0; i < loginLines.size(); i++) {
                                if(passwordLines.get(i).equals(password)) {
                                    passwordLines.remove(i);
                                    loginLines.remove(i);
                                    dateLines.remove(i);
                                }
                            }

                            try {
                                FileWriter loginWriter = new FileWriter("logins.txt");
                                FileWriter passwordWriter = new FileWriter("passwords.txt");
                                FileWriter dateWriter = new FileWriter("loginTime.txt");
                                FileWriter changePswd = new FileWriter("waitingForPswdChange.txt", true);

                                for(int i = 0; i < loginLines.size(); i++) {
                                    loginWriter.write(" " + loginLines.get(i));
                                    passwordWriter.write(" " + passwordLines.get(i));
                                    dateWriter.write(" " + dateLines.get(i));
                                }

                                changePswd.write(" " + username);

                                loginWriter.close();
                                passwordWriter.close();
                                dateWriter.close();
                                changePswd.close();

                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }

                        }
                    }
                },
                30000);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String fileName = "logins.txt";
        List<String> loginLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] words = line.split(" ");
                loginLines.addAll(Arrays.asList(words));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String nameFile = "passwords.txt";
        List<String> passwordLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(nameFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] words = line.split(" ");
                passwordLines.addAll(Arrays.asList(words));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        for(int i = 0; i < loginLines.size(); i++) {
            userAccount.put(loginLines.get(i), passwordLines.get(i));
        }

        char[] userPassword = pf.getPassword();
        String password = "";
        String username = tf1.getText();

        for(char psw : userPassword) {
            password += psw;
        }

        if (e.getSource() == btn1) {
            if(!usernamePattern.matcher(username).matches())
            {
                JOptionPane.showMessageDialog(null, "Запрещенные символы в логине",
                        "Внимание!", JOptionPane.INFORMATION_MESSAGE);
            }
            if(!passwordPattern.matcher(password).matches())
            {
                JOptionPane.showMessageDialog(null, "Пароль не удовлетворяет требованиям",
                        "Внимание!", JOptionPane.INFORMATION_MESSAGE);
            }
            if(SignUp.userAccount.containsKey(username)) {
                JOptionPane.showMessageDialog(null, "Логин занят", "Внимание!",
                        JOptionPane.INFORMATION_MESSAGE);
                tf1.grabFocus();
            }
            if(!SignUp.userAccount.containsKey(username) && passwordPattern.matcher(password).matches() &&
                    usernamePattern.matcher(username).matches()) {
                registration(username, password);

                long currentTime = System.currentTimeMillis();
                timeOfLogin.put(username, currentTime);

                try {
                    FileWriter writer = new FileWriter("logins.txt", true);
                    FileWriter pswdWriter = new FileWriter("passwords.txt", true);
                    FileWriter dateWriter = new FileWriter("loginTime.txt", true);

                    String curTimeStr = String.valueOf(currentTime);

                    writer.write(" " + username);
                    pswdWriter.write(" " + password);
                    dateWriter.write(" " + curTimeStr);

                    dateWriter.close();
                    writer.close();
                    pswdWriter.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                passwordChanging(username, password, SignUp.timeOfLogin.get(username));

                JOptionPane.showMessageDialog(null, "Вы успешно зарегистрировались",
                        "Поздравляем!", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            }
        }

        if(e.getSource() == btn2) {
            frame.dispose();
        }
    }
}