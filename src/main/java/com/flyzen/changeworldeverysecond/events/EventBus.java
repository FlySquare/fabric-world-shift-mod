package com.flyzen.changeworldeverysecond.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public final class EventBus {
	private final Map<Class<?>, List<Consumer<?>>> listeners = new HashMap<>();

	public <T> void subscribe(Class<T> eventType, Consumer<T> listener) {
		Objects.requireNonNull(eventType, "eventType");
		Objects.requireNonNull(listener, "listener");
		listeners.computeIfAbsent(eventType, key -> new ArrayList<>()).add(listener);
	}

	@SuppressWarnings("unchecked")
	public <T> void publish(T event) {
		Objects.requireNonNull(event, "event");

		List<Consumer<?>> eventListeners = listeners.get(event.getClass());
		if (eventListeners == null || eventListeners.isEmpty()) {
			return;
		}

		for (Consumer<?> listener : List.copyOf(eventListeners)) {
			((Consumer<T>) listener).accept(event);
		}
	}
}
