package com.brunomnsilva.yacl.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a cluster of objects.
 *
 * @param <T> the type of objects contained in the cluster, must implement the {@link Clusterable} interface
 */
public class Cluster<T extends Clusterable<T>> implements Iterable<T> {

    private final int id;
    private final List<T> members;

    /**
     * Constructs a new Cluster with the specified ID.
     *
     * @param id the ID of the cluster
     */
    public Cluster(int id) {
        this.id = id;
        this.members = new ArrayList<>();
    }

    /**
     * Adds a member to the cluster.
     *
     * @param member the member to add
     */
    public void addMember(T member) {
        members.add(member);
    }

    /**
     * Adds a list of members to the cluster.
     *
     * @param members the list of members to add
     * @throws IllegalArgumentException if the members list is null
     */
    public void addMembers(List<T> members) {
        Args.nullNotPermitted(members, "members");
        this.members.addAll(members);
    }

    /**
     * Returns the ID of the cluster.
     *
     * @return the ID of the cluster
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the list of members in the cluster.
     *
     * @return the list of members in the cluster
     */
    public List<T> getMembers() {
        return members;
    }

    /**
     * Returns an iterator over the members of the cluster.
     *
     * @return an iterator over the members of the cluster
     */
    @Override
    public Iterator<T> iterator() {
        return members.iterator();
    }

    /**
     * Returns the size of the cluster.
     *
     * @return the size of the cluster
     */
    public int size() {
        return members.size();
    }

    /**
     * Returns a string representation of the cluster.
     *
     * @return a string representation of the cluster
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Cluster Id = %d\n", getId()));
        sb.append(String.format("Members (%d) = {\n", size()));
        for (T member : members) {
            sb.append("\t").append(member.clusterableLabel()).append("\n");
        }
        sb.append("}\n");
        return sb.toString();
    }
}