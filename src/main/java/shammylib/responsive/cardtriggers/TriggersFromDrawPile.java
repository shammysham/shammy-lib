package shammylib.responsive.cardtriggers;

import shammylib.responsive.permissible.CardPermissible;

public interface TriggersFromDrawPile extends CardPermissible {
  @Override
  default boolean triggersFrom(TriggerLocation location) {
    return location == TriggerLocation.DRAW_PILE;
  }
}
