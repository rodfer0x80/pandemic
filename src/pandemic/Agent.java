package pandemic;

import java.util.Comparator;

// montecarlo search tree
// show user rules, own cards, deck probabilities, possible next turn actions
// interact with user to select game plans and show probabilities of 4 best next moves
// along with next actions that need to be taken for the next 4 turns

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class Node {
    private static final double EXPLORATION_FACTOR = 1.0 / Math.sqrt(2);

    private final GameState state;
    private Node parent;
    final List<Node> children = new ArrayList<>();
    private int visitCount = 0;
    private double winScore = 0;

    public Node(GameState state, Node parent) {
        this.state = state;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.visitCount = 0;
        this.winScore = 0;
    }

    public Node select() {
        Node selected = null;
        double bestUCT = Double.NEGATIVE_INFINITY;
        for (Node child : children) {
            double uct = child.getUCTValue();
            if (uct > bestUCT) {
                selected = child;
                bestUCT = uct;
            }
        }
        if (selected == null) {
            throw new RuntimeException("No children to select from.");
        }
        return selected;
    }

    public Node expand() {
        List<GameState> possibleMoves = state.getLegalMoves();
        Collections.shuffle(possibleMoves);
        for (GameState move : possibleMoves) {
            Node child = new Node(move);
            child.setParent(this);
            children.add(child);
        }
        Node selected = children.get(0);
        return selected;
    }

    public double simulate() {
        GameState simulationState = state.clone();
        Random random = new Random();
        while (!simulationState.isGameOver()) {
            List<GameState> possibleMoves = simulationState.getLegalMoves();
            Collections.shuffle(possibleMoves);
            simulationState = possibleMoves.get(random.nextInt(possibleMoves.size())).clone();
        }
        return simulationState.getScore();
    }

    public void backpropagate(double score) {
        visitCount++;
        winScore += score;
        if (parent != null) {
            parent.backpropagate(score);
        }
    }

    private double getUCTValue() {
        if (visitCount == 0) {
            return Double.MAX_VALUE;
        }
        return winScore / visitCount + EXPLORATION_FACTOR * Math.sqrt(Math.log(parent.visitCount) / visitCount);
    }

    public GameState getState() {
        return state;
    }
    
    public List<Node> getChildren() {
        return children;
    }

    private void setParent(Node parent) {
        this.parent = parent;
    }

    public int getVisitCount() {
        return visitCount;
    }
}

public class Agent {
    private final Node rootNode;
    private final int maxIterations;

    public Agent(Node rootNode, int maxIterations) {
        this.rootNode = rootNode;
        this.maxIterations = maxIterations;
    }

    public GameState findNextMove() {
        if (rootNode.getState().getLegalMoves().isEmpty()) {
            throw new RuntimeException("No legal moves for root node.");
        }
        for (int i = 0; i < maxIterations; i++) {
            Node promisingNode = selectPromisingNode(rootNode);
            if (promisingNode.getState().isGameOver()) {
                throw new RuntimeException("Promising node is a terminal state.");
            }
            Node expandedNode = expandNode(promisingNode);
            double score = simulateRandomPlayout(expandedNode);
            backpropagateScore(expandedNode, score);
        }
        List<Node> childNodes = rootNode.getChildren();
        if (childNodes.isEmpty()) {
            throw new RuntimeException("No children for root node.");
        }
        return childNodes.stream()
                .max(Comparator.comparing(Node::getVisitCount))
                .orElseThrow(RuntimeException::new)
                .getState();
    }

    private Node selectPromisingNode(Node rootNode) {
        Node node = rootNode;
        while (!node.getChildren().isEmpty()) {
            node = node.select();
        }
        return node;
    }

    private Node expandNode(Node node) {
        return node.expand();
    }

    private double simulateRandomPlayout(Node node) {
        return node.simulate();
    }

    private void backpropagateScore(Node node, double score) {
        node.backpropagate(score);
    }
}
