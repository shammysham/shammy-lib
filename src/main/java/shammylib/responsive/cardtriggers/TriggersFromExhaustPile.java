package shammylib.responsive.cardtriggers;

import shammylib.responsive.permissible.CardPermissible;

public interface TriggersFromExhaustPile extends CardPermissible {
  @Override
  default boolean triggersFrom(TriggerLocation location) {
    return location == TriggerLocation.EXHAUST_PILE;
  }
}
