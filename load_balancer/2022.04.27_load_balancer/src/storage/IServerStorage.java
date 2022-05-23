package storage;

import model.Server;

/**
 * Data structure for storing the info about servers
 */

public interface IServerStorage {

    /**
     * adds a server if it was not in the storage, updates the server otherwise
     * @param server
     */
    public void updateServer(Server server);

    /**
     * Clear the storage from servers which did not update for the recent "milliseconds" time
     * @param milliseconds
     */
    public void clear (int milliseconds);

    /**
     *
     * @return the Server which is least loaded at the moment
     */
    public Server getOptimal();
}
