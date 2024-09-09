/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2024 Dwight Studio
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.dwightstudio.jarmemu.base.gui.view;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class UpdatableWrapper<T> implements Property<T> {

    private static final CopyOnWriteArrayList<WeakReference<UpdatableWrapper<?>>> weakRefs = new CopyOnWriteArrayList<>();

    public static void resetUpdatables() {

        for (WeakReference<UpdatableWrapper<?>> ref : weakRefs) {
            UpdatableWrapper<?> wrapper = ref.get();

            if (wrapper == null) {
                weakRefs.remove(ref);
                continue;
            }

            if (wrapper.updated) {
                wrapper.updated = false;
                wrapper.notifUpdate();
            }
        }
    }

    private boolean updated;
    private WeakReference<T> currentVal;
    private final ArrayList<ChangeListener<? super T>> changeListeners = new ArrayList<>();
    private final ArrayList<InvalidationListener> invalidationListeners = new ArrayList<>();
    private final Property<T> property;
    private final ObservableValue<T> value;

    public UpdatableWrapper(@NotNull ObservableValue<T> value) {
        if (value instanceof Property<T>) {
            this.property = (Property<T>) value;
        } else {
            this.property = null;
        }
        this.value = value;

        currentVal = new WeakReference<>(this.value.getValue());

        this.value.addListener((obs, oldVal, newVal) -> {
            if (!Objects.equals(oldVal, newVal)) {
                updated = true;
                notifUpdate(oldVal, newVal);
            }
        });

        this.value.addListener(obs -> {
            if (!Objects.equals(value.getValue(), currentVal.get())) {
                updated = true;
                notifUpdate(currentVal.get(), value.getValue());
            }
        });

        weakRefs.add(new WeakReference<>(this));
    }

    private void notifUpdate() {
        notifUpdate(value.getValue(), value.getValue());
    }

    private void notifUpdate(T oldVal, T newVal) {
        synchronized (this) {
            changeListeners.forEach(listener -> listener.changed(this, oldVal, newVal));
            invalidationListeners.forEach(listener -> listener.invalidated(this));
        }

        currentVal = new WeakReference<>(newVal);
    }

    public boolean isUpdated() {
        return updated;
    }

    @Override
    public T getValue() {
        return value.getValue();
    }

    @Override
    public void setValue(T t) {
        if (property != null) {
            if (!Objects.equals(t, value.getValue())) {
                updated = true;
                notifUpdate(value.getValue(), t);
            }
            property.setValue(t);
        }
    }

    @Override
    public void addListener(ChangeListener<? super T> changeListener) {
        synchronized (this) {
            changeListeners.add(changeListener);
        }
    }

    @Override
    public void removeListener(ChangeListener<? super T> changeListener) {
        synchronized (this) {
            changeListeners.remove(changeListener);
        }
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        synchronized (this) {
            invalidationListeners.add(invalidationListener);
        }
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        synchronized (this) {
            invalidationListeners.remove(invalidationListener);
        }
    }

    @Override
    public void bind(ObservableValue<? extends T> observableValue) {
        if (property != null) {
            property.bind(observableValue);
        }
    }

    @Override
    public void unbind() {
        if (property != null) {
            property.unbind();
        }
    }

    @Override
    public boolean isBound() {
        if (property != null) {
            return property.isBound();
        } else return false;
    }

    @Override
    public void bindBidirectional(Property<T> property) {
        if (property != null) {
            property.bindBidirectional(property);
        }
    }

    @Override
    public void unbindBidirectional(Property<T> property) {
        if (property != null) {
            property.unbindBidirectional(property);
        }
    }

    @Override
    public Object getBean() {
        return null;
    }

    @Override
    public String getName() {
        return "";
    }
}
