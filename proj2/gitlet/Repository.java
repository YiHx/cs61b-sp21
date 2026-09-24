package gitlet;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 *
 * @author xuanh08
 */
public class Repository {

    public static String head;

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public Repository() {
    }

    public void initFunction() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            File nowFile = join(GITLET_DIR, "objects", "commits");
            if (!nowFile.exists()) {
                nowFile.mkdirs();
            }

            File indexFile = join(GITLET_DIR, "index");
            File blobs = join(GITLET_DIR, "objects", "Blobs");
            File indexStage = join(GITLET_DIR, "stage");
            File branch = join(GITLET_DIR,"branch");

            branch.mkdir();
            indexStage.mkdir();
            blobs.mkdirs();
            indexFile.mkdir();

            Commit nowCommit = new gitlet.Commit();
            String nowSha1String = Utils.sha1(Utils.serialize(nowCommit));
            Utils.writeObject(join(nowFile, nowSha1String), nowCommit);
            head = nowSha1String;

            File thisBranch = join(GITLET_DIR,"branch","master");
            Utils.writeContents(thisBranch, nowSha1String);

            File headFile = join(GITLET_DIR, "HEAD");
            Utils.writeContents(headFile, "master");

        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
    }

    public static void addFunction(String fileName) {
        File directFile = join(CWD, fileName);
        if (!directFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
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
        String currentBranch = Utils.readContentsAsString(join(GITLET_DIR, "HEAD"));
        String lastCommitSha1 = Utils.readContentsAsString(join(GITLET_DIR, "branch", currentBranch));

        File commitFile = join(GITLET_DIR, "objects", "commits", lastCommitSha1);
        Commit lastCommit = Utils.readObject(commitFile, Commit.class);
        Repository.head = lastCommitSha1;
        Commit nowCommit = new Commit(commitMessage);

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

        File currentBranchFile = join(GITLET_DIR, "branch", currentBranch);
        Utils.writeContents(currentBranchFile, nowSha1String);
    }

    public static void removeFunction(String fileName) {
        File indexFile = join(GITLET_DIR, "index");

        String currentBranch = Utils.readContentsAsString(join(GITLET_DIR, "HEAD"));
        String lastCommitSha1 = Utils.readContentsAsString(join(GITLET_DIR, "branch", currentBranch));

        File commitFile = join(GITLET_DIR, "objects", "commits", lastCommitSha1);
        Commit lastCommit = Utils.readObject(commitFile, Commit.class);

        if ((!join(indexFile, fileName).exists()) && !lastCommit.commitFile.containsKey(fileName)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        File thisFile = join(indexFile, fileName);
        if (thisFile.exists()) {
            thisFile.delete();
        }
        if (lastCommit.commitFile.containsKey(fileName)) {
            File stageFile = join(GITLET_DIR, "stage", fileName);
            File thisBlobFile = join(GITLET_DIR, "objects", "Blobs", lastCommit.commitFile.get(fileName));
            Utils.writeContents(stageFile, Utils.readContents(thisBlobFile));
            File userFile = join(CWD, fileName);
            userFile.delete();
        }
    }

    public static void logFunction() {
        String currentBranch = Utils.readContentsAsString(join(GITLET_DIR, "HEAD"));
        String lastCommitSha1 = Utils.readContentsAsString(join(GITLET_DIR, "branch", currentBranch));

        File commitFile = join(GITLET_DIR, "objects", "commits", lastCommitSha1);
        Commit lastCommit = Utils.readObject(commitFile, Commit.class);

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
            Commit nowFileCommit = Utils.readObject((join(GITLET_DIR,"objects","commits",nowFile)),Commit.class);
            if(nowFileCommit.message.equals(message)){
                check = true;
                System.out.println(nowFile);
            }
        }
        if(!check){
            System.out.println("Found no commit with that message.");
        }
    }

    public static void statusFunction(){
        List<String> allBranch = Utils.plainFilenamesIn(join(GITLET_DIR,"branch"));

        String currentBranch = Utils.readContentsAsString(join(Repository.GITLET_DIR, "HEAD"));

        System.out.println("=== Branches ===");
        for(String thisBranch : allBranch){
            if(thisBranch.equals(currentBranch)){
                System.out.println('*' + thisBranch);
                continue;
            }
            System.out.println(thisBranch);
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        List<String> allIndexFile = Utils.plainFilenamesIn(join(GITLET_DIR,"index"));
        if (allIndexFile != null && !allIndexFile.isEmpty()) {
            for(String thisIndexFile : allIndexFile){
                System.out.println(thisIndexFile);
            }
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        List<String> allStageFile = Utils.plainFilenamesIn(join(GITLET_DIR,"stage"));
        if(!allStageFile.isEmpty()){
            for(String nowStageFile : allStageFile ){
                System.out.println(nowStageFile);
            }
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();

        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public static void checkoutFunction(String[] args) {
        if (args.length == 3) {
            if (!Objects.equals(args[1], "--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            // [MODIFIED]: 解析HEAD
            String currentBranch = Utils.readContentsAsString(join(GITLET_DIR, "HEAD"));
            String lastCommitSha1 = Utils.readContentsAsString(join(GITLET_DIR, "branch", currentBranch));

            File commitFile = join(GITLET_DIR, "objects", "commits", lastCommitSha1);
            Commit lastCommit = Utils.readObject(commitFile, Commit.class);
            if (!lastCommit.commitFile.containsKey(args[2])) {
                System.out.println("File does not exist in that commit.");
                System.exit(0);
            }
            File thisFile = join(GITLET_DIR, "objects", lastCommit.commitFile.get(args[2]));
            File theChangeFile = join(CWD, args[2]);
            Utils.writeContents(theChangeFile, Utils.readContents(thisFile));
        }

        if (args.length == 4) {
            String thisFileName = args[3];
            String thisCommitSha1 = args[1];
            File lastCommitFold = join(GITLET_DIR, "objects", "commits", thisCommitSha1);
            if (!lastCommitFold.exists()) {
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
            Commit lastCommit = Utils.readObject(lastCommitFold, Commit.class);
            if (!lastCommit.commitFile.containsKey(thisFileName)) {
                System.out.println("File does not exist in that commit.");
                System.exit(0);
            }
            File thisFile = join(GITLET_DIR, "objects", lastCommit.commitFile.get(thisFileName));
            File thisChangeFile = join(CWD, thisFileName);
            Utils.writeContents(thisChangeFile, Utils.readContents(thisFile));

        }

        if (args.length == 2) {
            String willChangeBranch = args[1];
            List<String> allBranch = Utils.plainFilenamesIn(join(GITLET_DIR, "branch"));

            // [MODIFIED]: 解析当前HEAD对应的分支名和哈希值
            String currentBranch = Utils.readContentsAsString(join(GITLET_DIR, "HEAD"));
            String lastCommitSha1 = Utils.readContentsAsString(join(GITLET_DIR, "branch", currentBranch));

            if (!allBranch.contains(willChangeBranch)) {
                System.out.println("No such branch exists.");
                System.exit(0);
            }


            if (willChangeBranch.equals(currentBranch)) {
                System.out.println("No need to checkout the current branch.");
                System.exit(0);
            }

            String willChangeBranchSha1 = Utils.readContentsAsString(join(GITLET_DIR, "branch", willChangeBranch));
            Commit otherBranch = Utils.readObject(join(GITLET_DIR, "objects", "commits", willChangeBranchSha1), Commit.class);
            List<String> nowAllFile = Utils.plainFilenamesIn(join(CWD));

            for (String now : nowAllFile) {
                if (otherBranch.commitFile.containsKey(now)) {
                    File commitFile = join(GITLET_DIR, "objects", "commits", lastCommitSha1);
                    Commit lastCommit = Utils.readObject(commitFile, Commit.class);
                    List<String> willDeleteFile = Utils.plainFilenamesIn(join(GITLET_DIR, "stage"));
                    if (!willDeleteFile.contains(now) && !lastCommit.commitFile.containsKey(now)) {
                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                        System.exit(0);
                    }
                }
            }

            for (var it : otherBranch.commitFile.entrySet()) {
                File thisFile = join(CWD, it.getKey());
                Utils.writeContents(thisFile, Utils.readContents(join(GITLET_DIR, "objects", "blobs", it.getValue())));
            }
            File commitFile = join(GITLET_DIR, "objects", "commits", lastCommitSha1);
            Commit lastCommit = Utils.readObject(commitFile, Commit.class);
            for (String currentFile : lastCommit.commitFile.keySet()) {
                if (!otherBranch.commitFile.containsKey(currentFile)) {
                    Utils.restrictedDelete(join(CWD, currentFile));
                }
            }

            File headFile = join(GITLET_DIR, "HEAD");
            Utils.writeContents(headFile, willChangeBranch);

            File deleteFile = join(GITLET_DIR, "stage");
            File[] stageFiles = deleteFile.listFiles();
            if (stageFiles != null) {
                for (File file : stageFiles) {
                    file.delete();
                }
            }

            File indexFile = join(GITLET_DIR, "index");
            File[] idxFiles = indexFile.listFiles();
            if (idxFiles != null) {
                for (File file : idxFiles) {
                    file.delete();
                }
            }
        }
    }

    public static void branchFunction(String branch){
        File newBranch = join(GITLET_DIR, "branch", branch);

        if (newBranch.exists()) {
            System.out.print("A branch with that name already exists.");
            return;
        }

        // [MODIFIED]: 通过 HEAD 拿到当前分支，再拿到当前的 commit 哈希，赋给新分支
        String currentBranch = Utils.readContentsAsString(join(GITLET_DIR, "HEAD"));
        String currentCommitHash = Utils.readContentsAsString(join(GITLET_DIR, "branch", currentBranch));
        Utils.writeContents(newBranch, currentCommitHash);
    }
}