package pl.rosiakit.graph;

import java.util.HashSet;
import java.util.Set;

/**
 * Wrapper class used in ShortestPathsFinder in BFS algorithm
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-08-12
 */
class Node<K>{

    private Set<Node> parents = new HashSet<>();

    private final K key;

    private int level = -1;

    private boolean isVisited = false;

    Node(K key){
        this(key, -1);
    }

    Node(K key, int level){
        this.key = key;
        this.level = level;
    }

    K getKey() {
        return key;
    }

    boolean isVisited(){
        return this.isVisited;
    }

    void markAsVisited(){
        this.isVisited = true;
    }

    int getLevel() {
        return level;
    }

    Set<Node> getParents() {
        return parents;
    }

    void addParent(Node node){
        if(node.getLevel() != this.getLevel()){
            parents.add(node);
            level = node.getLevel()+1;
        }
        else{
            throw new IllegalArgumentException("Parent cannot be on the same level as child!");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;

        Node<?> node = (Node<?>) o;

        return getKey().equals(node.getKey());

    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    @Override
    public String toString() {
        return "Node{" +
                "key=" + key +
                ", level=" + level +
                ", parents=" + parents +
                '}';
    }
}