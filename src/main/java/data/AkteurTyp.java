package data;

import java.io.Serializable;

/**
 * Beschreibt den Typ eines Akteurs. Dies beinhaltet die graphische
 * Repraesentation bei Auswahl (Icon fuer Button) und auf der Zeichenflaeche
 * (Image).
 * 
 */
@Deprecated
public class AkteurTyp implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private String					bezeichnung;

	private String					imageFile;

	public AkteurTyp()
	{
	}

	public AkteurTyp(final String bezeichnung)
	{
		this.bezeichnung = bezeichnung;
	}

	public String getBezeichnung()
	{
		return this.bezeichnung;
	}

	public String getImageFile()
	{
		return this.imageFile;
	}

	/**
	 * Overwrites all values with those contained in <code>t</code>.
	 * 
	 * @param t
	 *           The original to copy the values from.
	 */
	public void overwrite(final AkteurTyp t)
	{
		this.imageFile = t.imageFile;
		this.bezeichnung = t.bezeichnung;
	}

	public void setBezeichnung(final String bezeichnung)
	{
		this.bezeichnung = bezeichnung;
	}

	public void setImageFile(final String imageFile)
	{
		this.imageFile = imageFile;
	}

	/**
	 * Erzwingt das Anmelden aller Modellobjekte am <code>EventProcessor</code>.
	 * Nach dem Deserialisieren muss diese Methode explizit aufgerufen werden!
	 * Dies wird von <code>Projekt.registerEventListeners()</code> erledigt.
	 */
	protected void registerEventListeners()
	{

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Object clone()
	{
		AkteurTyp t = new AkteurTyp();
		t.imageFile = this.imageFile;
		t.bezeichnung = this.bezeichnung;
		return t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return getBezeichnung() + " (" + getImageFile() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

}
