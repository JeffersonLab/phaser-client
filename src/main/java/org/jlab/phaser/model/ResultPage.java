package org.jlab.phaser.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An immutable page of results as returned from a results query. The number of records in the page
 * is determined based on the paginator, filter, and actual number in the database.
 *
 * @author ryans
 */
public final class ResultPage {

  private final List<ResultRecord> records;
  private final Paginator paginator;
  private final ResultFilter filter;

  /**
   * Create a new ResultPage.
   *
   * @param records The records
   * @param paginator The paginator
   * @param filter The filter
   */
  public ResultPage(List<ResultRecord> records, Paginator paginator, ResultFilter filter) {
    this.records = new ArrayList<>(records);
    this.paginator = paginator;
    this.filter = filter;
  }

  /**
   * Returns the records.
   *
   * @return The records
   */
  public List<ResultRecord> getRecords() {
    // This potentially has better performance than doing a copy
    return Collections.unmodifiableList(records);
  }

  /**
   * Returns the paginator.
   *
   * @return The paginator
   */
  public Paginator getPaginator() {
    return paginator;
  }

  /**
   * Returns the filter.
   *
   * @return The filter
   */
  public ResultFilter getFilter() {
    return filter;
  }
}
