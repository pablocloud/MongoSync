package classes;

/**
 * Created by eduardo on 23/09/16.
 */
public interface Synchronization {

    /**
     * TODO: A synchronization object are composed by: query, dump, restore and index threads.
     *
     * Every synchronization has only one strategy of sync.
     * The strategy is the main kernel to manage the threads.
     *
     * Every process of synchronization has necessarily at less one query asking for the convenience to do it.
     *
     * If the sync is necessary the system must make a dump request or as many as need it.
     *
     * After download the source, the next step is the restoration.
     *
     * At the end do the index
     *
     */

    /**
     * TASKS:
     * Query
     * @Dump exist more than one king of dump to do the synchronicity:
     *  @theLast in both sides comparison
     *  @lastLocalPlusX
     *  @HowManyTheyAre
     *  @GiveMeTheSelection
     *
     * Restore
     * @Index
     *  @All
     *  @One
     *  @Seletion
     *
     */


    public Dump getDump();

    public Restore getRestore();

    public Index getIndex();




}
