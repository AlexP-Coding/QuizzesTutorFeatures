<template>
  <v-container fluid>
    <v-card class="table">
      <v-row fluid>
        <v-col> <h1> WeeklyScores </h1>
        </v-col>
        <v-col class="text-right">
          <v-btn
              color="primary"
              dark
              data-cy="refreshWeeklyScoresMenuButton"
              @click="refresh"
          >Refresh</v-btn>
        </v-col>
      </v-row>
      <v-data-table
        :headers="headers"
        :items="weeklyScores"
        :sort-by="['creationDate']"
        :sort-desc="[true]"
        :mobile-breakpoint="0"
        :items-per-page="10"
        :footer-props="{ itemsPerPageOptions: [10, 25, 50, 100] }"
      >
        <template v-slot:[`item.action`]="{ item }">
          <v-tooltip>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                v-on="on"
                data-cy="deleteWeeklyScores"
                @click="deleteWeekly(item)"
                color="red"
                >delete</v-icon
              >
            </template>
            <span>Delete Weekly</span>
          </v-tooltip>
        </template>
      </v-data-table>
    </v-card>
  </v-container>
</template>

<script lang="ts">
import {Component, Prop, Vue, Watch} from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import WeeklyScore from '@/models/dashboard/WeeklyScore';

@Component({
})
export default class WeeklyScoreView extends Vue {
  @Prop() readonly dashboardId!: number;
  weeklyScores: WeeklyScore[] = [];

  headers: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '5px',
      sortable: false,
    },
    {
      text: 'Week',
      value: 'week',
      width: '150px',
      align: 'center',
    },
    {
      text: 'Number Answered',
      value: 'numberAnswered',
      width: '5px',
      align: 'center',
    },
    {
      text: 'Uniquely Answered',
      value: 'uniquelyAnswered',
      width: '5px',
      align: 'center',
    },
    {
      text: 'Percentage Answered',
      value: 'percentageCorrect',
      width: '5px',
      align: 'center',
    },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.weeklyScores = await RemoteServices.getWeeklyScores(this.dashboardId);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
  async refresh(){
    await RemoteServices.updateWeeklyScores(this.dashboardId);
    this.weeklyScores = await RemoteServices.getWeeklyScores(this.dashboardId);
    await this.$emit('refresh');
  }
  async deleteWeekly(toDeleteWeekly: WeeklyScore) {
    if (
      toDeleteWeekly.id &&
      confirm('Are you sure you want to delete this WeeklyScore?')
    ) {
      try {
        await RemoteServices.deleteWeekly(toDeleteWeekly.id);
        this.weeklyScores = this.weeklyScores.filter(
          (weeklyScore) => weeklyScore.id != toDeleteWeekly.id
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.question-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 200px !important;
  }
}
.option-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 100px !important;
  }
}
h1 {
  font-size: 26px;
  text-align: left;
  small {
    font-size: 0.5em;
  }

}
</style>
