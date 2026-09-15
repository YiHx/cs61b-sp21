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
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
        }
    }
}
