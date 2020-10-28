package Database;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class User {
    private int id;
    private String name;
    private String password;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public User() {
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

    /**
     * Create a user to database
     */
    public synchronized void create() {
        Session session = HibernateUtil.getSessionFactory().openSession(); // Open Session
        Transaction tx = session.beginTransaction(); // Start a session
        session.save(this); // Save object to database
        tx.commit();
        session.close();
        HibernateUtil.shutdown();
    }

    public static User[] read() {
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
}
