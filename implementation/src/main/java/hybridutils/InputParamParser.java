package hybridutils;

import com.beust.jcommander.JCommander;
import sasesystem.UI.CommandLineUI;
import sasesystem.engine.ConfigFlags;

import java.nio.file.Paths;
import java.util.Locale;

public final class InputParamParser {

    private static Params params = new Params();

    public static void validateParams(String args[]){
        JCommander.newBuilder().addObject(params).build().parse(args);
        return;
    }

    public static void readParams(String args[]){
        String parrent_dir = Paths.get(System.getProperty("user.dir")).getParent()+"/";

        CommandLineUI.nfaFileLocation = parrent_dir+params.query;
        CommandLineUI.inputFile = parrent_dir+params.input;
        ConfigFlags.inputFile = CommandLineUI.inputFile;
        ConfigFlags.engine = params.engine;

        if(params.conf!=null){
            CommandLineUI.streamConfigFile = parrent_dir+params.conf;
        }

        ConfigFlags.printResults = params.isWrite;
        ConfigFlags.outFile = parrent_dir+params.outFile;

        CommandLineUI.eventtype = params.eventtype.equalsIgnoreCase("kite")?"check":params.eventtype;
        ConfigFlags.eventtype = CommandLineUI.eventtype.toLowerCase(Locale.ROOT);
    }

}





