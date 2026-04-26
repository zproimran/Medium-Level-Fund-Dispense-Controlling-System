package smarthrms;
public class DepartmentNameModel {
    private String dep; 
    
    public DepartmentNameModel(String dep){
    this.dep=dep;
    }
    public String getDeptName(){
    return dep;
    }
    public void setDeptName(String dept){
    this.dep=dept;
    }

}
