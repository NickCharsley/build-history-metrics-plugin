package jenkins.plugins.model;

import com.google.common.collect.Ordering;
import hudson.model.Result;
import java.util.List;

public class MTTFMetric implements AggregateBuildMetric {
  private String metricName;
  private long metricValue;
  private int occurences;

  public MTTFMetric(String metricName, List<BuildMessage> builds) {
    this.metricName = metricName;
    initialize(Ordering.natural().sortedCopy(builds));
  }

  private void initialize(List<BuildMessage> builds) {
    long successBuildDate = 0;
    long totalSuccessTime = 0;
    for (BuildMessage build : builds) {
      String result = build.getResult();
      if (result == null) continue;

      if (!result.equals(Result.FAILURE.toString())) {
        if (successBuildDate != 0) continue;

        successBuildDate = build.getStartTime();
        continue;
      }

      if (successBuildDate == 0) continue;

      long successLastTime = build.getStartTime() - successBuildDate;
      totalSuccessTime += successLastTime;
      occurences++;

      successBuildDate = 0;
    }
    metricValue = occurences > 0 ? totalSuccessTime / occurences : 0;
  }

  @Override
  public int getOccurences() {
    return occurences;
  }

  @Override
  public long calculateMetric() {
    return metricValue;
  }

  @Override
  public String getName() {
    return metricName;
  }
}
