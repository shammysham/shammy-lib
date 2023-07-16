package shammylib.responsive.cardtriggers;

import shammylib.responsive.permissible.CardPermissible;

public interface TriggersFromHand extends CardPermissible {
  @Override
  default boolean triggersFrom(TriggerLocation location) {
    return location == TriggerLocation.HAND;
  }
}
