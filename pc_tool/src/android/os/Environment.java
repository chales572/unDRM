package android.os;

public class Environment {
    public static final String DIRECTORY_DOWNLOADS = "Downloads";
    
    public static java.io.File getExternalStoragePublicDirectory(String type) {
        return new java.io.File("C:\\Users\\Public\\Downloads");
    }
    
    public static java.io.File getExternalStorageDirectory() {
        return new java.io.File("C:\\Users\\Public");
    }

    public static String getExternalStorageState() {
        return "mounted";
    }
}
