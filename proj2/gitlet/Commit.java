package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;// TODO: You'll likely use this in this class

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
    private String message = "initial commit";
    private String date;

    /* TODO: fill in the rest of this class. */
//   第一次提交
    public Commit(){
        this.date = "01/01/1970:0000";

    }
//  其余的提交
    public Commmit(String message){
        this.message =message;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy:HHmm");
        Date date = new Date();
        this.date = sdf.format(date);

    }
}
