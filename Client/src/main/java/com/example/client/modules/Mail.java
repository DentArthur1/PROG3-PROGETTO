package com.example.client.modules;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Mail {
    /**
     *
     * Classe dati per le Mail (mittente, destinatario, contenuto, data di invio e un boolean per gestire la selezione della mail in una lista)
     * @param sender: mittente della mail;
     * @param title: oggetto della mail;
     * @param date: data di invio della mail con ora;
     * @param receivs: array destinatari della mail;
     * @param content: body della mail;
     * @param selected: boolean per la selezione della mail, usato per la gestione della checkbox;
     */
    private String sender;
    private String title;
    private String[] receivers;
    private String content;
    private LocalDateTime date;
    private final BooleanProperty selected;

    public Mail(String id, String sender, String title, String content, String[] receivers, LocalDateTime date) {
        this.sender = sender;
        this.title = title;
        this.date = date;
        this.receivers = receivers;
        this.content = content;
        this.selected = new SimpleBooleanProperty(false);
    }

    public String getSender() { /** getter per il mittente dell'email  */

        return sender;
    }

    public void setSender(String snd) { /** setter per il mitente */

        this.sender = snd;
    }

    public String getTitle() { /** getter per l'oggetto */

        return title;
    }

    public void setTitle(String title) { /** setter per l'oggetto */
        this.title = title;

    }

    public LocalDateTime getDate() { /** getter per la data */
        return date;
    }

    public void setDate(LocalDateTime date) { /** setter per la data */
        this.date = date;
    }

    public String[] getReceivers() { /** getter per i destinatari */
        return receivers;
    }
    public void setReceivers(String[] receivers) { /** setter per i destinatari */
        this.receivers = receivers;
    }

    @Override
    /**
     * Metodo `toString`.
     * Converte l'oggetto `Mail` in una stringa con un formato specifico.
     *
     * @return Rappresentazione testuale dell'email.
     */

    public String toString() { /** Concatena i campi dell'email in un formato delimitato da "§" */
        return "id" + "§" + sender + "§" + title + "§" + content + "§" + String.join("", receivers) + "§" + date;
    }

    /**
     * Metodo `isSelected`.
     * Restituisce lo stato di selezione dell'email.
     *
     * @return `true` se l'email è selezionata, altrimenti `false`.
     */

    public boolean isSelected() {
        return selected.get();
    }

    /**
     * Metodo `selectedProperty`.
     * Fornisce accesso alla proprietà boolean della selezione.
     * Utile per il binding con i controlli di interfaccia utente (checkbox).
     *
     * @return Proprietà boolean della selezione.
     */

    public BooleanProperty selectedProperty() {
        return selected;
    }

    /**
     * Metodo `setSelected`.
     * Imposta lo stato di selezione dell'email.
     *
     * @param selected Nuovo stato di selezione (`true` o `false`).
     */

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
