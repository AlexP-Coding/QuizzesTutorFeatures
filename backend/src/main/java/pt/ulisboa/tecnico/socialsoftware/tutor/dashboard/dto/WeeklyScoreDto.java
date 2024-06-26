package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.WeeklyScore;
import java.time.LocalDateTime;

import java.io.Serializable;

public class WeeklyScoreDto implements Serializable {

  @JsonProperty("id")
  private Integer id;

  @JsonProperty("numberAnswered")
  private int numberAnswered;

  @JsonProperty("uniquelyAnswered")
  private int uniquelyAnswered;

  @JsonProperty("percentageCorrect")
  private int percentageCorrect;

  @JsonProperty("week")
  private String week;

  public WeeklyScoreDto() {
  }

  public WeeklyScoreDto(WeeklyScore weeklyScore) {
    setId(weeklyScore.getId());
    setNumberAnswered(weeklyScore.getNumberAnswered());
    setUniquelyAnswered(weeklyScore.getUniquelyAnswered());
    setPercentageCorrect(weeklyScore.getPercentageCorrect());
    setWeek(weeklyScore.getWeek().atStartOfDay().toString());
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public int getNumberAnswered() {
    return numberAnswered;
  }

  public void setNumberAnswered(int numberAnswered) {
    this.numberAnswered = numberAnswered;
  }

  public int getUniquelyAnswered() {
    return uniquelyAnswered;
  }

  public void setUniquelyAnswered(int uniquelyAnswered) {
    this.uniquelyAnswered = uniquelyAnswered;
  }

  public int getPercentageCorrect() {
    return percentageCorrect;
  }

  public void setPercentageCorrect(int percentageCorrect) {
    this.percentageCorrect = percentageCorrect;
  }

  public String getWeek() {
    return week;
  }

  public void setWeek(String week) {
    this.week = week;
  }

  @Override
  public String toString() {
    return "WeeklyScoreDto{" +
            "id=" + id +
            ", numberAnswered=" + numberAnswered +
            ", uniquelyAnswered=" + uniquelyAnswered +
            ", percentageCorrect=" + percentageCorrect +
            ", week=" + week +
            '}';
  }
}