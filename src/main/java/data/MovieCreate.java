package data;

import gui.ErrorCenter;
import gui.Messages;
import gui.VennMaker;

import java.io.IOException;
import java.util.Vector;

import javax.swing.ImageIcon;

/**
 * 
 *         Erzeugt eine Bilderfolge anhand der Events
 */

public class MovieCreate
{

	private String					path;

	/**
	 * Singleton: Referenz.
	 */
	private static MovieCreate	instance;

	/**
	 * Singleton: Zugriff.
	 * 
	 * @return Die einzige Audiorecorder-Instanz in diesem Prozess.
	 */
	public synchronized static MovieCreate getInstance()
	{
		if (instance == null)
		{
			instance = new MovieCreate();

		}
		return instance;
	}

	private Vector<Vector<String>>	activityList	= new Vector<Vector<String>>();

	/**
	 * 
	 * @param VennMakerView
	 */
	private MovieCreate()
	{
	}

	public synchronized Vector<Vector<String>> getActivityList()
	{
		return this.activityList;
	}

	public synchronized void setPath(String path)
	{
		this.path = path;
	}

	private void setActivityVector()
	{

		activityList.removeAllElements();

		while ((EventProcessor.getInstance().isUndoable() == true))
		{

			if (EventProcessor.getInstance().isUndoable() == true)
				EventProcessor.getInstance().undoEvent();

		}

		/*
		 * Reset: Redo all events...
		 */
		while (EventProcessor.getInstance().isRedoable() == true)
		{

			EventProcessor.getInstance().redoEvent();

			Vector<String> row = new Vector<String>();

			row.add("" + (EventProcessor.getInstance().getCurrentTimestamp())); //$NON-NLS-1$
			row.add(EventProcessor.getInstance().getCurrentDescription()
					.toString());

			activityList.add(row);

		}

	}

	/**
	 * Difference between first activity and selected activity element
	 * @param q ActivityList element index number
	 * @return Return the time difference of the given position and the first event
	 */
	public long getTime(int q)
	{
		long zeit = 0;
		if ((q >= 0) && (q <= activityList.size()))
		{
			zeit = Long.parseLong(((Vector<String>) activityList.get(q))
					.elementAt(0).toString())
					- this.getFirstEventTime();
		}
		return zeit;
	}

	/**
	 * Returns the movie length
	 * 
	 * @return Returns the movie length
	 */
	public long getMovieLength()
	{
		return getLastEventTime() - getFirstEventTime();
	}

	/**
	 * Search the first Event time -1: If the list contains no event
	 * 
	 * @return first Event time
	 */
	public synchronized long getFirstEventTime()
	{

		if (activityList.size() > 0)
			return Long.parseLong(((Vector<String>) activityList.firstElement())
					.elementAt(0).toString());

		return -1;

	}

	/**
	 * Search the first Event time -1: If the list contains no event
	 * 
	 * @return first Event time
	 */
	public synchronized long getLastEventTime()
	{

		if (activityList.size() > 0)
			return Long.parseLong(((Vector<String>) activityList.lastElement())
					.elementAt(0).toString());

		return -1;

	}

	/**
	 * Search the previous Event time
	 * 
	 * 
	 * @param timeStamp
	 * @return previous Event time
	 */
	public synchronized long getPreviousEventTime(long timeStamp)
	{

		long timeTmp = 0;

		for (Object row : activityList)
		{

			if (Long.parseLong(((Vector<Object>) row).elementAt(0).toString()) >= timeStamp)
				return timeTmp;

			timeTmp = Long.parseLong(((Vector<Object>) row).elementAt(0)
					.toString());

		}

		return timeTmp;

	}

	/**
	 * Search the next Event time
	 * 
	 * 
	 * @param timeStamp
	 * @return next Event time
	 */
	public synchronized long getNextEventTime(long timeStamp)
	{

		long timeTmp = 0;

		for (Object row : activityList)
		{

			timeTmp = Long.parseLong(((Vector<Object>) row).elementAt(0)
					.toString());

			if (timeTmp >= timeStamp)
				return timeTmp;

		}

		return timeTmp;

	}

