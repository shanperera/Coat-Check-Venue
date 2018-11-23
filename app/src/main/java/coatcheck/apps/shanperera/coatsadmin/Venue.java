package coatcheck.apps.shanperera.coatsadmin;

import java.util.ArrayList;

/**
 * Created by Shan on 2016-07-20.
 */
public class Venue {
    String address, name, uid;

    public Venue(){
    }

    public Venue(String uid){
        this.uid = uid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
