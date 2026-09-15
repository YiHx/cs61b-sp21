package gitlet;

import java.io.File;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author xuanh08
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    public static  String head;

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /* TODO: fill in the rest of this class. */
    public Repository(){

    }

    public  void initFunction(){
//        判断.gitlet目录是否存在，如果不存在则创建
        if(!GITLET_DIR.exists()){
            GITLET_DIR.mkdir();
            File nowFile = join(GITLET_DIR,"objects","commits");
//            判断commits目录是否存在，如果不存在则创建
            if(!nowFile.exists()){
                nowFile.mkdirs();
            }

            File indexFile = join(GITLET_DIR,"index");
            indexFile.mkdir();
//            检查完毕创建一个新的提交，此时没有跟踪任何文件
            Commit nowCommit = new gitlet.Commit();
            String nowSha1String = Utils.sha1(Utils.serialize(nowCommit));
            Utils.writeObject(join(nowFile,nowSha1String), nowCommit);
            head =nowSha1String;
            File headFile =  join (GITLET_DIR,"HEAD");
            Utils.writeContents(headFile,head);

        }else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

    }

    public void addFunction(String fileName){
//        判断当前文件是否存在
        File directFile = join(CWD,fileName);
        if(!directFile.exists()){
            System.out.println("File does not exist.");
            System.exit(0);
        }
//        将当前目录的文件放入暂存区
        File indexFile = join(GITLET_DIR,"index");
        byte[] thisFile =Utils.readContents(directFile);
        Utils.writeContents(join(indexFile,fileName),thisFile);


    }

}
