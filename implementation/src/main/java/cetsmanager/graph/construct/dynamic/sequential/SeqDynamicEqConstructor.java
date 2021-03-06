package cetsmanager.graph.construct.dynamic.sequential;

import java.util.ArrayList;
import java.util.HashMap;

import cetsmanager.graph.AttributeVertex;
import cetsmanager.graph.EventVertex;
import cetsmanager.graph.construct.Constructor;
import cetscommon.common.values.Value;
import cetscommon.events.Event;
import cetsmanager.graph.construct.dynamic.ValueAttributeVertex;
import cetscommon.query.Predicate;

public class SeqDynamicEqConstructor extends Constructor {

  private final HashMap<Value, ValueAttributeVertex> attrs;
  public SeqDynamicEqConstructor(Predicate predicate) {
    super(predicate);
    attrs = new HashMap<>();
  }

  @Override
  public void link(EventVertex eventVertex) {
    Event event = eventVertex.event;
    Value tv =  predicate.func.apply(event.get(predicate.rightOperand));
    Value fv =  event.get(predicate.leftOperand);

    AttributeVertex from = attrs.get(fv);
    if(from == null) {
      from = new ValueAttributeVertex(fv);
    }
    from.linkToEvent(eventVertex);
    countF++;

    AttributeVertex to = attrs.get(tv);
    if(to == null) {
      to = new ValueAttributeVertex(tv);
    }
    eventVertex.linkToAttr(predicate.tag,to);
    countT++;
  }

  @Override
  public void manage() {

  }

  @Override
  public int countAttr() {
    return attrs.size();
  }

  @Override
  public ArrayList<AttributeVertex> attributes() {
    return new ArrayList<>(attrs.values());
  }
}
