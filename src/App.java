import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Timer;


public class App implements ActionListener{

    JFrame frame;
    JLabel lbl1, lbl2, lbl3;
    JTextField tf1;
    public static JTextField tf2;
    JPasswordField pf;
    JButton btn1, btn2;

    App() {
        frame = new JFrame("login window");

        lbl1 = new JLabel("log in form");
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

        btn1 = new JButton("log in");
        btn1.setBounds(130, 200, 120, 25);
        btn1.addActionListener(this);
        frame.add(btn1);

        btn2 = new JButton("sign up");
        btn2.setBounds(250, 15, 120, 25);
        btn2.addActionListener(this);
        frame.add(btn2);

        tf2 = new JTextField();
        tf2.setBounds(10, 15, 120, 25);
        tf2.setEditable(false);
        frame.add(tf2);

        frame.setSize(400, 390);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void makePfEditable() {
        new Timer().schedule(
                new TimerTask() {
                    public void run() {
                        pf.setEditable(true);
                        btn1.setEnabled(true);
                    }
                },
                10000 );
    }

    Map<String, Integer> loginFails = new HashMap<>();

    @Override
    public void actionPerformed(ActionEvent e) {

        /*System.out.println(SignUp.userAccount);
        System.out.println(loginFails);
        System.out.println(SignUp.timeOfLogin);
        System.out.println(SignUp.accountNewPassword);*/

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
            SignUp.userAccount.put(loginLines.get(i), passwordLines.get(i));
        }

        char[] userPassword = pf.getPassword();
        String password = "";
        String username = tf1.getText();

        for(char psw : userPassword) {
            password += psw;
        }

        if(e.getSource() == btn2) {
            new SignUp();
        }
        if(e.getSource() == btn1) {
            //если пользователь еще ни разу не авторизовывался
            if(!loginFails.containsKey(username)) {
                loginFails.put(username, 0);
            }
            //если логин правильный, но пароль - нет.
            if(SignUp.userAccount.containsKey(username) && !password.equals(SignUp.userAccount.get(username))) {
                int failCounter = loginFails.get(username) + 1;
                loginFails.put(username, failCounter);

                //если количество неправильных попыток превышает 2
                if(loginFails.get(username) > 2) {
                    pf.setEditable(false);
                    btn1.setEnabled(false);
                    makePfEditable();
                }
            }
            //уведомление, если логин неправильный
            if(!SignUp.userAccount.containsKey(username)) {
                JOptionPane.showMessageDialog(null, "Неправильный логин",
                        "Внимание!", JOptionPane.INFORMATION_MESSAGE);
                tf1.grabFocus();
            }
            //условие успешного входа, и не истекшего срока годности пароля
            if (password.equals(SignUp.userAccount.get(username)) &&
                    !SignUp.accountNewPassword.containsKey(username)) {
                JOptionPane.showMessageDialog(null, "Логин и пароль верные", "Внимание!",
                        JOptionPane.INFORMATION_MESSAGE);

                //если вход успешно осуществлен, счетчик неудачных попыток устанавливаем в ноль
                loginFails.put(username, 0);
                pf.setEditable(true);
                btn1.setEnabled(true);
            }

            //если пароль правильный, но срок действия пароля истек
            if(password.equals(SignUp.userAccount.get(username)) &&
                    SignUp.accountNewPassword.containsKey(username)) {
                new ChangePassword(username);
            }

            //уведомление, что пароль неправильный
            if(!password.equals(SignUp.userAccount.get(username)) &&
                    SignUp.userAccount.containsKey(username)) {
                if(loginFails.get(username) < 3) {
                    int triesLeft = 3 - loginFails.get(username);
                    String triesLeftStr = String.valueOf(triesLeft);
                    JOptionPane.showMessageDialog(null, "Неправильный пароль \n" +
                            "осталось попыток: " + triesLeftStr, "Внимание!", JOptionPane.INFORMATION_MESSAGE);
                }
                if(loginFails.get(username) > 3) {
                    JOptionPane.showMessageDialog(null, "Неправильный пароль",
                            "Внимание!", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    public static void main(String[] args) {
        new App();
    }
}
