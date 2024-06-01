<template>
  <div class="container">
    <h2>Dashboard</h2>

    <v-card class="table">
      <v-row>
        <v-col>
          <v-btn
            color="primary"
            dark
            v-on:click="show = 'Global'"
          >
            Global Statistics</v-btn
          >
        </v-col>
        <v-col>
          <v-btn color="primary"
                 dark
                 data-cy="weeklyScoresMenuButton"
                 v-on:click="show = 'Weekly'"
          >Weekly Scores <br/> {{dashboard != null ? dashboard.lastCheckWeeklyScores : '-'}}</v-btn
          >
        </v-col>
        <v-col>
          <v-btn
            color="primary"
            dark
            data-cy="failedAnswersMenuButton"
            v-on:click="show = 'Failed'"
          >Failed Answers <br/> {{lastCheckFailedAnswers}}</v-btn
          ></v-col
        >
        <v-col>
          <v-btn color="primary"
                 dark
                 data-cy="difficultQuestionsMenuButton"
                 v-on:click="show = 'Difficult'"
          >Difficult Questions <br/> {{dashboard != null ? dashboard.lastCheckDifficultQuestions : '-'}}</v-btn
          ></v-col
        >
      </v-row>
    </v-card>

    <div v-if="show === 'Global'" class="stats-container">
      <global-stats-view></global-stats-view>
    </div>
    
    <div v-if="show === 'Difficult'" class="stats-container">
      <difficult-questions-view :dashboardId="dashboardId" v-on:refresh="onDifficultQuestionsRefresh">
      </difficult-questions-view>
    </div>
    
    <div v-if="show === 'Failed'" class="stats-container">
      <failed-answers-view :dashboardId="dashboardId" :lastCheckFailedAnswers="lastCheckFailedAnswers" v-on:refresh="onFailedAnswersRefresh">
      </failed-answers-view>
    </div>

    <div v-if="show === 'Weekly'" class="stats-container">
      <weekly-scores-view :dashboardId="dashboardId" v-on:refresh="onWeeklyScoresRefresh">
      </weekly-scores-view>
    </div>


  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import GlobalStatsView from '@/views/student/GlobalStatsView.vue';

import DifficultQuestionsView from '@/views/student/DifficultQuestionsView.vue';

import FailedAnswersView from '@/views/student/FailedAnswersView.vue';

import WeeklyScoreView from '@/views/student/WeeklyScoreView.vue';

import Dashboard from '@/models/dashboard/Dashboard';

@Component({
  components: { 'global-stats-view': GlobalStatsView,
  
                'difficult-questions-view': DifficultQuestionsView,

                'failed-answers-view': FailedAnswersView,

                'weekly-scores-view': WeeklyScoreView,
  },
})
export default class StatsView extends Vue {
  dashboard: Dashboard | null = null;
  dashboardId!: number;
  lastCheckFailedAnswers: string = '-';

  show: string = 'Global';

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.dashboard = await RemoteServices.getUserDashboard();
      this.dashboardId = this.dashboard.id;

      if (this.dashboard != null && this.dashboard.lastCheckFailedAnswers != null)
        this.lastCheckFailedAnswers = this.dashboard.lastCheckFailedAnswers;

    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async onDifficultQuestionsRefresh() {
    this.dashboard = await RemoteServices.getUserDashboard();
  }

  async onFailedAnswersRefresh() {
    await this.$store.dispatch('loading');
    try {
      this.dashboard = await RemoteServices.getUserDashboard();
      this.dashboardId = this.dashboard.id;
      if (this.dashboard != null && this.dashboard.lastCheckFailedAnswers != null)
        this.lastCheckFailedAnswers = this.dashboard.lastCheckFailedAnswers;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async onWeeklyScoresRefresh() {
    this.dashboard = await RemoteServices.getUserDashboard();
  }
}
</script>