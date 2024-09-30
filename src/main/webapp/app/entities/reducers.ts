import notification from 'app/entities/notification/notification.reducer';
import task from 'app/entities/task/task.reducer';
import workspace from 'app/entities/workspace/workspace.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  notification,
  task,
  workspace,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
