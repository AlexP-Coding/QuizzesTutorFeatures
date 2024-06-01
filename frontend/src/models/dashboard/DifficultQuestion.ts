import Question from '@/models/management/Question'
import {ISOtoString} from '@/services/ConvertDateService';

export default class DifficultQuestion {
    id!: number;
    percentage!: number
    removeDate!: string
    removed!: boolean
    questionDto!: Question

    constructor(jsonObj?: DifficultQuestion) {
        if (jsonObj) {
            this.id = jsonObj.id;
            this.percentage = jsonObj.percentage;
            this.removeDate = ISOtoString(jsonObj.removeDate);
            this.removed = jsonObj.removed;
            this.questionDto = new Question(jsonObj.questionDto);
        }
    }
}