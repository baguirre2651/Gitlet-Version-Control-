package gitlet;

import java.io.IOException;
import java.util.Objects;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author Bryan Aguirre and Natalie Rivas
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) throws IOException {
        String firstArg = null;
        if (args.length > 0) {
            firstArg = args[0];
        } else {
            firstArg = "Other Error";
        }


        /**Creating the Command object to be called within the switch/Main class **/
        Repository gitRepoCommands = new Repository();

        switch (firstArg) {
            case "Other Error":
                System.out.println("Please enter a command.");
                break;
            case "init":
                if (validCommandArgs(1, args)) {
                    gitRepoCommands.init();
                }
                break;
            case "add":
                if (validCommandArgs(2, args)) {
                    gitRepoCommands.add(args[1]);
                }
                break;
            case "commit":
                if (validCommandArgs(2, args)) {
                    gitRepoCommands.commit(args[1]);
                }
                break;
            case "rm":
                if (validCommandArgs(2, args)) {
                    gitRepoCommands.rm(args[1]);
                }
                break;
            case "log":
                if (validCommandArgs(1, args)) {
                    gitRepoCommands.log();
                }
                break;
            case "global-log":
                if (validCommandArgs(1, args)) {
                    gitRepoCommands.globalLog();
                }
                break;
            case "find":
                if (validCommandArgs(2, args)) {
                    gitRepoCommands.find(args[1]);
                }
                break;
            case "status":
                if (validCommandArgs(1, args)) {
                    gitRepoCommands.status();
                }
                break;
            case "branch":
                gitRepoCommands.branch(args[1]);
                break;
            case "rm-branch":
                gitRepoCommands.rm_branchCommand(args[1]);
                break;
            case "reset":
                gitRepoCommands.reset(args[1]);
                break;

            case "checkout":
                if (args.length == 2) {
                    gitRepoCommands.checkoutbranch(args[1]);
                } else if (args.length == 3) {
                    gitRepoCommands.checkout(args[2]);
                } else {
                    if (Objects.equals(args[2], "--")) {
                        gitRepoCommands.checkout(args[1], args[3]);
                    } else {
                        System.out.println("Incorrect operands.");
                    }
                }
                break;
            default:
                System.out.println("No command with that name exists.");
        }

    }


    public static boolean validCommandArgs(int k, String... args) {
        if (args.length == k) {
            return true;
        }
        throw new RuntimeException("Incorrect operands");
    }
}