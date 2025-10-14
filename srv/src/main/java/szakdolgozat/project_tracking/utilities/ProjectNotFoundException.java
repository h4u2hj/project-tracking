package szakdolgozat.project_tracking.utilities;

public class ProjectNotFoundException extends Exception {

    public ProjectNotFoundException(String message) {
        super(message);
    }

    public ProjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
