/**
 * 
 */
package data;

import gui.VennMaker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Compute
{

	int[][]							adjacency_matrix;

	HashMap<Akteur, Integer>	actorMap				= new HashMap<Akteur, Integer>();

	String[]							actorNames;

	private Netzwerk				network;

	private Collection<Akteur>	actorList;

	private boolean				DEBUG					= false;

	/*
	 * saves the number of attributes, which are to be listed in the
	 * compute-dialogue (mainly to control the number of rows in said dialogue)
	 */
	private int						attributeCounter	= 0;

	/**
	 * Create an adjacency matrix only for actors drawn on the network map
	 * 
	 * @param Network
	 * @param Actors
	 */
	public Compute(Netzwerk n, Collection<Akteur> a)
	{
		network = n;
		actorList = a;
		this.calculateAdjacencyMatrix();
	}

	/**
	 * Create an adjacency matrix
	 * 
	 * @param Network
	 * @param Actors
	 * @param allActors
	 *           : true: for all actors, false: only for actors drawn on the
	 *           network map
	 */
	public Compute(Netzwerk n, Collection<Akteur> a, boolean allActors)
	{

		network = n;
		actorList = a;
		// only actors drawn on the network map
		if (allActors == false)
			this.calculateAdjacencyMatrix();

		// for all actors
		if (allActors == true)
			this.calculateAdjacencyMatrixAllActors();

	}

	private void debug(String t)
	{
		if (DEBUG == true)
			System.out.println("Debug: " + t);
	}

	/*
	 * Calculate nn-matrix (Binary- or Adjacency-Matrix: 0 or 1)
	 * 
	 * actor in row = sender (from) actor in coloumn = receiver (to)
	 */
	private void calculateAdjacencyMatrix()
	{

		int x = 0;
		int y = 0;

		adjacency_matrix = new int[actorList.size()][actorList.size()];
		actorNames = new String[actorList.size()];

		debug("calculateAdjacencyMatrix...");

		// Reset the matrix
		for (int q = 0; q < actorList.size(); q++)
			for (int w = 0; w < actorList.size(); w++)
				adjacency_matrix[q][w] = 0;

		for (final Akteur alter : actorList)
		{
			for (final Akteur alter2 : actorList)
			{

				if ((alter != alter2) && network.getAkteure().contains(alter2))
				{
					setConnectionValue(x, y, alter, alter2);
				}
				else if (network.getAkteure().contains(alter2))
					adjacency_matrix[x][y] = 0;

				x++;
			}
			x = 0;
			this.actorMap.put(alter, y);
			actorNames[y] = alter.getName().toString();

			y++;
		}

	}

	/*
	 * Calculate nn-matrix (Binary- or Adjacency-Matrix: 0 or 1) for all actors
	 * 
	 * actor in row = sender (from) actor in coloumn = receiver (to)
	 */
	private void calculateAdjacencyMatrixAllActors()
	{

		int x = 0;
		int y = 0;

		adjacency_matrix = new int[actorList.size()][actorList.size()];
		actorNames = new String[actorList.size()];

		debug("calculateAdjacencyMatrix for all actors...");

		// Reset the matrix
		for (int q = 0; q < actorList.size(); q++)
			for (int w = 0; w < actorList.size(); w++)
				adjacency_matrix[q][w] = 0;

		for (final Akteur alter : actorList)
		{

			for (final Akteur alter2 : actorList)
			{
				if (alter != alter2)
				{
					setConnectionValue(x, y, alter, alter2);
				}
				else
					adjacency_matrix[x][y] = 0;

				x++;
			}
			x = 0;
			this.actorMap.put(alter, y);
			actorNames[y] = alter.getName().toString();

			y++;

		}

	}

	/**
	 * Checks, if alter and alter2 are connected with each other and inserts the
	 * corresponding value into the adjacency matrix at position x, y
	 * 
	 * @param x
	 *           position x in the adjacency matrix
	 * @param y
	 *           position y in the adjacency matrix
	 * @param alter
	 *           first actor to check, if it's connected with alter2
	 * @param alter2
	 *           second actor to check the connection between alter and alter2
	 */
	private void setConnectionValue(int x, int y, final Akteur alter,
			final Akteur alter2)
	{
		Relation tempRelation = alter.getRelationTo(alter2, network);
		if (tempRelation != null)
			adjacency_matrix[x][y] = 1;

		else
		{
			Relation tempRelation2 = alter2.getRelationTo(alter, network);
			if ((tempRelation2 != null)
					&& (!VennMaker.getInstance().getProject()
							.getIsDirected(tempRelation2.getAttributeCollectorValue())))
				adjacency_matrix[x][y] = 1;
			else
				adjacency_matrix[x][y] = 0;
		}
	}

	public int[][] getAdjacencyMatrix()
	{

		return this.adjacency_matrix;
	}

	/*
	 * Outdegree
	 * 
	 * @param column of an adjacency matrix (=actor)
	 */
	public int outDegree(Akteur actor)
	{
		int sum = 0;

		debug("outDegree...");

		if (actorMap.containsKey(actor))
		{
			sum = outDegree(actor, sum);
		}

		return sum;
	}

	private int outDegree(Akteur actor, int sum)
	{
		int column;
		column = actorMap.get(actor);

		for (int row = 0; row < this.adjacency_matrix.length; row++)
		{
			sum += this.adjacency_matrix[row][column];
		}
		return sum;
	}

	/*
	 * Outdegree Standardized
	 * 
	 * @param column of an adjacency matrix (=actor)
	 */
	public double outDegreeStd(Akteur actor)
	{

		int sum = 0;
		double std = 0.0;

		debug("outDegreeStd...");

		if (actorMap.containsKey(actor))
		{
			sum = outDegree(actor, sum);

			if (this.adjacency_matrix.length > 0)
				std = (double) sum / (this.adjacency_matrix.length - 1);
		}

		return std;
	}

	/*
	 * Indegree
	 * 
	 * @param row of an adjacency matrix (=actor)
	 */
	public int inDegree(Akteur actor)
	{
		int sum = 0;

		debug("inDegree...");

		if (actorMap.containsKey(actor))
		{
			sum = inDegree(actor, sum);
		}

		return sum;
	}

	private int inDegree(Akteur actor, int sum)
	{
		int row;
		row = actorMap.get(actor);

		for (int column = 0; column < this.adjacency_matrix.length; column++)
		{
			sum += this.adjacency_matrix[row][column];

		}
		return sum;
	}

	/*
	 * Indegree Standardized
	 * 
	 * @param row of an adjacency matrix (=actor)
	 */
	public double inDegreeStd(Akteur actor)
	{

		int sum = 0;
		double std = 0.0;

		debug("inDegreeStd...");

		if (actorMap.containsKey(actor))
		{
			sum = inDegree(actor, sum);

			if (this.adjacency_matrix.length > 0)
				std = (double) sum / (this.adjacency_matrix.length - 1);
		}

		return std;
	}

	/*
	 * Centrality: Calculate the Density. Ego is included!
	 */
	public double densityWithEgo()
	{

		double result;
		int sum = 0;

		debug("densityWithEgo...");

		for (int row = 0; row < this.adjacency_matrix.length; row++)
			for (int column = 0; column < (this.adjacency_matrix.length); column++)
				sum += this.adjacency_matrix[row][column];

		if (this.adjacency_matrix.length > 1)
			result = (double) sum
					/ (this.adjacency_matrix.length * (this.adjacency_matrix.length - 1));
		else
			result = 0;

		return result;
	}

	/*
	 * Centrality: Calculate the Density. Ego is not included!
	 * 
	 * @param Aktor ego
	 */
	public double densityWithOutEgo(Akteur ego)
	{

		double result;
		int sum = 0;
		int ego_position = 0;

		debug("densityWithOutEgo...");

		if (actorMap.containsKey(ego))
		{
			ego_position = actorMap.get(ego);
		}

		for (int row = 0; row < this.adjacency_matrix.length; row++)
			for (int column = 0; column < this.adjacency_matrix.length; column++)
				if ((row != ego_position) && (column != ego_position))
					sum += this.adjacency_matrix[row][column];

		if (this.adjacency_matrix.length > 2)
			result = (double) (sum)
					/ ((this.adjacency_matrix.length - 1) * (this.adjacency_matrix.length - 2));
		else
			result = 0;

		return result;
	}

	/**
	 * Returns all directly connected actors (actor_source -> other actors)
	 * 
	 * @param actor_source
	 * @param nodes
	 *           (other actors)
	 * @return a list of adjacency actors
	 */
	private List<Akteur> checkAdjacencyFromActor(Akteur actor_source,
			HashMap<Akteur, Integer> nodes)
	{

		ArrayList<String> tmpRelGroup = new ArrayList<String>();
		List<Akteur> actorList2 = new ArrayList<Akteur>();

		// Relationsgruppen-Liste erstellen
		for (AttributeType at : VennMaker.getInstance().getProject()
				.getAttributeTypes())
		{
			if ((!at.getType().equals("ACTOR"))
					&& (tmpRelGroup.contains(at.getType()) == false))
			{
				tmpRelGroup.add(at.getType());
			}
		}

		// alle Akteure durchgehen
		if ((network.getAkteure().contains(actor_source)))
		{

			// Gerichtete Relationen
			for (final Akteur alter2 : actorList)
			{

				if ((network.getAkteure().contains(alter2)))
				{
					// Gerichtete Relationen durchgehen und schauen, ob
					// Relationsgruppe vorkommt

					for (String relG : tmpRelGroup)
					{

						for (AttributeType at : VennMaker.getInstance().getProject()
								.getAttributeTypes())
						{

							// Alter -> Alter2
							for (Relation tmpR : actor_source.getRelations(network))
							{

								if ((tmpR.getAttributes(network) != null)
										&& (tmpR.getAkteur().equals(alter2))
										&& (!at.getType().equals("ACTOR"))
										&& (relG.equals(at.getType()))
										&& (VennMaker.getInstance().getProject()
												.getIsDirected(at.getType()))
										&& (tmpR.getAttributeCollectorValue().equals(at
												.getType())))
								{
									if ((nodes.containsKey(alter2) == false)
											&& (actorList2.contains(alter2) == false))
										actorList2.add(alter2);
								}
							}
						}
					}

					// undirected relations

					for (String relG : tmpRelGroup)
					{

						for (AttributeType at : VennMaker.getInstance().getProject()
								.getAttributeTypes())
						{

							// Alter -> Alter2
							for (Relation tmpR : actor_source.getRelations(network))

								if ((tmpR.getAttributes(network) != null)
										&& (tmpR.getAkteur().equals(alter2))
										&& (!at.getType().equals("ACTOR"))
										&& (relG.equals(at.getType()))
										&& (!VennMaker.getInstance().getProject()
												.getIsDirected(at.getType()))
										&& (tmpR.getAttributeCollectorValue().equals(at
												.getType())))
								{
									if ((nodes.containsKey(alter2) == false)
											&& (actorList2.contains(alter2) == false))
										actorList2.add(alter2);

								}

							// Alter2 -> Alter
							for (Relation tmpR : alter2.getRelations(network))

								if ((tmpR.getAttributes(network) != null)
										&& (tmpR.getAkteur().equals(actor_source))
										&& (!at.getType().equals("ACTOR"))
										&& (relG.equals(at.getType()))
										&& (!VennMaker.getInstance().getProject()
												.getIsDirected(at.getType()))
										&& (tmpR.getAttributeCollectorValue().equals(at
												.getType())))
								{
									if ((nodes.containsKey(alter2) == false)
											&& (actorList2.contains(alter2) == false))
										actorList2.add(alter2);

								}

						}

					}

				}
			}
		}

		return actorList2;
	}

	/*
	 * Liefere alle Akteure zurück, die mit Akteur direkt verbunden sind Akteur
	 * <---- andere Akteure
	 * 
	 * @param Akteur Actor who is connected FROM other actors
	 * 
	 * @return a list of adjacency actors
	 */
	private List<Akteur> checkAdjacencyToActor(Akteur actorTarget,
			HashMap<Akteur, Integer> nodes)
	{

		ArrayList<String> tmpRelGroup = new ArrayList<String>();
		List<Akteur> actorList2 = new ArrayList<Akteur>();

		// Relationsgruppen-Liste erstellen
		for (AttributeType at : VennMaker.getInstance().getProject()
				.getAttributeTypes())
		{
			if ((!at.getType().equals("ACTOR"))
					&& (tmpRelGroup.contains(at.getType()) == false))
			{
				tmpRelGroup.add(at.getType());
			}
		}

		// alle Akteure durchgehen
		if ((network.getAkteure().contains(actorTarget)))
		{

			// Gerichtete Relationen
			for (final Akteur alter2 : actorList)
			{

				if ((network.getAkteure().contains(alter2)))
				{
					// Gerichtete Relationen durchgehen und schauen, ob
					// Relationsgruppe vorkommt

					for (String relG : tmpRelGroup)
					{

						for (AttributeType at : VennMaker.getInstance().getProject()
								.getAttributeTypes())
						{
							// Alter2 -> Alter
							for (Relation tmpR : alter2.getRelations(network))

								if ((tmpR.getAttributes(network) != null)
										&& (tmpR.getAkteur().equals(actorTarget))
										&& (!at.getType().equals("ACTOR"))
										&& (relG.equals(at.getType()))
										&& (VennMaker.getInstance().getProject()
												.getIsDirected(at.getType()))
										&& (tmpR.getAttributeCollectorValue().equals(at
												.getType())))
								{
									if ((nodes.containsKey(alter2) == false)
											&& (actorList2.contains(alter2) == false))
										actorList2.add(alter2);

								}
						}

					}

					// undirected relations

					for (String relG : tmpRelGroup)
					{

						for (AttributeType at : VennMaker.getInstance().getProject()
								.getAttributeTypes())
						{

							// Alter -> Alter2
							for (Relation tmpR : actorTarget.getRelations(network))

								if ((tmpR.getAttributes(network) != null)
										&& (tmpR.getAkteur().equals(alter2))
										&& (!at.getType().equals("ACTOR"))
										&& (relG.equals(at.getType()))
										&& (!VennMaker.getInstance().getProject()
												.getIsDirected(at.getType()))
										&& (tmpR.getAttributeCollectorValue().equals(at
												.getType())))
								{
									if ((nodes.containsKey(alter2) == false)
											&& (actorList2.contains(alter2) == false))
										actorList2.add(alter2);

								}

							// Alter2 -> Alter
							for (Relation tmpR : alter2.getRelations(network))

								if ((tmpR.getAttributes(network) != null)
										&& (tmpR.getAkteur().equals(actorTarget))
										&& (!at.getType().equals("ACTOR"))
										&& (relG.equals(at.getType()))
										&& (!VennMaker.getInstance().getProject()
												.getIsDirected(at.getType()))
										&& (tmpR.getAttributeCollectorValue().equals(at
												.getType())))
								{
									if ((nodes.containsKey(alter2) == false)
											&& (actorList2.contains(alter2) == false))
										actorList2.add(alter2);

								}

						}

					}

				}
			}
		}

		return actorList2;
	}

	/**
	 * Path distances from actor source
	 * 
	 * @param actor
	 *           source
	 * @return path distances
	 */
	public HashMap<Akteur, Integer> calculateDistancesFromActor(
			Akteur actor_start)
	{

		int level = 0;
		boolean newNode = true;

		// Ziel-Akteur und Tiefe speichern
		HashMap<Akteur, Integer> nodes = new HashMap<Akteur, Integer>();

		nodes.put(actor_start, level);

		HashMap<Akteur, Integer> levelNodes = new HashMap<Akteur, Integer>();

		if (actor_start.getRelations(network) != null)
			while (newNode == true)
			{

				level++;
				newNode = false;

				levelNodes.clear();

				for (Akteur actor_next : nodes.keySet())
				{
					;
					for (Akteur newActor : checkAdjacencyFromActor(actor_next, nodes))
					{
						levelNodes.put(newActor, level);

					}

				}

				// Akteure, mit gleicher Distanz zur Gesamtliste hinzufügen
				if (levelNodes.size() > 0)
				{
					nodes.putAll(levelNodes);
					newNode = true;
				}

			}

		return nodes;

	}

	/*
	 * Pfaddistanzen in Richtung Akteur
	 */
	public HashMap<Akteur, Integer> calculateDistancesToActor(Akteur actor_start)
	{

		int level = 0;
		boolean newNode = true;

		HashMap<Akteur, Integer> nodes = new HashMap<Akteur, Integer>();

		HashMap<Akteur, Integer> levelNodes = new HashMap<Akteur, Integer>();

		nodes.put(actor_start, level);

		while (newNode == true)
		{

			level++;

			newNode = false;

			levelNodes.clear();

			for (Akteur actor_next : nodes.keySet())
			{
				for (Akteur newActor : checkAdjacencyToActor(actor_next, nodes))
				{
					levelNodes.put(newActor, level);
				}
			}

			// Akteure, mit gleicher Distanz zur Gesamtliste hinzufügen
			if (levelNodes.size() > 0)
			{
				nodes.putAll(levelNodes);
				newNode = true;
			}

		}

		return nodes;

	}

	/*
	 * In-Closeness: The sum of the geodesic distances to all other nodes in the
	 * graph. Ego is included. Missing relations will be ignored!
	 */
	public int inCloseness(Akteur actor)
	{

		int sum_col = 0;

		debug("inCloseness...");

		if (actorMap.containsKey(actor))
		{

			HashMap<Akteur, Integer> nodes = new HashMap<Akteur, Integer>();
			nodes = calculateDistancesToActor(actor);
			for (int value : nodes.values())
				sum_col = sum_col + value;

		}
		return sum_col;
	}

	/*
	 * Out-Closeness: The sum of the geodesic distances from the actor to all
	 * other nodes in the graph. Ego is included. Missing relations will be
	 * ignored!
	 */
	public int outCloseness(Akteur actor)
	{

		int sum_row = 0;

		debug("outCloseness...");

		if (actorMap.containsKey(actor))
		{
			HashMap<Akteur, Integer> nodes = new HashMap<Akteur, Integer>();
			nodes = calculateDistancesFromActor(actor);
			for (int value : nodes.values())
				sum_row = sum_row + value;

		}

		return sum_row;
	}

	/*
	 * Proximity Prestige: The invers sum of the geodesic distances to all other
	 * nodes in the graph, multiplied with the sum of all other nodes. Ego is
	 * included. Missing relations will be ignored!
	 */
	public double proximityPrestige(Akteur actor)
	{

		int sum_col = 0;
		double proximityPrestigeStd = 0;

		HashMap<Akteur, Integer> nodes = new HashMap<Akteur, Integer>();

		debug("proximityPrestige...");

		if (actorMap.containsKey(actor))
		{

			nodes = calculateDistancesToActor(actor);

			for (int value : nodes.values())
				sum_col = sum_col + value;

			if (sum_col > 0)

				proximityPrestigeStd = ((double) ((double) (nodes.size() - 1) / (double) (actorMap
						.size() - 1)))
						/ ((double) ((double) sum_col / (double) (nodes.size() - 1)));

		}
		return proximityPrestigeStd;
	}

	/*
	 * Out-Closeness (Standardized): The invers sum of the geodesic distances
	 * from the actor to all other nodes in the graph, multiplied with the sum of
	 * all other nodes. Ego is included. Missing relations will be ignored!
	 */
	public double outClosenessStd(Akteur actor)
	{

		int sum_row = 0;

		double out_closeness_std = 0;

		debug("outClosenessStd...");

		// calculateDistanceMatrix();

		if (actorMap.containsKey(actor))
		{
			HashMap<Akteur, Integer> nodes = new HashMap<Akteur, Integer>();
			nodes = calculateDistancesFromActor(actor);
			for (int value : nodes.values())
				sum_row = sum_row + value;

			if (sum_row > 0)

				out_closeness_std = ((double) ((double) (nodes.size() - 1) / (double) (network
						.getAkteure().size() - 1)))
						/ ((double) ((double) sum_row / (double) (nodes.size() - 1)));

		}

		return out_closeness_std;
	}

	/**
	 * Counts, how often specific attributetypes are among the actors in the
	 * specified network and returns the corresponding Hashmap: (<AttributeType,
	 * <characteristic, count>>)
	 * 
	 * @param n
	 *           the network yet to check
	 * @return the corresponding Hashmap
	 */
	public HashMap<AttributeType, HashMap<String, Integer>> attributeQuantities(
			Netzwerk n)
	{
		HashMap<AttributeType, HashMap<String, Integer>> returnMap = createPreValueMap();

		/* Fill the map, with the attributes of all actors in the given network */
		for (Akteur a : n.getAkteure())
		{
			/* currently only all actorattributes */
			for (AttributeType at : VennMaker.getInstance().getProject()
					.getAttributeTypes("ACTOR"))
			{

				/*
				 * only attributes with predefined values at the moment (if you want
				 * more, delete if-clause (also in the create-map-loop))
				 */
				if (at.getPredefinedValues() != null)
				{
					String atValue;
					atValue = (a.getAttributeValue(at, n) == null ? null : a
							.getAttributeValue(at, n).toString());

					if ((atValue != null) && returnMap.get(at).get(atValue) != null)
					{

						HashMap<String, Integer> r = returnMap.get(at);
						if (r.containsKey(atValue))
						{
							int v = r.get(atValue);
							returnMap.get(at).put(atValue, v + 1);
						}
					}
				}
			}
		}

		return returnMap;
	}

	/**
	 * Counts, how often specific attributetypes are among all actors in the
	 * specified network and returns the corresponding Hashmap: (<AttributeType,
	 * <characteristic, count>>)
	 * 
	 * @param n
	 *           the network yet to check
	 * @return the corresponding Hashmap
	 */
	public HashMap<AttributeType, HashMap<String, Integer>> attributeQuantities_allActors(
			Netzwerk n)
	{
		HashMap<AttributeType, HashMap<String, Integer>> returnMap = createPreValueMap();

		/* Fill the map, with the attributes of all actors in the given network */
		for (Akteur a : VennMaker.getInstance().getProject().getAkteure())
		{
			/* currently only all actorattributes */
			for (AttributeType at : VennMaker.getInstance().getProject()
					.getAttributeTypes("ACTOR"))
			{
				/*
				 * only attributes with predefined values at the moment (if you want
				 * more, delete if-clause (also in the create-map-loop))
				 */
				if (at.getPredefinedValues() != null)
				{
					String atValue;
					atValue = (a.getAttributeValue(at, n) == null ? null : a
							.getAttributeValue(at, n).toString());

					if ((atValue != null)
							&& (returnMap.get(at).containsKey(atValue)))
					{
						returnMap.get(at).put(atValue,
								returnMap.get(at).get(atValue) + 1);
					}
				}
			}
		}

		return returnMap;
	}

	/**
	 * creates the map with all predefined values
	 * 
	 * @return
	 */
	private HashMap<AttributeType, HashMap<String, Integer>> createPreValueMap()
	{
		HashMap<AttributeType, HashMap<String, Integer>> returnMap = new HashMap<AttributeType, HashMap<String, Integer>>();
		this.attributeCounter = 0;

		/* create the map with all predefined Values */
		for (AttributeType at : VennMaker.getInstance().getProject()
				.getAttributeTypes("ACTOR"))
		{
			if (at.getPredefinedValues() != null)
			{
				if (!returnMap.containsKey(at))
					returnMap.put(at, new HashMap<String, Integer>());
				for (Object o : at.getPredefinedValues())
				{
					returnMap.get(at).put(o.toString(), 0);
					this.attributeCounter++;
				}
			}
		}
		return returnMap;
	}

	/**
	 * returns the number of attributes, to list in the compute-dialog
	 * 
	 * @return number of attributes
	 */
	public int getAttributeCounter()
	{
		return this.attributeCounter;
	}

	/**
	 * Calculate the frequency of each predefined relation attribute value
	 * 
	 * @return relation attributes and frequencies
	 */
	public HashMap<Object, Integer> countRelations()
	{

		ArrayList<String> tmpRelGroup = new ArrayList<String>();

		HashMap<Object, Integer> relations = new HashMap<Object, Integer>();

		// Relationsgruppen-Liste erstellen
		for (AttributeType at : VennMaker.getInstance().getProject()
				.getAttributeTypes())
		{
			if ((!at.getType().equals("ACTOR"))
					&& (tmpRelGroup.contains(at.getType()) == false))
			{
				tmpRelGroup.add(at.getType());
			}
		}

		// alle Akteure durchgehen
		for (final Akteur alter : this.network.getAkteure())
		{

			// Gerichtete Relationen
			for (final Akteur alter2 : this.network.getAkteure())
			{
				// Gerichtete Relationen durchgehen und schauen, ob
				// Relationsgruppe vorkommt

				for (String relG : tmpRelGroup)
				{

					for (AttributeType at : VennMaker.getInstance().getProject()
							.getAttributeTypes())
					{

						for (Relation tmpR : alter.getRelations(this.network))
						{

							if ((tmpR.getAttributes(this.network) != null)
									&& (tmpR.getAkteur().equals(alter2))
									&& (!at.getType().equals("ACTOR"))
									&& (relG.equals(at.getType()))
									&& (VennMaker.getInstance().getProject()
											.getIsDirected(at.getType()))
									&& (tmpR.getAttributeCollectorValue().equals(at
											.getType())))
							{
								/* count frequency */
								relations = this.increaseRelationAttributeFrequency(at,
										tmpR, relations);
							}
						}
					}

				}

				// undirected relations

				for (String relG : tmpRelGroup)
				{

					for (AttributeType at : VennMaker.getInstance().getProject()
							.getAttributeTypes())
					{

						// Alter -> Alter2
						for (Relation tmpR : alter.getRelations(this.network))

							if ((tmpR.getAttributes(this.network) != null)
									&& (tmpR.getAkteur().equals(alter2))
									&& (!at.getType().equals("ACTOR"))
									&& (relG.equals(at.getType()))
									&& (!VennMaker.getInstance().getProject()
											.getIsDirected(at.getType()))
									&& (tmpR.getAttributeCollectorValue().equals(at
											.getType())))
							{
								// count frequency
								relations = this.increaseRelationAttributeFrequency(at,
										tmpR, relations);

							}

						// Alter2 -> Alter
						for (Relation tmpR : alter2.getRelations(this.network))

							if ((tmpR.getAttributes(this.network) != null)
									&& (tmpR.getAkteur().equals(alter))
									&& (!at.getType().equals("ACTOR"))
									&& (relG.equals(at.getType()))
									&& (!VennMaker.getInstance().getProject()
											.getIsDirected(at.getType()))
									&& (tmpR.getAttributeCollectorValue().equals(at
											.getType())))
							{
								// count frequency
								relations = this.increaseRelationAttributeFrequency(at,
										tmpR, relations);

							}

					}

				}

			}

		}

		// debug
		if (DEBUG == true)
			for (Map.Entry<Object, Integer> entry : relations.entrySet())
				System.out.println(entry.getKey().toString() + "="
						+ entry.getValue());

		return relations;
	}

	/**
	 * Increase relation attribute frequency
	 * 
	 * @param AttributeType
	 * @param relations
	 * @return relations
	 */
	private HashMap<Object, Integer> increaseRelationAttributeFrequency(
			AttributeType at, Relation relation, HashMap<Object, Integer> relations)
	{

		HashMap<Object, Integer> r = relations;

		// count frequency

		// free attribute value
		if (at.getPredefinedValues() == null)
		{
			if (relation.getAttributeValue(at, this.network) != null)
			{
				// free attribute value

				String value = relation.getAttributeValue(at, this.network)
						.toString();

				String entry = at.toString();
				if (r.get(entry) == null)
					r.put(entry, 0);
				if (value.length() > 0)
				{
					r.put(entry, r.get(entry) + 1);
				}
			}
		}
		else
		{
			// predefined attribute value
			if (relation.getAttributeValue(at, network) != null)
			{
				String value = relation.getAttributeValue(at, this.network)
						.toString();

				String entry = at.toString() + "[" + value + "]";

				if (r.get(entry) == null)
					r.put(entry, 0);

				r.put(entry, r.get(entry) + 1);
			}
		}

		return r;

	}

}
