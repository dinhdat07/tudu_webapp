import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import UserWorkspaces from './user-workspaces';
import UserWorkspacesDetail from './user-workspaces-detail';
import UserWorkspacesUpdate from './user-workspaces-update';
import UserWorkspacesDeleteDialog from './user-workspaces-delete-dialog';

const UserWorkspacesRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<UserWorkspaces />} />
    <Route path="new" element={<UserWorkspacesUpdate />} />
    <Route path=":id">
      <Route index element={<UserWorkspacesDetail />} />
      <Route path="edit" element={<UserWorkspacesUpdate />} />
      <Route path="delete" element={<UserWorkspacesDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default UserWorkspacesRoutes;
