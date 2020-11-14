package Server.Database;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public class User {
    private int id;
    private String name;
    private String password;

    private static User loginUser = null;

    public static User getLoginUser() {
        return loginUser;
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public User() {
    }

    /**
     * This method is used to login, if logged successfully, User.getLoginUser() will return the logged user.
     */
    public void login() {
        try {
            User[] users = User.getAll();
            User loginUser = Arrays.stream(users)
                    .filter(user -> user.name.equals(this.name) && user.password.equals(this.password))
                    .findFirst()
                    .get();
            User.loginUser = loginUser;
        } catch (NoSuchElementException exception) {
            System.err.println("Wrong User or password");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Sign a user
     */
    public void signUp() {
        try {
            this.create();
        } catch (ConstraintViolationException exception) {
            System.err.println("Duplicate Name");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Create a user
     */
    private synchronized void create() {
        Session session = HibernateUtil.getSessionFactory().openSession(); // Open Session
        Transaction tx = session.beginTransaction(); // Start a session
        session.save(this); // Save object to database
        tx.commit();
        session.close();
        HibernateUtil.shutdown();
    }

    /**
     * Get all users
     *
     * @return - Users
     */
    private static User[] getAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();

        Criteria criteria = session.createCriteria(User.class);

        List<User> users = criteria.list();
        User[] userArray = new User[users.size()];
        for (int i = 0; i < users.size(); i++) {
            userArray[i] = users.get(i);
        }
        return userArray;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
