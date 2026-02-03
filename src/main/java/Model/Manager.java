/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;



/**
 *
 * @author Admin
 */
public class Manager {
    private int managerId;        // manager_id (NOT NULL)
    private String fullName;      // full_name (NOT NULL)
    private String email;         // email (NOT NULL)
    private String password;      // password (NOT NULL)
    private String managerRole;   // manager_role (NOT NULL)
    private Boolean status;       // status (NULL allowed) bit

    public Manager() {
    }

    public Manager(int managerId, String fullName, String email, String password, String managerRole, Boolean status) {
        this.managerId = managerId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.managerRole = managerRole;
        this.status = status;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getManagerRole() {
        return managerRole;
    }

    public void setManagerRole(String managerRole) {
        this.managerRole = managerRole;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
    
}
