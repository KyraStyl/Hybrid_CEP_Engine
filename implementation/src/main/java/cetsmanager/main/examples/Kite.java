package cetsmanager.main.examples;

import cetscommon.events.Event;
import cetscommon.events.EventTemplate;
import cetscommon.query.Operator;
import cetscommon.query.Predicate;
import cetscommon.query.Query;
import cetsmanager.graph.construct.Constructor;
import cetsmanager.graph.construct.dynamic.parallel.ParallelStaticDynamicEqConstructor;
import cetsmanager.graph.construct.dynamic.sequential.SeqDynamicEqConstructor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;

public class Kite extends Example {

  private final Predicate kitePredicate;
  private final EventTemplate template;
  private final int parallism;

  public Kite(Config args) {
    super(args);
    template = new EventTemplate.Builder()
        .addNumeric("id")
        .addStr("src")
        .addStr("dest")
        .build();
    kitePredicate = new Predicate(Operator.eq, template.indexOf("src"), template.indexOf("dest"));
    this.parallism = args.parallism;
  }

  @Override
  protected String parameters() {
    return null;
  }

  @Override
  public String getName() {
    return "kite";
  }

  @Override
  public Query getQuery() {
    return new Query("C", kitePredicate, wl, sl);
  }

  @Nonnull
  @Override
  public Iterator<Event> readInput(ArrayList<String> eventsStr) {
    return readInputFromFile(path, eventsStr);
  }

  @Override
  public EventTemplate getTemplate() {
    return template;
  }

  @Override
  public ArrayList<Constructor> getConstructors() {
    ArrayList<Constructor> constructors = new ArrayList<>();
    if (parallism > 0) {
      constructors.add(new ParallelStaticDynamicEqConstructor(kitePredicate));
//      List<Value> attrs = Lists.newArrayList();
//      File file = new File(path);
//      File attrFile = new File(file.getParentFile(),"attr");
//      try(BufferedReader br = new BufferedReader(new FileReader(attrFile))){
//        attrs = br.lines().map(Value::str).collect(Collectors.toList());
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//      constructors.add(new StaticConstructor(kitePredicate,attrs));
    } else {
      constructors.add(new SeqDynamicEqConstructor(kitePredicate));
    }
    return constructors;
  }
}
