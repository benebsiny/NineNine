package Client.Status;

import javafx.scene.layout.AnchorPane;

public class WindowStatus {
    private static String windowMessage = null;
    private static MessageSeverity severity;

    public static String getWindowMessage() {
        return windowMessage;
    }

    public static void setWindowMessage(String windowMessage, MessageSeverity severity) {
        WindowStatus.windowMessage = windowMessage;
        WindowStatus.severity = severity;
    }

    public static void clear() {
        windowMessage = null;
    }

    public static MessageSeverity getSeverity() {
        return severity;
    }

    public enum MessageSeverity {
        SUCCESS, ERROR
    }
}
