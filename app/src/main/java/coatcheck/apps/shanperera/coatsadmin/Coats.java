package coatcheck.apps.shanperera.coatsadmin;

/**
 * Created by Shan on 2016-09-28.
 */
public class Coats {

    int availableHangers;
    int hangerIndex;
    String orderedHangers;
    int usedHangers;

    public Coats(){
    }

    public Coats(int a, int i, String o, int u){
        this.availableHangers = a;
        this.hangerIndex = i;
        this.orderedHangers = o;
        this.usedHangers = u;
    }

    public int getAvailableHangers() {
        return availableHangers;
    }

    public void setAvailableHangers(int availableHangers) {
        this.availableHangers = availableHangers;
    }

    public int getHangerIndex() {
        return hangerIndex;
    }

    public void setHangerIndex(int hangerIndex) {
        this.hangerIndex = hangerIndex;
    }

    public String getOrderedHangers() {
        return orderedHangers;
    }

    public void setOrderedHangers(String orderedHangers) { this.orderedHangers = orderedHangers; }

    public int getUsedHangers() {
        return usedHangers;
    }

    public void setUsedHangers(int usedHangers) {
        this.usedHangers = usedHangers;
    }
}
