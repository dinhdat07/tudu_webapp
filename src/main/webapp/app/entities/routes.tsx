import React from 'react';

import { Route } from 'react-router-dom';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Notification from './notification';
import Task from './task';
import UserTasks from './user-tasks';
import UserWorkspaces from './user-workspaces';
import Workspace from './workspace';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="notification/*" element={<Notification />} />
        <Route path="task/*" element={<Task />} />
        <Route path="user-tasks/*" element={<UserTasks />} />
        <Route path="user-workspaces/*" element={<UserWorkspaces />} />
        <Route path="workspace/*" element={<Workspace />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
