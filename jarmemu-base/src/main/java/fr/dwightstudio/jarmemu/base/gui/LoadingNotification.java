package fr.dwightstudio.jarmemu.base.gui;

import javafx.application.Preloader;

public record LoadingNotification(String message) implements Preloader.PreloaderNotification {
}
