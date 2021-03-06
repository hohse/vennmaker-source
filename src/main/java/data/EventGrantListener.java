/**
 * 
 */
package data;

import events.VennMakerEvent;

/**
 * Das Interface für Listener, die ein Event genehmigen können bzw. es ablehnen
 * können. Diese Listener werden vor der Eventverarbeitung informiert und können
 * Vetos einlegen. 
 * 
 * <b>WICHTIG:</b> Dies wird aber nur bei Events, die nicht Bestandteil eines 
 * <code>ComplexEvent</code> sind durchgeführt. 
 * 
 *
 */
public interface EventGrantListener
{
	/**
	 * Diese Methode wird von <code>EventProcessor</code> aufgerufen bevor
	 * das angegebene Event verarbeitet (an das Modell weitergereicht) wird.
	 * 
	 * Dies betrifft inbesondere auch künstliche Events, wie sie bei einem ReDo
	 * auftreten.
	 * 
	 * @param event Das zu prüfende Event.
	 * @return <code>true</code> wenn das Event ausgeführt werden darf, <code>false</code> wenn 
	 * eine Ausführung nicht erlaubt ist.
	 */
	public boolean grantEvent(VennMakerEvent event);
}
