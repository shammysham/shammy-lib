package shammylib.responsive.permissible;

import shammylib.responsive.Responsive;
import shammylib.responsive.cardtriggers.TriggerLocation;

public interface CardPermissible extends Responsive {
  boolean triggersFrom(TriggerLocation location);
}
