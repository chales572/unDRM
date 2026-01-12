
import com.wjthinkbig.bookclubdrm.DrmDecode;

public class TestJar {
    public static void main(String[] args) {
        try {
            System.out.println("Attempting to load DrmDecode class...");
            Class<?> clazz = Class.forName("com.wjthinkbig.bookclubdrm.DrmDecode");
            System.out.println("Class found: " + clazz.getName());

            // Try to instantiate if possible, or just checking class loading is a good
            // first step.
            // Constructor: DrmDecode(Context context, String devKey, String memberCode,
            // String orderID)
            android.content.Context mockContext = new android.content.Context();
            DrmDecode drm = new DrmDecode(mockContext, "testKey", "testMember", "testOrder");
            System.out.println("Instance created successfully.");

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
