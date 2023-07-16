import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import shammylib.responsive.ResponseHandler;
import shammylib.responsive.Responsive;
import shammylib.responsive.cardtriggers.TriggersFromDiscardPile;
import shammylib.responsive.cardtriggers.TriggersFromDrawPile;
import shammylib.responsive.permissible.PowerPermissible;
import shammylib.responsive.permissible.RelicPermissible;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ResponseHandlerTest {

  @Test
  void collapseStreamToType_castUpwards() {
    Stream<Integer> stream = Stream.of(1, 2, 3);
    Stream<Object> objStream = ResponseHandler.collapseStreamToType(stream, Object.class);

    assertEquals(3, objStream.count());
  }

  @Test
  void collapseStreamToType_invalidCast() {
    Stream<Integer> stream = Stream.of(1, 2, 3);
    Stream<String> objStream = ResponseHandler.collapseStreamToType(stream, String.class);

    assertEquals(0, objStream.count());
  }

  @Test
  void collapseStreamToType_castDownwards() {
    Stream<Object> stream = Stream.of(1, 2, 3, "Test");
    List<String> strings = ResponseHandler.collapseStreamToType(stream, String.class).collect(Collectors.toList());

    assertEquals(1, strings.size());
    assertEquals(strings.get(0), "Test");
  }

  @Test
  void registerPowerListener_registerRelicPermissible() throws NoSuchFieldException, IllegalAccessException {
    Field powerListeners = ResponseHandler.class.getDeclaredField("ADDITIONAL_POWER_LISTENERS");
    powerListeners.setAccessible(true);

    ResponseHandler.registerPowerListener(TestRelicInterface.class);

    HashSet<Class<? extends Responsive>> ADDITIONAL_POWER_LISTENERS = (HashSet<Class<? extends Responsive>>) powerListeners.get(null);
    assertEquals(1, ADDITIONAL_POWER_LISTENERS.size());
    assertTrue(ADDITIONAL_POWER_LISTENERS.contains(TestRelicInterface.class));
  }

  @Test
  void registerPowerListener_registerPowerPermissible() throws NoSuchFieldException, IllegalAccessException {
    Field powerListeners = ResponseHandler.class.getDeclaredField("ADDITIONAL_POWER_LISTENERS");
    powerListeners.setAccessible(true);

    ResponseHandler.registerPowerListener(TestPowerInterface.class);

    HashSet<Class<? extends Responsive>> ADDITIONAL_POWER_LISTENERS = (HashSet<Class<? extends Responsive>>) powerListeners.get(null);
    assertEquals(0, ADDITIONAL_POWER_LISTENERS.size());
  }

  @Test
  void registerDrawPileTriggerListener_registerNonCardPermissible() throws NoSuchFieldException, IllegalAccessException {
    Field drawPileTriggerListeners = ResponseHandler.class.getDeclaredField("ADDITIONAL_DRAW_PILE_TRIGGER_LISTENERS");
    drawPileTriggerListeners.setAccessible(true);

    Field cardListeners = ResponseHandler.class.getDeclaredField("ADDITIONAL_CARD_LISTENERS");
    cardListeners.setAccessible(true);

    ResponseHandler.registerDrawPileTriggerListener(TestRelicInterface.class);
    HashSet<Class<? extends Responsive>> ADDITIONAL_DRAW_PILE_TRIGGER_LISTENERS = (HashSet<Class<? extends Responsive>>) drawPileTriggerListeners.get(null);
    HashSet<Class<? extends Responsive>> ADDITIONAL_CARD_LISTENERS = (HashSet<Class<? extends Responsive>>) cardListeners.get(null);

    assertEquals(1, ADDITIONAL_DRAW_PILE_TRIGGER_LISTENERS.size());
    assertTrue(ADDITIONAL_DRAW_PILE_TRIGGER_LISTENERS.contains(TestRelicInterface.class));

    assertEquals(1, ADDITIONAL_CARD_LISTENERS.size());
    assertTrue(ADDITIONAL_CARD_LISTENERS.contains(TestRelicInterface.class));
  }

  @Test
  void registerDrawPileTriggerListener_registerOtherTypeCardPermissible() throws NoSuchFieldException, IllegalAccessException {
    Field drawPileTriggerListeners = ResponseHandler.class.getDeclaredField("ADDITIONAL_DRAW_PILE_TRIGGER_LISTENERS");
    drawPileTriggerListeners.setAccessible(true);

    Field cardListeners = ResponseHandler.class.getDeclaredField("ADDITIONAL_CARD_LISTENERS");
    cardListeners.setAccessible(true);

    ResponseHandler.registerDrawPileTriggerListener(TestTriggersFromDiscardPileInterface.class);
    HashSet<Class<? extends Responsive>> ADDITIONAL_DRAW_PILE_TRIGGER_LISTENERS = (HashSet<Class<? extends Responsive>>) drawPileTriggerListeners.get(null);
    HashSet<Class<? extends Responsive>> ADDITIONAL_CARD_LISTENERS = (HashSet<Class<? extends Responsive>>) cardListeners.get(null);

    assertEquals(1, ADDITIONAL_DRAW_PILE_TRIGGER_LISTENERS.size());
    assertTrue(ADDITIONAL_DRAW_PILE_TRIGGER_LISTENERS.contains(TestTriggersFromDiscardPileInterface.class));

    assertEquals(0, ADDITIONAL_CARD_LISTENERS.size());
  }

  @Test
  void registerDrawPileTriggerListener_registerTriggersFromDrawPile() throws NoSuchFieldException, IllegalAccessException {
    Field drawPileTriggerListeners = ResponseHandler.class.getDeclaredField("ADDITIONAL_DRAW_PILE_TRIGGER_LISTENERS");
    drawPileTriggerListeners.setAccessible(true);

    Field cardListeners = ResponseHandler.class.getDeclaredField("ADDITIONAL_CARD_LISTENERS");
    cardListeners.setAccessible(true);

    ResponseHandler.registerDrawPileTriggerListener(TestTriggersFromDrawPileInterface.class);
    HashSet<Class<? extends Responsive>> ADDITIONAL_DRAW_PILE_TRIGGER_LISTENERS = (HashSet<Class<? extends Responsive>>) drawPileTriggerListeners.get(null);
    HashSet<Class<? extends Responsive>> ADDITIONAL_CARD_LISTENERS = (HashSet<Class<? extends Responsive>>) cardListeners.get(null);

    assertEquals(0, ADDITIONAL_DRAW_PILE_TRIGGER_LISTENERS.size());
    assertEquals(0, ADDITIONAL_CARD_LISTENERS.size());
  }

  @AfterEach
  void cleanupRegistrations() {
    Arrays.stream(ResponseHandler.class.getDeclaredFields())
        .map(field -> {
          field.setAccessible(true);
          try {
            return (HashSet<Class<? extends Responsive>>) field.get(null);
          } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
          }
        })
        .forEach(HashSet::clear);
  }

  public interface TestRelicInterface extends RelicPermissible {}
  public interface TestPowerInterface extends PowerPermissible {}
  public interface TestTriggersFromDiscardPileInterface extends TriggersFromDiscardPile {}
  public interface TestTriggersFromDrawPileInterface extends TriggersFromDrawPile {}
}
