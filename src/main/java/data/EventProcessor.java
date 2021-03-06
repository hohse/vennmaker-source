/**
 * 
 */
package data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import events.ComplexEvent;
import events.VennMakerEvent;
import gui.VennMaker;

/**
 * Der EventProcessor ist eine Singleton-Klasse, die alle eintreffenden Events
 * an die entsprechenden Datenobjekte weiterleitet, das Logging übernimmt und
 * die Undo/Redo-Funktionen auf den Daten bereitstellt. Änderungen am
 * Datenbestand sollen nur über diese Klasse möglich sein, während die GUI
 * jederzeit lesend auf die Daten im Modell zugreifen kann.
 * 
 * Dieser Prozessor leitet Events an alle registrierten Listener/Observer
 * weiter.
 * 
 * 
 */
public class EventProcessor
{
	/**
	 * Singleton: Referenz. Sollte VennMaker irgendwann mal MDI
	 * (Multiple-Documents)-tauglich sein, so ist dies allerdings schlecht.
	 */
	private static EventProcessor																								instance;

	/**
	 * Die Eventlistener, die nach Modell-Updates ausgeführt werden.
	 */
	private final List<EventPerformedListener>																			afterPerformanceListeners;

	/**
	 * Die Eventlistener, die eingehende Events erst auf Gültigkeit/Rechte
	 * prüfen.
	 */
	private final List<EventGrantListener>																					eventGrantListeners;

	/**
	 * Die Eventlistener, die für Modell-Updates zuständig sind. Je nach
	 * eintreffendem Event werden die jeweiligen Listener informiert. Dabei
	 * werden die speziellsten Listener zuerst informiert. Dies bedeutet, dass
	 * Listener, die auf <code>VennMakerEvent</code> reagieren zuletzt
	 * informiert werden.
	 */
	private final Map<Class<? extends VennMakerEvent>, List<EventListener<? extends VennMakerEvent>>>	eventListeners;

	/**
	 * Sollte nach jedem Neuladen einer Datei aufgerufen werden. Dadurch wird ein
	 * Modell komplett vom EventProcessor abgekoppelt - auch wenn es noch als
	 * Datenmüll im Speicher residiert. Sämtliche Listener werden entsorgt, so
	 * dass nur das neue Modell beteiligt wird. Die
	 * <code>EventPerformedListener</code> sind davon nicht beinträchtigt.
	 */
	public void resetEventListener()
	{
		this.afterPerformanceListeners.clear();
		this.eventListeners.clear();
	}

	/**
	 * Fügt einen Eventlistener für eingehende neue Events hinzu. Diese Listener
	 * werden über Events des Typs informiert, für den sie geschrieben wurden
	 * (Generics). Auch speziellere Events werden darüber erfasst. Es gibt keine
	 * Garantie über die Reihenfolge der aufgerufenen EventListener, außer dass
	 * EventListener für Oberklassen erst informiert werden, wenn die
	 * spezielleren Inkarnationen informiert wurden. Das heißt
	 * <code>EventListener &lt;VennMaker&gt;</code> wird garantiert <b>nach</b>
	 * <code>EventListener &lt;NetworkEvent&gt;</code> informiert.
	 * 
	 * @param listener
	 */
	public void addEventListener(EventListener<? extends VennMakerEvent> listener)
	{
		final Class<? extends VennMakerEvent> eventType = listener.getEventType();
		if (eventType == null)
		{
			System.err
					.println("No Event Type defined! Listener will not be informed about appropriate events!");
		}
		if (!eventListeners.containsKey(eventType))
		{
			eventListeners.put(eventType,
					new LinkedList<EventListener<? extends VennMakerEvent>>());
		}
		eventListeners.get(eventType).add(listener);
	}
	
	
	/**
	 * Remove one EventListener
	 * @param listener
	 */
	public void removeEventListener(EventListener<? extends VennMakerEvent> listener)
	{
		final Class<? extends VennMakerEvent> eventType = listener.getEventType();
		if (eventType == null)
		{
			System.err
					.println("No Event Type defined! Listener will not be removed!");
		}
		if (eventListeners.containsKey(eventType))
		{
				eventListeners.get(eventType).remove(listener);
		}
	}

	/**
	 * Eine Hilfsvariable, die anzeigt, wieviele atomare Events eines komplexen
	 * Events noch nicht verarbeitet wurden. Solange dieser Wert nicht
	 * <code>0</code> ist, findet eine Abarbeitung eines komplexen Events
	 * statt.
	 */
	private int	nonComplexEventsRemaining;

	/**
	 * Singleton: Zugriff.
	 * 
	 * @return Die einzige EventProcessor Instanz in diesem Prozess.
	 */
	public static EventProcessor getInstance()
	{
		if (instance == null)
		{
			instance = new EventProcessor();
		}
		return instance;
	}

