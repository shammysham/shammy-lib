package shammylib.responsive;

import shammylib.responsive.cardtriggers.*;
import shammylib.responsive.permissible.*;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.commons.lang3.ClassUtils;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

public class ResponseHandler {

  private static final HashSet<Class <? extends Responsive>> ADDITIONAL_RELIC_LISTENERS = new HashSet<>();
  private static final HashSet<Class <? extends Responsive>> ADDITIONAL_POWER_LISTENERS = new HashSet<>();
  private static final HashSet<Class <? extends Responsive>> ADDITIONAL_CARD_LISTENERS = new HashSet<>();
  private static final HashSet<Class <? extends Responsive>> ADDITIONAL_HAND_TRIGGER_LISTENERS = new HashSet<>();
  private static final HashSet<Class <? extends Responsive>> ADDITIONAL_DRAW_PILE_TRIGGER_LISTENERS = new HashSet<>();
  private static final HashSet<Class <? extends Responsive>> ADDITIONAL_DISCARD_PILE_TRIGGER_LISTENERS = new HashSet<>();
  private static final HashSet<Class <? extends Responsive>> ADDITIONAL_EXHAUST_PILE_TRIGGER_LISTENERS = new HashSet<>();

  public static void registerRelicListener(Class<? extends Responsive> function) {
    registerListener(function, RelicPermissible.class, ADDITIONAL_RELIC_LISTENERS);
  }

  private static void registerCardListener(Class<? extends Responsive> function) {
    registerListener(function, CardPermissible.class, ADDITIONAL_CARD_LISTENERS);
  }

  public static void registerPowerListener(Class<? extends Responsive> function) {
    registerListener(function, PowerPermissible.class, ADDITIONAL_POWER_LISTENERS);
  }

  private static void registerListener(Class<? extends Responsive> function, Class<? extends Responsive> exclude, HashSet<Class<? extends Responsive>> listenerGroup) {
    if(!ClassUtils.getAllInterfaces(function).contains(exclude)) {
      listenerGroup.add(function);
    }
  }

  public static void registerHandTriggerListener(Class<? extends Responsive> function) {
    registerCardTriggerLocationListener(function, TriggersFromHand.class, ADDITIONAL_HAND_TRIGGER_LISTENERS);
  }

  public static void registerDrawPileTriggerListener(Class<? extends Responsive> function) {
    registerCardTriggerLocationListener(function, TriggersFromDrawPile.class, ADDITIONAL_DRAW_PILE_TRIGGER_LISTENERS);
  }

  public static void registerDiscardPileTriggerListener(Class<? extends Responsive> function) {
    registerCardTriggerLocationListener(function, TriggersFromDiscardPile.class, ADDITIONAL_DISCARD_PILE_TRIGGER_LISTENERS);
  }

  public static void registerExhaustPileTriggerListener(Class<? extends Responsive> function) {
    registerCardTriggerLocationListener(function, TriggersFromExhaustPile.class, ADDITIONAL_EXHAUST_PILE_TRIGGER_LISTENERS);
  }

  private static void registerCardTriggerLocationListener(Class<? extends Responsive> function, Class<? extends Responsive> exclude, HashSet<Class<? extends Responsive>> listenerGroup) {
    if(!ClassUtils.getAllInterfaces(function).contains(exclude)) {
      registerCardListener(function);
      listenerGroup.add(function);
    }
  }

  public static void unregisterRelicListener(Class<? extends Responsive> function) {
    ADDITIONAL_RELIC_LISTENERS.remove(function);
  }

  public static void unregisterPowerListener(Class<? extends Responsive> function) {
    ADDITIONAL_POWER_LISTENERS.remove(function);
  }

  public static void unregisterHandTriggerListener(Class<? extends Responsive> function) {
    unregisterCardTriggerLocationListener(function, ADDITIONAL_HAND_TRIGGER_LISTENERS);
  }

  public static void unregisterDrawPileTriggerListener(Class<? extends Responsive> function) {
    unregisterCardTriggerLocationListener(function, ADDITIONAL_DRAW_PILE_TRIGGER_LISTENERS);
  }

  public static void unregisterDiscardPileTriggerListener(Class<? extends Responsive> function) {
    unregisterCardTriggerLocationListener(function, ADDITIONAL_DISCARD_PILE_TRIGGER_LISTENERS);
  }

  public static void unregisterExhaustPileTriggerListener(Class<? extends Responsive> function) {
    unregisterCardTriggerLocationListener(function, ADDITIONAL_EXHAUST_PILE_TRIGGER_LISTENERS);
  }

  private static void unregisterCardTriggerLocationListener(Class<? extends Responsive> function, HashSet<Class<? extends Responsive>> listenerGroup) {
    listenerGroup.remove(function);
    checkCardLocationListeners(function);
  }

