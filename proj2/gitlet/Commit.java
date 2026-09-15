package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;// TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author xuanh08
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    public String message = "initial commit";
    public String date;
    public String parents;
    public Map<String, String> commitFile = new HashMap<>();



    /* TODO: fill in the rest of this class. */
//   第一次提交
    public Commit(){
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        this.date = sdf.format(new Date(0));
        this.parents = "0";


    }
//  其余的提交
    public Commit(String message){
        this.message =message;
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        Date date = new Date();
        this.date = sdf.format(date);
        this.parents = Repository.head;


    }
}
