package Server.Database;

import Shared.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;


import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public class UserDB {
    private int id;
    private String name;
    private String password;

    private static UserDB loginUser = null;


    public static UserDB getLoginUser() {
        return loginUser;
    }

    public UserDB(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public UserDB() { }


    /**
     * This method is used to login, if logged successfully, User.getLoginUser() will return the logged user.
     */

    public boolean login() {
        try {
            UserDB[] users = UserDB.getAll();
            UserDB loginUser = Arrays.stream(users)
                    .filter(user -> user.name.equals(this.name) && user.password.equals(this.password))
                    .findFirst()
                    .get();
            UserDB.loginUser = loginUser;
        } catch (NoSuchElementException exception) {
            System.err.println("Wrong User or password");
            return false;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return true;
    }



    /**
     * Sign a user
     */
    public boolean signUp() {
        try {
            this.create();
        } catch (ConstraintViolationException exception) {
            System.err.println("Duplicate Name");
            return false;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
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
    private static UserDB[] getAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();

        Criteria criteria = session.createCriteria(UserDB.class);

        List<UserDB> users = criteria.list();
        UserDB[] userArray = new UserDB[users.size()];
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
