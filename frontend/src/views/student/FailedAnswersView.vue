<template>
  <v-container fluid>
    <v-card class="table">
      <v-data-table
        :headers="headers"
        :items="failedAnswers"
        :sort-by="['collected']"
        sort-desc
        :mobile-breakpoint="0"
        :items-per-page="15"
        :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
      >
        <template v-slot:top>
          <v-card-title>
            <h3>Failed Answers</h3>
            <v-spacer />
            <v-btn
              color="primary"
              dark
              @click="updateFailedAnswers"
              data-cy="refreshFailedAnswersMenuButton"
            >REFRESH</v-btn>
          </v-card-title>
        </template>
        <template v-slot:[`item.collected`]="{ item }">
          {{ item.collected }}
        </template>
        <template v-slot:[`item.answered`]="{ item }">
          {{ item.answered? 'Yes' : 'No'}}
        </template>
        <template v-slot:[`item.question`]="{ item }">
          {{ item.questionAnswerDto.question.content }}
        </template>

        <template v-slot:[`item.action`]="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                v-on="on"
                data-cy="showStudentViewDialog"
                @click="showStudentViewDialog(item.questionAnswerDto.question)"
              >school</v-icon
              >
            </template>
            <span>Student View</span>
          </v-tooltip>
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                v-on="on"
                data-cy="deleteFailedAnswerButton"
                @click="deleteFailedAnswer(item.id)"
                color="red"
              >delete</v-icon
              >
            </template>
            <span>Delete Failed Answer (at least 5 days old)</span>
          </v-tooltip>
        </template>
      </v-data-table>
      <student-view-dialog
        v-if="statementQuestion && studentViewDialog"
        v-model="studentViewDialog"
        :statementQuestion="statementQuestion"
        v-on:close-show-question-dialog="onCloseStudentViewDialog"
      />
    </v-card>
  </v-container>
</template>

<script lang="ts">
import {Component, Prop, Vue} from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Question from '@/models/management/Question';
import FailedAnswer from '@/models/dashboard/FailedAnswer';
import StudentViewDialog from '@/views/teacher/questions/StudentViewDialog.vue';
import StatementQuestion from '@/models/statement/StatementQuestion';

@Component({
  components: {
    'student-view-dialog': StudentViewDialog,
  },
})
export default class FailedAnswersView extends Vue {
  @Prop({type: Number, required: true}) dashboardId!: number;
  @Prop({type: String}) lastCheckFailedAnswers: string = '-';
  failedAnswers: FailedAnswer[] = [];
  statementQuestion: StatementQuestion | null = null;
  studentViewDialog: boolean = false;

  headers: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '5px',
      sortable: false,
    },
    {
      text: 'Question',
      value: 'question',
      width: '30%',
      align: 'left',
      sortable: false,
    },
    {
      text: 'Answered',
      value: 'answered',
      width: '5px',
      align: 'right',
      sortable: false,
    },
    {
      text: 'Collected',
      value: 'collected',
      width: '5px',
      align: 'right',
    },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.failedAnswers = await RemoteServices.getFailedAnswers(this.dashboardId);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async updateFailedAnswers() {
    try {
      await RemoteServices.refreshFailedAnswers(this.dashboardId);
      this.failedAnswers = await RemoteServices.getFailedAnswers(this.dashboardId);
      this.$emit('refresh');
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async deleteFailedAnswer(failedAnswerId: number) {
    if (failedAnswerId) {
      try {
        await RemoteServices.deleteFailedAnswers(failedAnswerId);
        this.failedAnswers = await RemoteServices.getFailedAnswers(this.dashboardId);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async showStudentViewDialog(question: Question) {
    if (question.id) {
      try {
        this.statementQuestion = await RemoteServices.getStatementQuestion(
          question.id
        );
        this.studentViewDialog = true;
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  onCloseStudentViewDialog() {
    this.statementQuestion = null;
    this.studentViewDialog = false;
  }

}
</script>