	/**
	 * Konstruktor.
	 */
	private EventProcessor()
	{
		afterPerformanceListeners = new LinkedList<EventPerformedListener>();
		eventGrantListeners = new LinkedList<EventGrantListener>();
		eventListeners = new HashMap<Class<? extends VennMakerEvent>, List<EventListener<? extends VennMakerEvent>>>();

		nonComplexEventsRemaining = 0;
	}

	/**
	 * Registriert den angegebenen Listener. Soll ein Event verarbeitet werden,
	 * werden zunächst alle hier angemeldeten Listener darüber informiert und
	 * erhalten Gelegenheit das Event abzulehnen. Ein Event wird abgelehnt, sobal
	 * ein Listener das Event ablehnt. Ein abgelehntes Event erscheint in keinem
	 * Log und wird auch sonst nirgends gespeichert.
	 * 
	 * @param listener
	 *           Ein Listener, der Events ablehnen und genehmigen kann.
	 */
	public void addEventGrantListener(EventGrantListener listener)
	{
		assert (listener != null);
		eventGrantListeners.add(listener);
	}

	/**
	 * Registriert den angegebenen Listener. Ein
	 * <code>EventPerformedListener</code> wird über das Event informiert,
	 * sobald die Änderung im Datenmodell durchgeführt wurde. Ansonsten ist die
	 * Reihenfolge der Aufrufe nicht näher spezifiziert.
	 * 
	 * @param listener
	 *           Ein gültiger Listener.
	 */
	public void addEventPerformedListener(EventPerformedListener listener)
	{
		assert (listener != null);
		this.afterPerformanceListeners.add(listener);
	}

	/**
	 * Macht die Änderungen des letzten Events rückgängig und löscht dieses aus
	 * der History.
	 */
	public void undoEvent()
	{
		try
		{
			VennMakerEvent event = VennMaker.getInstance().getLogger()
					.getLastEvent();

			final VennMakerEvent undoEvent = event.getUndoEvent();
			VennMaker.getInstance().checkForVisibility(undoEvent);
			fireEvent(undoEvent);

		} catch (NoSuchElementException exn)
		{
			// Gab wohl nix zum undo-en.
		}
	}

	/**
	 * Führt das zuletzt rückgängig gemachte Event erneut aus.
	 */
	public void redoEvent()
	{
		try{
		VennMakerEvent event = VennMaker.getInstance().getLogger()
				.getLastUndoEvent();
		if (event != null)
		{
			VennMaker.getInstance().checkForVisibility(event);
			VennMaker.getInstance().getLogger().redoLastEvent();
			fireEvent(event, false, null);
		}
		}
		 catch (NoSuchElementException exn)
			{
				// Gab wohl nix zum undo-en.
			}
	}

	/**
	 * Liefert <code>true</code>, wenn zuletzt ein Event verzeichnet worden
	 * und dies rückgängigmachbar ist.
	 * 
	 * @return <code>false</code> wenn kein solches Event vorliegt.
	 */
	public boolean isUndoable()
	{
		return !VennMaker.getInstance().getLogger().isEmpty();
	}

	/**
	 * Liefert die Beschreibung des aktuell un-undoable Events zurück.
	 * 
	 * @return <code>null</code> wenn kein solches Event vorliegt, d.h.
	 *         isRedoable() <code> false</code> zurückliefert.
	 */
	public String getRedoDescription()
	{
		return VennMaker.getInstance().getLogger().getLastUndoEvent()
				.getDescription();
	}

	/**
	 * Liefert <code>true</code>, wenn ein Ereignis nochmal durchgeführt
	 * werden kann (nach erfolgreichem Undo)
	 * 
	 * @return <code>false</code>, wenn kein solches Event vorliegt
	 */
	public boolean isRedoable()
	{
		try
		{
			return VennMaker.getInstance().getLogger().getLastUndoEvent() != null;
		} catch (NoSuchElementException exn)
		{
			return false;
		}
	}

	/**
	 * Liefert den Zeitpunkt des Events zurueck
	 */
	public long getTimestamp()
	{
		return VennMaker.getInstance().getLogger().getLastUndoEvent()
				.getTimestamp();
	}

	/**
	 * Wiederholt die letzte Änderung (sofern möglich).
	 */
	public void repeatEvent()
	{
		if (isRepeatable())
		{
			VennMakerEvent event = VennMaker.getInstance().getLogger()
					.getLastEvent();
			final VennMakerEvent repeatEvent = event.getRepeatEvent();
			VennMaker.getInstance().checkForVisibility(repeatEvent);
			fireEvent(repeatEvent);
		}
	}

