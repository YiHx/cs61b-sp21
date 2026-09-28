package gitlet;

import java.io.File;
import java.util.*;

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
            File branch = join(GITLET_DIR, "branch");

            branch.mkdir();
            indexStage.mkdir();
            blobs.mkdirs();
            indexFile.mkdir();

            Commit nowCommit = new gitlet.Commit();
            String nowSha1String = Utils.sha1(Utils.serialize(nowCommit));
            Utils.writeObject(join(nowFile, nowSha1String), nowCommit);
            head = nowSha1String;

            File thisBranch = join(GITLET_DIR, "branch", "master");
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
        List<String> nowAllFile = Utils.plainFilenamesIn(Utils.join(GITLET_DIR, "objects", "commits"));
        for (String nowFile : nowAllFile) {
            Commit nowFileCommit = Utils.readObject(join(GITLET_DIR, "objects", "commits", nowFile), Commit.class);
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

    public static void findFunction(String message) {
        List<String> nowAllFile = Utils.plainFilenamesIn(Utils.join(GITLET_DIR, "objects", "commits"));
        boolean check = false;
        for (String nowFile : nowAllFile) {
            Commit nowFileCommit = Utils.readObject((join(GITLET_DIR, "objects", "commits", nowFile)), Commit.class);
            if (nowFileCommit.message.equals(message)) {
                check = true;
                System.out.println(nowFile);
            }
        }
        if (!check) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void statusFunction() {
        List<String> allBranch = Utils.plainFilenamesIn(join(GITLET_DIR, "branch"));

        String currentBranch = Utils.readContentsAsString(join(Repository.GITLET_DIR, "HEAD"));

        System.out.println("=== Branches ===");
        for (String thisBranch : allBranch) {
            if (thisBranch.equals(currentBranch)) {
                System.out.println('*' + thisBranch);
                continue;
            }
            System.out.println(thisBranch);
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        List<String> allIndexFile = Utils.plainFilenamesIn(join(GITLET_DIR, "index"));
        if (allIndexFile != null && !allIndexFile.isEmpty()) {
            for (String thisIndexFile : allIndexFile) {
                System.out.println(thisIndexFile);
            }
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        List<String> allStageFile = Utils.plainFilenamesIn(join(GITLET_DIR, "stage"));
        if (!allStageFile.isEmpty()) {
            for (String nowStageFile : allStageFile) {
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

    public static void branchFunction(String branch) {
        File newBranch = join(GITLET_DIR, "branch", branch);

        if (newBranch.exists()) {
            System.out.print("A branch with that name already exists.");
            return;
        }


        String currentBranch = Utils.readContentsAsString(join(GITLET_DIR, "HEAD"));
        String currentCommitHash = Utils.readContentsAsString(join(GITLET_DIR, "branch", currentBranch));
        Utils.writeContents(newBranch, currentCommitHash);
    }

    public static void rmBranchFunction(String branchName) {
        File allBranch = join(GITLET_DIR, "branch");
        List<String> allBranchName = Utils.plainFilenamesIn(allBranch);
        if (allBranchName != null && !allBranchName.contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }

        String currentBranch = Utils.readContentsAsString(join(GITLET_DIR, "HEAD"));

        File thisBranch = join(GITLET_DIR, "branch", branchName);
        String thisBranchName = Utils.readContentsAsString(thisBranch);
        if (currentBranch.equals(thisBranchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }

        Utils.restrictedDelete(thisBranch);
    }

    public static void resetFunction(String thisCommitId) {
        File commitsDir = join(GITLET_DIR, "objects", "commits");
        List<String> allCommits = Utils.plainFilenamesIn(commitsDir);
        String targetCommitSha1 = null;

        if (allCommits != null) {
            for (String cId : allCommits) {
                if (cId.startsWith(thisCommitId)) {
                    targetCommitSha1 = cId;
                    break;
                }
            }
        }

        if (targetCommitSha1 == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }


        File targetCommitFile = join(commitsDir, targetCommitSha1);
        Commit targetCommit = Utils.readObject(targetCommitFile, Commit.class);

        String currentBranch = Utils.readContentsAsString(join(GITLET_DIR, "HEAD"));
        String currentCommitSha1 = Utils.readContentsAsString(join(GITLET_DIR, "branch", currentBranch));
        File currentCommitFile = join(commitsDir, currentCommitSha1);
        Commit currentCommit = Utils.readObject(currentCommitFile, Commit.class);


        List<String> cwdFiles = Utils.plainFilenamesIn(CWD);
        List<String> stagedFiles = Utils.plainFilenamesIn(join(GITLET_DIR, "index"));

        if (cwdFiles != null) {
            for (String fileName : cwdFiles) {

                if (targetCommit.commitFile.containsKey(fileName)) {
                    boolean isTrackedByCurrent = currentCommit.commitFile.containsKey(fileName);
                    boolean isStaged = (stagedFiles != null && stagedFiles.contains(fileName));

                    if (!isTrackedByCurrent && !isStaged) {
                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                        System.exit(0);
                    }
                }
            }
        }


        for (var entry : targetCommit.commitFile.entrySet()) {
            String fileName = entry.getKey();
            String blobSha1 = entry.getValue();
            File blobFile = join(GITLET_DIR, "objects", "Blobs", blobSha1);
            File cwdFile = join(CWD, fileName);
            Utils.writeContents(cwdFile, Utils.readContents(blobFile));
        }


        for (String currentFile : currentCommit.commitFile.keySet()) {
            if (!targetCommit.commitFile.containsKey(currentFile)) {
                Utils.restrictedDelete(join(CWD, currentFile));
            }
        }


        File currentBranchFile = join(GITLET_DIR, "branch", currentBranch);
        Utils.writeContents(currentBranchFile, targetCommitSha1);

        File indexDir = join(GITLET_DIR, "index");
        File[] indexFiles = indexDir.listFiles();
        if (indexFiles != null) {
            for (File file : indexFiles) {
                file.delete();
            }
        }

        File stageDir = join(GITLET_DIR, "stage");
        File[] stageFiles = stageDir.listFiles();
        if (stageFiles != null) {
            for (File file : stageFiles) {
                file.delete();
            }
        }
    }

    public static String findSplitPoint(String headSha1, String givenSha1) {
        Map<String, Integer> headDistanceMap = new HashMap<>();
        Queue<String> headQueue = new ArrayDeque<>();
        headQueue.add(headSha1);
        headDistanceMap.put(headSha1, 0);

        File commitDir = join(GITLET_DIR, "objects", "commits");
        while (!headQueue.isEmpty()) {
            String nowSha1 = headQueue.poll();
            int nowDist = headDistanceMap.get(nowSha1);
            Commit nowCommit = Utils.readObject(join(commitDir, nowSha1), Commit.class);
            for (String parentSha1 : nowCommit.parents) {
                if (!parentSha1.equals("0") && !headDistanceMap.containsKey(parentSha1)) {
                    headDistanceMap.put(parentSha1, nowDist + 1);
                    headQueue.add(parentSha1);
                }
            }
        }

        java.util.Queue<String> givenQueue = new java.util.ArrayDeque<>();
        java.util.Set<String> visitedGiven = new java.util.HashSet<>();
        givenQueue.add(givenSha1);
        visitedGiven.add(givenSha1);

        String splitPointSha1 = null;
        int minDistance = Integer.MAX_VALUE;

        while (!givenQueue.isEmpty()) {
            String nowSha1 = givenQueue.poll();
            if (headDistanceMap.containsKey(nowSha1)) {
                int nowDist = headDistanceMap.get(nowSha1);
                if (nowDist < minDistance) {
                    minDistance = nowDist;
                    splitPointSha1 = nowSha1;
                }
            }
            Commit nowCommit = Utils.readObject(join(commitDir, nowSha1), Commit.class);
            for (String parentSha1 : nowCommit.parents) {
                if (!parentSha1.equals("0") && !visitedGiven.contains(parentSha1)) {
                    visitedGiven.add(parentSha1);
                    givenQueue.add(parentSha1);
                }
            }
        }
        return splitPointSha1;
    }

    public static void mergeFunction(String branchName) {
        List<String> stageFile = Utils.plainFilenamesIn(join(GITLET_DIR, "stage"));
        List<String> indexFile = Utils.plainFilenamesIn(join(GITLET_DIR, "index"));
        List<String> allBranch = Utils.plainFilenamesIn(join(GITLET_DIR, "branch"));
        String nowBranch = Utils.readContentsAsString(join(GITLET_DIR, "HEAD"));

        if (!stageFile.isEmpty() || !indexFile.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        if (!allBranch.contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (nowBranch.equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }

        String currentCommitSha1 = Utils.readContentsAsString(join(GITLET_DIR, "branch", nowBranch));
        File currentCommitFile = join(GITLET_DIR, "objects", "commits", currentCommitSha1);
        Commit currentCommit = Utils.readObject(currentCommitFile, Commit.class);

        String mergeCommitSha1 = Utils.readContentsAsString(join(GITLET_DIR, "branch", branchName));
        File mergeCommitFile = join(GITLET_DIR, "objects", "commits", mergeCommitSha1);
        Commit mergeCommit = Utils.readObject(mergeCommitFile, Commit.class);

        String splitPointSha1 = findSplitPoint(currentCommitSha1, mergeCommitSha1);
        File splitPointFile = join(GITLET_DIR, "objects", "commits", splitPointSha1);
        Commit splitPointCommit = Utils.readObject(splitPointFile, Commit.class);

        if (splitPointSha1.equals(mergeCommitSha1)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }

        if (splitPointSha1.equals(currentCommitSha1)) {
            checkoutFunction(new String[]{"checkout", branchName});
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        List<String> nowAllFile = Utils.plainFilenamesIn(CWD);
        if (nowAllFile != null) {
            for (String nowFile : nowAllFile) {
                if (!currentCommit.commitFile.containsKey(nowFile)) {
                    if (mergeCommit.commitFile.containsKey(nowFile)) {
                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                        System.exit(0);
                    }
                }
            }
        }

        java.util.HashSet<String> allFiles = new java.util.HashSet<>();
        allFiles.addAll(splitPointCommit.commitFile.keySet());
        allFiles.addAll(currentCommit.commitFile.keySet());
        allFiles.addAll(mergeCommit.commitFile.keySet());

        boolean checkConflict = false;
        java.util.HashMap<String, String> nowCommitFile = new java.util.HashMap<>(currentCommit.commitFile);
        File indexDir = join(GITLET_DIR, "index");
        File stageDir = join(GITLET_DIR, "stage");
        File BlobsFile = join(GITLET_DIR, "objects", "Blobs");

        for (String nowFile : allFiles) {
            String splitSha1 = splitPointCommit.commitFile.get(nowFile);
            String currentSha1 = currentCommit.commitFile.get(nowFile);
            String mergeSha1 = mergeCommit.commitFile.get(nowFile);

            if (Objects.equals(splitSha1, currentSha1) && !Objects.equals(splitSha1, mergeSha1)) {
                if (mergeSha1 != null) {
                    byte[] thisFile = Utils.readContents(join(BlobsFile, mergeSha1));
                    Utils.writeContents(join(CWD, nowFile), thisFile);
                    Utils.writeContents(join(indexDir, nowFile), thisFile);
                    nowCommitFile.put(nowFile, mergeSha1);
                } else {
                    Utils.restrictedDelete(join(CWD, nowFile));
                    File stageTarget = join(stageDir, nowFile);
                    Utils.writeContents(stageTarget, Utils.readContents(join(BlobsFile, currentSha1)));
                    nowCommitFile.remove(nowFile);
                }
            } else if (!Objects.equals(currentSha1, mergeSha1)) {
                if (!Objects.equals(currentSha1, splitSha1) && !Objects.equals(mergeSha1, splitSha1)) {
                    checkConflict = true;
                    String currentText = (currentSha1 == null) ? "" : Utils.readContentsAsString(join(BlobsFile, currentSha1));
                    String mergeText = (mergeSha1 == null) ? "" : Utils.readContentsAsString(join(BlobsFile, mergeSha1));

                    String conflictText = "<<<<<<< HEAD\n" + currentText + "=======\n" + mergeText + ">>>>>>>\n";
                    Utils.writeContents(join(CWD, nowFile), conflictText);
                    Utils.writeContents(join(indexDir, nowFile), conflictText);

                    String conflictShaString = Utils.sha1(conflictText);
                    Utils.writeContents(join(BlobsFile, conflictShaString), conflictText);
                    nowCommitFile.put(nowFile, conflictShaString);
                }
            }
        }

        Commit nowCommit = new Commit("Merged " + branchName + " into " + nowBranch + ".");
        nowCommit.parents = new String[]{currentCommitSha1, mergeCommitSha1};
        nowCommit.commitFile = nowCommitFile;

        String nowSha1String = Utils.sha1(Utils.serialize(nowCommit));
        File nowFile = join(GITLET_DIR, "objects", "commits", nowSha1String);
        Utils.writeObject(nowFile, nowCommit);
        head = nowSha1String;

        File currentBranchFile = join(GITLET_DIR, "branch", nowBranch);
        Utils.writeContents(currentBranchFile, nowSha1String);

        File[] allIndex = indexDir.listFiles();
        if (allIndex != null) {
            for (File it : allIndex) {
                it.delete();
            }
        }

        File[] allStage = stageDir.listFiles();
        if (allStage != null) {
            for (File it : allStage) {
                it.delete();
            }
        }

        if (checkConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

}