  private static void checkCardLocationListeners(Class<? extends Responsive> function) {
    if(!(ADDITIONAL_HAND_TRIGGER_LISTENERS.contains(function)
        || ADDITIONAL_DRAW_PILE_TRIGGER_LISTENERS.contains(function)
        || ADDITIONAL_DISCARD_PILE_TRIGGER_LISTENERS.contains(function)
        || ADDITIONAL_EXHAUST_PILE_TRIGGER_LISTENERS.contains(function))) {
      ADDITIONAL_CARD_LISTENERS.remove(function);
    }
  }

  public static <T extends Responsive> Stream<T> relics(Class<T> function) {
    return collapseStreamToType(AbstractDungeon.player.relics.stream(), function);
  }

  public static <T extends Responsive> Stream<T> allPowers(Class<T> function) {
    return collapseStreamToType(Stream.of(
        AbstractDungeon.player.powers.stream(),
        AbstractDungeon.getMonsters().monsters.stream().flatMap(monster -> monster.powers.stream())
    ).flatMap(obj -> obj), function);
  }

  public static <T extends Responsive> Stream<T> playerPowers(Class<T> function) {
    return collapseStreamToType(AbstractDungeon.player.powers.stream(), function);
  }

  public static <T extends Responsive> Stream<T> monsterPowers(Class<T> function) {
    return collapseStreamToType(AbstractDungeon.getMonsters().monsters.stream().flatMap(monster -> monster.powers.stream()), function);
  }

  public static <T extends Responsive> Stream<T> drawPile(Class<T> function) {
    return cards(function, AbstractDungeon.player.drawPile, TriggerLocation.DRAW_PILE);
  }

  public static <T extends Responsive> Stream<T> hand(Class<T> function) {
    return cards(function, AbstractDungeon.player.hand, TriggerLocation.HAND);
  }

  public static <T extends Responsive> Stream<T> discardPile(Class<T> function) {
    return cards(function, AbstractDungeon.player.discardPile, TriggerLocation.DISCARD_PILE);
  }

  public static <T extends Responsive> Stream<T> exhaustPile(Class<T> function) {
    return cards(function, AbstractDungeon.player.exhaustPile, TriggerLocation.EXHAUST_PILE);
  }

  public static <T extends Responsive> Stream<T> allCards(Class<T> function) {
    List<Class<?>> responsiveTypes = ClassUtils.getAllInterfaces(function);
    return Stream.of(
        responsiveTypes.contains(TriggersFromHand.class) || ADDITIONAL_HAND_TRIGGER_LISTENERS.contains(function) ? hand(function) : Stream.<T>empty(),
        responsiveTypes.contains(TriggersFromDiscardPile.class) || ADDITIONAL_DISCARD_PILE_TRIGGER_LISTENERS.contains(function) ? discardPile(function) : Stream.<T>empty(),
        responsiveTypes.contains(TriggersFromDrawPile.class) || ADDITIONAL_DRAW_PILE_TRIGGER_LISTENERS.contains(function) ? drawPile(function) : Stream.<T>empty(),
        responsiveTypes.contains(TriggersFromExhaustPile.class) || ADDITIONAL_EXHAUST_PILE_TRIGGER_LISTENERS.contains(function) ? exhaustPile(function) : Stream.<T>empty()
    ).flatMap(obj -> obj);
  }

  public static <T extends Responsive> Stream<T> all(Class<T> function) {
    List<Class<?>> responsiveTypes = ClassUtils.getAllInterfaces(function);
    return Stream.of(
        responsiveTypes.contains(PowerPermissible.class) || ADDITIONAL_POWER_LISTENERS.contains(function) ? playerPowers(function) : Stream.<T>empty(),
        responsiveTypes.contains(RelicPermissible.class) || ADDITIONAL_RELIC_LISTENERS.contains(function) ? relics(function) : Stream.<T>empty(),
        responsiveTypes.contains(CardPermissible.class) || ADDITIONAL_CARD_LISTENERS.contains(function) ? allCards(function) : Stream.<T>empty(),
        responsiveTypes.contains(PowerPermissible.class) || ADDITIONAL_POWER_LISTENERS.contains(function) ? monsterPowers(function) : Stream.<T>empty()
    ).flatMap(obj -> obj);
  }

  public static <T extends Responsive> Stream<T> cards(Class<T> function, CardGroup group, TriggerLocation location) {
    return collapseStreamToType(group.group.stream(), function).filter((card) -> card.triggersFrom(location));
  }

  public static <T> Stream<T> collapseStreamToType(Stream<?> stream, Class<T> type){
    return stream.filter(type::isInstance).map(type::cast);
  }
}
