package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author xuanh08
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if(args.length==0){
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];

        switch(firstArg) {
            case "init":
                if(args.length>1){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository nowAction =new Repository();
                nowAction.initFunction();
                break;
            case "add":
                if(args.length>2){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                if(args.length == 0){
                    System.out.println("Please enter a command.");
                    System.exit(0);
                }
                if(!Repository.GITLET_DIR.exists()){
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }
                Repository.addFunction(args[1]);
                break;
            case "commit":
                if (args.length == 0) {
                    System.out.println("Please enter a command.");
                    System.exit(0);
                }
                if(args.length > 2 ){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                if(!Repository.GITLET_DIR.exists()){
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }
                Repository.commitFunction(args[1]);
                break;
            case "rm":
                if(args.length==0){
                    System.out.println("Please enter a command.");
                    System.exit(0);
                }
                if(args.length > 2){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                if(!Repository.GITLET_DIR.exists()){
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }
                Repository.removeFunction(args[1]);
                break;
            case "log":
                if(args.length ==0){
                    System.out.println("Please enter a command.");
                    System.exit(0);
                }
                if(args.length > 1){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                if(!Repository.GITLET_DIR.exists()){
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }
                Repository.logFunction();
                break;

            // TODO: FILL THE REST IN
        }
    }
}