	/**
	 * Verarbeitet das angegebene Event, indem alle angemeldeten EventListener
	 * darüber informiert werden. Diese Methode darf NICHT aus der
	 * Initialisierung der Klasse <code>Vennmaker</code> heraus aufgerufen
	 * werden (insbesondere auch Klassen die währenddessen erzeugt werden sind
	 * davon betroffen). Ansonsten droht eine Endlosschleife mit Pufferüberlauf.
	 * 
	 * @param event
	 *           Das aufgerufene Event.
	 */
	public void fireEvent(VennMakerEvent event)
	{
		fireEvent(event, true, null);
	}

	/**
	 * 
	 * @param event
	 *           Event wie oben
	 * @param newEvent
	 *           zeigt an, ob das Event neu ist (beispiel für alte Events: nach
	 *           redo-wunsch, da dieses event nur erneut durchgejagt wird).
	 * @param dispatch
	 *           zeigt an, für welches Netzwerk dieses Ereignis bestimmt sein
	 *           könnte. Zwar könnte das in vielen Fällen aus dem event
	 *           herausgelesen werden, jedoch nicht immer. Durch Angabe eines
	 *           Netzwerks, wird das Event in die Undo-Liste des jeweiligen
	 *           Netzwerks aufgenommen. Ist dispatch <code>null</code>, so
	 *           wird das Event nur normal geloggt, kann aber nicht mehr
	 *           rückgängig gemacht werden.
	 * 
	 */
	private void fireEvent(VennMakerEvent event, boolean newEvent,
			Netzwerk dispatch)
	{
		// Erst prüfen, ob Event erlaubt ist.
		// Wenn ein Event Bestandteil eines komplexen Events ist, so kann es nicht
		// abgelehnt werden.
		// Abgelehnt werden können nur Events, die nicht Teil eines komplexen
		// Events sind. Dadurch
		// werden Fehler in der Datenhaltung nach Undo/Redo vermieden.
		if (nonComplexEventsRemaining == 0)
			for (EventGrantListener egl : this.eventGrantListeners)
			{
				if (!egl.grantEvent(event))
				{
					return;
				}
			}

		if (event instanceof ComplexEvent)
		{
			// Mitzählen, wie oft neu evaluiert werden muss.
			nonComplexEventsRemaining += ((ComplexEvent) event).length();

			// Komplexe Events werden der Reihe nach abgearbeitet...
			for (VennMakerEvent ev : (ComplexEvent) event)
			{
				fireEvent(ev);
			}
		}

		// Der Reihe nach alle angemeldeten Listener informieren (in
		// Klassenhierarchie
		// aufwärts gehen).
		Class<? extends Object> curClass = event.getClass();
		do
		{
			if (this.eventListeners.get(curClass) != null)
				for (EventListener<? extends VennMakerEvent> l : this.eventListeners
						.get(curClass))
				{
					l.eventOccured(event);
				}
			else
			{
				// No Eventlisteners for Event defined: IGNORE!
				// System.out.println(event+ " ignored");
			}
		} while (VennMakerEvent.class != curClass
				&& (null != (curClass = curClass.getSuperclass())));

		// Logger aktualisieren.
		if (nonComplexEventsRemaining == 0)
		{
			if (event.isUndoevent())
			{
				VennMaker.getInstance().getLogger().undoLastEvent();
			}
			else
			{
				VennMaker.getInstance().getLogger().logEvent(event, newEvent);
			}
		}

		// Informiere Listener
		for (EventPerformedListener listener : new LinkedList<EventPerformedListener>(
				this.afterPerformanceListeners))
		{
			listener.eventConsumed(event);
		}

		// Für komplexe Events
		if (nonComplexEventsRemaining > 0)
		{
			--nonComplexEventsRemaining;
		}
	}

	/**
	 * Liefert <code>true</code> wenn das zuletzt ausgeführte Ereignis nochmal
	 * ausgeführt werden könnte.
	 * 
	 * @return <coed>false</code> wenn keine Wiederholung möglich ist.
	 */
	public boolean isRepeatable()
	{
		try
		{
			return VennMaker.getInstance().getLogger().getLastEvent()
					.isRepeatable();
		} catch (NoSuchElementException exn)
		{
			return false;
		}
	}

	/**
	 * Liefert die Beschreibung des zuletzt ausgeführten Ereignisses (ohne
	 * Undo-Events!).
	 * 
	 * @return Eine gültige Bezeichnung eines Events.
	 */
	public String getCurrentDescription()
	{
		try
		{
			return VennMaker.getInstance().getLogger().getLastEvent()
					.getDescription();
		} catch (NoSuchElementException exn)
		{
			return null;
		}
	}
	
	/**
	 * Liefert die Beschreibung des zuletzt ausgeführten Ereignisses (ohne
	 * Undo-Events!).
	 * 
	 * @return Eine gültige Bezeichnung eines Events.
	 */
	public long getCurrentTimestamp()
	{
		try
		{
			return VennMaker.getInstance().getLogger().getLastEvent()
					.getTimestamp();
		} catch (NoSuchElementException exn)
		{
			return 0;
		}
	}
}
