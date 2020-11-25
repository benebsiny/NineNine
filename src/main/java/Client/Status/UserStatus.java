package Client.Status;

public class UserStatus {
    private static String signInUser = null; // Only set username for sign in user
    private static int myTurnId = -1;

    public static String getSignInUser() {
        return signInUser;
    }

    public static void setSignInUser(String signInUser) {
        UserStatus.signInUser = signInUser;
    }

    public static int getMyTurnId() {
        return myTurnId;
    }

    public static void setMyTurnId(int myTurnId) {
        UserStatus.myTurnId = myTurnId;
    }
}
