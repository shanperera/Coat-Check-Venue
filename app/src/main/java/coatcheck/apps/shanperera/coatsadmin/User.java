package coatcheck.apps.shanperera.coatsadmin;

/**
 * Created by Shan on 2016-03-11.
 */
public class User {
    String fullName, phoneNumber, email, coat, id;

    public User(){
    }

    public User(String fn, String pn, String email, String coat){
        this.fullName = fn;
        this.phoneNumber = pn;
        this.email = email;
        this.coat = coat;
    }
    public User(String fn, String pn, String email, String coat, String id){
        this.fullName = fn;
        this.phoneNumber = pn;
        this.email = email;
        this.coat = coat;
        this.id = id;
    }
    //public User(String coat){
    //    this.coat = coat;
    //}

    public User(String phoneNumber) { this.phoneNumber = phoneNumber; }


    public String getFullName(){
        return fullName;
    }

    public void setFullName(String fName){
        this.fullName = fName;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getCoat(){ return coat; }

    public void setCoat(String coat) { this.coat = coat; }

    public void setId(String id) { this.id = id; }

    public String getId() { return id; }
}