package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author xuanh08
 */
    public class    MyHashMap<K, V> implements Map61B<K, V> {

        private int initialSize ;
        private double  loadFactor;
        private Collection<Node>[] table;
        private  int size =0;


    @Override
    public V remove(K key){
        throw  new UnsupportedOperationException();
    }

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        initialSize =16;
        loadFactor =0.75;
        table = createTable(initialSize);
    }

    public MyHashMap(int initialSize) {
        this.initialSize =initialSize;
        loadFactor =0.75;
        table = createTable(initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize =initialSize;
        this.loadFactor = maxLoad;
        table = createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = (Collection<Node>[]) new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }


    public void clear(){
        table = createTable(this.initialSize);
    }

    public boolean containsKey(K key){
        int number =Math.floorMod(key.hashCode(),initialSize);
        return !table[number].isEmpty();
    }


    public V get(K key){
        if(!containsKey(key)){
            return null;
        }

        int number = Math.floorMod(key.hashCode(),initialSize);
        for(Node it : table[number]){
            if(it.key.equals(key)){
                return it.value;
            }
        }
        return null;
    }

    public int size(){
        return this.size;
    }

    public void put(K key,V value){
        size++;
        int number = Math.floorMod(key.hashCode(),initialSize);
        Node curr = new Node(key,value);
        if(table[number].isEmpty()){
            table[number].add(curr);
        }else{
            for(Node it:table[number]){
                if(it.key.equals(key)){
                    it.value=value;
                    return ;
                }

            }
            table[number].add(curr);

        }
    }

    public Set<K> keySet(){
        Set<K> now =  new HashSet<>();
        for (Collection<Node> nodes : table) {
            if (nodes.isEmpty()) {
                continue;
            }
            for (Node it : nodes) {
                if (!now.contains(it.key)) {
                    now.add(key);
                }
            }
        }
        return now;

    }


//    public V remove (K key){
//        if (key == null) {
//            return null;
//        }
//        int number = Math.floorMod(key.hashCode(),initialSize);
//        Iterator<Node> it = table[number].iterator();
//            while(it.hasNext()){
//                Node node =it.next();
//                if(node.key.equals(key)){
//                    V val = node.value;
//                    it.remove();
//                    return val;
//                }
//            }
//            return null;
//        }

    public V remove(K key, V value){
        throw new UnsupportedOperationException();

    }

    public void resize(){
        if((double)size/(double)initialSize>loadFactor){
            Collection<Node>[] curr = table;
            table = createTable(initialSize*2);
            initialSize =initialSize*2;
            for(Collection<Node> nodes : curr){
                if(nodes.isEmpty()){
                    continue;
                }
                for(Node now : nodes){
                    int number = Math.floorMod(now.key.hashCode(),initialSize);
                    table[number].add(now);
                }
            }
        }
    }



}
