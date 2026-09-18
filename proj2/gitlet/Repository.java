package gitlet;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static gitlet.Utils.*;

// TODO: any imports you need here

/**
 * Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author xuanh08
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     * <p>
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    public static String head;

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /* TODO: fill in the rest of this class. */
    public Repository() {

    }

    public void initFunction() {
//        判断.gitlet目录是否存在，如果不存在则创建
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            File nowFile = join(GITLET_DIR, "objects", "commits");
//            判断commits目录是否存在，如果不存在则创建
            if (!nowFile.exists()) {
                nowFile.mkdirs();
            }

            File indexFile = join(GITLET_DIR, "index");
            File blobs = join(GITLET_DIR, "objects", "Blobs");
            File indexStage = join(GITLET_DIR, "stage");
            indexStage.mkdir();
            blobs.mkdirs();
            indexFile.mkdir();
//            检查完毕创建一个新的提交，此时没有跟踪任何文件
            Commit nowCommit = new gitlet.Commit();
            String nowSha1String = Utils.sha1(Utils.serialize(nowCommit));
            Utils.writeObject(join(nowFile, nowSha1String), nowCommit);
            head = nowSha1String;
            File headFile = join(GITLET_DIR, "HEAD");
            Utils.writeContents(headFile, head);

        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

    }

    public static void addFunction(String fileName) {
//        判断当前文件是否存在
        File directFile = join(CWD, fileName);
        if (!directFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
//        将当前目录的文件放入暂存区
        File indexFile = join(GITLET_DIR, "index");
        File BlobsFile = join(GITLET_DIR, "objects", "Blobs");
        List<String> alreadyFile = Utils.plainFilenamesIn(BlobsFile);
        if (alreadyFile != null) {
            for (String it : alreadyFile) {
                if (sha1(readContents(join(BlobsFile, it))).equals(sha1(readContents(directFile)))) {
                    File checkFile = join(indexFile, sha1(readContents(directFile)));
                    if (checkFile.exists()) {
                        Utils.restrictedDelete(checkFile);
                    }
                    return;
                }
            }
        }
        byte[] thisFile = Utils.readContents(directFile);
        Utils.writeContents(join(indexFile, fileName), thisFile);


    }

    public static void commitFunction(String commitMessage) {
//        提取上一个提交并且新建当前的提交
        String lastCommitSha1 = Utils.readContentsAsString(join(GITLET_DIR, "HEAD"));
        File commitFile = join(GITLET_DIR, "objects", "commits", lastCommitSha1);
        Commit lastCommit = Utils.readObject(commitFile, Commit.class);
        Repository.head = lastCommitSha1;
        Commit nowCommit = new Commit(commitMessage);

//        将上一个提交中追踪的文件暂时全部都复制到当前的这个提交内，
//        然后将暂存区里的文件一个一个更新到当前的提交的文件映射的map
        nowCommit.commitFile = lastCommit.commitFile;
        File indexFile = join(GITLET_DIR, "index");
        List<String> fileName = Utils.plainFilenamesIn(indexFile);
        if (fileName == null) {
            return;
        }
        for (String nowFile : fileName) {
            File nowFileDirect = join(indexFile, nowFile);
            String nowFileShaString = Utils.sha1(Utils.readContents(nowFileDirect));
            nowCommit.commitFile.put(nowFile, nowFileShaString);
            File BlobFile = join(GITLET_DIR, "objects", "Blobs", nowFileShaString);
            Utils.writeContents(BlobFile, Utils.readContents(nowFileDirect));
            nowFileDirect.delete();
        }
        File stageFile = join(GITLET_DIR, "stage");
        List<String> allStageFile = Utils.plainFilenamesIn(stageFile);
        if (allStageFile != null) {
            for (String nowFile : allStageFile) {
                File nowFileDirect = join(stageFile, nowFile);
                String nowFileShaString = Utils.sha1(Utils.readContents(nowFileDirect));
                nowCommit.commitFile.remove(nowFile, nowFileShaString);
                nowFileDirect.delete();
            }
        }
        File nowFile = join(GITLET_DIR, "objects", "commits");
        String nowSha1String = Utils.sha1(Utils.serialize(nowCommit));
        Utils.writeObject(join(nowFile, nowSha1String), nowCommit);
        head = nowSha1String;
        File headFile = join(GITLET_DIR, "HEAD");
        Utils.writeContents(headFile, head);
    }

    public static void removeFunction(String fileName) {
        File indexFile = join(GITLET_DIR, "index");
//        提取上一个提交
        String lastCommitSha1 = Utils.readContentsAsString(join(GITLET_DIR, "HEAD"));
        File commitFile = join(GITLET_DIR, "objects", "commits", lastCommitSha1);
        Commit lastCommit = Utils.readObject(commitFile, Commit.class);
//        判断是否当前要删除的文件是否在暂存区或者被上一个提交跟踪
        if ((!join(indexFile, fileName).exists()) && !lastCommit.commitFile.containsKey(fileName)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
//        如果当前文件被放到了暂存区，则从暂存区移除
        File thisFile = join(indexFile, fileName);
        if (thisFile.exists()) {
            thisFile.delete();
        }
//        如果当前的文件被当前的头提交跟踪了，那么把这个文件添加到将要删除的区域，在下一个提交的时候把这个文件删除，
//        同时把用户当前工作目录的这个文件删除
        if (lastCommit.commitFile.containsKey(fileName)) {
            File stageFile = join(GITLET_DIR, "stage", fileName);
            File thisBlobFile = join(GITLET_DIR, "objects", "Blobs", lastCommit.commitFile.get(fileName));
            Utils.writeContents(stageFile, Utils.readContents(thisBlobFile));
            File userFile = join(CWD, fileName);
            userFile.delete();
        }

    }


    public static void logFunction() {
//        提取当前的头提交
        String lastCommitSha1 = Utils.readContentsAsString(join(GITLET_DIR, "HEAD"));
        File commitFile = join(GITLET_DIR, "objects", "commits", lastCommitSha1);
        Commit lastCommit = Utils.readObject(commitFile, Commit.class);
//        在没有到达第一个提交之前，打印出当前提交的信息
        while (!lastCommit.parents[0].equals("0")) {
            System.out.println("===");
            System.out.print("commit");
            System.out.print(" ");
            System.out.println(lastCommitSha1);
            if (!Objects.equals(lastCommit.parents[1], "0")) {
                System.out.printf("Merge: %s %s\n",
                        lastCommit.parents[0].substring(0, 7),
                        lastCommit.parents[1].substring(0, 7));
            }
            System.out.printf("Date: %s\n", lastCommit.date);
            System.out.println(lastCommit.message);
//            来到当前提交的上一个提交
            lastCommitSha1 = lastCommit.parents[0];
            commitFile = join(GITLET_DIR, "objects", "commits", lastCommitSha1);
            lastCommit = Utils.readObject(commitFile, Commit.class);
        }
        System.out.println("===");
        System.out.print("commit");
        System.out.print(" ");
        System.out.println(lastCommitSha1);
        if (!Objects.equals(lastCommit.parents[1], "0")) {
            System.out.printf("Merge: %s %s\n",
                    lastCommit.parents[0].substring(0, 7),
                    lastCommit.parents[1].substring(0, 7));
        }
        System.out.printf("Date: %s\n", lastCommit.date);
        System.out.println(lastCommit.message);
    }


    public static void globalLogFunction() {
        List<String> nowAllFile = Utils.plainFilenamesIn(Utils.join(GITLET_DIR,"objects","commits"));
        for(String nowFile : nowAllFile){
            Commit nowFileCommit = Utils.readObject(join(GITLET_DIR,"objects","commits",nowFile),Commit.class);
            System.out.println("===");
            System.out.print("commit");
            System.out.print(" ");
            System.out.println(nowFile);
            if (!Objects.equals(nowFileCommit.parents[1], "0")) {
                System.out.printf("Merge: %s %s\n",
                        nowFileCommit.parents[0].substring(0, 7),
                        nowFileCommit.parents[1].substring(0, 7));
            }
            System.out.printf("Date: %s\n", nowFileCommit.date);
            System.out.println(nowFileCommit.message);

        }
    }

    public static void findFunction(String message){
        List<String> nowAllFile = Utils.plainFilenamesIn(Utils.join(GITLET_DIR,"objects","commits"));
        boolean check =false;
        for(String nowFile : nowAllFile){
            Commit  nowFileCommit = Utils.readObject((join(GITLET_DIR,"objects","commits",nowFile)),Commit.class);
            if(nowFileCommit.message.equals(message)){
                check = true;
                System.out.println(nowFile);
            }
        }
        if(!check){
            System.out.println("Found no commit with that message.");
        }
    }

}
