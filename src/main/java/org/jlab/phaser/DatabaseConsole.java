package org.jlab.phaser;

import java.util.LinkedHashSet;
import org.jlab.phaser.exception.CommandException;
import org.jlab.phaser.model.CavityWithLastCorrection;
import org.jlab.phaser.model.JobFilter;
import org.jlab.phaser.model.JobPage;
import org.jlab.phaser.model.ResultFilter;
import org.jlab.phaser.model.ResultPage;
import org.jlab.phaser.model.Paginator;

/**
 * A contract for classes to query the Phaser Database.
 *
 * @author ryans
 */
public interface DatabaseConsole {

    /**
     * Returns the phasing results after applying the supplied filter and
     * paginator.
     *
     * @param filter The filter
     * @param paginator The paginator
     * @return The phasing results
     * @throws CommandException If unable to query the results
     */
    public ResultPage results(ResultFilter filter, Paginator paginator) throws
            CommandException;

    /**
     * Returns the Phaser jobs after applying the supplied filter and paginator.
     *
     * @param filter The filter
     * @param paginator The paginator
     * @return The Phaser jobs
     * @throws CommandException If unable to query the jobs
     */
    public JobPage jobs(JobFilter filter, Paginator paginator) throws
            CommandException;

    /**
     * Queries the results database table for the most recent correction date of
     * each cavity having result data.
     *
     * @return A list of cavities with their last correction date
     * @throws CommandException If unable to query the results
     */
    public LinkedHashSet<CavityWithLastCorrection> cavitiesWithLastCorrection()
            throws CommandException;
}
