package de.raidcraft.dragontravelplus.paths;

import de.raidcraft.api.flight.flight.Waypoint;
import de.raidcraft.api.pathfinding.Graph;
import de.raidcraft.api.pathfinding.GraphNode;
import de.raidcraft.api.pathfinding.GraphSearch_Astar;
import org.bukkit.Location;

import java.util.List;

/**
 * @author mdoering
 */
public class AStarFlightPath extends DynamicFlightPath {

    public AStarFlightPath(Location start, Location end) {

        super(start, end);
    }

    @Override
    public void calculate() {

        super.calculate();
        List<Waypoint> waypoints = clearWaypoints();
        Graph graph = new Graph();
        for (int i = 0; i < waypoints.size(); i++) {
            GraphNode node = new GraphNode(i, waypoints.get(i).getX(), waypoints.get(i).getY(), waypoints.get(i).getZ());
            graph.addNode(node);
        }
        GraphSearch_Astar searcher = new GraphSearch_Astar(graph);
        searcher.search(0, getWaypointAmount() - 1, true);
        clearWaypoints();
        for (GraphNode node : searcher.getRoute()) {
            addWaypoint(new Waypoint(getStartLocation().getWorld(), node.x(), node.y(), node.z()));
        }
    }
}
