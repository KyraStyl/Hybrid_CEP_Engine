package cetsmanager.main.examples;

import com.beust.jcommander.Parameter;

public class Config {

  @Parameter(names = {"-i", "--inputEvent"}, description = "input event path", required = true)
  String path;

  @Parameter(names = {"-wl"}, description = "window length")
  long wl;

  @Parameter(names = {"-sl"}, description = "window slide")
  long sl;

  @Parameter(names = {"-p", "--parallism"}, description = "parallism")
  int parallism = -1;

  @Parameter(names = {"-static"}, description = "use static constructor")
  boolean isStatic;

  @Parameter(names = {"-w", "-write"}, description = "write graph to directory")
  String dirPath;

  @Parameter(names = {"-sel"}, description = "select an anchor for each selectivity nodes")
  int selectivity = 2;

  @Parameter(names = {"-out"}, description = "if write results")
  boolean isWrite;

  @Parameter(names = {"-pdfs"}, description = "if parallel dfs")
  boolean pdfs;

  @Parameter(names = {"-prednum"}, description = "number of predicates for the kleene sub-pattern")
  int prednum;

  @Parameter(names = {"-iter"}, description = "num iteration, default 1, "
      + "which means do not further iterate")
  int numInteration = 1;

  @Parameter(names = {"-pr", "--preds"}, description = "predicates")
  String[] preds = new String[]{"Hello"};

  @Parameter(names = "--help", help = true)
  private boolean help;
}
