package pl.rosiakit.graph;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is using BFS to find all shortest paths. Remember that algorithm is ignoring edges weights.
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-08-12
 */
public class ShortestPathsFinder<V, E> {

    private final AdjacencyList<Node<V>> adjacencyList;

    private final DirectedGraph<V, E> graph;

    // This map is using to interning nodes
    private Map<V, Node<V>> existingNodes = new HashMap<>();

    /**
     * @param graph DirectedGraph provided by JGraphT library
     */
    public ShortestPathsFinder(DirectedGraph<V, E> graph) {
        this.adjacencyList = createAdjacencyListFromGraph(graph);
        this.graph = graph;
    }

    private AdjacencyList<Node<V>> createAdjacencyListFromGraph(DirectedGraph<V, E> graph){
        AdjacencyList<Node<V>> adjacencyList = new AdjacencyList<>();

        for(V v : graph.vertexSet()){
            List<V> vertices = Graphs.successorListOf(graph, v);
            Node<V> thisNode = getNode(v);
            Set<Node<V>> successors = new HashSet<>(convertAllVerticesToNodes(vertices));
            adjacencyList.addAllSuccessors(thisNode, successors);
        }

        return adjacencyList;
    }

    private Set<Node<V>> convertAllVerticesToNodes(List<V> vertices){
        return vertices.stream().map(this::getNode).collect(Collectors.toSet());
    }

    private Node<V> getNode(V v){
        Node<V> node = existingNodes.get(v);

        if(node == null){
            node = new Node<>(v);
            existingNodes.put(v, node);
        }

        return node;
    }

    /**
     * How does it works? First it tries to find target vertex using modified BFS algorithm. BFS modification allows to
     * visit again nodes (there are no gray colored vertices, but only white and black) and this creates a Directed
     * Acyclic Graph from start to target node. Then it uses DFS algorithm to split it into set of shortest routes.
     * @param from starting node
     * @param to node that you are looking for
     * @return all the shortest paths as set of list of edges.
     * @throws IllegalArgumentException when source or target node does not exist in graph.
     */
    public Set<List<E>> findAllShortestPaths(V from, V to){
        Node<V> startNode = new Node<>(from, 0);

        if(!adjacencyList.containsNode(startNode)){
            throw new IllegalArgumentException("Graph must contain start node!");
        }

        Node<V> endNode = BreadthFirstSearch(startNode, to);

        if(endNode == null){
            throw new IllegalArgumentException("Graph must contain end node!");
        }

        Set<List<V>> result = constructPathsFromDAG(startNode, endNode);

        return convertAllPathsFromVerticesToEdges(result);
    }

    private Set<List<E>> convertAllPathsFromVerticesToEdges(Set<List<V>> paths){
        Set<List<E>> result = new HashSet<>();

        for(List<V> path : paths){
            try{
                result.add(convertPathFromVerticesToEdges(path));
            }
            catch(Exception e){
                System.err.println(e.getMessage());
            }
        }

        return result;
    }

    private List<E> convertPathFromVerticesToEdges(List<V> path) throws NoSuchElementException{
        List<E> result = new ArrayList<>();

        for(int i = 1; i < path.size(); ++i){
            V curr = path.get(i);
            V prev = path.get(i-1);

            E edge = graph.getEdge(prev, curr);

            if(edge != null){
                result.add(edge);
            }
            else{
                throw new NoSuchElementException("There is no edge between "+prev+" and "+curr);
            }

        }

        return result;
    }

    private Set<List<V>> constructPathsFromDAG(Node<V> source, Node<V> target){
        Set<List<V>> result = new HashSet<>();

        List<V> list = new ArrayList<>();

        Stack<Node<V>> stack = new Stack<>();
        stack.push(target);

        while(!stack.isEmpty()){
            Node<V> u = stack.pop();

            list.add(0, u.getKey());

            if(u.equals(source)){
                result.add(list);
                list = new ArrayList<>();
                list.add(target.getKey());
            }

            u.getParents().forEach(stack::push);
        }

        return result;
    }

    private Node<V> BreadthFirstSearch(Node<V> source, V targetValue){
        Node<V> targetNode = null;

        Set<Node<V>> queue = new LinkedHashSet<>();
        queue.add(source);

        while (!queue.isEmpty()){
            Node<V> u = queue.iterator().next();

            // Searching element has been found
            if(u.getKey().equals(targetValue)){
                targetNode = u;
            }

            adjacencyList.getSuccessorsOf(u).stream().filter(v -> !v.isVisited()).forEach(v -> {
                if (!queue.contains(v)) {
                    queue.add(v);
                }

                if (v.getLevel() != u.getLevel()) {
                    v.addParent(u);
                }
            });

            queue.remove(u);
            u.markAsVisited();
        }

        return targetNode;
    }

}
