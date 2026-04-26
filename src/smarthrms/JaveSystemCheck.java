package smarthrms;

public class JaveSystemCheck {
    public static void main(String[] args) {
        System.out.println("=== System Environment Check ===");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Java VM: " + System.getProperty("java.vm.name"));
        System.out.println("Java Home: " + System.getProperty("java.home"));
        System.out.println("OS Arch: " + System.getProperty("os.arch"));
        System.out.println("OS Name: " + System.getProperty("os.name"));
        System.out.println("OS Version: " + System.getProperty("os.version"));
        System.out.println("Java Library Path: " + System.getProperty("java.library.path"));
        System.out.println("ClassPath: " + System.getProperty("java.class.path"));
        
        // Check if we're running in 32-bit or 64-bit mode
        String arch = System.getProperty("os.arch");
        String sunArch = System.getProperty("sun.arch.data.model");
        System.out.println("Architecture: " + arch);
        System.out.println("Data Model: " + (sunArch != null ? sunArch + "-bit" : "Unknown"));
    }
}