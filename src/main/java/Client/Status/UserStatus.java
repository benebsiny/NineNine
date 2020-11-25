package Client.Status;

import Shared.User;

public class UserStatus {
    private static String signInUser = null; // Only set username for sign in user

    public static String getSignInUser() {
        return signInUser;
    }

    public static void setSignInUser(String signInUser) {
        UserStatus.signInUser = signInUser;
    }
}
