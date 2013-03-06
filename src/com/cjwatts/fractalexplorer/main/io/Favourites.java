package com.cjwatts.fractalexplorer.main.io;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.yaml.snakeyaml.Yaml;

/**
 * A sorted list of favourite fractals
 * 
 * Automatically saves to disk after modification
 */
public class Favourites implements List<Favourite> {

	private String filename = "favourites.yml";
	private Yaml yaml = new Yaml();
	private List<Favourite> list = new ArrayList<Favourite>();
	
	/**
	 * Load the favourites file into memory
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void load() throws IOException {
		FileInputStream in = new FileInputStream(filename);
		list = (List<Favourite>) yaml.load(in);
		
		// Sort the list by name
		Collections.sort(list);
	}
	
	/**
	 * Save the favourites file to disk
	 * @throws IOException
	 */
	public void save() throws IOException {
		FileWriter fstream = new FileWriter(filename);
		BufferedWriter writer = new BufferedWriter(fstream);
		writer.write(yaml.dump(list));
		writer.close();
	}
	
	@Override
	public boolean add(Favourite arg0) {
		boolean result = list.add(arg0);
		if (result)
			try {
				Collections.sort(list);
				save();
			} catch (IOException ex) {
				System.err.println("Autosave failed - " + ex.getMessage());
				ex.printStackTrace();
				return false;
			}
		return result;
	}

	@Override
	public void add(int arg0, Favourite arg1) {
		list.add(arg0, arg1);
		try {
			save();
		} catch (IOException ex) {
			System.err.println("Autosave failed - " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	@Override
	public boolean addAll(Collection<? extends Favourite> arg0) {
		boolean result = list.addAll(arg0);
		if (result)
			try {
				Collections.sort(list);
				save();
			} catch (IOException ex) {
				System.err.println("Autosave failed - " + ex.getMessage());
				ex.printStackTrace();
				return false;
			}
		return result;
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends Favourite> arg1) {
		boolean result = list.addAll(arg0, arg1);
		if (result)
			try {
				save();
			} catch (IOException ex) {
				System.err.println("Autosave failed - " + ex.getMessage());
				ex.printStackTrace();
				return false;
			}
		return result;
	}

	@Override
	public void clear() {
		list.clear();
		try {
			save();
		} catch (IOException ex) {
			System.err.println("Autosave failed - " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	@Override
	public boolean contains(Object arg0) {
		return list.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return list.containsAll(arg0);
	}

	@Override
	public Favourite get(int arg0) {
		return list.get(arg0);
	}
	
	/**
	 * Return an entry by its name
	 * @param name
	 */
	public Favourite getByName(String name) {
		return getByName(name, 0, list.size() - 1);
	}
	
	/**
	 * Recursive helper function for binary search
	 * @param name
	 * @param lower Lower bound of search
	 * @param upper Upper bound of search
	 */
	private Favourite getByName(String name, int lower, int upper) {
		// Compare the middle element to the search name
		int mid = lower + upper / 2;
		int compare = list.get(mid).getName().compareTo(name);

		if (compare < 0)
			return getByName(name, lower, mid - 1);
		else if (compare > 0)
			return getByName(name, mid + 1, upper);
		else
			return list.get(compare);
	}

	@Override
	public int indexOf(Object arg0) {
		return list.indexOf(arg0);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Iterator<Favourite> iterator() {
		return list.iterator();
	}

	@Override
	public int lastIndexOf(Object arg0) {
		return list.lastIndexOf(arg0);
	}

	@Override
	public ListIterator<Favourite> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<Favourite> listIterator(int arg0) {
		return list.listIterator(arg0);
	}

	@Override
	public boolean remove(Object arg0) {
		boolean result = list.remove(arg0);
		if (result)
			try {
				save();
			} catch (IOException ex) {
				System.err.println("Autosave failed - " + ex.getMessage());
				ex.printStackTrace();
				return false;
			}
		return result;
	}

	@Override
	public Favourite remove(int arg0) {
		Favourite result = list.remove(arg0);
		try {
			save();
		} catch (IOException ex) {
			System.err.println("Autosave failed - " + ex.getMessage());
			ex.printStackTrace();
		}
		return result;
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		boolean result = list.removeAll(arg0);
		if (result)
			try {
				save();
			} catch (IOException ex) {
				System.err.println("Autosave failed - " + ex.getMessage());
				ex.printStackTrace();
				return false;
			}
		return result;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		boolean result = list.retainAll(arg0);
		if (result)
			try {
				save();
			} catch (IOException ex) {
				System.err.println("Autosave failed - " + ex.getMessage());
				ex.printStackTrace();
				return false;
			}
		return result;
		
	}

	@Override
	public Favourite set(int arg0, Favourite arg1) {
		Favourite result = list.set(arg0, arg1);
		try {
			save();
		} catch (IOException ex) {
			System.err.println("Autosave failed - " + ex.getMessage());
			ex.printStackTrace();
		}
		return result;
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public List<Favourite> subList(int arg0, int arg1) {
		return list.subList(arg0, arg1);
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		return list.toArray(arg0);
	}
	
}
