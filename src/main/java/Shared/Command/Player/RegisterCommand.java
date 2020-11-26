package Shared.Command.Player;

import Shared.Data.User;

import java.io.Serializable;

public class RegisterCommand implements Serializable {
    private static final long serialVersionUID = 1L;

    private User user; // The user who is going to register

    public RegisterCommand(User user) {
        this.user = new User(user.getUsername(), user.getPassword());
    }

    public User getUser() {
        return new User(user.getUsername(), user.getPassword());
    }

    public void setUser(User user) {
        this.user = new User(user.getUsername(), user.getPassword());
    }
}
