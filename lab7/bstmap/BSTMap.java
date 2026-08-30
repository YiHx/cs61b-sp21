package bstmap;

import afu.org.checkerframework.checker.oigj.qual.O;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>,V> implements Map61B<K,V> {

    int size;
    BSTNode Tree;

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

    }
}
