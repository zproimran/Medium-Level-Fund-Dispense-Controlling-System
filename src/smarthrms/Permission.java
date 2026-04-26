package smarthrms;

public class Permission {
    private final String code;
    private final String name;
    private final String description;
    private final String category;
    private final boolean active;
    
    public Permission(String code, String name, String description) {
        this(code, name, description, "", true);
    }
    
    public Permission(String code, String name, String description, String category) {
        this(code, name, description, category, true);
    }
    
    public Permission(String code, String name, boolean active) {
        this(code, name, "", "", active);
    }
    
    public Permission(String code, String name, String description, boolean active) {
        this(code, name, description, "", active);
    }
    
    public Permission(String code, String name, String description, String category, boolean active) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.category = category;
        this.active = active;
    }
    
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public boolean isActive() { return active; }
    
    @Override
    public String toString() {
        return name + " (" + code + ")";
    }
}