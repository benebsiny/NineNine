package Shared;

public class NextPlayerCommand {
    private String nextPlayerUsername;

    public NextPlayerCommand(String nextPlayerUsername) {
        this.nextPlayerUsername = nextPlayerUsername;
    }

    public String getNextPlayerUsername() {
        return nextPlayerUsername;
    }

    public void setNextPlayerUsername(String nextPlayerUsername) {
        this.nextPlayerUsername = nextPlayerUsername;
    }
}
