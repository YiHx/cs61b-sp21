package bstmap;


import java.util.Iterator;
import java.util.Set;

/**
 * @author xuanh08
 */

public class BSTMap<K extends Comparable<K>,V> implements Map61B<K,V> {

    private  BSTNode Tree;
    private int size;

    public BSTMap(){
        size=0;
        Tree=null;
    }

    @Override
    public void clear(){
        size=0;
        Tree=null;
    }

    @Override
    public boolean containsKey(K key){
        if(Tree==null){
            return false;
        }
        return Tree.get(key) != null;
    }

    @Override
    public V get(K key){
        if(Tree==null){
            return null;
        }
        BSTNode aNode = Tree.get(key);
        if(aNode!=null){
            return aNode.value;
        }else{
            return null;
        }
    }


    @Override
    public int size(){
        return size;
    }


    @Override
    public void put (K key,V value){
        if (Tree != null) {
            BSTNode aNode = Tree.get(key);
            if (aNode == null) {
                Tree= Tree.insert(Tree,key,value);
                size++;
            } else {
                aNode.value = value;
            }
        } else {
            Tree = new BSTNode(key, value, null,null);
            size++;
        }
    }






    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value){
        throw new UnsupportedOperationException();
    }


    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    public void printInOrder(){
        Tree.printInOrder();
    }

    private class BSTNode{
        K key;
        V value;
        BSTNode leftNode;
        BSTNode rightNode;
        BSTNode(K k,V v,BSTNode left,BSTNode right){
            this.key=k;
            this.value=v;
            this.leftNode=left;
            this.rightNode=right;

        }

        BSTNode get(K k) {
            if (k != null && k.equals(key)) {
                return this;
            }
            if(this.leftNode==null&&this.rightNode==null){
                return null;
            }
            if(this.rightNode!=null&& k.compareTo(this.key)>0){
                return this.rightNode.get(k);
            }else if(this.leftNode!=null&& k.compareTo(this.key)<0){
                return this.leftNode.get(k);
            }
            return null;
        }

        BSTNode insert( BSTNode Node, K key,V value) {
            if (Node == null){
                return new BSTNode(key,value,null,null);
            }
            if (key.compareTo(Node.key)<0 ){
                Node.leftNode = insert(Node.leftNode, key,value);
            }
            else if (key.compareTo(Node.key)>0 ){
                Node.rightNode = insert(Node.rightNode, key,value);
            }
            return Node;

        }
        void printInOrder(){
            printInOrder(Tree);
        }

        void printInOrder(BSTNode Node){
            if(Node==null){
                return ;
            }
            printInOrder(Node.leftNode);
            System.out.print(Node.key);
            printInOrder(Node.rightNode);
        }

    }
}
