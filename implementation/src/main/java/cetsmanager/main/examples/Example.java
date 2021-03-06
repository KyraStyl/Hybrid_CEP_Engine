package cetsmanager.main.examples;

import cetscommon.events.Event;
import cetscommon.events.EventTemplate;
import cetscommon.query.Query;
import cetsmanager.graph.AttributeVertex;
import cetsmanager.graph.EventVertex;
import cetsmanager.graph.Graph;
import cetsmanager.graph.construct.Constructor;
import cetsmanager.util.Global;
import com.beust.jcommander.JCommander;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import sasesystem.engine.Profiling;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class Example {

  protected final String path;
  protected final long wl;
  protected final long sl;
  protected final int parallism;
  protected int numWindow;
  protected final boolean isStatic;
  protected final String graphDir;
  protected final int selectivity;
  protected final boolean isWrite;
  protected final int numInteration;
  protected final int numPredicates;


  public Example(Config args) {
    this.path = args.path;
    this.wl = args.wl;
    this.sl = args.sl;
    this.parallism = args.parallism;
    if (parallism > 0) {
      Global.initParallel(parallism);
    }
    numWindow = 1;
    this.isStatic = args.isStatic;
    this.graphDir = args.dirPath;
    selectivity = args.selectivity;
    isWrite = args.isWrite;
    numInteration = args.numInteration;
    Global.pdfs = args.pdfs;
    this.numPredicates = args.prednum;
  }

  public static Example getExample(String[] args) {
    int idx, num=0;
    Preconditions.checkArgument(args.length > 0, "must specify example name");
    Config config;
    String[] nargs = Arrays.copyOfRange(args, 1, args.length);

    for(idx=0;idx<args.length && args[idx]!="-prednum";idx++);
    //System.out.println("idx = = "+idx); //just for debug

    ArrayList<String> extracted = new ArrayList<>();
    ArrayList<String> preds = new ArrayList<>();
    String[] predlist = null;
    if(idx!=0 && idx<args.length){
      num = Integer.parseInt(args[idx+1]);
      for(int i=1;i<args.length;i++){
        if(i<idx || i>idx+num+2){
          extracted.add(args[i]);
        }else if(i>=idx+2){
          preds.add(args[i]);
        }
      }
      nargs = new String[extracted.size()];
      for(int i=0;i<extracted.size();i++)
        nargs[i] = extracted.get(i);
      predlist = new String[preds.size()];
      for(int i=0;i<preds.size();i++)
        predlist[i] = preds.get(i);
    }

    if (args[0].startsWith("stock")) {
      String ratios = args[0].substring(5);
      double ratio = 1;
      if(ratios.length() > 0) ratio = Double.parseDouble(ratios);
      config = Stock.getArgument();
      JCommander.newBuilder()
          .addObject(config)
          .build()
          .parse(nargs);
      return new Stock((Stock.Argument) config,ratio,predlist);
    } else if("check".equalsIgnoreCase(args[0])){
      config = new Config();
      JCommander.newBuilder()
          .addObject(config)
          .build()
          .parse(nargs);
      return new Kite(config, predlist);
    }
    else {
      config = new Config();
      return new EmptyExample(config);
    }
  }

  public void start(ArrayList<String> eventsStr) {
    String localParameters = parameters();
    String parameters = "************************************\n"
        + "name: " + getName() + "\n"
        + "input events: " + path + "\n"
        + "window: " + wl + ", " + sl + "\n"
        + "parallism: " + parallism + "\n"
        + "selectivity: " + selectivity + "\n"
        + "isWrite: " + isWrite + "\n"
        + (localParameters == null ? "" : (localParameters + "\n"))
        + "************************************";
    System.out.println(parameters);

//    File file = null;
//    try {
//      file = new File("out-windows.txt");
//      if (file.createNewFile()) {
//        System.out.println("File created: " + file.getName());
//      }
//    } catch (IOException e) {
//      System.out.println("An error occurred.");
//      e.printStackTrace();
//    }

    ArrayList<Window> windowedEvents = windowEvents(eventsStr);
    Graph graph = new Graph(windowedEvents.get(0).events, getTemplate(),
            getQuery(), getConstructors());
    graph.construct();

    wait(1);
    Profiling.tempStart = System.nanoTime();
    Profiling.initSlide();

    processGraph(graph,0);

    for(int i=1; i<windowedEvents.size(); i++) {
//      try {
//        Writer output ;
//        output = new BufferedWriter(new FileWriter(file, true));
//        int start = (int) windowedEvents.get(i).events.get(0).get(0).numericVal();
//        int end = (int) windowedEvents.get(i).events.get(windowedEvents.get(i).events.size()-1).get(0).numericVal();
//        output.append("i:"+i+"-> start: "+String.valueOf(start)+" - end: "+String.valueOf(end)+", number of events: "+windowedEvents.get(i).events.size()+"\n");
//        output.close();
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
      Profiling.curWin+=1;
      Profiling.initSlide();
      int lastidx = windowedEvents.get(i).events.size()-1;
      System.gc();
      Profiling.tempStart = System.nanoTime();
      graph.incrementalChange(windowedEvents.get(i-1).events.get(0), windowedEvents.get(i).events.get(lastidx));
      processGraph(graph,i);
    }
    Global.close(100L * (wl + sl * (numWindow - 1)), TimeUnit.MILLISECONDS);
  }

  public void processGraph(Graph graph,int i){
    if (graphDir != null && graphDir.length() > 0) {
      graph.writeGraph(graphDir,i);
    }
    //checkNull(graph);
    if (selectivity > 0) {
      graph.detect(selectivity, isWrite ? graphDir : null, numInteration);
    }

    if (parallism > 0) {
      System.out.println(Global.threadInfo());
      //Global.close(100L * (wl + sl * (numWindow - 1)), TimeUnit.MILLISECONDS);
      //Global.close(20,TimeUnit.SECONDS);
    }

    //Global.reinitExecutors();

    System.gc();
    System.out.println("============================================");
    System.out.println("============================================");
  }

  private ArrayList<Window> windowEvents(ArrayList<String> eventsStr) {
    ArrayList<Window> windowedEvents = new ArrayList<>();
    Iterator<Event> it = readInput(eventsStr);
    this.numWindow = eventsStr.size()- (int)wl/(int)sl +1;
    this.numWindow = this.numWindow <= 0 ? 1: this.numWindow;
    Query query = getQuery();
    long nextW = 0;

    while (it.hasNext()) {
      Event event = it.next();
      while (event.timestamp > nextW && windowedEvents.size() < numWindow) {
        Window window = new Window(nextW);
        windowedEvents.add(window);
        nextW = nextW + query.sl;
      }
      int i = 0;
      while (!(windowedEvents.get(i).start <= event.timestamp
          && event.timestamp <= windowedEvents.get(i).start + query.wl)) {
        i++;
        if (i >= numWindow) {
          break;
        }
      }

      if (i >= numWindow) {
        break;
      }

      for (; i < windowedEvents.size(); i++) {
        if (windowedEvents.get(i).start <= event.timestamp
            && event.timestamp <= windowedEvents.get(i).start + query.wl) {
          windowedEvents.get(i).events.add(event);
        }
      }
    }
    return windowedEvents;
  }

  protected abstract String parameters();

  public abstract String getName();

  public abstract Query getQuery();

  protected void setPrecision(double... precisions) {
    Preconditions.checkArgument(precisions.length > 0);
    Global.initValue(Arrays.stream(precisions).min().getAsDouble());
  }

  @Nonnull
  public abstract Iterator<Event> readInput(ArrayList<String> eventsStr);

  public abstract EventTemplate getTemplate();

  public abstract ArrayList<Constructor> getConstructors();

  protected Iterator<Event> readInputFromFile(String path, ArrayList<String> eventsStr) {
    if(eventsStr!=null){
      if(eventsStr.size()!=0){
        Iterator<String> it = eventsStr.iterator();
        EventTemplate template = getTemplate();
        return Iterators.transform(it, template::str2event);
      }
    }

    Iterator<String> it = new FileIterator(path);
    EventTemplate template = getTemplate();
    return Iterators.transform(it, template::str2event);
  }

  private void wait(int sec) {
    try {
      TimeUnit.SECONDS.sleep(sec);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * for debug
   */
  private void checkNull(Graph graph) {
    for(EventVertex ev: graph.events()) {
      assert ev.event != null;
      for(ArrayList<AttributeVertex> edges: ev.getEdges().values()) {
        for(AttributeVertex av:edges) {
          assert av != null;
        }
      }
    }

    for(AttributeVertex av: graph.attributes()) {
      assert av != null;
      for(EventVertex ev: av.getEdges()) {
        assert ev != null;
      }
    }
  }

  protected static class FileIterator implements Iterator<String> {

    private final BufferedReader br;
    private String nextLine = null;

    public FileIterator(String path) {
      BufferedReader br = null;
      try {
        br = new BufferedReader(new FileReader(path));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      this.br = br;
    }

    @Override
    public boolean hasNext() {
      if (nextLine != null) {
        return true;
      } else {
        try {
          nextLine = br.readLine();
          return (nextLine != null);
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }
    }

    @Override
    public String next() {
      if (nextLine != null || hasNext()) {
        String line = nextLine;
        nextLine = null;
        return line;
      } else {
        throw new NoSuchElementException();
      }
    }
  }

  private static class EmptyExample extends Example {

    public EmptyExample(Config args) {
      super(args);
    }

    @Override
    protected String parameters() {
      return "";
    }

    @Override
    public String getName() {
      return "empty";
    }

    @Override
    public Query getQuery() {
      return null;
    }

    @Nonnull
    @Override
    public Iterator<Event> readInput(ArrayList<String> str) {
      return Collections.emptyIterator();
    }

    @Override
    public EventTemplate getTemplate() {
      return null;
    }

    @Override
    public ArrayList<Constructor> getConstructors() {
      return null;
    }
  }
}
