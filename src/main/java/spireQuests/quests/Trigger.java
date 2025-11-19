package spireQuests.quests;

import java.util.function.Consumer;

public class Trigger<T> {
    public T param;

    public Trigger() {

    }

    public void trigger() {
        trigger(null);
    }

    public void trigger(T obj) {
        param = obj;
        QuestManager.triggerTrackers(this);
    }

    public Consumer<Trigger<?>> getTriggerMethod(Consumer<T> method) {
        return (trigger -> {
            if (trigger == this) {
                method.accept(param);
            }
        });
    }
}
