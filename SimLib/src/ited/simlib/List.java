package ited.simlib;

import lnu.sms.SimObject;

class ListNode {
	SimObject data; //The node holds a SimObject which holds data
	float index;  // I added this for method insertInOrder
	ListNode next;

	ListNode(SimObject o)
	{
		this(o, null);
	}

	ListNode(SimObject o, ListNode nextNode)
	{
		data = o;
		index = (float)0.0;
		next = nextNode;
	}

	SimObject getSimObject()
	{
		return data;
	}

	ListNode getnext()
	{
		return next;
	}
}

public class List
{
	private ListNode firstNode;
	private ListNode lastNode;
	private ListNode newNode;
	private ListNode thisNode;
	private String name;

	public List (String s)
	{
		name = s;
		firstNode = lastNode = null;
	}

	public List()
	{
		this("list");
	}

	public synchronized void insertInOrder
		(SimObject insertItem, float newNodeIndex)
	// Data in the first argument is inserted according to the value
	// in the second argument
	{
		if (isEmpty() || firstNode.index > newNodeIndex)
			insertAtFront(insertItem, newNodeIndex);
		else
		{
			newNode = new ListNode(insertItem);
			newNode.index = newNodeIndex;
			thisNode = firstNode;
			while (thisNode.next != null && 
				thisNode.next.index <= newNodeIndex)
				thisNode = thisNode.next;
			newNode.next = thisNode.next;
			thisNode.next = newNode;
			if (newNode.next == null) // Reset lastNode if needed
				lastNode = newNode;
		}

	}

	public synchronized void insertAtFront(SimObject insertItem)
	{
		if (isEmpty())
			firstNode = lastNode = new ListNode(insertItem);
		else
			firstNode = new ListNode(insertItem, firstNode);
	}

	public synchronized void insertAtFront(SimObject insertItem,
		float newNodeIndex)
	{
		if (isEmpty())
			firstNode = lastNode = new ListNode(insertItem);
		else
			firstNode = new ListNode(insertItem, firstNode);
		firstNode.index = newNodeIndex;
	}


	public synchronized void insertAtBack(SimObject insertItem)
	{
		if (isEmpty())
			firstNode = lastNode = new ListNode(insertItem);
		else
			lastNode = lastNode.next = new ListNode(insertItem);
	}

	public synchronized SimObject removeFromFront()
		throws EmptyListException
	{
		SimObject removeItem = null;

		if (isEmpty())
			throw new EmptyListException(name);
		removeItem = firstNode.data;
		if(firstNode.equals(lastNode))
			firstNode = lastNode = null;
		else
			firstNode = firstNode.next;
		return removeItem;
	}

	public synchronized SimObject removeFromBack()
		throws EmptyListException
	{
		SimObject removeItem = null;
		if (isEmpty())
			throw new EmptyListException(name);
		removeItem = lastNode.data;
		if (firstNode.equals(lastNode))
			firstNode = lastNode = null;
		else
		{
			ListNode current = firstNode;
			while (current.next != lastNode)
				current = current.next;
			lastNode = current;
			current.next = null;
		}
		return removeItem;
	}

	public boolean isEmpty()
	{
		return firstNode == null;
	}
	
	public void print()
	{
		if (isEmpty())
		{
			System.out.println("Empty " + name);
		}

		System.out.print("The " + name + " is: ");
		ListNode current = firstNode;
		while (current != null)
		{
			System.out.print(current.data.toString() + " ");
			current = current.next;
		}
		System.out.println("\n");
	}
}
