/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class Customer {
    private int customerId;               
    private String username;              
    private String fullName;                
    private String email;                  
    private String password;               

    private String gender;                 
    private LocalDate dateOfBirth;          

    private Boolean status;                
    private Boolean emailVerified;         

    private String authToken;             
    private String authTokenType;           
    private LocalDateTime authTokenExpiredAt; 
    private Boolean authTokenUsed;   

    public Customer() {
    }

    public Customer(int customerId, String username, String fullName, String email, String password, String gender, LocalDate dateOfBirth, Boolean status, Boolean emailVerified, String authToken, String authTokenType, LocalDateTime authTokenExpiredAt, Boolean authTokenUsed) {
        this.customerId = customerId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
        this.emailVerified = emailVerified;
        this.authToken = authToken;
        this.authTokenType = authTokenType;
        this.authTokenExpiredAt = authTokenExpiredAt;
        this.authTokenUsed = authTokenUsed;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthTokenType() {
        return authTokenType;
    }

    public void setAuthTokenType(String authTokenType) {
        this.authTokenType = authTokenType;
    }

    public LocalDateTime getAuthTokenExpiredAt() {
        return authTokenExpiredAt;
    }

    public void setAuthTokenExpiredAt(LocalDateTime authTokenExpiredAt) {
        this.authTokenExpiredAt = authTokenExpiredAt;
    }

    public Boolean getAuthTokenUsed() {
        return authTokenUsed;
    }

    public void setAuthTokenUsed(Boolean authTokenUsed) {
        this.authTokenUsed = authTokenUsed;
    }

  
    
}
