package org.jlab.phaser.model;

/**
 * An immutable paginator.
 *
 * @author ryans
 */
public final class Paginator {

    private final long offset;
    private final long maxPerPage;
    private final long totalRecords;

    /**
     * Create a new Paginator.
     *
     * @param totalRecords The total number of records in the result set
     * @param offset The offset into the result set
     * @param maxPerPage The maximum number of records per page
     */
    public Paginator(long totalRecords, long offset, long maxPerPage) {
        this.totalRecords = totalRecords;
        this.offset = offset;
        this.maxPerPage = maxPerPage;
    }

    /**
     * Return the total number of records.
     *
     * @return The total number of records
     */
    public long getTotalRecords() {
        return totalRecords;
    }

    /**
     * Return the offset into the record list.
     *
     * @return The record list offset
     */
    public long getOffset() {
        return offset;
    }

    /**
     * Return the maximum number of records per page.
     *
     * @return The maximum records per page
     */
    public long getMaxPerPage() {
        return maxPerPage;
    }

    /**
     * Return the first record number for the current page.
     *
     * @return The current page starting record number
     */
    public long getStartNumber() {
        long startNumber = offset + 1;

        if (startNumber > totalRecords) {
            startNumber = totalRecords;
        }

        return startNumber;
    }

    /**
     * Return the last record number for the current page.
     *
     * @return The current page ending record number.
     */
    public long getEndNumber() {
        long endNumber = offset + maxPerPage;

        if (endNumber > totalRecords) {
            endNumber = totalRecords;
        }

        return endNumber;
    }

    /**
     * Return true if there at least one previous page.
     *
     * @return true if there is a previous page, false otherwise
     */
    public boolean isPrevious() {
        boolean previous = false;

        if (offset > 0) {
            previous = true;
        }

        return previous;
    }

    /**
     * Return true if there is at least one next page.
     *
     * @return true if there is a next page, false otherwise
     */
    public boolean isNext() {
        boolean next = false;

        if (totalRecords > offset + maxPerPage) {
            next = true;
        }

        return next;
    }

    /**
     * Return the offset index for the previous page.
     *
     * @return The previous page offset index
     */
    public long getPreviousOffset() {
        long previousOffset = offset - maxPerPage;

        if (previousOffset < 0) {
            previousOffset = 0;
        }

        return previousOffset;
    }

    /**
     * Return the offset index for the next page.
     *
     * @return The next page offset index
     */
    public long getNextOffset() {
        long nextOffset = offset + maxPerPage;

        if (nextOffset > (totalRecords - 1)) {
            nextOffset = totalRecords - 1;
        }

        return nextOffset;
    }

    @Override
    public String toString() {
        return "totalRecords: " + totalRecords + ", offset: " + offset + ", maxPerPage: "
                + maxPerPage;
    }
}
