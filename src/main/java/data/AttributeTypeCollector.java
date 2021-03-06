package data;

/**
 * Class to combine ids and captions for the different
 * 
 * 
 */
public class AttributeTypeCollector
{
	private static int	nextId	= 0;

	/** human-readable caption for this AttributeTypeCollector */
	private String			caption	= "";

	/** id to identify this AttributeTypeCollector irrespective of the caption */
	private int				id;

	/**
	 * constructs a new AttributeTypeCollector without a caption - the id is
	 * calculated
	 */
	public AttributeTypeCollector()
	{
		this.id = nextId;
		nextId++;
	}

	/**
	 * constructs a new AttributeTypeCollector with the given caption - id will
	 * be the next available
	 * 
	 * @param caption
	 */
	public AttributeTypeCollector(String caption)
	{
		this();
		this.caption = caption;
	}

	/**
	 * Constructor, if id and caption are known (e.g. when loading)
	 * 
	 * @param id
	 * @param caption
	 */
	public AttributeTypeCollector(int id, String caption)
	{
		this.id = id;
		this.caption = caption;
	}

	/** GETTER & SETTER */
	public String getCaption()
	{
		return caption;
	}

	public void setCaption(String caption)
	{
		this.caption = caption;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

}
