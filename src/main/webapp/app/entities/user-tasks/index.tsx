import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import UserTasks from './user-tasks';
import UserTasksDetail from './user-tasks-detail';
import UserTasksUpdate from './user-tasks-update';
import UserTasksDeleteDialog from './user-tasks-delete-dialog';

const UserTasksRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<UserTasks />} />
    <Route path="new" element={<UserTasksUpdate />} />
    <Route path=":id">
      <Route index element={<UserTasksDetail />} />
      <Route path="edit" element={<UserTasksUpdate />} />
      <Route path="delete" element={<UserTasksDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default UserTasksRoutes;
