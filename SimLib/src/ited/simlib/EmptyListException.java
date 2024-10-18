package ited.simlib;
public class EmptyListException extends RuntimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6647888259505698052L;

	public EmptyListException(String name)
	{
		super("The " + name + " is empty");
	}
}