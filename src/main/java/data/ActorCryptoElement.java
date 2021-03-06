package data;

/**
 * Objects of this class represent an pending crypto-operation for encrypting or decrypting actor names
 * 
 *
 */
public class ActorCryptoElement extends CryptoElement
{
	/**
	 * Create a new object of this class with the given password
	 * @param password password for encryption or decryption operations
	 */
	public ActorCryptoElement(char[] password)
	{
		super(password);
	}
}