	/**
	 * Generate Images per Secondes (FPS)
	 * 
	 * @param fps Frames per Seconds
	 * 
	 * @param path Target path for the images
	 */
	public synchronized void generateImagesFPS(double fps, String path)
	{

		// Microseconds per Frame
		double time_diff = 1000 / fps;

		setActivityVector();

		// Schrittweise zurueck zum Anfang gehen und pro Schritt ein Bild erzeugen
		while ((EventProcessor.getInstance().isUndoable() == true)
				&& (EventProcessor.getInstance().getCurrentTimestamp() > 0))

		{

			long event_time = EventProcessor.getInstance().getCurrentTimestamp();
			long previous_event_time = getPreviousEventTime(event_time);

			// Fill the gap with images between each activity
			while ((event_time > previous_event_time) && (previous_event_time > 0)
					&& (EventProcessor.getInstance().getCurrentTimestamp() > 0))
			{
				try
				{
					VennMaker.getInstance().getActualVennMakerView().screenshot(
							path + event_time + ".png", "PNG", 800, 600);
				} catch (IOException exn)
				{
					// TODO Auto-generated catch block
					exn.printStackTrace();
					gui.ErrorCenter.manageException(exn, Messages.getString("FileOperations.1"), ErrorCenter.ERROR, true, true);

				}

				event_time = event_time - (long) time_diff;

			}
			// one event back
			if (EventProcessor.getInstance().isUndoable() == true)
				EventProcessor.getInstance().undoEvent();

		}
	}

	/**
	 * Generate Images per Event
	 * 
	 */
	public synchronized void generateSlides()
	{

		int w = (int) VennMaker.getInstance().getActualVennMakerView().getViewArea().getWidth();
		int h = (int) VennMaker.getInstance().getActualVennMakerView().getViewArea().getHeight();

		setActivityVector();

		// Schrittweise zurueck zum Anfang gehen und pro Schritt ein Bild erzeugen
		while ((EventProcessor.getInstance().isUndoable() == true)
				&& (EventProcessor.getInstance().getCurrentTimestamp() > 0))

		{

			long event_time = EventProcessor.getInstance().getCurrentTimestamp();

			try
			{
						VennMaker.getInstance().getActualVennMakerView().screenshot(
							path + event_time + ".png", "PNG", w, h);
		
			} catch (IOException exn)
			{ // TODO Auto-generated
				exn.printStackTrace();
				gui.ErrorCenter.manageException(exn, Messages.getString("FileOperations.1"), ErrorCenter.ERROR, true, true);

			}

			// one event back
			if (EventProcessor.getInstance().isUndoable() == true)
				EventProcessor.getInstance().undoEvent();

		}

		/*
		 * Reset: Redo all events...
		 */
		while (EventProcessor.getInstance().isRedoable() == true)
		{
			EventProcessor.getInstance().redoEvent();
		}
	}

	/**
	 * Returns a image
	 * 
	 * @param position
	 * @return ImageIcon
	 */
	public synchronized ImageIcon getImage(int position)
	{

		if (position < activityList.size())
		{
			Vector<String> p = activityList.get(position);
			return new ImageIcon(this.path + "" + p.get(0).toString() + ".png");
		}
		else
			return null;
	}

	/**
	 * Returns the time code
	 * 
	 * @param position activity element index number
	 * @return time code of the selected activity element
	 */
	public synchronized long getTimecode(int position)
	{

		if (position < activityList.size())
		{
			Vector<String> p = activityList.get(position);
			return Long.valueOf(p.get(0).toString());
		}
		else
			return -1;
	}

	/**
	 * Liefert das letzte Netzwerkkarten-Bilder aus der Aktivitaetsliste zurueck
	 * 
	 * @return ImageIcon
	 */
	public synchronized ImageIcon getLastImage()
	{
		if (activityList.size() > 0)
		{
			Vector<String> p = activityList.lastElement();
			return new ImageIcon(this.path + "" + p.get(0).toString() + ".png");
		}
		else
			return null;

	}

	/**
	 * Return time code of previous audio record event
	 * 
	 * @return ImageIcon
	 */
	public synchronized long getPreviousAudioRecordEvent(int position)
	{
		long timeEvent = 0;
		int q = 0;

		for (Object row : activityList)
		{

			if (((Vector<Object>) row).elementAt(1).toString().equals(
					"AudioRecordStart"))
			{
				timeEvent = Long.parseLong(((Vector<Object>) row).elementAt(0)
						.toString());
			}
			if (q >= position)
				return timeEvent;
			q++;
		}

		return -1;

	}
}
