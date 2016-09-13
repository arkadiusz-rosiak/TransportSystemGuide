package pl.rosiakit.graph;

import java.util.*;

/**
 * Class representing adjacency list (Only for simple graphs). Used in ShortestPathsFinder.
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-08-10
 */
class AdjacencyList<N>{

    private Map<N, Set<N>> adjacencyList = new HashMap<>();

    AdjacencyList() {
    }

    public void addSuccessorTo(N key, N successor){
        Set<N> currentSet = getSuccessorsOf(key);
        currentSet.add(successor);
        adjacencyList.put(key, currentSet);
    }

    void addAllSuccessors(N key, Set<N> successors){
        Set<N> currentSet = getSuccessorsOf(key);
        currentSet.addAll(successors);
        adjacencyList.put(key, currentSet);
    }

    Set<N> getSuccessorsOf(N node){
        Set<N> successors = new HashSet<>();
        successors.addAll(adjacencyList.getOrDefault(node, new HashSet<>()));
        return successors;
    }

    boolean containsNode(N node){
        return adjacencyList.containsKey(node);
    }


    @Override
    public String toString() {
        return adjacencyList.toString();
    }
}
