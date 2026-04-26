
package smarthrms;

public class SystemPermission {
    private String code;
    private String name;
    private String description;
    private String category;
    private boolean active;
    
    public SystemPermission(String code, String name, String description, String category, boolean active) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.category = category;
        this.active = active;
    }
    
    // Getters and setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}