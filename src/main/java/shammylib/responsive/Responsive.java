package shammylib.responsive;

import shammylib.responsive.cardtriggers.TriggerLocation;

public interface Responsive {
  default boolean triggersFrom(TriggerLocation location){
    return false;
  }
}
