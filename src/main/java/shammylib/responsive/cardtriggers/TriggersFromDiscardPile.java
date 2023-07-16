package shammylib.responsive.cardtriggers;

import shammylib.responsive.permissible.CardPermissible;

public interface TriggersFromDiscardPile extends CardPermissible {
  @Override
  default boolean triggersFrom(TriggerLocation location) {
    return location == TriggerLocation.DISCARD_PILE;
  }
}
